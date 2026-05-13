param(
    [string]$JdkPath = '',
    [switch]$RenameResources
)

Write-Host "== NutrikaliApp: setup_and_build.ps1 =="

if ($JdkPath -ne '') {
    if (-Not (Test-Path $JdkPath)) {
        Write-Host "Provided JDK path does not exist: $JdkPath" -ForegroundColor Red
        exit 1
    }
    $env:JAVA_HOME = $JdkPath
    $env:PATH = "$env:JAVA_HOME\bin;${env:PATH}"
    Write-Host "Set JAVA_HOME for this session to: $env:JAVA_HOME"
}

Write-Host "java -version output:"
java -version 2>&1 | Write-Host

if ($RenameResources) {
    Write-Host "Renaming resources (with backups) and updating references..."
    $resPath = Join-Path -Path $PSScriptRoot -ChildPath "..\app\src\main\res\mipmap-hdpi"
    $resPath = (Resolve-Path $resPath).Path

    $file1 = Join-Path $resPath 'LogoNutri.png'
    $file2 = Join-Path $resPath 'Fondo_Ventana.png'

    if (Test-Path $file1) {
        Copy-Item -Path $file1 -Destination "$file1.bak" -Force
        Rename-Item -Path $file1 -NewName 'logonutri.png' -Force
        Write-Host "Renamed LogoNutri.png -> logonutri.png"
    } else { Write-Host "File not found: $file1" }

    if (Test-Path $file2) {
        Copy-Item -Path $file2 -Destination "$file2.bak" -Force
        Rename-Item -Path $file2 -NewName 'fondo_ventana.png' -Force
        Write-Host "Renamed Fondo_Ventana.png -> fondo_ventana.png"
    } else { Write-Host "File not found: $file2" }

    # Update references in known layout files (activity_main.xml)
    $layout = Join-Path -Path $PSScriptRoot -ChildPath "..\app\src\main\res\layout\activity_main.xml"
    $layout = (Resolve-Path $layout).Path
    if (Test-Path $layout) {
        (Get-Content $layout) -replace '@mipmap/LogoNutri','@mipmap/logonutri' | Set-Content $layout
        (Get-Content $layout) -replace '@mipmap/Fondo_Ventana','@mipmap/fondo_ventana' | Set-Content $layout
        Write-Host "Updated references in activity_main.xml"
    } else { Write-Host "Layout file not found: $layout" }
}

# Stop any running Gradle daemons
Write-Host "Stopping Gradle daemons..."
& .\gradlew.bat --stop

# Run a clean build and try to assemble debug
Write-Host "Starting clean build (this may take a while)..."
$buildCmd = { .\gradlew.bat clean :app:assembleDebug --refresh-dependencies }
& $buildCmd.Invoke()

Write-Host "Done. Check output above for errors. If build still fails with Java version errors, install JDK 11 and re-run this script with -JdkPath 'C:\Path\To\jdk11'"

