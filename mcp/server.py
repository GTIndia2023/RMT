"""MCP server for the RMT Selenium automation project.

This server exposes a small set of project-specific tools so AI assistants can
run the existing Maven/TestNG/Allure workflow without changing the underlying
automation framework.
"""

from __future__ import annotations

import json
import os
import re
import shutil
import subprocess
import sys
from datetime import datetime
from pathlib import Path
from typing import Any
from xml.etree import ElementTree

SERVER_FILE = Path(__file__).resolve()
CURRENT_DIR = SERVER_FILE.parent
PROJECT_ROOT_FOR_IMPORT = SERVER_FILE.parents[1]
sys.path = [
    entry
    for entry in sys.path
    if Path(entry or ".").resolve() not in {CURRENT_DIR, PROJECT_ROOT_FOR_IMPORT}
]

from mcp.server.fastmcp import FastMCP


PROJECT_ROOT = Path(os.getenv("RMT_PROJECT_ROOT", PROJECT_ROOT_FOR_IMPORT))
DEFAULT_ENV = os.getenv("RMT_DEFAULT_ENV", "uat")
DEFAULT_BROWSER = os.getenv("RMT_DEFAULT_BROWSER", "edge")
DEFAULT_HEADLESS = os.getenv("RMT_DEFAULT_HEADLESS", "true").strip().lower() == "true"
DEFAULT_TIMEOUT_SECONDS = 60 * 60
OUTPUT_TAIL_LIMIT = 4000

SUREFIRE_TEXT_REPORT = PROJECT_ROOT / "target" / "surefire-reports" / "TestSuite.txt"
SUREFIRE_XML_REPORT = PROJECT_ROOT / "target" / "surefire-reports" / "testng-results.xml"
ALLURE_CONFIG = PROJECT_ROOT / "allurerc.mjs"
ALLURE_RESULTS_DIR = PROJECT_ROOT / "allure-results"
ALLURE_REPORT_DIR = PROJECT_ROOT / "allure-report"
TEST_LOG_DIR = PROJECT_ROOT / "test_Logs"
FAILURE_ARTIFACT_DIR = TEST_LOG_DIR / "failure-artifacts"
ALLURE_RESULT_PATTERN = "*-result.json"
TEST_RUNNER_DIR = PROJECT_ROOT / "src" / "test" / "resources" / "TestRunners"
DEFAULT_SUITE_XML = TEST_RUNNER_DIR / "Test_Sanity.xml"
CI_DIR = PROJECT_ROOT / "ci"
CI_SUMMARY_SCRIPT = CI_DIR / "generate_ci_summary.py"
CI_ARTIFACT_DIR = PROJECT_ROOT / "artifacts" / "ci"
CI_SUMMARY_JSON = CI_ARTIFACT_DIR / "ci-summary.json"
CI_SUMMARY_MD = CI_ARTIFACT_DIR / "ci-summary.md"
ALLOCATION_TEST_CLASSES = [
    "com.qa.rmt.MyTest.AllocationFormulaValidationTest",
    "com.qa.rmt.MyTest.AllocationModulePOMTest",
]
CONFIGURATION_TEST_CLASSES = [
    "com.qa.rmt.MyTest.ConfigurationPageTest",
]
LOGIN_SMOKE_TEST_CLASS = "com.qa.rmt.MyTest.LoginTest"
DB_VALIDATION_TEST_CLASS = "com.qa.rmt.MyTest.AllocationDbValidationTest"
FAILURE_ARTIFACT_PATTERN = re.compile(
    r"^(?P<test_name>.+?)_(?P<stamp>\d{8}-\d{6})\.(?P<ext>png|html)$",
    re.IGNORECASE,
)

mcp = FastMCP(
    "RMT Automation MCP",
    instructions=(
        "Tools for running the RMT Selenium/TestNG automation suite, "
        "reading reports, and generating Allure artifacts."
    ),
)


def _bool_to_maven_flag(value: bool) -> str:
    """Convert a Python bool into the lowercase string expected by Maven flags."""
    return "true" if value else "false"


def _tail_text(text: str, max_chars: int = OUTPUT_TAIL_LIMIT) -> str:
    """Return the trailing portion of command output so tool responses stay compact."""
    if not text:
        return ""
    normalized = text.strip()
    if len(normalized) <= max_chars:
        return normalized
    return normalized[-max_chars:]


def _safe_int(value: str | None, default: int = 0) -> int:
    """Convert optional numeric text into an int without raising on malformed values."""
    try:
        return int(value or default)
    except (TypeError, ValueError):
        return default


