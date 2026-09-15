# Projetos de referência — DS

Implementação de referência do material construído ao longo de `Tutorial_DS.md` (pasta acima). Java 21 LTS. Três projetos separados, exatamente como descritos no tutorial:

- [`javafx-game/`](javafx-game/) — Tópicos 1–11 (POO Avançada até Testes Unitários/TestFX): o jogo JavaFX com quadrados/círculos caindo por gravidade, persistência com DAO/SQLite, a ferramenta CRUD "Gerenciador de Fases" em FXML, coleções, generics/exceções, arquivos/streams, refatoração MVC, padrões Singleton/Factory, logging e testes.
- [`projeto-final/`](projeto-final/) — "Minha Estante", um exemplo completo e funcional do Projeto Final, satisfazendo os 5 requisitos mínimos do tópico "Projeto Final: Início".
- [`robocode-robo/`](robocode-robo/) — `MeuRobo`, o robô de combate Robocode construído ao longo do mini-projeto final do tutorial, com batalhas reais registradas contra os robôs de exemplo do próprio Robocode.

Cada projeto tem seu próprio `README.md` com instruções de execução e o que foi verificado, com comandos e saída reais.

## Rodando os três

```bash
# jogo JavaFX
cd javafx-game && mvn compile && mvn exec:java

# CRUD de fases (mesmo projeto, outra classe main)
cd javafx-game && mvn exec:java -Dexec.mainClass=jogo.GerenciadorFasesApp

# Projeto Final
cd projeto-final && mvn compile && mvn exec:java

# Robocode: exige a instalação real do Robocode (ver robocode-robo/README.md)
cd robocode-robo && javac -cp ~/robocode/libs/robocode.jar -d build src/meurobo/MeuRobo.java
```

## Como esta implementação foi verificada

Nada aqui foi apenas escrito e assumido como funcional. `javafx-game` e `projeto-final` foram de fato compilados e testados com **Maven 3.9.16** contra um **JDK 21.0.12** local, incluindo testes JUnit 5 reais, um teste de UI real com **TestFX** (clicando de verdade em campos e botões de uma tela JavaFX) e persistência real em arquivos **SQLite** (`jogo.db`, `fases.db`, `estante.db`) — como este ambiente não tem uma tela física conectada, as janelas JavaFX (o jogo, o Gerenciador de Fases, o teste TestFX) rodaram sob um **X virtual real** (`Xvfb`), não sob um mock: é a mesma pilha gráfica que rodaria numa máquina com monitor.

O **Robocode 1.11.1** foi baixado da distribuição oficial e instalado de verdade neste ambiente (também via `Xvfb`, para a caixa de diálogo do instalador). `MeuRobo` foi compilado, implantado na pasta `robots/` do Robocode, e colocado para brigar de verdade — 10 rounds cada — contra os quatro robôs de exemplo citados no tutorial (`sample.Corners`, `sample.Crazy`, `sample.Walls`, `sample.RamFire`), via a API `robocode.control.RobocodeEngine` e também via linha de comando (`-nodisplay -battle ... -results ...`). Os números de vitória/derrota no README de `robocode-robo/` e no tópico "Refinamento e Testes" do tutorial são os números reais dessas batalhas, não valores inventados.

Dois problemas reais apareceram durante essa verificação e foram corrigidos:

1. **`openjfx-monocle` não funciona com JavaFX 21** (`AbstractMethodError` em `MonocleWindow`). Os testes de UI com TestFX foram configurados para rodar sob `Xvfb` em vez de Monocle — ver a nota em `javafx-game/README.md`.
2. **A identificação de robô no Robocode não é só o nome da classe.** `robocode.battle.selectedRobots=meurobo.MeuRobo` sozinho falha com `Can't find 'meurobo.MeuRobo'`; o Robocode exige o nome completo que o próprio repositório atribui a um robô "de desenvolvimento" (compilado a partir de `.class`, não empacotado), que inclui um asterisco e a versão: `meurobo.MeuRobo 1.0*`. Isso só foi descoberto escrevendo um pequeno programa Java contra `robocode.control.RobocodeEngine.getLocalRepository()` para listar os nomes reais — documentado em `robocode-robo/README.md` e no tópico correspondente do tutorial.

Sem gitignore, os arquivos gerados por build/teste (`target/`, `*.db`, `*.class`, `build/`) poluiriam o repositório — cada projeto tem seu próprio `.gitignore` cobrindo isso.
