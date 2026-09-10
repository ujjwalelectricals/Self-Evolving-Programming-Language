$ErrorActionPreference = 'Stop'
& "$PSScriptRoot/build.ps1"
java -cp "$PSScriptRoot/out" evo.lang.LexerSelfTest
Write-Host "All Phase 1 checks passed."