def _resolve_executable(*candidates: str) -> str:
    """Resolve a CLI executable from the current PATH using fallback candidate names."""
    for candidate in candidates:
        resolved = shutil.which(candidate)
        if resolved:
            return resolved
    raise FileNotFoundError(
        f"Unable to find any of these executables on PATH: {', '.join(candidates)}"
    )


def _run_command(
    command: list[str],
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
) -> dict[str, Any]:
    """Run a command from the project root and return a structured summary."""
    started_at = datetime.now().isoformat(timespec="seconds")
    completed = subprocess.run(
        command,
        cwd=PROJECT_ROOT,
        capture_output=True,
        text=True,
        timeout=timeout_seconds,
        shell=False,
    )
    finished_at = datetime.now().isoformat(timespec="seconds")
    return {
        "command": command,
        "cwd": str(PROJECT_ROOT),
        "started_at": started_at,
        "finished_at": finished_at,
        "exit_code": completed.returncode,
        "stdout_tail": _tail_text(completed.stdout),
        "stderr_tail": _tail_text(completed.stderr),
        "success": completed.returncode == 0,
    }


def _build_maven_command(
    *maven_args: str,
    env: str = DEFAULT_ENV,
    browser: str = DEFAULT_BROWSER,
    headless: bool = DEFAULT_HEADLESS,
    clean: bool | None = None,
) -> list[str]:
    """Build a Maven command with the standard RMT execution flags applied consistently."""
    mvn = _resolve_executable("mvn.cmd", "mvn")
    command = [mvn]
    if clean is True:
        command.extend(["clean", "test"])
    elif clean is False:
        command.append("test")

    command.extend(maven_args)
    command.extend(
        [
            f"-Denv={env}",
            f"-Dbrowser={browser}",
            f"-Dheadless={_bool_to_maven_flag(headless)}",
        ]
    )
    return command


def _read_text_file(path: Path) -> str:
    """Read a text file safely and return an empty string when it is not present."""
    if not path.exists():
        return ""
    return path.read_text(encoding="utf-8", errors="replace")


def _normalize_test_name(test_name: str) -> str:
    """Reduce a fully qualified or decorated test name down to the core method identifier."""
    if not test_name:
        return ""
    normalized = test_name.strip()
    if not normalized:
        return ""
    if "::" in normalized:
        normalized = normalized.split("::")[-1]
    if "." in normalized:
        normalized = normalized.split(".")[-1]
    if "(" in normalized:
        normalized = normalized.split("(", 1)[0]
    return normalized.strip()


def _extract_param_values(method_element: ElementTree.Element) -> list[str]:
    """Read TestNG parameter values for a method so failures can be tied back to the test data."""
    values: list[str] = []
    params_node = method_element.find("params")
    if params_node is None:
        return values

    for param_node in params_node.findall("param"):
        value_node = param_node.find("value")
        if value_node is not None:
            values.append("".join(value_node.itertext()).strip())
    return values


def _extract_exception_details(method_element: ElementTree.Element) -> dict[str, str]:
    """Extract compact exception details from a failed TestNG method node."""
    exception_node = method_element.find("exception")
    if exception_node is None:
        return {}

    message = exception_node.findtext("message", default="").strip()
    stacktrace = exception_node.findtext("full-stacktrace", default="").strip()

    details = {
        "class": exception_node.attrib.get("class", "").strip(),
        "message": _tail_text(message, max_chars=1500),
        "stacktrace_tail": _tail_text(stacktrace, max_chars=3000),
    }
    return {key: value for key, value in details.items() if value}


def _summarize_test_method(
    method_element: ElementTree.Element,
    suite_name: str,
    test_name: str,
    class_name: str,
) -> dict[str, Any]:
    """Convert a TestNG method node into a JSON-friendly summary for MCP responses."""
    method_name = method_element.attrib.get("name", "").strip()
    is_config = method_element.attrib.get("is-config", "false").strip().lower() == "true"
    summary = {
        "suite_name": suite_name,
        "test_name": test_name,
        "class_name": class_name,
        "method_name": method_name,
        "full_name": f"{class_name}.{method_name}" if class_name and method_name else method_name,
        "status": method_element.attrib.get("status", "").strip(),
        "is_config": is_config,
        "duration_ms": _safe_int(method_element.attrib.get("duration-ms")),
        "started_at": method_element.attrib.get("started-at", "").strip(),
        "finished_at": method_element.attrib.get("finished-at", "").strip(),
        "parameters": _extract_param_values(method_element),
        "exception": _extract_exception_details(method_element),
    }
    return summary


