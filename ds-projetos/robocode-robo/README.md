# robocode-robo — referência do mini-projeto Robocode de `Tutorial_DS.md`

`MeuRobo`, um `AdvancedRobot` com radar lock, mira preditiva (Linear/Circular Targeting), movimento evasivo por Wave Surfing simplificado (com fallback de *circling* fixo), gerenciamento de energia na potência de tiro e recuperação defensiva de parede/colisão — a soma de todas as aulas de Robocode do tutorial (API básica → `AdvancedRobot` → mira preditiva → Wave Surfing → refinamento/testes → finalização).

## Pré-requisitos

Robocode **1.11.1** instalado de verdade a partir da distribuição oficial (`https://sourceforge.net/projects/robocode/files/`), com JDK 21. Nesta sessão foi instalado em `~/robocode` via o instalador oficial (`robocode-1.11.1-setup.jar`), rodado sob um X virtual (`Xvfb`) para interagir com a caixa de diálogo "Install to" que o instalador exige.

## Estrutura

```
src/meurobo/MeuRobo.java              — código-fonte do robô
src/meurobo/MeuRobo.properties        — metadados (nome, autor, descrição, versão)
battles/*.battle                      — um arquivo de batalha por oponente de exemplo, 10 rounds cada
tools/RodarBatalhas.java              — runner via API robocode.control.RobocodeEngine (batalhas headless)
tools/rodar-batalha-vscode.sh         — compila+implanta+roda 1 batalha (Linux/macOS), usado pela VSCode Task
tools/rodar-batalha-vscode.ps1        — o mesmo, em PowerShell (Windows)
.vscode/tasks.json                    — VSCode Task com dropdown de oponente, sem precisar do menu do Robocode
resultados/batalhas-reais.txt         — saída real da última rodada de batalhas
meurobo.jar                           — robô empacotado (Tópico "Finalização")
```

## Como compilar e implantar

```bash
javac -cp ~/robocode/libs/robocode.jar -d build src/meurobo/MeuRobo.java
mkdir -p ~/robocode/robots/meurobo
cp build/meurobo/*.class src/meurobo/MeuRobo.properties ~/robocode/robots/meurobo/
rm -f ~/robocode/robots/robot.database   # força o Robocode a reindexar o robô novo
```

Note: `build/meurobo/*.class` (não só `MeuRobo.class`) porque, desde a introdução do Wave Surfing, a classe interna `Onda` compila para um `.class` separado (`MeuRobo$Onda.class`) que também precisa ser implantado — esquecê-lo produz `ClassNotFoundException: meurobo.MeuRobo$Onda` ao carregar o robô.

## Como rodar as batalhas (headless, via linha de comando)

```bash
cd ~/robocode
java -cp "libs/*" robocode.Robocode -nodisplay -nosound \
     -battle caminho/para/meurobo-vs-Corners.battle \
     -results resultado.txt
```

Ou, para todas de uma vez com saída estruturada, via a API `robocode.control.RobocodeEngine` (`tools/RodarBatalhas.java`):

```bash
javac -cp ~/robocode/libs/* tools/RodarBatalhas.java -d /tmp/rb
java -DNOSECURITY=true -cp "/tmp/rb:~/robocode/libs/*" RodarBatalhas
```

`RodarBatalhas` também aceita um argumento opcional: passado, roda uma única batalha (10 rounds) contra essa classe em vez do loop fixo dos 4 oponentes de sempre — é a base do mecanismo descrito a seguir.

## Rodando pelo VSCode, sem o menu do Robocode

Descrito em detalhe na seção "Ferramentas: batalhas via VSCode, sem o menu do Robocode" de `Tutorial_DS.md` (entre os Tópicos 13 e 14). Resumo: uma VSCode Task (`.vscode/tasks.json`) com um input `pickString` mostra um dropdown de robôs de exemplo; ao rodar a task (`Ctrl+Shift+P` → "Tasks: Run Task"), ela chama um script que compila `MeuRobo`, implanta em `~/robocode` e roda a batalha, direto no terminal integrado — sem abrir a GUI do Robocode. Existem dois scripts equivalentes, um por sistema operacional (a task escolhe o certo automaticamente via os campos `windows`/`command` de `tasks.json`):

