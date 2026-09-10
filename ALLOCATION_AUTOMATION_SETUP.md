# Allocation Automation Setup (RMT)

This guide is tailored for this project (`RMT`) and the new allocation validation classes.

## 0. Standard Commands

Use these commands from the project root as the standard day-to-day workflow.

Compile only:

```bash
mvn -q -DskipTests test-compile
```

Run the active sanity suite on UAT in headless mode:

```bash
mvn clean test -Denv=uat -Dbrowser=edge -Dheadless=true
```

Run the active sanity suite on UAT in headed mode:

```bash
mvn clean test -Denv=uat -Dbrowser=edge -Dheadless=false
```

Run a single test class on UAT in headless mode:

```bash
mvn -q -Denv=uat -Dbrowser=edge -Dheadless=true "-Dtest=com.qa.rmt.MyTest.LoginTest" test
```

Run a single test class on UAT in headed mode:

```bash
mvn -q -Denv=uat -Dbrowser=edge -Dheadless=false "-Dtest=com.qa.rmt.MyTest.ConfigurationPageTest" test
```

Delete old Allure results and report before a fresh run:

```powershell
Remove-Item -Recurse -Force .\allure-results, .\allure-report -ErrorAction SilentlyContinue
```

Generate the Allure 3 report:

```bash
allure generate --config ./allurerc.mjs allure-results -o allure-report
```

Open the generated Allure 3 report:

```bash
allure open --config ./allurerc.mjs allure-report
```

Show the latest generated run log:

```powershell
Get-ChildItem .\test_Logs | Sort-Object LastWriteTime -Descending | Select-Object -First 1 FullName, LastWriteTime
```

Read the latest generated run log:

```powershell
$latestLog = Get-ChildItem .\test_Logs | Sort-Object LastWriteTime -Descending | Select-Object -First 1
Get-Content $latestLog.FullName
```

Current active suite:

- `src/test/resources/TestRunners/Test_Sanity.xml`

Current sanity coverage:

- `com.qa.rmt.MyTest.LoginTest`
- `com.qa.rmt.MyTest.AllocationFormulaValidationTest`
- `com.qa.rmt.MyTest.AllocationModulePOMTest`
- `com.qa.rmt.MyTest.ConfigurationPageTest`

Note:

- `AllocationDbValidationTest` remains available but is intentionally kept out of the shared sanity suite until the DB query/authentication path is finalized for unattended execution.

Standard artifact locations:

- Run logs: `test_Logs`
- Failure screenshots and HTML snapshots: `test_Logs/failure-artifacts`
- Surefire summary reports: `target/surefire-reports`
- Raw Allure results: `allure-results`
- Generated Allure report: `allure-report`

Run log naming pattern:

- `Test_Run_DD-MM-YY_<TestNG test name>_HH-mm-ss.log`

## 1. What is added

- Formula calculator:
  `src/main/java/RMT/Utils/AllocationCapacityCalculator.java`
- DB utility:
  `src/main/java/RMT/Utils/PostgresValidationUtil.java`
- DB row model:
  `src/main/java/RMT/Models/AllocationValidationRow.java`
- Test classes:
  `src/test/java/com/qa/rmt/MyTest/AllocationFormulaValidationTest.java`
  `src/test/java/com/qa/rmt/MyTest/AllocationDbValidationTest.java`
- POM page classes for allocation module:
  `src/main/java/RMT/Pages/CommonAllocationPage.java`
  `src/main/java/RMT/Pages/AppendedCapacityConfigPage.java`
  `src/main/java/RMT/Pages/AllocationWorkflowPage.java`
- POM tests for UC008/UC017 and appended-capacity flows:
  `src/test/java/com/qa/rmt/MyTest/AllocationModulePOMTest.java`
  `src/test/java/com/qa/rmt/MyTest/AppendedCapacityConfigTest.java`
- TestNG runner:
  `src/test/resources/TestRunners/Test_Sanity.xml`

## 2. BRD rules currently automated

- `Total Capacity = Base + Appended`
- `Available Hours = Total - (Leave + Allocation + Holiday)`
- No negative availability.
- Half-day leave scenarios:
  - Allocation 0 -> 4 available
  - Allocation 2 -> 2 available
  - Allocation 4 -> 0 available
  - Allocation >=5 -> 0 available
- Extended utilization trigger when requested hours exceed base capacity and appended is enabled.
- Request blocking when requested hours exceed balance/total capacity.

## 3. DB validation prerequisites

Set these in `config.uat.properties` or as environment variables:

- `allocation.db.validation.enabled=true`
- `allocation.validation.query=<your SQL>`
- `db.url=jdbc:postgresql://<host>:<port>/<db>`
- `db.username=<username>`
- `db.password=<password>`

Environment variable overrides supported:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

## 4. Required SQL aliases

Your query **must** return these aliases:

- `record_key`
- `base_capacity_hours`
- `appended_capacity_hours`
- `leave_hours`
- `allocation_hours`
- `holiday_hours`
- `ui_available_hours`

Use this template and replace table names/joins per UAT schema.

```sql
SELECT
    '<any_unique_row_key>'::text AS record_key,
    <base_capacity_expression> AS base_capacity_hours,
    COALESCE(<appended_capacity_expression>, 0) AS appended_capacity_hours,
    COALESCE(<leave_hours_expression>, 0) AS leave_hours,
    COALESCE(<allocation_hours_expression>, 0) AS allocation_hours,
    COALESCE(<holiday_hours_expression>, 0) AS holiday_hours,
    <ui_available_hours_expression> AS ui_available_hours;
```

## 5. Execution Notes

Before running the UI workflows, enable required toggles in `config.uat.properties`:

- `allocation.ui.run.enabled=true`
- `appended.config.run.enabled=true` (only for admin config test)
- `allocation.workflow.run.enabled=true` (for UC017 visibility/action test)

Note: if these flags are `false`, browser bootstrap is skipped for the corresponding classes.

Run DB validation explicitly only when the SQL query and DB authentication path are confirmed:

```bash
mvn -q -Denv=uat -Dbrowser=edge -Dheadless=true "-Dtest=com.qa.rmt.MyTest.AllocationDbValidationTest" test
```

## 6. Allure 3 report generation

This project uses `allurerc.mjs` at the repository root for Allure 3 history and graph configuration.

Generate the report manually:

```bash
allure generate --config ./allurerc.mjs allure-results -o allure-report
```

Open the latest report in the browser:

```bash
allure open --config ./allurerc.mjs allure-report
```

The `historyPath` setting in `allurerc.mjs` points to `allure-history/allure-history.jsonl`, which is what enables the History and trend graphs across runs.

## 7. Logs and Failure Artifacts

Every suite run writes a timestamped log file into `test_Logs`.

If a test fails, use these locations first:

- `test_Logs`
- `test_Logs/failure-artifacts`
- `target/surefire-reports`
- `allure-results`
- `allure-report`

Useful PowerShell commands:

Show the latest 5 run logs:

```powershell
Get-ChildItem .\test_Logs | Sort-Object LastWriteTime -Descending | Select-Object -First 5 FullName, LastWriteTime
```

Show failure artifacts:

```powershell
Get-ChildItem .\test_Logs\failure-artifacts
```

Open Surefire suite summary:

```powershell
Get-Content .\target\surefire-reports\TestSuite.txt
```

Open TestNG result summary:

```powershell
Get-Content .\target\surefire-reports\testng-results.xml
```