def _parse_testng_results_detailed(
    include_passed_methods: bool = False,
    include_config_methods: bool = True,
) -> dict[str, Any]:
    """Parse TestNG XML into per-test and per-method details that are easier to debug with."""
    if not SUREFIRE_XML_REPORT.exists():
        return {
            "exists": False,
            "path": str(SUREFIRE_XML_REPORT),
            "tests": [],
            "failed_methods": [],
            "skipped_methods": [],
            "passed_methods": [],
        }

    root = ElementTree.parse(SUREFIRE_XML_REPORT).getroot()
    suite_node = root.find("suite")
    suite_name = suite_node.attrib.get("name", "").strip() if suite_node is not None else ""

    tests_summary: list[dict[str, Any]] = []
    failed_methods: list[dict[str, Any]] = []
    skipped_methods: list[dict[str, Any]] = []
    passed_methods: list[dict[str, Any]] = []

    for test_node in root.findall("./suite/test"):
        test_name = test_node.attrib.get("name", "").strip()
        test_summary = {
            "name": test_name,
            "started_at": test_node.attrib.get("started-at", "").strip(),
            "finished_at": test_node.attrib.get("finished-at", "").strip(),
            "duration_ms": _safe_int(test_node.attrib.get("duration-ms")),
            "passed": 0,
            "failed": 0,
            "skipped": 0,
            "methods": [],
        }

        for class_node in test_node.findall("class"):
            class_name = class_node.attrib.get("name", "").strip()
            for method_node in class_node.findall("test-method"):
                method_summary = _summarize_test_method(
                    method_node,
                    suite_name=suite_name,
                    test_name=test_name,
                    class_name=class_name,
                )

                if method_summary["is_config"] and not include_config_methods:
                    continue

                status = method_summary["status"].upper()
                if status == "PASS":
                    test_summary["passed"] += 1
                    if include_passed_methods:
                        passed_methods.append(method_summary)
                        test_summary["methods"].append(method_summary)
                elif status == "FAIL":
                    test_summary["failed"] += 1
                    failed_methods.append(method_summary)
                    test_summary["methods"].append(method_summary)
                elif status == "SKIP":
                    test_summary["skipped"] += 1
                    skipped_methods.append(method_summary)
                    test_summary["methods"].append(method_summary)

        tests_summary.append(test_summary)

    return {
        "exists": True,
        "path": str(SUREFIRE_XML_REPORT),
        "summary": _parse_testng_results(),
        "tests": tests_summary,
        "failed_methods": failed_methods,
        "skipped_methods": skipped_methods,
        "passed_methods": passed_methods,
    }


def _parse_testng_results() -> dict[str, Any]:
    """Parse the TestNG XML summary file into a small JSON-friendly structure."""
    if not SUREFIRE_XML_REPORT.exists():
        return {
            "exists": False,
            "path": str(SUREFIRE_XML_REPORT),
        }

    root = ElementTree.parse(SUREFIRE_XML_REPORT).getroot()
    suite = root.find("suite")

    return {
        "exists": True,
        "path": str(SUREFIRE_XML_REPORT),
        "total": int(root.attrib.get("total", "0")),
        "passed": int(root.attrib.get("passed", "0")),
        "failed": int(root.attrib.get("failed", "0")),
        "skipped": int(root.attrib.get("skipped", "0")),
        "ignored": int(root.attrib.get("ignored", "0")),
        "suite_name": suite.attrib.get("name", "") if suite is not None else "",
        "started_at": suite.attrib.get("started-at", "") if suite is not None else "",
        "finished_at": suite.attrib.get("finished-at", "") if suite is not None else "",
        "duration_ms": int(suite.attrib.get("duration-ms", "0")) if suite is not None else 0,
    }


def _find_latest_log() -> Path | None:
    """Return the newest timestamped test log if any log file exists."""
    if not TEST_LOG_DIR.exists():
        return None
    candidates = [path for path in TEST_LOG_DIR.iterdir() if path.is_file() and path.suffix.lower() == ".log"]
    if not candidates:
        return None
    return max(candidates, key=lambda item: item.stat().st_mtime)


