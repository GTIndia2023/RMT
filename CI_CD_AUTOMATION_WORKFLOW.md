# Phase 4 CI/CD Automation Workflow

This document describes the Phase 4 CI/CD setup for the RMT automation project.

## Goals

- make pipeline runs deterministic and headless by default
- allow CI systems to inject secrets without editing committed config files
- publish stronger debugging artifacts when tests fail
- give Codex a machine-readable CI summary to speed up failure analysis

## Runtime override model

The framework now supports runtime overrides from:

1. Java system properties
2. environment variables
3. committed config files

The resolution order is:

1. system property
2. environment variable
3. property file

## Recommended CI environment variables

Use CI secret storage for these variables instead of editing `config.uat.properties` in the repository:

- `RMT_URL`
- `RMT_USERNAME`
- `RMT_PASSWORD`
- `RMT_BROWSER`
- `RMT_HEADLESS`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `ALLOCATION_VALIDATION_QUERY`

## Generated CI artifacts

Phase 4 standardizes the following artifact locations:

- `artifacts/ci/ci-summary.json`
- `artifacts/ci/ci-summary.md`
- `target/surefire-reports/`
- `test_Logs/`
- `allure-results/`
- `allure-report/`

## CI summary generator

The CI summary is created by:

```powershell
python ci/generate_ci_summary.py
```

It reads:

- Surefire text summary
- TestNG XML summary
- Allure summary when present
- latest test log
- recent failure artifacts

and writes:

- `artifacts/ci/ci-summary.json`
- `artifacts/ci/ci-summary.md`

## Bitbucket pipeline behavior

The Bitbucket pipeline now:

1. runs tests in UAT headless mode
2. captures the Maven exit code without losing later artifact generation
3. generates Allure when `allure-results` exists
4. generates the CI summary
5. zips the Allure report when present
6. sends email only when email secrets and the report ZIP exist
7. exits with the original Maven test status

## Azure pipeline behavior

The Azure pipeline now:

1. uses Java 21
2. uses Python 3
3. runs tests in UAT headless mode
4. stores the Maven exit code in a pipeline variable
5. always generates the CI summary
6. always publishes CI artifacts
7. fails the pipeline at the end when the Maven test exit code is non-zero

## MCP integration in Phase 4

The RMT MCP server now supports:

- `generate_ci_summary`
- `read_ci_summary`

These tools let Codex read the same CI summary that pipelines produce.

## Suggested autonomous failure workflow

1. run the pipeline or the headless sanity suite
2. generate the CI summary
3. read the CI summary in Codex
4. call:
   - `read_testng_results`
   - `get_failure_artifacts`
   - `collect_failure_bundle`
5. apply the code fix
6. rerun the focused suite:
   - `run_login_smoke`
   - `run_allocation_suite`
   - `run_configuration_suite`
7. rerun the sanity suite

## First verification commands

Local sanity check:

```powershell
mvn clean test -Denv=uat -Dbrowser=edge -Dheadless=true
python ci/generate_ci_summary.py
```

Then review:

- `artifacts/ci/ci-summary.json`
- `artifacts/ci/ci-summary.md`

## Next Phase 4 extensions

The next natural additions are:

1. pipeline-specific rerun hints in the CI summary
2. MCP tools to build a PR-ready failure digest
3. optional branch-safe auto-fix workflow on a non-main branch
