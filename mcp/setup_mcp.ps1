<#
.SYNOPSIS
Bootstraps a local Python virtual environment for the RMT MCP server.

.DESCRIPTION
Creates `mcp\.venv-mcp`, installs the Python `mcp` dependency, and prints the
exact command/config values needed to register the server in Codex or Claude.

.PARAMETER PythonPath
Optional explicit Python executable path. Use this when Python is not available
on PATH.

.PARAMETER VenvPath
Optional virtual environment target directory. Defaults to `mcp\.venv-mcp`.
#>

[CmdletBinding()]
param(
    [string]$PythonPath = "",
    [string]$VenvPath = ".\\mcp\\.venv-mcp"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Invoke-NativeChecked {
    param(
        [Parameter(Mandatory = $true)]
        [string]$FilePath,

        [Parameter(Mandatory = $false)]
        [string[]]$ArgumentList = @(),

        [Parameter(Mandatory = $true)]
        [string]$FailureMessage
    )

    & $FilePath @ArgumentList
    if ($LASTEXITCODE -ne 0) {
        throw "$FailureMessage Exit code: $LASTEXITCODE"
    }
}

function Resolve-PythonExecutable {
    param([string]$RequestedPythonPath)

    if ($RequestedPythonPath -and (Test-Path $RequestedPythonPath)) {
        return (Resolve-Path $RequestedPythonPath).Path
    }

    if ($env:RMT_PYTHON_PATH -and (Test-Path $env:RMT_PYTHON_PATH)) {
        return (Resolve-Path $env:RMT_PYTHON_PATH).Path
    }

    $candidatePaths = @(
        "C:\\Users\\Piyush.Wadhwa\\.cache\\codex-runtimes\\codex-primary-runtime\\dependencies\\python\\python.exe"
    )

    foreach ($candidate in $candidatePaths) {
        if ($candidate -and (Test-Path $candidate)) {
            return (Resolve-Path $candidate).Path
        }
    }

    $pythonCommand = Get-Command python -ErrorAction SilentlyContinue
    if ($pythonCommand) {
        return $pythonCommand.Source
    }

    throw "No Python runtime found. Re-run with -PythonPath '<full path to python.exe>'."
}

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$resolvedPython = Resolve-PythonExecutable -RequestedPythonPath $PythonPath
$resolvedVenvPath = Join-Path $projectRoot $VenvPath
$requirementsPath = Join-Path $projectRoot "mcp\\requirements.txt"

Write-Host "Using project root: $projectRoot"
Write-Host "Using Python: $resolvedPython"

if (-not (Test-Path $resolvedVenvPath)) {
    Write-Host "Creating virtual environment at: $resolvedVenvPath"
    Invoke-NativeChecked -FilePath $resolvedPython -ArgumentList @("-m", "venv", $resolvedVenvPath) `
        -FailureMessage "Failed to create the MCP virtual environment."
} else {
    Write-Host "Virtual environment already exists at: $resolvedVenvPath"
}

$venvPython = Join-Path $resolvedVenvPath "Scripts\\python.exe"
if (-not (Test-Path $venvPython)) {
    throw "Virtual environment Python not found at: $venvPython"
}

Write-Host "Upgrading pip..."
Invoke-NativeChecked -FilePath $venvPython -ArgumentList @("-m", "pip", "install", "--upgrade", "pip") `
    -FailureMessage "Failed to upgrade pip inside the MCP virtual environment."

Write-Host "Installing MCP server requirements..."
Invoke-NativeChecked -FilePath $venvPython -ArgumentList @("-m", "pip", "install", "-r", $requirementsPath) `
    -FailureMessage "Failed to install MCP server requirements."

Invoke-NativeChecked -FilePath $venvPython -ArgumentList @("-c", "import mcp") `
    -FailureMessage "The MCP Python package is still not importable after installation."

$serverPath = Join-Path $projectRoot "mcp\\server.py"

Write-Host ""
Write-Host "MCP bootstrap completed successfully."
Write-Host ""
Write-Host "Use this command in your MCP client config:"
Write-Host "Command: $venvPython"
Write-Host "Args   : $serverPath"
Write-Host ""
Write-Host "Suggested environment values:"
Write-Host "RMT_PROJECT_ROOT=$projectRoot"
Write-Host "RMT_DEFAULT_ENV=uat"
Write-Host "RMT_DEFAULT_BROWSER=edge"
Write-Host "RMT_DEFAULT_HEADLESS=true"