def _list_failure_artifact_groups() -> list[dict[str, Any]]:
    """Group screenshot and HTML failure artifacts by test method and timestamp."""
    if not FAILURE_ARTIFACT_DIR.exists():
        return []

    grouped: dict[tuple[str, str], dict[str, Any]] = {}
    for artifact_path in FAILURE_ARTIFACT_DIR.iterdir():
        if not artifact_path.is_file():
            continue
        match = FAILURE_ARTIFACT_PATTERN.match(artifact_path.name)
        if not match:
            continue

        test_name = match.group("test_name")
        stamp = match.group("stamp")
        ext = match.group("ext").lower()
        key = (test_name, stamp)
        entry = grouped.setdefault(
            key,
            {
                "test_name": test_name,
                "artifact_stamp": stamp,
                "modified_at": datetime.fromtimestamp(artifact_path.stat().st_mtime).isoformat(timespec="seconds"),
                "screenshot_path": "",
                "html_path": "",
            },
        )
        if ext == "png":
            entry["screenshot_path"] = str(artifact_path)
        elif ext == "html":
            entry["html_path"] = str(artifact_path)

    return sorted(grouped.values(), key=lambda item: item["artifact_stamp"], reverse=True)


def _match_failure_artifact_group(test_name: str = "") -> dict[str, Any] | None:
    """Pick the best matching historical failure-artifact bundle for a test name."""
    groups = _list_failure_artifact_groups()
    if not groups:
        return None

    normalized_target = _normalize_test_name(test_name).lower()
    if normalized_target:
        for group in groups:
            if _normalize_test_name(group["test_name"]).lower() == normalized_target:
                return group

    return groups[0]


def _list_allure_result_entries(status_filter: set[str] | None = None) -> list[dict[str, Any]]:
    """Read Allure result JSON files so failed or broken tests can be mapped to attachments."""
    if not ALLURE_RESULTS_DIR.exists():
        return []

    entries: list[dict[str, Any]] = []
    for result_path in ALLURE_RESULTS_DIR.glob(ALLURE_RESULT_PATTERN):
        try:
            payload = json.loads(_read_text_file(result_path))
        except json.JSONDecodeError:
            continue

        status = str(payload.get("status", "")).strip().lower()
        if status_filter and status not in status_filter:
            continue

        attachments = []
        for attachment in payload.get("attachments", []):
            source_name = str(attachment.get("source", "")).strip()
            attachment_path = ALLURE_RESULTS_DIR / source_name if source_name else None
            attachments.append(
                {
                    "name": str(attachment.get("name", "")).strip(),
                    "type": str(attachment.get("type", "")).strip(),
                    "source": source_name,
                    "path": str(attachment_path) if attachment_path else "",
                    "exists": bool(attachment_path and attachment_path.exists()),
                }
            )

        entries.append(
            {
                "uuid": str(payload.get("uuid", "")).strip(),
                "name": str(payload.get("name", "")).strip(),
                "full_name": str(payload.get("fullName", "")).strip(),
                "status": status,
                "status_details": payload.get("statusDetails", {}) or {},
                "description": str(payload.get("description", "")).strip(),
                "parameters": payload.get("parameters", []) or [],
                "attachments": attachments,
                "start": _safe_int(str(payload.get("start", "0"))),
                "stop": _safe_int(str(payload.get("stop", "0"))),
                "result_path": str(result_path),
            }
        )

    return sorted(entries, key=lambda item: item.get("stop", 0), reverse=True)


def _match_allure_failure_entry(test_name: str = "") -> dict[str, Any] | None:
    """Pick the best failed or broken Allure result for a given test name."""
    entries = _list_allure_result_entries(status_filter={"failed", "broken"})
    if not entries:
        return None

    normalized_target = _normalize_test_name(test_name).lower()
    if normalized_target:
        for entry in entries:
            if _normalize_test_name(entry["name"]).lower() == normalized_target:
                return entry
            if _normalize_test_name(entry["full_name"]).lower() == normalized_target:
                return entry

    return entries[0]


def _read_log_tail(path: Path, lines: int) -> str:
    """Read the last N lines from a log file without loading excessive content into the tool response."""
    content = _read_text_file(path)
    if not content:
        return ""
    selected_lines = content.splitlines()[-max(lines, 1):]
    return "\n".join(selected_lines)


def _safe_remove_path(path: Path, dry_run: bool) -> dict[str, Any]:
    """Remove only project-local files or directories and report what happened."""
    resolved = path.resolve()
    project_root = PROJECT_ROOT.resolve()

    if project_root not in resolved.parents and resolved != project_root:
        raise ValueError(f"Refusing to remove a path outside the project root: {resolved}")

    entry = {
        "path": str(resolved),
        "exists_before": resolved.exists(),
        "removed": False,
        "dry_run": dry_run,
    }
    if not resolved.exists():
        return entry

    if dry_run:
        entry["removed"] = False
        return entry

    if resolved.is_dir():
        shutil.rmtree(resolved)
    else:
        resolved.unlink()
    entry["removed"] = True
    return entry


