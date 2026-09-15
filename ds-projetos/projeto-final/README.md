# projeto-final — "Minha Estante" (referência do Projeto Final de `Tutorial_DS.md`)

Exemplo completo do Projeto Final descrito nos tópicos "Projeto Final: Início" e "Apresentações" do `Tutorial_DS.md`: um gerenciador pessoal de livros e filmes, cobrindo os 5 requisitos mínimos do projeto.

| Requisito mínimo | Onde está |
|---|---|
| Herança + polimorfismo | `ItemDeAcervo` (abstrata) ← `Livro`, `Filme`; `getCategoria()` sobrescrito em cada subtipo |
| DAO + SQLite | `dao.ItemDAO` / `dao.ItemDAOSQLite`, gravando em `estante.db` |
| JavaFX TableView + MVC | `estante.fxml` (View) + `EstanteController` (Controller) + `GerenciadorDeAcervo` (Model, sem `import javafx.*`) |
| Coleção não trivial | `Map<String, Long>` em `GerenciadorDeAcervo.contarPorCategoria()` |
| ≥ 3 testes JUnit | `GerenciadorDeAcervoTest` (5 testes) + `PersistenciaTest` (1 teste real contra SQLite) |

## Como rodar

```bash
mvn compile
mvn exec:java     # abre a tela "Minha Estante"
mvn test
mvn package
```

## O que foi verificado de verdade

- `mvn test` — **6 testes, 0 falhas, 0 erros** (JDK 21.0.12, Maven 3.9.16).
- `mvn exec:java` rodou 8 segundos sob `Xvfb :99` sem exceções; `sqlite3 estante.db "SELECT categoria,titulo,ano FROM item;"` confirmou a linha `Filme|Cidade de Deus|2002` gravada pelo próprio teste `PersistenciaTest` na mesma pasta.
- `mvn package` gerou `target/minha-estante-1.0.0.jar` com sucesso.

Nenhum stub: todos os métodos de `ItemDAOSQLite`, `GerenciadorDeAcervo` e `EstanteController` têm implementação completa e foram exercitados pelos testes acima ou pela execução manual da tela.
