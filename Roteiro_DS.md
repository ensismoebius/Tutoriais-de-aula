# Apostila — Desenvolvimento de Sistemas (DS)

Linguagem de referência: Java.

### 1. POO Avançada (construtores, static)

Construtores inicializam o estado de um objeto no momento da criação; podem ser sobrecarregados (vários construtores com assinaturas diferentes).

```java
public class Produto {
    private String nome;
    private double preco;
    private static int totalProdutos = 0; // compartilhado por todas as instâncias

    public Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
        totalProdutos++;
    }

    public Produto(String nome) {
        this(nome, 0.0); // encadeamento de construtores
    }

    public static int getTotalProdutos() {
        return totalProdutos;
    }
}
```

- `static` pertence à classe, não à instância — é compartilhado por todos os objetos.
- Métodos `static` não podem acessar atributos de instância diretamente (não existe `this`).

**Exercício:** Crie uma classe `ContaBancaria` com um contador `static` de contas criadas e um construtor que exige nome do titular e saldo inicial (mínimo zero).

### 2. Polimorfismo (classes abstratas)

Uma classe abstrata não pode ser instanciada diretamente e pode conter métodos abstratos (sem corpo, implementados pelas subclasses) e métodos concretos (com comportamento padrão).

```java
public abstract class FormaGeometrica {
    public abstract double calcularArea();

    public void imprimirArea() {
        System.out.println("Área: " + calcularArea());
    }
}

public class Circulo extends FormaGeometrica {
    private double raio;
    public Circulo(double raio) { this.raio = raio; }

    @Override
    public double calcularArea() {
        return Math.PI * raio * raio;
    }
}

public class Quadrado extends FormaGeometrica {
    private double lado;
    public Quadrado(double lado) { this.lado = lado; }

    @Override
    public double calcularArea() {
        return lado * lado;
    }
}
```

Polimorfismo permite tratar objetos de subclasses diferentes de forma uniforme através do tipo da superclasse:
```java
List<FormaGeometrica> formas = List.of(new Circulo(2), new Quadrado(3));
for (FormaGeometrica f : formas) f.imprimirArea();
```

**Exercício:** Modele `Funcionario` (abstrata) com o método abstrato `calcularSalario()`, e subclasses `FuncionarioCLT` e `FuncionarioPJ` com regras diferentes.

### 3. Interfaces

Uma interface define um contrato (métodos que a classe implementadora deve fornecer), sem carregar estado próprio (além de constantes e, desde o Java 8, métodos `default`/`static`).

```java
public interface Pagavel {
    double calcularValorPagamento();

    default void exibirPagamento() {
        System.out.println("Valor a pagar: " + calcularValorPagamento());
    }
}

public class Pedido implements Pagavel {
    private double total;
    @Override
    public double calcularValorPagamento() { return total; }
}
```

**Diferença interface vs. classe abstrata:** uma classe pode implementar várias interfaces, mas só pode estender uma classe (abstrata ou não). Interfaces definem "o que" a classe faz; herança define "o que" a classe é.

**Exercício:** Crie a interface `Notificavel` com o método `enviarNotificacao(String msg)` e implemente-a em `NotificacaoEmail` e `NotificacaoSMS`.

### 4. Persistência com DAO

DAO (Data Access Object) é um padrão que isola a lógica de acesso a dados (SQL, arquivo, etc.) do restante da aplicação, expondo uma interface simples (`salvar`, `buscarPorId`, `listarTodos`, `atualizar`, `remover`).

```java
public interface AlunoDAO {
    void salvar(Aluno aluno);
    Aluno buscarPorId(int id);
    List<Aluno> listarTodos();
    void atualizar(Aluno aluno);
    void remover(int id);
}

public class AlunoDAOJdbc implements AlunoDAO {
    private Connection conexao;

    @Override
    public void salvar(Aluno aluno) {
        String sql = "INSERT INTO aluno (nome, data_nascimento) VALUES (?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, aluno.getNome());
            ps.setDate(2, Date.valueOf(aluno.getDataNascimento()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // demais métodos...
}
```

**Exercício:** Implemente `ProdutoDAO` com todos os métodos do CRUD usando `PreparedStatement` (nunca concatenar SQL diretamente — risco de SQL Injection).

### 5. CRUD com Interface + DAO

Une os dois tópicos anteriores: a camada de serviço/negócio depende apenas da **interface** DAO, não da implementação concreta — isso permite trocar a fonte de dados (JDBC, arquivo, mock para testes) sem alterar quem usa o DAO.