def _read_json_file(path: Path) -> Any:
    """Read a JSON file safely and return None when the file is missing or malformed."""
    if not path.exists():
        return None
    try:
        return json.loads(_read_text_file(path))
    except json.JSONDecodeError:
        return None


def _run_python_script(script_path: Path, timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS) -> dict[str, Any]:
    """Run a project-local Python script using the current interpreter so MCP and CI helpers stay aligned."""
    if not script_path.exists():
        raise FileNotFoundError(f"Python script was not found: {script_path}")
    return _run_command([sys.executable, str(script_path)], timeout_seconds=timeout_seconds)


@mcp.tool()
def run_full_suite(
    env: str = DEFAULT_ENV,
    browser: str = DEFAULT_BROWSER,
    headless: bool = DEFAULT_HEADLESS,
    clean: bool = True,
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
) -> dict[str, Any]:
    """Run the active Test_Sanity.xml-backed Maven suite for the RMT project."""
    command = _build_maven_command(
        env=env,
        browser=browser,
        headless=headless,
        clean=clean,
    )

    result = _run_command(command, timeout_seconds=timeout_seconds)
    result["surefire_summary"] = _parse_testng_results()
    return result


@mcp.tool()
def run_test_class(
    test_class: str,
    env: str = DEFAULT_ENV,
    browser: str = DEFAULT_BROWSER,
    headless: bool = DEFAULT_HEADLESS,
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
) -> dict[str, Any]:
    """Run a single TestNG test class through Maven for focused validation or debugging."""
    if not test_class or not test_class.strip():
        raise ValueError("test_class is required.")

    command = _build_maven_command(
        "-q",
        f"-Dtest={test_class.strip()}",
        "test",
        env=env,
        browser=browser,
        headless=headless,
    )

    result = _run_command(command, timeout_seconds=timeout_seconds)
    result["surefire_summary"] = _parse_testng_results()
    return result


@mcp.tool()
def run_testng_suite(
    suite_xml_path: str = str(DEFAULT_SUITE_XML),
    env: str = DEFAULT_ENV,
    browser: str = DEFAULT_BROWSER,
    headless: bool = DEFAULT_HEADLESS,
    clean: bool = True,
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
    dry_run: bool = False,
) -> dict[str, Any]:
    """Run a specific TestNG XML suite file through Maven using the suiteXmlFile property override."""
    requested_path = Path(suite_xml_path)
    suite_path = requested_path if requested_path.is_absolute() else (PROJECT_ROOT / requested_path)
    suite_path = suite_path.resolve()

    if not suite_path.exists():
        raise FileNotFoundError(f"Suite XML was not found: {suite_path}")

    command = _build_maven_command(
        f"-DsuiteXmlFile={suite_path}",
        env=env,
        browser=browser,
        headless=headless,
        clean=clean,
    )

    if dry_run:
        return {
            "dry_run": True,
            "suite_xml_path": str(suite_path),
            "command": command,
            "cwd": str(PROJECT_ROOT),
        }

    result = _run_command(command, timeout_seconds=timeout_seconds)
    result["suite_xml_path"] = str(suite_path)
    result["surefire_summary"] = _parse_testng_results()
    return result


@mcp.tool()
def run_allocation_suite(
    env: str = DEFAULT_ENV,
    browser: str = DEFAULT_BROWSER,
    headless: bool = DEFAULT_HEADLESS,
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
    dry_run: bool = False,
) -> dict[str, Any]:
    """Run the allocation-focused regression subset used most often during allocation workflow debugging."""
    test_argument = ",".join(ALLOCATION_TEST_CLASSES)
    command = _build_maven_command(
        "-q",
        f"-Dtest={test_argument}",
        "test",
        env=env,
        browser=browser,
        headless=headless,
    )

    if dry_run:
        return {
            "dry_run": True,
            "test_classes": ALLOCATION_TEST_CLASSES,
            "command": command,
            "cwd": str(PROJECT_ROOT),
        }

    result = _run_command(command, timeout_seconds=timeout_seconds)
    result["test_classes"] = ALLOCATION_TEST_CLASSES
    result["surefire_summary"] = _parse_testng_results()
    return result


