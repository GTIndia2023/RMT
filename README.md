# RMT Automation Framework

RMT is a Java-based UI automation framework for the OptiWise Resource Management Tool. It uses Selenium WebDriver, TestNG, Maven, Page Object Model (POM), Allure reporting, and optional PostgreSQL validation.

The project is designed for both local execution and CI/CD execution. It supports UAT and PROD property files, headless browser runs, runtime secret injection, failure screenshots, HTML captures, timestamped test logs, Allure history, and a project-specific MCP server for AI-assisted test operations.

## Contents

- [Technology](#technology)
- [Repository Structure](#repository-structure)
- [Framework Architecture](#framework-architecture)
- [Modules and Coverage](#modules-and-coverage)
- [Java Concepts Used](#java-concepts-used)
- [Prerequisites](#prerequisites)
- [Configuration and Secrets](#configuration-and-secrets)
- [Run Tests](#run-tests)
- [Reporting and Logs](#reporting-and-logs)
- [Database Validation](#database-validation)
- [CI/CD](#cicd)
- [MCP Integration](#mcp-integration)
- [Troubleshooting](#troubleshooting)
- [Contribution Rules](#contribution-rules)

## Technology

| Area | Technology | Version in project / usage |
| --- | --- | --- |
| Language | Java | Source and target level 21 |
| Build and dependency management | Maven | Maven project with Surefire TestNG execution |
| UI automation | Selenium Java | 4.38.0 |
| Test framework | TestNG | 7.11.0 |
| Browser driver resolution | Selenium Manager / installed browser driver support | Selenium WebDriver managed execution |
| Reporting | Allure TestNG | 2.31.0 |
| Report viewer | Allure Report CLI | Allure 3 configuration in `allurerc.mjs` |
| Spreadsheet data | Apache POI | 5.3.0 |
| Database validation | PostgreSQL JDBC | 42.7.3 |
| API automation foundation | REST Assured | 6.0.1, test scope |
| Logging | Log4j API and Core | 2.24.1 |
| Retry instrumentation | AspectJ Weaver | 1.9.22.1 |
| CI/CD | Azure Pipelines and Bitbucket Pipelines | YAML definitions included |
| AI test operations | Python MCP server | Project-local server in `mcp/` |

## Repository Structure

```text
RMT/
|-- pom.xml                                  Maven dependencies and build configuration
|-- README.md                                 Project guide
|-- ALLOCATION_AUTOMATION_SETUP.md            Allocation-specific setup and commands
|-- CI_CD_AUTOMATION_WORKFLOW.md              CI/CD implementation guide
|-- azure-pipelines.yml                       Azure Pipeline definition
|-- bitbucket-pipelines.yml                   Bitbucket Pipeline definition
|-- allurerc.mjs                              Allure 3 history and environment configuration
|-- send_mail.py                              Optional email delivery of an Allure ZIP
|
|-- src/
|   |-- main/java/RMT/
|   |   |-- Constants/                         Shared application and test constants
|   |   |-- Errors/                            Reusable assertion and error messages
|   |   |-- Exceptions/                        Domain-specific runtime exceptions
|   |   |-- Factory/                           WebDriver, browser options, property loading
|   |   |-- Listeners/                         Retry and Allure listener support
|   |   |-- Models/                            Typed data objects used by validations
|   |   |-- Pages/                             Page Object Model classes and workflow logic
|   |   |-- Utils/                             Element, JavaScript, Excel, DB, date utilities
|   |   `-- App.java                           Maven starter class; not the test entry point
|   |
|   |-- test/java/com/qa/rmt/
|   |   |-- base/                              Shared TestNG setup and teardown
|   |   |-- listeners/                         Failure capture and Allure attachment listener
|   |   |-- utils/                             Screenshot and HTML attachment helper
|   |   `-- MyTest/                            TestNG test classes and assertions
|   |
|   |-- test/resources/
|   |   |-- Config/                            Environment property files
|   |   |-- META-INF/services/                 TestNG listener registration
|   |   `-- TestRunners/                       TestNG suite XML files
|   |
|   `-- test/TestData/                        Excel input data for legacy data-driven tests
|
|-- mcp/                                      Project-specific MCP server and setup script
|-- ci/                                       CI summary generator
|-- test_Logs/                                Generated timestamped run logs and failure artifacts
|-- allure-results/                           Raw Allure test result files
|-- allure-history/                           Persistent Allure trend and history data
|-- allure-report/                            Generated local report, ignored by Git
|-- artifacts/ci/                             Generated CI summaries, ignored by Git
`-- target/                                   Maven build and Surefire results, ignored by Git
```

## Framework Architecture

```text
TestNG XML suite
        |
        v
Test class: assertions and test metadata
        |
        v
BaseTest: lifecycle, driver setup, logs, environment labels
        |
        v
Page Object Model: screen actions and business workflow logic
        |
        +--> ElementUtil: explicit waits, stable clicks, reliable text entry
        +--> JavascriptUtil: controlled JavaScript fallback actions
        +--> ExcelUtil / DesignationUtil: spreadsheet-driven data helpers
        +--> PostgresValidationUtil: optional JDBC validation
        |
        v
DriverManager and OptionsManager
        |
        v
Chrome / Edge / Firefox WebDriver
        |
        v
OptiWise application
```

The separation of responsibilities is intentional.

| Layer | Responsibility |
| --- | --- |
| Test class | Starts a business scenario and contains assertions only where practical. |
| Page class | Owns locators, navigation, UI actions, waits, workflow decisions, and reusable outcomes. |
| Utility class | Provides generic Selenium operations that can be safely reused across pages. |
| Factory class | Creates the browser driver and loads effective runtime configuration. |
| Listener | Retries failed TestNG methods and captures failure artifacts for Allure and file-based debugging. |

## Modules and Coverage

### Page Object Modules

| Module | Main Page Objects | Coverage |
| --- | --- | --- |
| Authentication | `LoginPage`, `ElementUtil` | Microsoft account picker, credential entry, KMSI prompt handling, application landing validation. |
| Project listings | `ProjectListingsPage` | Project search, actions, allocation navigation, release flow. |
| Allocation | `CommonAllocationPage`, `AllocationModuleFlowPage`, `AllocationWorkflowPage` | Employee allocation, skills, availability/date retry, task approval, allocation status, release lifecycle. |
| Configuration | `ConfigurationPage`, `AppendedCapacityConfigPage` | Configuration accordion validation, numeric and binary values, save acknowledgement, appended capacity, reason configuration, central allocation configuration. |
| Skill master | `SkillMasterPage` | Skill management and account switching support. |
| Requisition | `RequisitionPage` | Requisition creation and related actions. |
| Budget | `BudgetPage` | Budget cards, charts, grid values, and page-level checks. |
| Reports | `ReportsPage` | Report navigation and report validations. |

### Test Classes

| Test class | Purpose | Active in `Test_Sanity.xml` |
| --- | --- | --- |
| `LoginTest` | Authenticates and verifies the OptiWise landing page. | Yes |
| `AllocationFormulaValidationTest` | Validates capacity and availability formula rules without browser dependency. | Yes |
| `AllocationModulePOMTest` | Runs the requestor allocation, approval, status, and release lifecycle. | Yes |
| `ConfigurationPageTest` | Validates configurations and their update/create workflows. | Yes |
| `AllocationDbValidationTest` | Compares database rows with availability formulas. | No, explicit opt-in |
| `AppendedCapacityConfigTest` | Standalone appended-capacity configuration test. | No |
| `BudgetTest` | Budget UI checks. | No |
| `ProjectListingTest` | Legacy exploratory project-listing UI flows. | No |
| `ReportsPageTest` | Reports UI checks. | No |
| `RequisitionTest` | Requisition flows. | No |
| `SkillMasterTest` | Skill Master flows. | No |

The active suite is configured in `src/test/resources/TestRunners/Test_Sanity.xml`. Maven uses it by default through the `suiteXmlFile` property in `pom.xml`.

### Allocation Lifecycle

`AllocationModuleFlowPage` keeps the end-to-end allocation logic in the page layer. The active flow is:

1. Log in as the resource requestor.
2. Search the configured job code and open Allocate Employee.
3. Select the resource, enter description and skills, then choose valid future business dates.
4. Detect availability feedback and retry with later eligible dates when needed.
5. Submit the allocation and validate requestor-side allocation status.
6. Switch to the resource account only when the workflow creates a task.
7. Open Task ID, select the allocation row, accept the task, and validate the post-action status.
8. Switch back to the requestor account, reopen Allocations, release the resource, and validate the popup message.

When an allocation is already terminal, the page-level workflow avoids expecting a new employee task that the application will not create.

### Configuration Coverage

`ConfigurationPageTest` dynamically discovers editable configuration accordions and validates them one by one. It covers:

- Visibility and enabled state for each configuration.
- Accessible values for standard numeric configurations.
- Binary toggles between `-1` and `1` where the option supports a binary value.
- Range updates for allocation cost, budget consumption, requisition match range, timesheet hours, and preference limits.
- Multi-input updates for Requisition Form Parameters.
- Appended Capacity configuration creation, Business Unit search filtering, Business Unit selection, competency selection, and save feedback.
- Reason Configuration creation with reason type and conflict reason selection.
- Central Allocation Configuration creation with competency selection and save feedback.

## Java Concepts Used

| Concept | Project usage |
| --- | --- |
| Page Object Model | Each screen is represented by a class in `RMT.Pages`; UI locators and interaction logic remain outside test classes. |
| Encapsulation | Page locators, configuration reads, and low-level WebDriver interactions are kept private or behind clear public methods. |
| Inheritance | Test classes extend `BaseTest` to reuse browser setup, teardown, properties, logs, and Allure labels. |
| Polymorphism | The `WebDriver` interface is fulfilled by Chrome, Edge, and Firefox driver implementations. |
| Composition | Pages compose `ElementUtil`, `JavascriptUtil`, waits, and page objects to build business workflows. |
| Static utility methods | Capacity calculation, Excel access, designation lookup, and timeout constants are shared without page-specific state. |
| `ThreadLocal` | `DriverManager` stores the current WebDriver per execution thread for parallel-safe driver access. |
| Exception handling | Custom exceptions and contextual `IllegalStateException` messages make UI and environment failures easier to diagnose. |
| Collections and streams | Lists, sets, maps, and streams drive configuration discovery, data providers, and Excel-backed data handling. |
| Data-driven testing | TestNG `@DataProvider` supplies configuration titles and valid numeric ranges. |
| Annotations | TestNG lifecycle annotations and Allure metadata describe priority, owner, severity, and test intent. |
| Listeners and retry | TestNG listeners attach failure evidence and retry failed test methods up to the configured limit. |
| JDBC | `PostgresValidationUtil` retrieves configured PostgreSQL rows for optional formula validation. |

## Prerequisites

Install or make available on the local machine or CI agent:

| Requirement | Notes |
| --- | --- |
| JDK 21 | Required because Maven compiles this project with source and target level 21. |
| Maven | Maven must be available on `PATH`. |
| Google Chrome, Microsoft Edge, or Firefox | Use a current browser version compatible with Selenium. |
| Allure Report CLI 3 | Required only to generate and open the browser report locally. |
| Python 3 | Required for MCP setup, CI summary generation, and optional email tooling. |
| Network access to UAT | Required for UI automation against UAT. |
| PostgreSQL network access | Required only for the optional database validation test. |

Confirm tool availability:

```powershell
java -version
mvn -version
allure --version
python --version
```

## Configuration and Secrets

Environment files are stored in `src/test/resources/Config/`:

| File | Intended environment |
| --- | --- |
| `config.uat.properties` | UAT |
| `config.prod.properties` | Production |

Use the environment selector when running Maven:

```powershell
mvn clean test -Denv=uat
```

`DriverManager` resolves important settings in this order:

1. Maven system property, for example `-Dheadless=true`.
2. Operating-system or CI environment variable.
3. Selected `config.<env>.properties` file.

Supported runtime overrides include:

| Property | System property example | Environment variable candidates |
| --- | --- | --- |
| Browser | `-Dbrowser=edge` | `RMT_BROWSER`, `BROWSER` |
| Headless | `-Dheadless=true` | `RMT_HEADLESS`, `HEADLESS` |
| Incognito | `-Dincognito=true` | `RMT_INCOGNITO`, `INCOGNITO` |
| Remote execution | `-Dremote=true` | `RMT_REMOTE`, `REMOTE` |
| Application URL | `-Durl=https://...` | `RMT_URL`, `APP_URL`, `URL` |
| Application username | `-Dusername=...` | `RMT_USERNAME`, `APP_USERNAME` |
| Application password | `-Dpassword=...` | `RMT_PASSWORD`, `APP_PASSWORD`, `PASSWORD` |
| JDBC URL | `-Ddb.url=jdbc:postgresql://...` | `DB_URL`, `RMT_DB_URL` |
| JDBC username | `-Ddb.username=...` | `DB_USERNAME`, `RMT_DB_USERNAME` |
| JDBC password | `-Ddb.password=...` | `DB_PASSWORD`, `RMT_DB_PASSWORD` |
| DB validation SQL | `-Dallocation.validation.query=...` | `ALLOCATION_VALIDATION_QUERY`, `RMT_ALLOCATION_VALIDATION_QUERY` |
| Allocation-switch username | `-Dallocation.switch.username=...` | `RMT_ALLOCATION_SWITCH_USERNAME`, `ALLOCATION_SWITCH_USERNAME` |
| Allocation-switch password | `-Dallocation.switch.password=...` | `RMT_ALLOCATION_SWITCH_PASSWORD`, `ALLOCATION_SWITCH_PASSWORD` |

Example PowerShell session using environment variables:

```powershell
$env:RMT_USERNAME = "automation-account@example.com"
$env:RMT_PASSWORD = "<secret-from-approved-vault>"
$env:RMT_HEADLESS = "true"
mvn clean test -Denv=uat -Dbrowser=edge
```

Never place real passwords, SMTP credentials, or database credentials in documentation, source code, test logs, or Git commits. Store CI values in the platform's secret-variable store. If a credential has ever been committed to a remote repository, rotate it through the approved access-management process.

### Feature Flags

Several UI flows are intentionally gated because they create or update real UAT records.

| Property | Purpose |
| --- | --- |
| `allocation.ui.run.enabled` | Enables the allocation UI workflow. |
| `appended.config.run.enabled` | Enables standalone appended capacity UI coverage. |
| `allocation.workflow.run.enabled` | Enables allocation workflow visibility/action coverage. |
| `allocation.workflow.action.enabled` | Allows the configured approval or rejection action. |
| `allocation.db.validation.enabled` | Enables database-backed allocation validation. |

When a feature flag is `false`, the related test can skip browser bootstrap or be excluded from the shared suite. This avoids changing application data unintentionally during standard CI runs.

## Run Tests

Run all commands from the repository root.

Compile without executing tests:

```powershell
mvn -q -DskipTests test-compile
```

Run the active UAT sanity suite headlessly:

```powershell
mvn clean test -Denv=uat -Dbrowser=edge -Dheadless=true
```

Run the active UAT sanity suite with a visible browser:

```powershell
mvn clean test -Denv=uat -Dbrowser=edge -Dheadless=false
```

Run one test class headlessly:

```powershell
mvn -q test -Denv=uat -Dbrowser=edge -Dheadless=true "-Dtest=com.qa.rmt.MyTest.LoginTest"
```

Run the allocation workflow test:

```powershell
mvn test -Denv=uat -Dbrowser=edge -Dheadless=true "-Dtest=com.qa.rmt.MyTest.AllocationModulePOMTest"
```

Run the configuration test:

```powershell
mvn test -Denv=uat -Dbrowser=edge -Dheadless=true "-Dtest=com.qa.rmt.MyTest.ConfigurationPageTest"
```

Run the opt-in DB validation after configuring valid JDBC settings and SQL:

```powershell
mvn test -Denv=uat -Dbrowser=edge -Dheadless=true "-Dtest=com.qa.rmt.MyTest.AllocationDbValidationTest"
```

Run a specific TestNG XML suite:

```powershell
mvn clean test -Denv=uat -Dbrowser=edge -Dheadless=true "-DsuiteXmlFile=src/test/resources/TestRunners/Test_Sanity.xml"
```

Browser behavior:

| Setting | Result |
| --- | --- |
| `-Dbrowser=chrome` | Runs ChromeDriver with Chrome options. |
| `-Dbrowser=edge` | Runs EdgeDriver with Edge options. |
| `-Dbrowser=firefox` | Runs FirefoxDriver with Firefox options. |
| `-Dheadless=true` | Runs with a 1920x1080 deterministic viewport for CI-friendly rendering. |
| `-Dheadless=false` | Runs with a visible browser window and attempts to maximize it. |
| `-Dincognito=true` | Uses Chrome Incognito or Edge InPrivate mode. |
| `-Dremote=true` | Activates remote browser capability configuration where supported. |

## Reporting and Logs

### Allure Report

Raw Allure results are written to `allure-results/`. Generate the report after a Maven run:

```powershell
allure generate --config ./allurerc.mjs allure-results -o allure-report
```

Open the generated report in a browser:

```powershell
allure open --config ./allurerc.mjs allure-report
```

The Allure configuration uses `allure-history/allure-history.jsonl` to preserve trend and history information between report generations. Do not delete this history path if historical graphs are required.

Allure includes:

- Test status, duration, severity, owner, browser, OS, and environment labels.
- Failure screenshots when the browser is available.
- Failure HTML page source.
- Method-level failure context from TestNG listeners.
- History and trend data when the persistent history file is retained.

### Local Logs and Failure Evidence

| Artifact | Location | Purpose |
| --- | --- | --- |
| Timestamped test log | `test_Logs/Test_Run_DD-MM-YY_<TestNG-test-name>_HH-mm-ss.log` | Mirrors console output for each suite test. |
| Failure screenshot | `test_Logs/failure-artifacts/` | PNG evidence of the failed browser state. |
| Failure page source | `test_Logs/failure-artifacts/` | HTML capture for DOM-level debugging. |
| Surefire summary | `target/surefire-reports/TestSuite.txt` | Maven/TestNG suite summary. |
| Detailed TestNG XML | `target/surefire-reports/testng-results.xml` | Method-level test status and stack traces. |
| Raw Allure results | `allure-results/` | Input files used by the Allure CLI. |
| Generated report | `allure-report/` | Browser-ready Allure report. |
| CI summary | `artifacts/ci/ci-summary.json` and `artifacts/ci/ci-summary.md` | Compact, machine-readable and human-readable run summary. |

Find the latest test log:

```powershell
Get-ChildItem .\test_Logs | Sort-Object LastWriteTime -Descending | Select-Object -First 1 FullName, LastWriteTime
```

Read the latest test log:

```powershell
$rmtLatestLog = Get-ChildItem .\test_Logs | Sort-Object LastWriteTime -Descending | Select-Object -First 1
Get-Content $rmtLatestLog.FullName
```

Generate the CI summary locally:

```powershell
python ci/generate_ci_summary.py
```

## Database Validation

`AllocationDbValidationTest` is intentionally opt-in because it requires a reachable PostgreSQL instance, a valid account, and an environment-specific SQL query.

Required properties:

```properties
allocation.db.validation.enabled=true
allocation.validation.query=<approved SQL query>
db.url=jdbc:postgresql://<host>:<port>/<database>
db.username=<database username>
db.password=<database password>
```

The query must return these aliases:

| Required SQL alias | Meaning |
| --- | --- |
| `record_key` | Unique identifier for the validated record. |
| `base_capacity_hours` | Base capacity from the database. |
| `appended_capacity_hours` | Appended capacity, or zero. |
| `leave_hours` | Leave consumption. |
| `allocation_hours` | Allocated effort. |
| `holiday_hours` | Holiday consumption. |
| `ui_available_hours` | Availability value to compare with calculated availability. |

The calculator verifies:

```text
Total Capacity = Base Capacity + Appended Capacity
Available Hours = max(0, Total Capacity - Leave - Allocation - Holiday)
```

It also covers half-day leave behavior, extended-utilization decisions, and request-capacity limits through `AllocationFormulaValidationTest`.

## CI/CD

### Azure Pipelines

`azure-pipelines.yml` runs on `windows-latest` and performs the following:

1. Uses Java 21 and Python 3.
2. Runs the UAT sanity suite in headless Edge mode.
3. Preserves the Maven exit code while allowing artifact publication to continue.
4. Generates a CI summary.
5. Publishes CI summary, Surefire reports, and test logs.
6. Marks the pipeline failed at the end if Maven test execution failed.

### Bitbucket Pipelines

`bitbucket-pipelines.yml` targets a self-hosted Linux shell runner and performs the following:

1. Runs the UAT sanity suite in headless Chrome mode.
2. Generates an Allure report when raw results exist.
3. Generates a CI summary.
4. Archives Allure report, raw results, run logs, Surefire reports, and CI artifacts.
5. Optionally emails the Allure ZIP when `EMAIL_USER`, `EMAIL_PASS`, and `EMAIL_TO` are configured as secure variables.
6. Exits with the original Maven result, so a failing suite fails the pipeline.

### CI Secret Variables

Create these as masked secret variables in the CI platform instead of putting real values in committed property files:

```text
RMT_URL
RMT_USERNAME
RMT_PASSWORD
RMT_BROWSER
RMT_HEADLESS
DB_URL
DB_USERNAME
DB_PASSWORD
ALLOCATION_VALIDATION_QUERY
RMT_ALLOCATION_SWITCH_USERNAME
RMT_ALLOCATION_SWITCH_PASSWORD
EMAIL_USER
EMAIL_PASS
EMAIL_TO
```

Use a CI-safe test account. Interactive Microsoft Authenticator approval and other MFA challenges cannot be completed reliably in unattended execution.

## GitHub Actions

The workflow at `.github/workflows/rmt-ci.yml` compiles the project on every pull request and push to `master`. It does not run state-changing UAT UI tests automatically.

To enable the manual UAT sanity run, create a GitHub Environment named `uat` and add these environment secrets:

```text
RMT_URL
RMT_USERNAME
RMT_PASSWORD
RMT_ALLOCATION_SWITCH_USERNAME
RMT_ALLOCATION_SWITCH_PASSWORD
DB_URL
DB_USERNAME
DB_PASSWORD
ALLOCATION_VALIDATION_QUERY
```

Open **Actions**, select **RMT Java CI**, click **Run workflow**, and enable `run_uat_ui_tests`. The run uses Edge in headless mode and uploads Surefire reports, test logs, Allure results, and the CI summary as a downloadable artifact.

## MCP Integration

The project contains an MCP server in `mcp/`. It gives Codex or Claude project-aware tools for test execution and diagnostics; it does not replace Selenium, TestNG, Maven, or Allure.

Install the local MCP environment:

```powershell
.\mcp\setup_mcp.ps1
```

Start it manually if needed:

```powershell
.\mcp\.venv-mcp\Scripts\python.exe .\mcp\server.py
```

The MCP server supports tasks such as:

- Running the full UAT suite, a TestNG XML suite, or a focused test class.
- Running focused allocation, configuration, login, and DB validation suites.
- Reading Surefire and TestNG summaries.
- Finding the latest test log and failure artifacts.
- Generating or inspecting Allure report summary data.
- Creating and reading CI summary artifacts.
- Safely cleaning generated project-local test artifacts in dry-run mode.

For client configuration and the complete MCP tool list, see [mcp/README.md](mcp/README.md).

## Troubleshooting

| Symptom | Likely cause | Recommended action |
| --- | --- | --- |
| Only `LoginTest` runs | A focused `-Dtest=...` command was used, or the suite XML was changed. | Run `mvn clean test -Denv=uat -Dbrowser=edge -Dheadless=true` and review `Test_Sanity.xml`. |
| Browser is not maximized | CI/headless browser has no visible desktop window. | Use the framework's 1920x1080 deterministic viewport; headed mode also attempts maximize. |
| Login stops after password entry | Microsoft account picker, KMSI, network reset, or MFA challenge is present. | Read the latest log and failure HTML; use a CI-safe account for unattended execution. |
| PROD login fails on missing submit prompt | PROD can bypass the post-password submit/KMSI click. | Keep the environment-specific login helper behavior; run with `-Denv=prod` only when approved. |
| Allocation task is absent for Resource5 | Allocation may already be terminal or complete. | The allocation page flow treats terminal status as a valid state and does not expect a new task. |
| Allocation cannot proceed | Chosen dates are unavailable or skills are missing. | Verify resource, skill selection, future-business-date retry, and configured hours in the run log. |
| DB validation is skipped or fails before query | DB flag, query, authentication, schema permissions, or network access is missing. | Configure JDBC settings and run the DB test explicitly. |
| Allure graphs show no history | The persistent history file was deleted or report was generated without `allurerc.mjs`. | Preserve `allure-history/allure-history.jsonl` and use the configured generation command. |
| Screenshot is absent in Allure | The browser was unavailable when failure handling ran, or capture failed. | Check `test_Logs/failure-artifacts`, Surefire XML, and latest log for capture messages. |
| CI fails but no artifacts are visible | Artifact publication step was skipped or paths differ on the runner. | Check `azure-pipelines.yml` or `bitbucket-pipelines.yml` and verify generated directories before upload. |

## Contribution Rules

1. Create feature work on a non-main branch. Do not develop or test destructive workflows directly on `main`.
2. Keep test classes focused on orchestration and assertions. Keep browser actions, waits, selectors, and workflow decisions in page classes.
3. Use `ElementUtil` for interaction and explicit waits before adding custom WebDriver code.
4. Put stable constants in `AppConstants`. Put environment-specific values and secrets in properties or CI secret variables.
5. Add a concise JavaDoc comment to meaningful new page methods, helpers, and test methods.
6. Use robust locators based on accessible attributes, stable text, `data-testid`, labels, or semantic relationships. Avoid volatile CSS class names where possible.
7. Add failure messages that state the expected behavior and the actual result.
8. Run the focused test first, then the active sanity suite in headless mode before raising a pull request.
9. Review `test_Logs`, `target/surefire-reports`, and Allure evidence for every failed execution before changing locators or timing.
10. Do not commit credentials, generated reports, test logs, target output, or browser artifacts.

## Related Project Guides

- [Allocation setup and debugging guide](ALLOCATION_AUTOMATION_SETUP.md)
- [CI/CD automation workflow](CI_CD_AUTOMATION_WORKFLOW.md)
- [RMT MCP server guide](mcp/README.md)

## Quick Start

```powershell
git clone <repository-url>
cd RMT
mvn -q -DskipTests test-compile
mvn clean test -Denv=uat -Dbrowser=edge -Dheadless=true
allure generate --config ./allurerc.mjs allure-results -o allure-report
allure open --config ./allurerc.mjs allure-report
```
