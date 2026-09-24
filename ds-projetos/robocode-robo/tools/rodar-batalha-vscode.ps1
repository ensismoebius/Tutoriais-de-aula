# Compila MeuRobo, implanta no Robocode local e roda uma batalha headless real
# contra UM oponente passado como argumento. Equivalente PowerShell (Windows)
# de rodar-batalha-vscode.sh (Linux/macOS) -- mesma sequencia de passos. E o
# que a VSCode Task dispara no Windows (bloco "windows" de .vscode/tasks.json).
#
# NOTA HONESTA: este script foi escrito espelhando a logica ja verificada do
# script bash, mas nao pode ser executado de ponta a ponta nesta sessao --
# o ambiente onde este material foi verificado e Linux e nao tem PowerShell
# nem Robocode instalado em layout Windows. O mecanismo (compilar, implantar,
# rodar RodarBatalhas.java) e o mesmo codigo Java, ja testado via bash; a
# traducao para PowerShell em si nao foi rodada de verdade.

param(
    [Parameter(Mandatory = $true)]
    [string]$Oponente
)

$ErrorActionPreference = "Stop"

# Raiz do projeto (robocode-robo), calculada a partir da localizacao deste
# script -- funciona independente de onde o repositorio for clonado.
$Raiz = Split-Path -Parent $PSScriptRoot
$RobocodeHome = Join-Path $HOME "robocode"

Write-Host "== 1/4: compilando MeuRobo.java =="
javac -cp "$RobocodeHome\libs\robocode.jar" -d "$Raiz\build" "$Raiz\src\meurobo\MeuRobo.java"
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "== 2/4: implantando em $RobocodeHome\robots\meurobo =="
New-Item -ItemType Directory -Force -Path "$RobocodeHome\robots\meurobo" | Out-Null
# build\meurobo\*.class (nao so MeuRobo.class): a classe interna Onda, do Wave
# Surfing, compila para um .class separado (MeuRobo$Onda.class) que tambem
# precisa ser implantado -- esquece-lo produz ClassNotFoundException ao
# carregar o robo (mesmo problema real documentado no README do projeto).
Copy-Item "$Raiz\build\meurobo\*.class" "$RobocodeHome\robots\meurobo\" -Force
Copy-Item "$Raiz\src\meurobo\MeuRobo.properties" "$RobocodeHome\robots\meurobo\" -Force
Remove-Item "$RobocodeHome\robots\robot.database" -Force -ErrorAction SilentlyContinue

Write-Host "== 3/4: compilando RodarBatalhas.java =="
$BuildTools = Join-Path ([System.IO.Path]::GetTempPath()) ("rb-" + [System.Guid]::NewGuid().ToString("N"))
New-Item -ItemType Directory -Force -Path $BuildTools | Out-Null
javac -cp "$RobocodeHome\libs\*" "$Raiz\tools\RodarBatalhas.java" -d $BuildTools
if ($LASTEXITCODE -ne 0) { exit 1 }

Write-Host "== 4/4: rodando batalha contra $Oponente =="
# Separador de classpath no Windows e ";", nao ":" -- a unica diferenca real
# frente ao script bash, alem da sintaxe da propria linguagem (New-Item em vez
# de mkdir -p, Copy-Item em vez de cp, Remove-Item em vez de rm -f).
java -DNOSECURITY=true -cp "$BuildTools;$RobocodeHome\libs\*" RodarBatalhas $Oponente
$exitCode = $LASTEXITCODE

Remove-Item $BuildTools -Recurse -Force -ErrorAction SilentlyContinue
if ($exitCode -ne 0) { exit 1 }
