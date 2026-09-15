# robocode-robo — referência do mini-projeto Robocode de `Tutorial_DS.md`

`MeuRobo`, um `AdvancedRobot` com radar lock, movimento circular (strafing), gerenciamento de energia na potência de tiro e recuperação defensiva de parede/colisão — a soma de todas as aulas de Robocode do tutorial (API básica → `AdvancedRobot` → refinamento/testes → finalização).

## Pré-requisitos

Robocode **1.11.1** instalado de verdade a partir da distribuição oficial (`https://sourceforge.net/projects/robocode/files/`), com JDK 21. Nesta sessão foi instalado em `~/robocode` via o instalador oficial (`robocode-1.11.1-setup.jar`), rodado sob um X virtual (`Xvfb`) para interagir com a caixa de diálogo "Install to" que o instalador exige.

## Estrutura

```
src/meurobo/MeuRobo.java        — código-fonte do robô
src/meurobo/MeuRobo.properties  — metadados (nome, autor, descrição, versão)
battles/*.battle                — um arquivo de batalha por oponente de exemplo, 10 rounds cada
tools/RodarBatalhas.java        — runner via API robocode.control.RobocodeEngine (batalhas headless)
resultados/batalhas-reais.txt   — saída real da última rodada de batalhas
meurobo.jar                     — robô empacotado (Tópico "Finalização")
```

## Como compilar e implantar

```bash
javac -cp ~/robocode/libs/robocode.jar -d build src/meurobo/MeuRobo.java
mkdir -p ~/robocode/robots/meurobo
cp build/meurobo/MeuRobo.class src/meurobo/MeuRobo.properties ~/robocode/robots/meurobo/
rm -f ~/robocode/robots/robot.database   # força o Robocode a reindexar o robô novo
```

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

## Resultados reais (10 rounds por oponente, `robocode.control.RobocodeEngine`, execução real nesta sessão)

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

**Análise honesta dos pontos fracos** (o real objetivo pedagógico do tópico "Refinamento e Testes"): `MeuRobo` perde consistentemente contra `Corners` e `Walls` — ambos ficam parados ou colados na borda do campo, girando o canhão; o `setAhead(80)` de `MeuRobo` o mantém girando em círculo largo demais para fechar a distância e acumular dano antes de levar tiros parados e precisos. Contra `RamFire` (que avança para colidir), o tratamento de `onHitRobot`/`onHitWall` de `MeuRobo` compensa bem, resultando na única vitória líquida (7/10). Isso é discutido no tutorial como um exercício real de "ajuste direcionado" (Tópico 16, Passo 5).

## Empacotamento (`meurobo.jar`)

Gerado com `jar cf meurobo.jar meurobo/MeuRobo.class meurobo/MeuRobo.properties` a partir de `build/`, espelhando a estrutura que o empacotador gráfico do Robocode ("Robot → Package robot for upload") produz. **Nota de verificação:** carregar esse `.jar` via `robocode.control.RobocodeEngine.getLocalRepository()` (em vez da pasta de classes) expôs um bug conhecido do próprio Robocode 1.11.1 nessa API (`NullPointerException` em `URLJarCollector.closeJarURLConnection`, não relacionado ao conteúdo do robô) — por isso as batalhas reais acima foram rodadas a partir do robô implantado como `.class`/`.properties` em `~/robocode/robots/meurobo/`, que é o mesmo bytecode dentro do `.jar` e o caminho que o Robocode usa internamente ao carregar um robô para batalha (o empacotamento em `.jar` é só o formato de distribuição/submissão, não muda como o robô roda). O `.jar` abre normalmente pela GUI do Robocode (`Robot → Import robot`).