@mcp.tool()
def run_configuration_suite(
    env: str = DEFAULT_ENV,
    browser: str = DEFAULT_BROWSER,
    headless: bool = DEFAULT_HEADLESS,
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
    dry_run: bool = False,
) -> dict[str, Any]:
    """Run the configuration-screen regression subset used for configuration page validation and debugging."""
    test_argument = ",".join(CONFIGURATION_TEST_CLASSES)
    command = _build_maven_command(
        "-q",
        f"-Dtest={test_argument}",
        "test",
        env=env,
        browser=browser,
        headless=headless,
    )

    if dry_run:
        return {
            "dry_run": True,
            "test_classes": CONFIGURATION_TEST_CLASSES,
            "command": command,
            "cwd": str(PROJECT_ROOT),
        }

    result = _run_command(command, timeout_seconds=timeout_seconds)
    result["test_classes"] = CONFIGURATION_TEST_CLASSES
    result["surefire_summary"] = _parse_testng_results()
    return result


@mcp.tool()
def run_uat_sanity_headless(
    browser: str = DEFAULT_BROWSER,
    clean: bool = True,
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
    dry_run: bool = False,
) -> dict[str, Any]:
    """Run the active UAT sanity suite in headless mode using the current Test_Sanity.xml suite."""
    return run_testng_suite(
        suite_xml_path=str(DEFAULT_SUITE_XML),
        env="uat",
        browser=browser,
        headless=True,
        clean=clean,
        timeout_seconds=timeout_seconds,
        dry_run=dry_run,
    )


@mcp.tool()
def run_uat_sanity_headed(
    browser: str = DEFAULT_BROWSER,
    clean: bool = True,
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
    dry_run: bool = False,
) -> dict[str, Any]:
    """Run the active UAT sanity suite in headed mode for local verification and visual debugging."""
    return run_testng_suite(
        suite_xml_path=str(DEFAULT_SUITE_XML),
        env="uat",
        browser=browser,
        headless=False,
        clean=clean,
        timeout_seconds=timeout_seconds,
        dry_run=dry_run,
    )


@mcp.tool()
def run_login_smoke(
    env: str = DEFAULT_ENV,
    browser: str = DEFAULT_BROWSER,
    headless: bool = DEFAULT_HEADLESS,
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
    dry_run: bool = False,
) -> dict[str, Any]:
    """Run the dedicated LoginTest smoke class for fast authentication-flow validation."""
    if dry_run:
        return {
            "dry_run": True,
            "test_class": LOGIN_SMOKE_TEST_CLASS,
            "command": _build_maven_command(
                "-q",
                f"-Dtest={LOGIN_SMOKE_TEST_CLASS}",
                "test",
                env=env,
                browser=browser,
                headless=headless,
            ),
            "cwd": str(PROJECT_ROOT),
        }

    result = run_test_class(
        test_class=LOGIN_SMOKE_TEST_CLASS,
        env=env,
        browser=browser,
        headless=headless,
        timeout_seconds=timeout_seconds,
    )
    result["test_class"] = LOGIN_SMOKE_TEST_CLASS
    return result


@mcp.tool()
def run_db_validation(
    env: str = DEFAULT_ENV,
    browser: str = DEFAULT_BROWSER,
    headless: bool = DEFAULT_HEADLESS,
    timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS,
    dry_run: bool = False,
) -> dict[str, Any]:
    """Run the PostgreSQL-backed allocation DB validation test as an explicit opt-in check."""
    if dry_run:
        return {
            "dry_run": True,
            "test_class": DB_VALIDATION_TEST_CLASS,
            "command": _build_maven_command(
                "-q",
                f"-Dtest={DB_VALIDATION_TEST_CLASS}",
                "test",
                env=env,
                browser=browser,
                headless=headless,
            ),
            "cwd": str(PROJECT_ROOT),
        }

    result = run_test_class(
        test_class=DB_VALIDATION_TEST_CLASS,
        env=env,
        browser=browser,
        headless=headless,
        timeout_seconds=timeout_seconds,
    )
    result["test_class"] = DB_VALIDATION_TEST_CLASS
    return result


@mcp.tool()
def generate_allure_report(clean_report: bool = True) -> dict[str, Any]:
    """Generate the Allure 3 report from the current allure-results directory."""
    allure = _resolve_executable("allure.bat", "allure.cmd", "allure")

    if clean_report and ALLURE_REPORT_DIR.exists():
        shutil.rmtree(ALLURE_REPORT_DIR)

    command = [
        allure,
        "generate",
        "--config",
        str(ALLURE_CONFIG),
        str(ALLURE_RESULTS_DIR),
        "-o",
        str(ALLURE_REPORT_DIR),
    ]

    result = _run_command(command, timeout_seconds=DEFAULT_TIMEOUT_SECONDS)
    result["report_dir"] = str(ALLURE_REPORT_DIR)
    result["summary_file"] = str(ALLURE_REPORT_DIR / "summary.json")
    result["index_file"] = str(ALLURE_REPORT_DIR / "index.html")
    return result


