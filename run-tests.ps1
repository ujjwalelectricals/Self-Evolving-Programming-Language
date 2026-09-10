$ErrorActionPreference = 'Stop'
& "$PSScriptRoot/build.ps1"
java -cp "$PSScriptRoot/out" evo.lang.LexerSelfTest
java -cp "$PSScriptRoot/out" evo.lang.ParserSelfTest
Write-Host "All Phase 1 + Phase 2 checks passed."
