#!/usr/bin/env bash
# Compila MeuRobo, implanta no Robocode local e roda uma batalha headless real
# contra UM oponente passado como argumento. E exatamente o comando que a
# VSCode Task ("Robocode: batalha rapida contra oponente a escolha", em
# .vscode/tasks.json) dispara -- este script pode ser chamado direto do shell
# (para testar/depurar sem VSCode) ou pela Task (que so encaminha o valor
# escolhido no dropdown como $1).
set -euo pipefail

if [ "$#" -lt 1 ]; then
    echo "Uso: $0 <classe-do-oponente>  (ex.: $0 sample.SittingDuck)" >&2
    exit 1
fi

OPONENTE="$1"

# Raiz do projeto (robocode-robo), calculada a partir da localizacao deste
# script -- funciona independente de onde o repositorio for clonado.
RAIZ="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ROBOCODE_HOME="$HOME/robocode"

echo "== 1/4: compilando MeuRobo.java =="
javac -cp "$ROBOCODE_HOME/libs/robocode.jar" -d "$RAIZ/build" "$RAIZ/src/meurobo/MeuRobo.java"

echo "== 2/4: implantando em $ROBOCODE_HOME/robots/meurobo =="
mkdir -p "$ROBOCODE_HOME/robots/meurobo"
# build/meurobo/*.class (nao so MeuRobo.class): a classe interna Onda, do Wave
# Surfing, compila para um .class separado (MeuRobo$Onda.class) que tambem
# precisa ser implantado -- esquece-lo produz ClassNotFoundException ao
# carregar o robo (mesmo problema real documentado no README do projeto).
cp "$RAIZ"/build/meurobo/*.class "$RAIZ/src/meurobo/MeuRobo.properties" "$ROBOCODE_HOME/robots/meurobo/"
rm -f "$ROBOCODE_HOME/robots/robot.database"   # forca o Robocode a reindexar

echo "== 3/4: compilando RodarBatalhas.java =="
BUILD_TOOLS="$(mktemp -d)"
trap 'rm -rf "$BUILD_TOOLS"' EXIT
javac -cp "$ROBOCODE_HOME/libs/*" "$RAIZ/tools/RodarBatalhas.java" -d "$BUILD_TOOLS"

echo "== 4/4: rodando batalha contra $OPONENTE =="
java -DNOSECURITY=true -cp "$BUILD_TOOLS:$ROBOCODE_HOME/libs/*" RodarBatalhas "$OPONENTE"