@mcp.tool()
def clean_test_artifacts(
    include_allure_results: bool = True,
    include_allure_report: bool = True,
    include_failure_artifacts: bool = False,
    include_surefire_reports: bool = False,
    dry_run: bool = True,
) -> dict[str, Any]:
    """Clean generated test artifacts from known project directories in a CI/CD-safe, project-scoped way."""
    targets: list[Path] = []
    if include_allure_results:
        targets.append(ALLURE_RESULTS_DIR)
    if include_allure_report:
        targets.append(ALLURE_REPORT_DIR)
    if include_failure_artifacts:
        targets.append(FAILURE_ARTIFACT_DIR)
    if include_surefire_reports:
        targets.append(PROJECT_ROOT / "target" / "surefire-reports")

    cleaned = [_safe_remove_path(path, dry_run=dry_run) for path in targets]
    return {
        "dry_run": dry_run,
        "project_root": str(PROJECT_ROOT),
        "targets": cleaned,
    }


@mcp.tool()
def open_allure_summary_data() -> dict[str, Any]:
    """Read the generated Allure summary, chart, timeline, and history data without opening a browser."""
    summary_file = ALLURE_REPORT_DIR / "summary.json"
    widgets_dir = ALLURE_REPORT_DIR / "widgets"
    data_dir = ALLURE_REPORT_DIR / "data"
    history_dir = data_dir / "history"

    charts_file = widgets_dir / "charts.json"
    timeline_file = widgets_dir / "timeline.json"
    categories_file = widgets_dir / "categories.json"

    history_files = []
    if history_dir.exists():
        history_files = [
            {
                "name": item.name,
                "path": str(item),
                "modified_at": datetime.fromtimestamp(item.stat().st_mtime).isoformat(timespec="seconds"),
            }
            for item in sorted(history_dir.iterdir())
            if item.is_file()
        ]

    return {
        "report_exists": ALLURE_REPORT_DIR.exists(),
        "report_dir": str(ALLURE_REPORT_DIR),
        "index_file": str(ALLURE_REPORT_DIR / "index.html"),
        "summary": _read_json_file(summary_file),
        "charts": _read_json_file(charts_file),
        "timeline": _read_json_file(timeline_file),
        "categories": _read_json_file(categories_file),
        "history_files": history_files,
        "history_file_count": len(history_files),
    }


@mcp.tool()
def generate_allure_bundle(
    clean_report: bool = True,
    include_summary_data: bool = True,
) -> dict[str, Any]:
    """Generate the Allure report and optionally return the parsed summary/chart data in one bundled response."""
    generation_result = generate_allure_report(clean_report=clean_report)
    bundle = {
        "generation": generation_result,
    }
    if include_summary_data:
        bundle["summary_data"] = open_allure_summary_data()
    return bundle


@mcp.tool()
def generate_ci_summary(timeout_seconds: int = DEFAULT_TIMEOUT_SECONDS) -> dict[str, Any]:
    """Run the CI summary generator script and return the generated summary artifact paths and data."""
    generation = _run_python_script(CI_SUMMARY_SCRIPT, timeout_seconds=timeout_seconds)
    return {
        "generation": generation,
        "summary_json_path": str(CI_SUMMARY_JSON),
        "summary_markdown_path": str(CI_SUMMARY_MD),
        "summary": _read_json_file(CI_SUMMARY_JSON),
        "summary_markdown": _read_text_file(CI_SUMMARY_MD),
    }


@mcp.tool()
def read_ci_summary() -> dict[str, Any]:
    """Read the generated CI summary artifacts so Codex can reason about a pipeline run quickly."""
    return {
        "exists": CI_SUMMARY_JSON.exists() or CI_SUMMARY_MD.exists(),
        "summary_json_path": str(CI_SUMMARY_JSON),
        "summary_markdown_path": str(CI_SUMMARY_MD),
        "summary": _read_json_file(CI_SUMMARY_JSON),
        "summary_markdown": _read_text_file(CI_SUMMARY_MD),
    }