```java
public class AlunoService {
    private final AlunoDAO dao; // depende da interface, não da implementação

    public AlunoService(AlunoDAO dao) { this.dao = dao; }

    public void matricular(Aluno aluno) {
        if (aluno.getNome() == null || aluno.getNome().isBlank())
            throw new IllegalArgumentException("Nome obrigatório");
        dao.salvar(aluno);
    }
}
```

**Exercício:** Monte um pequeno CRUD de console para `Produto`: menu com opções cadastrar/listar/atualizar/remover, chamando `ProdutoService` → `ProdutoDAO`.

### 6. Coleções (List, Set, Map)

- `List` — coleção ordenada, permite duplicados (`ArrayList`, `LinkedList`).
- `Set` — não permite duplicados (`HashSet`, `TreeSet` — ordenado).
- `Map` — pares chave-valor, chaves únicas (`HashMap`, `TreeMap`).

```java
List<String> nomes = new ArrayList<>();
nomes.add("Ana");
nomes.add("Ana"); // permitido, duplicado

Set<String> emails = new HashSet<>();
emails.add("a@x.com");
emails.add("a@x.com"); // ignorado, já existe

Map<Integer, String> alunosPorId = new HashMap<>();
alunosPorId.put(1, "Ana");
alunosPorId.put(2, "Bruno");
System.out.println(alunosPorId.get(1)); // "Ana"
```

**Exercício:** Dada uma `List<Produto>`, use um `Map<String, List<Produto>>` para agrupar produtos por categoria.

### 7. Generics + Tratamento de Exceções Avançado

**Generics** permitem escrever classes/métodos parametrizados por tipo, com verificação em tempo de compilação:
```java
public class Caixa<T> {
    private T conteudo;
    public void guardar(T item) { this.conteudo = item; }
    public T pegar() { return conteudo; }
}

Caixa<String> caixaTexto = new Caixa<>();
caixaTexto.guardar("Olá");
```

**Exceções customizadas e encadeamento:**
```java
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String msg) { super(msg); }
}

public void sacar(double valor) {
    if (valor > saldo) throw new SaldoInsuficienteException("Saldo insuficiente para saque de " + valor);
    saldo -= valor;
}

try {
    sacar(1000);
} catch (SaldoInsuficienteException e) {
    System.err.println(e.getMessage());
} finally {
    System.out.println("Operação finalizada");
}
```

- Exceções `checked` (ex.: `IOException`) devem ser tratadas ou declaradas com `throws`.
- Exceções `unchecked` (`RuntimeException`) não exigem tratamento obrigatório, mas devem ser usadas para erros de programação/regra de negócio.

**Exercício:** Crie uma classe genérica `Repositorio<T>` com uma lista interna, e uma exceção `ItemNaoEncontradoException` lançada quando `buscarPorId` não encontra o item.

### 8. Manipulação de Arquivos + Streams

**Arquivos** (pacote `java.nio.file`):
```java
Path caminho = Path.of("dados.txt");
Files.writeString(caminho, "Linha 1\nLinha 2\n");
List<String> linhas = Files.readAllLines(caminho);
```

**Streams** (pacote `java.util.stream`) processam coleções de forma declarativa:
```java
List<Produto> produtos = service.listarTodos();

double totalCaro = produtos.stream()
    .filter(p -> p.getPreco() > 100)
    .mapToDouble(Produto::getPreco)
    .sum();

List<String> nomes = produtos.stream()
    .map(Produto::getNome)
    .sorted()
    .collect(Collectors.toList());
```

**Exercício:** Leia um arquivo CSV de produtos (`nome;preco;categoria`), converta cada linha em objeto `Produto` e use Streams para calcular o preço médio por categoria.

### 9. Padrão MVC em Desktop

MVC separa a aplicação em três camadas:
- **Model** — dados e regras de negócio (entidades, DAO, service).
- **View** — interface gráfica (Swing/JavaFX), apenas exibe dados e captura eventos.
- **Controller** — recebe eventos da View, aciona o Model, atualiza a View.

```
[View: tela Swing] --evento--> [Controller] --chama--> [Model/Service]
      ^                                                      |
      -------------------- atualiza --------------------------
```

**Exercício:** Desenhe o diagrama de classes MVC para uma tela de cadastro de alunos (JFrame + JTable), indicando quais classes pertencem a cada camada.

### 10. Implementação do MVC

```java
// Model
public class AlunoService { /* ... como já visto ... */ }

// Controller
public class AlunoController {
    private final AlunoService service;
    private final TelaAluno view;

    public AlunoController(AlunoService service, TelaAluno view) {
        this.service = service;
        this.view = view;
        view.getBtnSalvar().addActionListener(e -> salvar());
    }

    private void salvar() {
        Aluno aluno = new Aluno(view.getNomeDigitado(), view.getDataNascimentoDigitada());
        service.matricular(aluno);
        view.atualizarTabela(service.listarTodos());
    }
}
```

