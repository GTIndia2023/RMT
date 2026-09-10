"""Generate machine-readable CI summary artifacts for the RMT automation project.

This script is intentionally dependency-free so it can run in both local and CI
environments after a Maven/TestNG execution. It reads the standard Surefire,
Allure, and log artifacts and writes a compact JSON and Markdown summary under
artifacts/ci/.
"""

from __future__ import annotations

import json
from datetime import datetime
from pathlib import Path
from typing import Any
from xml.etree import ElementTree


PROJECT_ROOT = Path(__file__).resolve().parents[1]
SUREFIRE_TEXT_REPORT = PROJECT_ROOT / "target" / "surefire-reports" / "TestSuite.txt"
SUREFIRE_XML_REPORT = PROJECT_ROOT / "target" / "surefire-reports" / "testng-results.xml"
ALLURE_REPORT_DIR = PROJECT_ROOT / "allure-report"
ALLURE_SUMMARY_FILE = ALLURE_REPORT_DIR / "summary.json"
ALLURE_HISTORY_DIR = ALLURE_REPORT_DIR / "data" / "history"
TEST_LOG_DIR = PROJECT_ROOT / "test_Logs"
FAILURE_ARTIFACT_DIR = TEST_LOG_DIR / "failure-artifacts"
CI_ARTIFACT_DIR = PROJECT_ROOT / "artifacts" / "ci"
CI_SUMMARY_JSON = CI_ARTIFACT_DIR / "ci-summary.json"
CI_SUMMARY_MD = CI_ARTIFACT_DIR / "ci-summary.md"


def read_text(path: Path) -> str:
    """Read a text file safely and return an empty string when it is absent."""
    if not path.exists():
        return ""
    return path.read_text(encoding="utf-8", errors="replace")


def read_json(path: Path) -> Any:
    """Read JSON safely and return None when the file is missing or malformed."""
    if not path.exists():
        return None
    try:
        return json.loads(read_text(path))
    except json.JSONDecodeError:
        return None


def tail_text(text: str, max_chars: int = 3000) -> str:
    """Trim large text payloads so the CI summary remains compact."""
    normalized = (text or "").strip()
    if len(normalized) <= max_chars:
        return normalized
    return normalized[-max_chars:]


def parse_testng_results() -> dict[str, Any]:
    """Parse the TestNG XML summary into a JSON-friendly structure."""
    if not SUREFIRE_XML_REPORT.exists():
        return {
            "exists": False,
            "path": str(SUREFIRE_XML_REPORT),
        }

    root = ElementTree.parse(SUREFIRE_XML_REPORT).getroot()
    suite = root.find("suite")
    tests: list[dict[str, Any]] = []
    failed_methods: list[dict[str, Any]] = []
    skipped_methods: list[dict[str, Any]] = []

    if suite is not None:
        for test_node in suite.findall("test"):
            test_info = {
                "name": test_node.attrib.get("name", "").strip(),
                "started_at": test_node.attrib.get("started-at", "").strip(),
                "finished_at": test_node.attrib.get("finished-at", "").strip(),
                "duration_ms": int(test_node.attrib.get("duration-ms", "0") or 0),
                "failed_methods": [],
                "skipped_methods": [],
            }

            for class_node in test_node.findall("class"):
                class_name = class_node.attrib.get("name", "").strip()
                for method_node in class_node.findall("test-method"):
                    if method_node.attrib.get("is-config", "false").lower() == "true":
                        continue
                    method_name = method_node.attrib.get("name", "").strip()
                    status = method_node.attrib.get("status", "").strip()
                    method_summary = {
                        "class_name": class_name,
                        "method_name": method_name,
                        "full_name": f"{class_name}.{method_name}",
                        "status": status,
                        "duration_ms": int(method_node.attrib.get("duration-ms", "0") or 0),
                    }
                    if status == "FAIL":
                        test_info["failed_methods"].append(method_summary)
                        failed_methods.append(method_summary)
                    elif status == "SKIP":
                        test_info["skipped_methods"].append(method_summary)
                        skipped_methods.append(method_summary)

            tests.append(test_info)

    return {
        "exists": True,
        "path": str(SUREFIRE_XML_REPORT),
        "total": int(root.attrib.get("total", "0") or 0),
        "passed": int(root.attrib.get("passed", "0") or 0),
        "failed": int(root.attrib.get("failed", "0") or 0),
        "skipped": int(root.attrib.get("skipped", "0") or 0),
        "ignored": int(root.attrib.get("ignored", "0") or 0),
        "suite_name": suite.attrib.get("name", "").strip() if suite is not None else "",
        "started_at": suite.attrib.get("started-at", "").strip() if suite is not None else "",
        "finished_at": suite.attrib.get("finished-at", "").strip() if suite is not None else "",
        "duration_ms": int(suite.attrib.get("duration-ms", "0") or 0) if suite is not None else 0,
        "tests": tests,
        "failed_methods": failed_methods,
        "skipped_methods": skipped_methods,
    }