```bash
# Linux/macOS
./tools/rodar-batalha-vscode.sh sample.SittingDuck
```

```powershell
# Windows
.\tools\rodar-batalha-vscode.ps1 sample.SittingDuck
```

Quem não usa VSCode pode rodar o script equivalente ao seu sistema operacional direto no terminal, sem passar pela task — o resultado é idêntico, já que a task só encaminha o valor escolhido no dropdown como argumento do mesmo script.

## Resultados reais — histórico (antes da mira preditiva e do Wave Surfing)

Números da primeira versão de `MeuRobo` (radar lock, mira direta, *circling* fixo — o robô dos Tópicos 13/14 do tutorial), mantidos aqui para comparação honesta com a versão atual, e não apagados.

```
=== MeuRobo vs sample.Corners (10 rounds) ===
meurobo.MeuRobo 1.0*   score=390   survival=0    bulletDmg=390  ramDmg=0    1st=0  2nd=10  3rd=0
sample.Corners         score=1424  survival=500  bulletDmg=687  ramDmg=0    1st=10 2nd=0   3rd=0

=== MeuRobo vs sample.Crazy (10 rounds) ===
meurobo.MeuRobo 1.0*   score=468   survival=100  bulletDmg=323  ramDmg=8    1st=2  2nd=8   3rd=0
sample.Crazy           score=1036  survival=400  bulletDmg=455  ramDmg=20   1st=8  2nd=2   3rd=0

=== MeuRobo vs sample.Walls (10 rounds) ===
meurobo.MeuRobo 1.0*   score=613   survival=50   bulletDmg=387  ramDmg=146  1st=1  2nd=9   3rd=0
sample.Walls           score=1422  survival=450  bulletDmg=728  ramDmg=17   1st=9  2nd=1   3rd=0

=== MeuRobo vs sample.RamFire (10 rounds) ===
meurobo.MeuRobo 1.0*   score=1554  survival=350  bulletDmg=849  ramDmg=142  1st=7  2nd=3   3rd=0
sample.RamFire         score=619   survival=150  bulletDmg=294  ramDmg=82   1st=3  2nd=7   3rd=0
```

Taxa de vitória real de `MeuRobo` (rounds vencidos / 10): **Corners 0/10, Crazy 2/10, Walls 1/10, RamFire 7/10.**

Uma segunda rodada isolada contra `sample.Crazy`, rodada via CLI (`-battle`/`-results`) em vez da API, deu 5/10 — um lembrete real de que o Robocode não é determinístico entre execuções (o movimento de `Crazy` é aleatório a cada rodada), então "10 rounds" é uma amostra, não uma taxa fixa; o texto do tutorial (Tópico de Refinamento e Testes) usa exatamente esses números reais e discute essa variância.

**Análise honesta dos pontos fracos (versão histórica):** `MeuRobo` perdia consistentemente contra `Corners` e `Walls` — ambos ficam parados ou colados na borda do campo, girando o canhão; o `setAhead(80)` de `MeuRobo` o mantinha girando em círculo largo demais para fechar a distância e acumular dano antes de levar tiros parados e precisos. Contra `RamFire` (que avança para colidir), o tratamento de `onHitRobot`/`onHitWall` de `MeuRobo` compensava bem, resultando na única vitória líquida (7/10). Isso é discutido no tutorial como um exercício real de "ajuste direcionado" (Tópico 17, Passo 5 — Refinamento e testes sistemáticos).

## Resultados reais — depois da mira preditiva e do Wave Surfing (versão atual, execução real nesta sessão)

Mesmos 4 oponentes, mesmo runner `RodarBatalhas.java`, mesmos 800×600 e 10 *rounds* cada. **Múltiplas baterias independentes**, rodadas nesta sessão (3 para "só mira preditiva", 5 para "mira preditiva + Wave Surfing"), isolando o efeito de cada técnica nova (Tópicos 15 e 16 do tutorial) e, mais importante, medindo a variação real entre execuções em vez de confiar numa única bateria:

```
                     circling fixo      mira preditiva          mira preditiva
                     (histórico,        só (Tópico 15,          + Wave Surfing
                     1 bateria)         3 baterias)              (Tópico 16, versão atual, 5 baterias)
vs sample.Corners:   0/10               2/10 a 5/10               3/10 a 5/10
vs sample.Crazy:     2/10 a 5/10        5/10 a 6/10               6/10 a 10/10
vs sample.Walls:     1/10               4/10 a 5/10               0/10 a 6/10
vs sample.RamFire:   7/10               6/10 a 9/10               4/10 a 10/10
```

**Descoberta real, não prevista quando o material original foi escrito:** o histórico deste projeto já registrava que `Crazy` varia entre execuções (movimento aleatório). O que só ficou claro ao rodar várias baterias da versão atual é que **`Corners`, `Walls` e `RamFire` também variam**, apesar de terem estratégia 100% determinística — a hipótese mais provável é que o Robocode sorteia a posição inicial de cada robô a cada *round*, afetando a dinâmica do combate independentemente da lógica dos dois lados. Isso muda a forma correta de comparar versões: um único número (`5/10` vs. `4/10`) pode ser só ruído estatístico; olhar a faixa completa é mais honesto.

**Análise honesta dos pontos fracos (versão atual):** com isso em mente, os dados sustentam:

- Melhora razoavelmente clara contra `Corners` (faixa sobe de "2–5" para "3–5", piso melhor) e `Crazy` (faixa sobe de "5–6" para "6–10", quase sem sobreposição).
- Contra `Walls`, o Wave Surfing tornou o resultado **mais instável**, não uniformemente pior: a faixa "4–5" (só mira preditiva) virou "0–6" (com Wave Surfing) — o piso caiu bastante em pelo menos uma das 5 execuções. A hipótese mais provável, discutida no tutorial (Tópico 16, Passo 7), é que a tabela de estatísticas de perigo não segmentada, com poucas amostras coletadas numa única batalha de 10 *rounds*, é mais ruído do que sinal em algumas execuções contra um padrão de movimento tão regular quanto o de `Walls`. A versão de torneio do Wave Surfing (segmentada por distância/velocidade, com muito mais dados por segmento) existe justamente para reduzir esse tipo de instabilidade — fora de escopo deste mini-projeto didático.
- Contra `RamFire`, a faixa também alargou ("6–9" para "4–10") em vez de subir de forma clara — o resultado contra esse oponente parece depender bastante de fatores fora do controle direto da mira ou do movimento (como a posição inicial sorteada), e o Wave Surfing não resolveu nem piorou isso de forma decisiva.

## Empacotamento (`meurobo.jar`)

Gerado com `jar cf meurobo.jar meurobo/MeuRobo.class meurobo/MeuRobo\$Onda.class meurobo/MeuRobo.properties` a partir de `build/`, espelhando a estrutura que o empacotador gráfico do Robocode ("Robot → Package robot for upload") produz — o `MeuRobo$Onda.class` (classe interna do Wave Surfing) precisa entrar no `.jar` também, pelo mesmo motivo do passo de implantação acima. **Nota de verificação:** carregar esse `.jar` via `robocode.control.RobocodeEngine.getLocalRepository()` (em vez da pasta de classes) expôs um bug conhecido do próprio Robocode 1.11.1 nessa API (`NullPointerException` em `URLJarCollector.closeJarURLConnection`, não relacionado ao conteúdo do robô) — por isso as batalhas reais acima foram rodadas a partir do robô implantado como `.class`/`.properties` em `~/robocode/robots/meurobo/`, que é o mesmo bytecode dentro do `.jar` e o caminho que o Robocode usa internamente ao carregar um robô para batalha (o empacotamento em `.jar` é só o formato de distribuição/submissão, não muda como o robô roda). O `.jar` abre normalmente pela GUI do Robocode (`Robot → Import robot`).