**Exercício:** Implemente o Controller completo de uma tela de CRUD de produtos, ligando os botões da View (Swing) às chamadas do Service.

### 11. Padrões de Projeto (Singleton, Factory)

**Singleton** — garante uma única instância de uma classe (ex.: conexão com banco):
```java
public class ConexaoBanco {
    private static ConexaoBanco instancia;
    private Connection conn;

    private ConexaoBanco() { /* abre conexão */ }

    public static ConexaoBanco getInstancia() {
        if (instancia == null) instancia = new ConexaoBanco();
        return instancia;
    }
}
```

**Factory** — centraliza a lógica de criação de objetos, desacoplando o código cliente da classe concreta:
```java
public class DAOFactory {
    public static AlunoDAO criarAlunoDAO() {
        return new AlunoDAOJdbc(ConexaoBanco.getInstancia().getConn());
    }
}
```

**Exercício:** Implemente uma `NotificacaoFactory` que, a partir de um tipo (`"EMAIL"`, `"SMS"`), retorne a implementação correta de `Notificavel` (visto no tópico de Interfaces).

### 12. Debug e Logging

- **Debug**: uso de breakpoints, step-over/step-into e inspeção de variáveis na IDE para rastrear a execução linha a linha e encontrar a causa raiz de um bug.
- **Logging**: registro estruturado de eventos da aplicação (em vez de `System.out.println`), com níveis de severidade.

```java
private static final Logger log = LoggerFactory.getLogger(AlunoService.class);

public void matricular(Aluno aluno) {
    log.info("Matriculando aluno: {}", aluno.getNome());
    try {
        dao.salvar(aluno);
    } catch (Exception e) {
        log.error("Erro ao matricular aluno {}", aluno.getNome(), e);
        throw e;
    }
}
```

Níveis comuns: `TRACE < DEBUG < INFO < WARN < ERROR`. Em produção normalmente só `INFO` para cima fica ativo.

**Exercício:** Adicione logging (`INFO` em operações normais, `ERROR` em falhas) a todo o CRUD de `ProdutoService`, e use o debugger para investigar um `NullPointerException` proposital.

### 13. Testes Unitários (JUnit)

Testes unitários verificam automaticamente se uma unidade de código (normalmente um método) se comporta como esperado, isoladamente.

```java
class ContaBancariaTest {

    @Test
    void deveSacarQuandoSaldoSuficiente() {
        ContaBancaria conta = new ContaBancaria("Ana", 100.0);
        conta.sacar(40.0);
        assertEquals(60.0, conta.getSaldo());
    }

    @Test
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        ContaBancaria conta = new ContaBancaria("Ana", 10.0);
        assertThrows(SaldoInsuficienteException.class, () -> conta.sacar(100.0));
    }
}
```

**Boas práticas:**
- Um teste deve verificar **um** comportamento (nome descritivo: `deveXQuandoY`).
- Testes devem ser independentes entre si e repetíveis (sem depender de banco real — usar dublês/mocks para o DAO).
- Padrão AAA: **A**rrange (preparar), **A**ct (executar), **A**ssert (verificar).

**Exercício:** Escreva testes JUnit para `ProdutoService.matricular` (ou equivalente), cobrindo o caso de sucesso e o caso de nome inválido (deve lançar `IllegalArgumentException`).

### 14. Projeto Final — Início

Aplicação de todo o conteúdo do semestre em um sistema desktop completo:
1. Modelagem das classes de domínio (POO, herança/interfaces onde fizer sentido).
2. Camada DAO com persistência real (JDBC).
3. Camada Service com regras de negócio e tratamento de exceções customizadas.
4. Interface gráfica em MVC (Swing/JavaFX).
5. Uso de pelo menos um padrão de projeto (Singleton para conexão, Factory para criação de objetos).
6. Logging das operações principais e testes unitários da camada de serviço.

### 15. Projeto Final — Apresentações

Checklist para a apresentação:
- Diagrama de classes (Model) e diagrama de camadas (MVC).
- Demonstração ao vivo do CRUD completo.
- Justificativa dos padrões de projeto escolhidos.
- Evidência de tratamento de exceções (cenário de erro provocado na demo).
- Suíte de testes JUnit executada ao vivo, com pelo menos um caso de sucesso e um de falha.
- Trecho de log mostrando uma operação bem-sucedida e uma com falha.