@mcp.tool()
def read_surefire_summary() -> dict[str, Any]:
    """Read the latest Surefire and TestNG summary artifacts without rerunning the tests."""
    return {
        "project_root": str(PROJECT_ROOT),
        "text_report_path": str(SUREFIRE_TEXT_REPORT),
        "xml_report_path": str(SUREFIRE_XML_REPORT),
        "text_report": _read_text_file(SUREFIRE_TEXT_REPORT),
        "xml_summary": _parse_testng_results(),
    }


@mcp.tool()
def get_latest_test_log(lines: int = 200) -> dict[str, Any]:
    """Return the newest timestamped test log so failures can be diagnosed quickly."""
    latest_log = _find_latest_log()
    if latest_log is None:
        return {
            "exists": False,
            "log_dir": str(TEST_LOG_DIR),
            "message": "No test log files were found.",
        }

    return {
        "exists": True,
        "path": str(latest_log),
        "modified_at": datetime.fromtimestamp(latest_log.stat().st_mtime).isoformat(timespec="seconds"),
        "tail": _read_log_tail(latest_log, lines),
    }


@mcp.tool()
def read_testng_results(
    include_passed_methods: bool = False,
    include_config_methods: bool = True,
) -> dict[str, Any]:
    """Read the detailed TestNG XML result structure so failures and skipped cases are easier to diagnose."""
    return _parse_testng_results_detailed(
        include_passed_methods=include_passed_methods,
        include_config_methods=include_config_methods,
    )


@mcp.tool()
def get_failure_artifacts(
    test_name: str = "",
    include_allure_failure: bool = True,
    allow_historical_fallback: bool = True,
) -> dict[str, Any]:
    """Return the best matching screenshot, HTML, and Allure failure details for a failed or recently failed test."""
    detailed_results = _parse_testng_results_detailed(
        include_passed_methods=False,
        include_config_methods=True,
    )

    current_failures = detailed_results.get("failed_methods", [])
    current_skips = detailed_results.get("skipped_methods", [])
    normalized_target = _normalize_test_name(test_name)
    matched_current_method = None

    if normalized_target:
        for method in current_failures + current_skips:
            if _normalize_test_name(method.get("method_name", "")).lower() == normalized_target.lower():
                matched_current_method = method
                break
    elif current_failures:
        matched_current_method = current_failures[-1]
    elif current_skips:
        matched_current_method = current_skips[-1]

    target_name = normalized_target or (
        matched_current_method.get("method_name", "") if matched_current_method else ""
    )

    artifact_group = _match_failure_artifact_group(target_name) if allow_historical_fallback else None
    allure_failure = _match_allure_failure_entry(target_name) if include_allure_failure else None

    used_historical_fallback = bool(
        artifact_group
        and not current_failures
        and not current_skips
    )

    return {
        "requested_test_name": test_name,
        "matched_current_method": matched_current_method,
        "current_failure_count": len(current_failures),
        "current_skipped_count": len(current_skips),
        "used_historical_fallback": used_historical_fallback,
        "failure_artifact_dir": str(FAILURE_ARTIFACT_DIR),
        "matched_failure_artifacts": artifact_group,
        "matched_allure_failure": allure_failure,
        "message": (
            "No current failed or skipped TestNG methods were found. Returned the latest historical artifacts instead."
            if used_historical_fallback
            else "Matched failure context was resolved from the current result set."
            if matched_current_method or artifact_group or allure_failure
            else "No failure artifacts were found."
        ),
    }


@mcp.tool()
def collect_failure_bundle(
    test_name: str = "",
    log_lines: int = 200,
    include_passed_methods: bool = False,
) -> dict[str, Any]:
    """Collect the current summary, latest log, and best matching failure artifacts into one debugging bundle."""
    latest_log = get_latest_test_log(lines=log_lines)
    testng_results = read_testng_results(
        include_passed_methods=include_passed_methods,
        include_config_methods=True,
    )
    failure_artifacts = get_failure_artifacts(
        test_name=test_name,
        include_allure_failure=True,
        allow_historical_fallback=True,
    )

    text_report = _read_text_file(SUREFIRE_TEXT_REPORT)
    return {
        "generated_at": datetime.now().isoformat(timespec="seconds"),
        "project_root": str(PROJECT_ROOT),
        "requested_test_name": test_name,
        "suite_summary": _parse_testng_results(),
        "testng_results": testng_results,
        "latest_log": latest_log,
        "failure_artifacts": failure_artifacts,
        "surefire_text_report_path": str(SUREFIRE_TEXT_REPORT),
        "surefire_text_report_excerpt": _tail_text(text_report, max_chars=2500),
    }


if __name__ == "__main__":
    mcp.run()
