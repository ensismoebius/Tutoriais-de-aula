# javafx-game — referência de `Tutorial_DS.md` (Tópicos 1–11)

Implementação de referência do jogo JavaFX construído ao longo dos tópicos "POO Avançada" até "Testes Unitários (JUnit + TestFX)" do `Tutorial_DS.md`. Java 21 LTS, JavaFX 21, Maven, SQLite via JDBC, SLF4J/Logback, JUnit 5 e TestFX.

## Pré-requisitos

```bash
java --version   # 21 ou superior
mvn --version    # 3.9+
```

## Estrutura

```
src/main/java/jogo/
  Main.java                    — jogo JavaFX (Tópico 1): quadrados/círculos caindo com gravidade static
  GerenciadorFasesApp.java     — ferramenta CRUD "Gerenciador de Fases" (Tópicos 3, 7–8: FXML + MVC)
  model/
    Entidade, Quadrado, Circulo, ComId, Repositorio<T>, Fase
    GerenciadorDeJogo          — Model puro, sem import javafx.* (Tópicos 7–8: MVC)
    GerenciadorDeConfiguracoes — Singleton (Tópico 9)
    EntidadeInvalidaException, LimiteDeFaseExcedidoException
  dao/
    Conexao, EntidadeDAO, EntidadeDAOSQLite, EntidadeFactory (Tópico 9: Factory)
    FaseDAO, FaseDAOImpl
  controller/
    FaseController.java        — Controller @FXML do Gerenciador de Fases
src/main/resources/
  jogo/fase.fxml                — View do Gerenciador de Fases
  logback.xml                   — configuração de log (Tópico 10)
src/test/java/jogo/
  GerenciadorDeJogoTest, RepositorioTest — testes JUnit 5 do Model
  PersistenciaSmokeTest         — testes reais contra SQLite (jogo.db, fases.db)
  GerenciadorFasesUiTest        — teste de UI real com TestFX, dirigindo a tela de verdade
```

## Como rodar

```bash
mvn compile
mvn exec:java                                  # jogo (Main) — ESPAÇO pula, clique cria quadrado
mvn exec:java -Dexec.mainClass=jogo.GerenciadorFasesApp   # tela CRUD de fases
mvn test
mvn package                                     # gera target/javafx-game-1.0.0.jar
```

## O que foi verificado de verdade nesta sessão

Todos os comandos abaixo foram executados neste ambiente (JDK 21.0.12, Maven 3.9.16, sem GUI física — usando um X virtual via `Xvfb :99` para as janelas JavaFX e testes TestFX renderizarem de verdade).

- `mvn compile` — **BUILD SUCCESS**, 19 arquivos-fonte compilados.
- `mvn test` — **12 testes, 0 falhas, 0 erros**, incluindo:
  - 7 testes de `GerenciadorDeJogoTest` (pontuação, exceções, coleções)
  - 2 testes de `RepositorioTest` (generics)
  - 2 testes de `PersistenciaSmokeTest`, que criam e consultam `jogo.db`/`fases.db` reais via SQLite/JDBC
  - 1 teste de `GerenciadorFasesUiTest`, rodando via TestFX + `ApplicationTest`, clicando de verdade nos campos da tela `fase.fxml` (`clickOn`, `.write(...)`) e conferindo que a linha aparece na `TableView`
- `mvn exec:java` (Main, o jogo) rodou por 8 segundos sob Xvfb sem exceções.
- `mvn exec:java` do `GerenciadorFasesApp` idem, e a consulta `sqlite3 estante... ` (ver `projeto-final`) e `sqlite3 jogo.db` / `fases.db` confirmou linhas reais gravadas pelos testes.
- `mvn package` gerou `target/javafx-game-1.0.0.jar` com sucesso.

Um problema real foi encontrado e corrigido durante a verificação: o `openjfx-monocle` (`jdk-12.0.1+2`), usado por muitos tutoriais antigos para rodar TestFX "headless", **não é compatível com JavaFX 21** (`AbstractMethodError` em `MonocleWindow._updateViewSize`). A correção foi trocar a estratégia de headless: em vez de Monocle, os testes de UI rodam sob um **X virtual real** (`Xvfb`), que é a abordagem que o próprio TestFX recomenda para CI em ambientes Linux modernos sem GPU. O `pom.xml` e o texto do tutorial refletem essa escolha.
