# RMT Automation MCP Server

This folder contains the Phase 2 and Phase 3 MCP integration for the RMT automation project.

## Purpose

The MCP server does not replace Selenium, TestNG, Maven, or Allure.

It exposes project-specific tools so Codex or Claude can:

- run the active sanity suite
- run a single test class
- generate the Allure report
- read Surefire/TestNG summary results
- read the latest timestamped run log
- inspect detailed TestNG method results
- gather failure screenshots, HTML captures, and Allure failure context
- collect one combined failure-debugging bundle

## Folder contents

- `server.py`
  Python MCP server using the official MCP Python SDK
- `requirements.txt`
  Python dependency for the server

## Implemented Phase 2 tools

1. `run_full_suite`
2. `run_test_class`
3. `generate_allure_report`
4. `read_surefire_summary`
5. `get_latest_test_log`

## Implemented Phase 3 tools

6. `read_testng_results`
7. `get_failure_artifacts`
8. `collect_failure_bundle`
9. `run_testng_suite`
10. `run_allocation_suite`
11. `run_configuration_suite`
12. `clean_test_artifacts`
13. `open_allure_summary_data`
14. `run_uat_sanity_headless`
15. `run_uat_sanity_headed`
16. `run_login_smoke`
17. `run_db_validation`
18. `generate_allure_bundle`
19. `generate_ci_summary`
20. `read_ci_summary`

## Install

From the repository root:

```powershell
.\mcp\setup_mcp.ps1
```

If Python is not available on PATH, pass it explicitly:

```powershell
.\mcp\setup_mcp.ps1 -PythonPath "C:\full\path\to\python.exe"
```

## Start the server manually

From the repository root:

```powershell
.\mcp\.venv-mcp\Scripts\python.exe .\mcp\server.py
```

The server uses `stdio`, which is the normal local transport for Codex and Claude Desktop integrations.

## Environment variables

Optional environment variables:

- `RMT_PROJECT_ROOT`
  Override the project root if needed
- `RMT_DEFAULT_ENV`
  Default test environment. Default: `uat`
- `RMT_DEFAULT_BROWSER`
  Default browser. Default: `edge`
- `RMT_DEFAULT_HEADLESS`
  Default headless flag. Default: `true`

## Example Codex or Claude MCP config

Use the server by registering it as a local MCP server in your client configuration.

```json
{
  "mcpServers": {
    "rmt-automation": {
      "command": "C:\\Users\\Piyush.Wadhwa\\IdeaProjects\\Automation\\RMT\\mcp\\.venv-mcp\\Scripts\\python.exe",
      "args": [
        "C:\\Users\\Piyush.Wadhwa\\IdeaProjects\\Automation\\RMT\\mcp\\server.py"
      ],
      "env": {
        "RMT_PROJECT_ROOT": "C:\\Users\\Piyush.Wadhwa\\IdeaProjects\\Automation\\RMT",
        "RMT_DEFAULT_ENV": "uat",
        "RMT_DEFAULT_BROWSER": "edge",
        "RMT_DEFAULT_HEADLESS": "true"
      }
    }
  }
}
```

## Combined Codex config for this machine

If you want both the project-aware RMT tools and the generic Selenium browser tools together in Codex, use this `config.toml` shape:

```toml
[mcp_servers.rmt_automation]
enabled = true
command = "C:\\Users\\Piyush.Wadhwa\\IdeaProjects\\Automation\\RMT\\mcp\\.venv-mcp\\Scripts\\python.exe"
args = ["C:\\Users\\Piyush.Wadhwa\\IdeaProjects\\Automation\\RMT\\mcp\\server.py"]
cwd = "C:\\Users\\Piyush.Wadhwa\\IdeaProjects\\Automation\\RMT"
env = { RMT_PROJECT_ROOT = "C:\\Users\\Piyush.Wadhwa\\IdeaProjects\\Automation\\RMT", RMT_DEFAULT_ENV = "uat", RMT_DEFAULT_BROWSER = "edge", RMT_DEFAULT_HEADLESS = "true" }
startup_timeout_sec = 30.0
tool_timeout_sec = 3600.0

[mcp_servers.selenium]
enabled = true
command = "C:\\Program Files\\nodejs\\npx.cmd"
args = ["-y", "@angiejones/mcp-selenium"]
startup_timeout_sec = 60.0
tool_timeout_sec = 300.0
```

