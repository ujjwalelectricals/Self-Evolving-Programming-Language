$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$out = Join-Path $root 'out'
New-Item -ItemType Directory -Force -Path $out | Out-Null
$files = Get-ChildItem -Path (Join-Path $root 'src/main/java') -Recurse -Filter '*.java' | ForEach-Object FullName
javac -encoding UTF-8 -d $out $files
Write-Host "EVO build complete: $out"