def find_latest_log() -> Path | None:
    """Return the newest timestamped log file when available."""
    if not TEST_LOG_DIR.exists():
        return None
    candidates = [path for path in TEST_LOG_DIR.iterdir() if path.is_file() and path.suffix.lower() == ".log"]
    if not candidates:
        return None
    return max(candidates, key=lambda item: item.stat().st_mtime)


def list_recent_failure_artifacts(limit: int = 10) -> list[dict[str, Any]]:
    """List the most recent failure artifacts for quick CI triage."""
    if not FAILURE_ARTIFACT_DIR.exists():
        return []
    artifacts = []
    for path in sorted(FAILURE_ARTIFACT_DIR.iterdir(), key=lambda item: item.stat().st_mtime, reverse=True):
        if not path.is_file():
            continue
        artifacts.append(
            {
                "name": path.name,
                "path": str(path),
                "modified_at": datetime.fromtimestamp(path.stat().st_mtime).isoformat(timespec="seconds"),
            }
        )
        if len(artifacts) >= limit:
            break
    return artifacts


def build_summary() -> dict[str, Any]:
    """Build the CI summary payload from the current project artifacts."""
    testng_summary = parse_testng_results()
    allure_summary = read_json(ALLURE_SUMMARY_FILE)
    latest_log = find_latest_log()

    if testng_summary.get("failed", 0) > 0:
        overall_status = "failed"
    elif testng_summary.get("skipped", 0) > 0:
        overall_status = "unstable"
    elif testng_summary.get("exists"):
        overall_status = "passed"
    else:
        overall_status = "unknown"

    latest_log_payload = {
        "exists": latest_log is not None,
        "path": str(latest_log) if latest_log else "",
        "modified_at": datetime.fromtimestamp(latest_log.stat().st_mtime).isoformat(timespec="seconds")
        if latest_log
        else "",
        "tail": tail_text(read_text(latest_log), max_chars=2000) if latest_log else "",
    }

    return {
        "generated_at": datetime.now().isoformat(timespec="seconds"),
        "project_root": str(PROJECT_ROOT),
        "overall_status": overall_status,
        "testng": testng_summary,
        "surefire_text_report_path": str(SUREFIRE_TEXT_REPORT),
        "surefire_text_report_excerpt": tail_text(read_text(SUREFIRE_TEXT_REPORT), max_chars=2000),
        "allure": {
            "report_dir": str(ALLURE_REPORT_DIR),
            "summary": allure_summary,
            "history_file_count": len(list(ALLURE_HISTORY_DIR.glob("*"))) if ALLURE_HISTORY_DIR.exists() else 0,
        },
        "latest_log": latest_log_payload,
        "recent_failure_artifacts": list_recent_failure_artifacts(),
    }


def build_markdown(summary: dict[str, Any]) -> str:
    """Render a human-readable Markdown summary from the CI summary payload."""
    testng = summary.get("testng", {})
    latest_log = summary.get("latest_log", {})

    lines = [
        "# RMT CI Summary",
        "",
        f"- Generated at: `{summary.get('generated_at', '')}`",
        f"- Overall status: `{summary.get('overall_status', 'unknown')}`",
        f"- Suite: `{testng.get('suite_name', '')}`",
        f"- Total: `{testng.get('total', 0)}`",
        f"- Passed: `{testng.get('passed', 0)}`",
        f"- Failed: `{testng.get('failed', 0)}`",
        f"- Skipped: `{testng.get('skipped', 0)}`",
        "",
        "## Latest Log",
        "",
        f"- Path: `{latest_log.get('path', '')}`",
        f"- Modified at: `{latest_log.get('modified_at', '')}`",
        "",
        "## Failed Methods",
        "",
    ]

    failed_methods = testng.get("failed_methods", [])
    if failed_methods:
        for method in failed_methods:
            lines.append(f"- `{method.get('full_name', '')}`")
    else:
        lines.append("- None")

    lines.extend([
        "",
        "## Recent Failure Artifacts",
        "",
    ])

    artifacts = summary.get("recent_failure_artifacts", [])
    if artifacts:
        for artifact in artifacts:
            lines.append(f"- `{artifact.get('name', '')}`")
    else:
        lines.append("- None")

    return "\n".join(lines) + "\n"


def main() -> int:
    """Generate the CI summary files and print their locations."""
    CI_ARTIFACT_DIR.mkdir(parents=True, exist_ok=True)
    summary = build_summary()
    CI_SUMMARY_JSON.write_text(json.dumps(summary, indent=2), encoding="utf-8")
    CI_SUMMARY_MD.write_text(build_markdown(summary), encoding="utf-8")

    print(f"CI summary JSON: {CI_SUMMARY_JSON}")
    print(f"CI summary Markdown: {CI_SUMMARY_MD}")
    print(f"Overall status: {summary.get('overall_status', 'unknown')}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