Use `rmt_automation` when you want Codex to run Maven/TestNG/Allure/log workflows.

Use `selenium` when you want Codex to directly drive a browser session through MCP tools like start browser, navigate, click, type, and capture screenshots.

## Suggested usage flow

1. Call `run_uat_sanity_headless`, `run_uat_sanity_headed`, or `run_testng_suite`
2. If failures occur, call `read_surefire_summary`
3. Call `read_testng_results`
4. Call `get_failure_artifacts`
5. Call `collect_failure_bundle`
6. Fix the relevant page object, utility, or test
7. Call `run_allocation_suite`, `run_configuration_suite`, `run_login_smoke`, or `run_test_class` for focused reruns
8. Call `generate_allure_bundle` or `generate_allure_report`
9. Call `open_allure_summary_data`

## Phase 3 tool behavior

### `read_testng_results`

- reads `target/surefire-reports/testng-results.xml`
- returns suite totals plus method-level failure and skip details
- can optionally include passed methods

### `get_failure_artifacts`

- looks for the current failed or skipped TestNG method
- matches the related files in:
  - `test_Logs/failure-artifacts`
  - `allure-results`
- when the current run is green, it can fall back to the latest historical failure artifact bundle

### `collect_failure_bundle`

- combines:
  - suite summary
  - detailed TestNG results
  - latest test log tail
  - matching failure artifacts
- Surefire text report excerpt
- use this tool first when you want Codex to diagnose a failed run quickly

### `run_testng_suite`

- runs any TestNG XML suite by overriding the Maven `suiteXmlFile` property
- default suite:
  - `src/test/resources/TestRunners/Test_Sanity.xml`
- supports `dry_run` so the final Maven command can be inspected safely before execution

### `run_allocation_suite`

- runs the allocation-focused subset:
  - `AllocationFormulaValidationTest`
  - `AllocationModulePOMTest`
- supports `dry_run`

### `run_configuration_suite`

- runs the configuration-focused subset:
  - `ConfigurationPageTest`
- supports `dry_run`

### `clean_test_artifacts`

- safely removes only project-local generated artifacts
- supports:
  - `allure-results`
  - `allure-report`
  - `test_Logs/failure-artifacts`
  - `target/surefire-reports`
- defaults to `dry_run=true` for safety

### `open_allure_summary_data`

- reads the generated Allure data files directly from disk
- returns:
  - summary
  - charts
  - timeline
  - categories
  - history file list

### `run_uat_sanity_headless`

- runs the active UAT sanity suite in headless mode
- uses:
  - `Test_Sanity.xml`
- intended for CI/CD-style validation

### `run_uat_sanity_headed`

- runs the active UAT sanity suite in headed mode
- intended for local visual verification and debugging

### `run_login_smoke`

- runs:
  - `com.qa.rmt.MyTest.LoginTest`
- intended for fast login/authentication flow checks without running the full suite

### `run_db_validation`

- runs:
  - `com.qa.rmt.MyTest.AllocationDbValidationTest`
- remains an explicit opt-in path because the DB query and authentication setup are environment dependent

### `generate_allure_bundle`

- generates the Allure report
- can also return:
  - summary
  - charts
  - timeline
- history metadata
- use this when you want one MCP response that both regenerates and summarizes the report

### `generate_ci_summary`

- runs:
  - `ci/generate_ci_summary.py`
- writes:
  - `artifacts/ci/ci-summary.json`
  - `artifacts/ci/ci-summary.md`
- returns the generated summary data immediately

### `read_ci_summary`

- reads the generated CI summary artifacts from `artifacts/ci/`
- useful after a pipeline or local headless run when you want Codex to reason from the same summary the CI system produced

## Notes

- The server is intentionally scoped to UAT-style local automation support.
- PROD-specific MFA workflows should remain outside unattended automation.
- DB validation remains separate until the JDBC query/authentication path is finalized for unattended execution.
