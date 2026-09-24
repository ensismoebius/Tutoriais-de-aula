# Tutorial — Desenvolvimento de Sistemas (DS)

Linguagem e stack de referência: Java 21 LTS com JavaFX 21 para interfaces desktop, Maven como ferramenta de build, SQLite via JDBC para persistência, SLF4J/Logback para log e JUnit 5 (com TestFX para telas) para testes. O projeto construído ao longo da maior parte deste material é o `javafx-game`: um jogo 2D simples, com quadrados e círculos caindo por gravidade, que evolui — aula a aula — para ganhar persistência, uma tela de CRUD, coleções, tratamento de exceções, arquivos, uma arquitetura MVC de verdade, padrões de projeto, logging e testes automatizados. Na reta final, esse conhecimento converge num Projeto Final de tema livre. Depois dele, o tutorial muda de projeto por completo para um mini-projeto separado e independente: construir e testar um robô de combate no motor **Robocode**.

Convenção de nomes: tudo que é criado neste tutorial (classes, métodos, variáveis, nomes de arquivo) é nomeado em português — `Quadrado`, `calcularPontuacaoTotal`, `gerenciadorDeJogo`. Tudo que pertence à linguagem Java, ao JavaFX, ao JDBC, ao Robocode ou a uma biblioteca externa mantém o nome original em inglês, exatamente como a API define — `AnimationTimer`, `PreparedStatement`, `AdvancedRobot`, `@Test`. Essa distinção ajuda a enxergar de relance o que é seu para editar e o que é contrato de terceiros para copiar exatamente como está.

## Sumário

- [O básico, para quem está começando agora](#o-básico-para-quem-está-começando-agora)
- [Conceitos fundamentais antes de começar](#conceitos-fundamentais-antes-de-começar)
- [1. POO Avançada (construtores, static) — o jogo JavaFX](#1-poo-avançada-construtores-static--o-jogo-javafx)
- [2. Persistência com DAO (JavaFX + SQLite)](#2-persistência-com-dao-javafx--sqlite)
- [3. CRUD com Interface + DAO (JavaFX TableView e FXML)](#3-crud-com-interface--dao-javafx-tableview-e-fxml)
- [4. Coleções (List, Set, Map)](#4-coleções-list-set-map)
- [5. Generics + Tratamento de Exceções Avançado](#5-generics--tratamento-de-exceções-avançado)
- [6. Manipulação de Arquivos + Streams](#6-manipulação-de-arquivos--streams)
- [7. Padrão MVC em Desktop — Planejamento](#7-padrão-mvc-em-desktop--planejamento)
- [8. Implementação do MVC](#8-implementação-do-mvc)
- [9. Padrões de Projeto (Singleton, Factory)](#9-padrões-de-projeto-singleton-factory)
- [10. Debug e Logging](#10-debug-e-logging)
- [11. Testes Unitários (JUnit + TestFX)](#11-testes-unitários-junit--testfx)
- [12. Projeto Final](#12-projeto-final)
- [Parte 2 — Robocode: um mini-projeto separado](#parte-2--robocode-um-mini-projeto-separado)
  - [13. Robocode: API — Movimento, tiros e sensoriamento](#13-robocode-api--movimento-tiros-e-sensoriamento)
  - [14. Robocode: de Robot para AdvancedRobot](#14-robocode-de-robot-para-advancedrobot)
  - [15. Robocode: Mira preditiva (Targeting)](#15-robocode-mira-preditiva-targeting)
  - [16. Robocode: Wave Surfing (movimento evasivo adaptativo)](#16-robocode-wave-surfing-movimento-evasivo-adaptativo)
  - [17. Robocode: Refinamento e testes sistemáticos](#17-robocode-refinamento-e-testes-sistemáticos)
  - [18. Robocode: Finalização e empacotamento](#18-robocode-finalização-e-empacotamento)
  - [19. Robocode: Campeonato](#19-robocode-campeonato)

Cada tópico é um tutorial *build-along*: execute cada passo no seu computador e confira o resultado antes de prosseguir. Cada seção termina com um **✅ Checkpoint** — pare e confirme que o resultado bate antes de continuar.

## O básico, para quem está começando agora

Esta seção existe para quem nunca programou em Java antes, ou só viu linguagens interpretadas (como PHP ou JavaScript) até aqui. Se os termos abaixo já são familiares, pule direto para "Conceitos fundamentais antes de começar".

**Linguagem compilada vs. interpretada.** Uma linguagem interpretada (PHP, JavaScript/Node, Python) roda seu código-fonte quase diretamente: um programa chamado interpretador lê o arquivo `.php` ou `.js` linha a linha e vai executando, traduzindo para instruções de máquina "na hora". Java funciona diferente: antes de rodar, todo arquivo `.java` precisa passar por um **compilador** (`javac`), que traduz o código inteiro para um formato intermediário chamado **bytecode**, guardado em arquivos `.class`. Só depois disso o programa roda de fato, através do comando `java`. Essa etapa extra tem um custo (você precisa recompilar depois de qualquer mudança), mas também uma vantagem: o compilador verifica um monte de erros — tipos incompatíveis, métodos inexistentes, variáveis não inicializadas — *antes* do programa rodar, em vez de você descobrir esses erros em produção, no meio da execução, como costuma acontecer em PHP ou JavaScript.

**O que é a JVM, e o que significa "compile once, run anywhere".** O bytecode gerado pelo `javac` não é instruções nativas de um processador específico (como um `.exe` do Windows seria) — é um formato próprio da linguagem, pensado para rodar dentro de uma **JVM** (*Java Virtual Machine*, Máquina Virtual Java): um programa que lê bytecode e o executa, traduzindo para instruções nativas da máquina onde está rodando, na hora. Isso é o que torna Java portável: o mesmo arquivo `.class`, compilado uma única vez, roda sem alteração em Windows, Linux ou macOS, desde que cada um tenha uma JVM instalada — daí o lema histórico "compile once, run anywhere" (compile uma vez, rode em qualquer lugar). O conjunto JVM + bibliotecas padrão + ferramentas de linha de comando (`javac`, `java`) forma o **JDK** (*Java Development Kit*), que é o que você instala para desenvolver em Java. Este tutorial assume o **JDK 21** (uma versão LTS — *Long-Term Support*, com suporte estendido — a mais recente disponível no início deste material).

**O que é uma classe, e o que é um arquivo `.java`.** Em Java, todo código vive dentro de uma **classe** — não existe função solta fora de uma classe, como existiria em JavaScript ou Python. Por convenção (e, para classes públicas, por exigência do compilador), cada arquivo `.java` contém exatamente uma classe pública, e o nome do arquivo precisa bater exatamente com o nome dela: a classe `public class Quadrado` só compila se estiver salva em um arquivo chamado `Quadrado.java`. Você vai ver isso já no primeiro tópico.

**Pacotes (`package`), e por que a pasta importa.** Java organiza classes em **pacotes** (*packages*) — um jeito de agrupar classes relacionadas e evitar colisão de nomes entre bibliotecas diferentes. Diferente de outras linguagens, em Java essa organização não é só lógica: o Java **exige** que o caminho de pastas do arquivo espelhe o nome do pacote declarado na primeira linha do código. Uma classe com `package jogo.model;` precisa estar salva em um arquivo dentro de uma pasta `jogo/model/`, senão o compilador recusa. Você vai sentir isso na prática já no PASSO 1 do primeiro tópico.

**O terminal, e o que significa "rodar um comando".** Assim como em qualquer stack de desenvolvimento, boa parte deste tutorial pede para você digitar comandos num terminal (ou linha de comando) — `javac`, `java`, e mais adiante `mvn`. Toda vez que aparecer uma caixa de código começando com um comando desses, é para você digitar literalmente e confirmar com Enter; a saída aparece como texto logo abaixo.

**O papel de uma IDE.** Um editor de texto comum (mesmo um bom, como o VS Code sem plugins) já é suficiente para escrever Java, mas uma **IDE** (*Integrated Development Environment* — ambiente de desenvolvimento integrado, como IntelliJ IDEA ou Eclipse) soma três coisas que tornam o trabalho muito mais rápido: autocompletar ciente de tipos (ela sabe que `Quadrado` tem um método `atualizar()` e sugere isso enquanto você digita), compilação incremental automática (você não digita `javac` manualmente a cada mudança) e, o que mais importa a partir da metade deste tutorial, um **depurador** (*debugger*) integrado — uma ferramenta para pausar seu programa no meio da execução e inspecionar variáveis, usada de verdade no tópico de Debug e Logging. Você pode acompanhar todo este tutorial digitando os arquivos manualmente e compilando pelo terminal, mas a partir do Tópico 10 uma IDE deixa de ser conveniência e passa a ser necessária.

**✅ Checkpoint:** você sabe explicar, com suas próprias palavras, a diferença entre compilar e interpretar, o que é a JVM, e por que a pasta de um arquivo Java precisa bater com o pacote declarado nele.

## Conceitos fundamentais antes de começar

Com o básico alinhado, vale aprofundar seis ideias mais específicas deste projeto, que aparecem o tempo todo ao longo do material. Elas são intencionalmente curtas aqui — cada uma é retomada e aprofundada no tópico onde passa a importar de verdade.

**O que é POO (Programação Orientada a Objetos), em resumo.** POO é um jeito de organizar código em torno de **objetos**: unidades que combinam dados (**atributos**) e comportamento (**métodos**) relacionados. Uma **classe** é o molde — a definição de que atributos e métodos um tipo de objeto tem; um **objeto** (ou *instância*) é uma ocorrência concreta desse molde, criada com a palavra-chave `new`. `Quadrado` é uma classe; `new Quadrado(100, 100, 30)` cria um objeto específico, com sua própria posição. **Encapsulamento** é o princípio de esconder os detalhes internos de um objeto atrás de uma interface pública controlada (métodos), em vez de deixar qualquer código de fora mexer diretamente nos seus atributos — é por isso que você vai ver `private` e métodos `getX()`/`setX()` em vez de atributos públicos soltos. Este tutorial assume que você já viu os fundamentos de classes e objetos antes (o 1º semestre do curso cobre Interfaces e Polimorfismo com classes abstratas), mas revisa cada ideia no ponto em que ela é usada, para que nada fique subentendido.

**O que é Maven, e por que Java precisa de uma ferramenta de build.** Um projeto Java raramente usa só a biblioteca padrão — ele depende de bibliotecas de terceiros (como o driver JDBC do SQLite, ou o JUnit), cada uma com sua própria versão e suas próprias dependências. Baixar cada `.jar` manualmente e organizá-los no *classpath* (a lista de lugares onde o Java procura classes) seria tão inviável quanto baixar `.js` na mão em vez de usar o `npm`, ou instalar pacotes PHP sem o Composer — se você já viu uma dessas ferramentas antes, o papel do **Maven** aqui é exatamente esse: ler um arquivo de configuração (`pom.xml`, em formato XML), baixar automaticamente as dependências listadas nele (de um repositório público, o Maven Central) e compilar, testar e empacotar o projeto com comandos padronizados (`mvn compile`, `mvn test`, `mvn package`). Sem uma ferramenta dessas, começar até um projeto JavaFX simples já seria um exercício manual de baixar `.jar` de módulos.

**O que é JavaFX, e por que ele não "vem" com o Java.** JavaFX é um **toolkit gráfico**: um conjunto de classes para desenhar janelas, botões, tabelas e formas na tela — não é parte da linguagem Java em si, é uma biblioteca, no mesmo sentido em que o Express é uma biblioteca sobre Node.js. Até o Java 8, o JavaFX vinha embutido no JDK; a partir do **Java 11**, ele foi desacoplado e passa a ser uma dependência comum, baixada pelo Maven como qualquer outra biblioteca — é por isso que o PASSO 1 do Tópico 1 existe: sem declarar o JavaFX como dependência, `import javafx.application.Application;` simplesmente não compila, mesmo com o JDK mais recente instalado.

**MVC numa aplicação desktop, comparado a MVC numa aplicação web.** Se você já viu MVC (*Model-View-Controller*) num framework web, o princípio central é o mesmo aqui: **Model** guarda os dados e as regras de negócio; **View** decide como mostrar algo na tela; **Controller** recebe uma ação do usuário, decide o que fazer, conversa com o Model, e atualiza a View. A diferença prática é o gatilho que aciona o Controller: numa aplicação web, é uma requisição HTTP chegando pela rede; numa aplicação JavaFX, é um evento de interface — um clique de botão, uma tecla pressionada, uma seleção numa tabela. No JavaFX, a View costuma ser escrita em **FXML** (um formato XML que descreve a estrutura visual de uma tela, separado do código Java que reage aos eventos dela) — você vai construir a primeira tela FXML deste tutorial no Tópico 3.

**O que é um padrão de projeto (design pattern), em geral.** Um padrão de projeto é uma solução reconhecida e batizada para um problema recorrente de organização de código — não é uma biblioteca que você importa, é uma *forma* de estruturar classes que programadores foram batizando conforme reconheciam o mesmo problema se repetindo em contextos diferentes. "Preciso garantir que só existe uma instância disso no sistema inteiro" tem uma resposta padrão chamada **Singleton**; "preciso centralizar a lógica de decidir qual classe concreta criar" tem uma resposta padrão chamada **Factory**. Você vai construir os dois no Tópico 9, depois de já ter sentido na prática o problema que cada um resolve.

**O que é JDBC, comparado a um ORM.** Se você já usou um ORM (*Object-Relational Mapper*, como o Prisma ou o Eloquent) em outra disciplina, ele traduz automaticamente entre objetos do código e linhas de tabela — você chama algo como `produto.save()` e o ORM monta o SQL sozinho. **JDBC** (*Java Database Connectivity*) é a camada por baixo disso: a API padrão do Java para conversar com bancos relacionais, mas em um nível bem mais baixo — você mesmo escreve o SQL, abre a conexão, prepara a consulta, e lê o resultado campo a campo. Não existe tradução automática de objeto para tabela: esse trabalho manual é exatamente o que o padrão **DAO** (*Data Access Object*, construído no Tópico 2) organiza, isolando o SQL bruto do resto do código.

**✅ Checkpoint:** você sabe explicar, com suas próprias palavras, o que o Maven resolve, por que o JavaFX precisa ser declarado como dependência, e a diferença entre um ORM (que você talvez já tenha visto) e o JDBC.

## 1. POO Avançada (construtores, static) — o jogo JavaFX

**Objetivo:** partir de um diretório vazio e chegar a um pequeno jogo JavaFX com gravidade, onde cada conceito de POO (construtores, sobrecarga, `this()`, `static`) aparece resolvendo um problema concreto do jogo.

Você não precisa ter feito nenhuma aula anterior de Java: tudo que o jogo usa é construído aqui, do zero.

### Pré-requisitos

```bash
java --version    # Deve mostrar 21 ou superior
mvn --version     # Deve mostrar 3.9 ou superior
```

Um editor de código (IntelliJ IDEA, VS Code com extensão Java, ou Eclipse). **✅ Checkpoint:** os dois comandos retornam versões sem erro.

### PASSO 1: crie o projeto Maven com JavaFX

Crie a estrutura de pastas. Como visto na seção "O básico", o Java exige que o caminho das pastas espelhe o nome do pacote — o projeto vai usar o pacote `jogo`, então a pasta é `jogo`:

```bash
mkdir -p javafx-game/src/main/java/jogo
cd javafx-game
```

Crie o arquivo `pom.xml` na raiz do projeto. Ele diz ao Maven quais dependências baixar e como compilar:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>br.edu.mtec.ds</groupId>
  <artifactId>javafx-game</artifactId>
  <version>1.0.0</version>

  <properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <javafx.version>21.0.4</javafx.version>
  </properties>
</project>
```

- `groupId`/`artifactId`/`version` → identificam o projeto de forma única (mais relevante quando um projeto depende de outro; aqui é só um nome).
- `maven.compiler.source`/`target` → dizem ao compilador "trate o código como Java 21" e "gere bytecode compatível com Java 21".
- `javafx.version` → uma propriedade própria, para não repetir o número "21.0.4" em cada dependência do JavaFX — você vai usá-la já a seguir.

Agora adicione as dependências do JavaFX dentro de um bloco `<dependencies>`:

```xml
  <dependencies>
    <dependency>
      <groupId>org.openjfx</groupId>
      <artifactId>javafx-controls</artifactId>
      <version>${javafx.version}</version>
    </dependency>
    <dependency>
      <groupId>org.openjfx</groupId>
      <artifactId>javafx-fxml</artifactId>
      <version>${javafx.version}</version>
    </dependency>
  </dependencies>
```

`javafx-graphics` (que traz `Stage`, `Scene` e as formas geométricas) é baixado automaticamente como dependência de `javafx-controls` — não precisa ser declarado à parte. `javafx-controls` traz os componentes de interface (botões, tabelas, campos de texto); `javafx-fxml` só entra em uso a partir do Tópico 3, mas já vale declarar agora.

Por fim, adicione o plugin que permite rodar a aplicação com `mvn exec:java`, dentro de um bloco `<build><plugins>`:

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>3.5.0</version>
        <configuration>
          <mainClass>jogo.Main</mainClass>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

`mainClass` → o caminho completo (`pacote.Classe`) da classe que tem o `main`. Você ainda não criou essa classe — ela vem no PASSO 7.

> Nota sobre Maven vs. Gradle: o material de referência desta disciplina menciona as duas ferramentas em pontos diferentes — Gradle no bootstrap inicial, Maven a partir da persistência com JDBC. As duas resolvem o mesmo problema (build automatizado) e ambas suportam JavaFX + SQLite + JUnit sem dificuldade; este tutorial usa **Maven do início ao fim** por consistência, porque é o que os tópicos de persistência, testes e o Projeto Final já assumem.

**✅ Checkpoint:** rodar `mvn compile` (mesmo sem nenhuma classe ainda) baixa as dependências do JavaFX sem erro.

### PASSO 2: entenda o que é um construtor

Construtores são métodos especiais executados automaticamente quando um objeto é criado com `new`. Eles têm duas características únicas: têm o mesmo nome da classe, e não declaram tipo de retorno (nem mesmo `void`).

Crie `src/main/java/jogo/model/Quadrado.java`. Repare que o pacote agora é `jogo.model` — a partir daqui, o projeto separa `model` (dados e regras) do resto, preparando o terreno para o MVC que vem nos Tópicos 7 e 8. Primeiro só os atributos:

```java
package jogo.model;

public class Quadrado {
    double x, y;        // posição na tela
    double vx, vy;      // velocidade (quanto anda por quadro)
    double tamanho;     // lado do quadrado, em pixels
}
```

Esses são os **atributos de instância**: cada quadrado criado terá sua própria cópia de `x`, `y`, `vx`, `vy` e `tamanho`. Mover um quadrado não mexe nos outros.

Agora o construtor:

```java
    // Construtor principal
    public Quadrado(double x, double y, double tamanho) {
        this.x = x;
        this.y = y;
        this.tamanho = tamanho;
        this.vx = 0;
        this.vy = 0;
    }
```

`public Quadrado(...)` tem o mesmo nome da classe e nenhum tipo de retorno: é um construtor, não um método comum. A linha `this.x = x;` existe por causa de um problema de nomes: o parâmetro se chama `x`, e o atributo também. Dentro do construtor, `x` sozinho se refere ao *parâmetro* (o mais próximo); `this.x` se refere ao *atributo do objeto*. Sem `this`, a linha `x = x` atribuiria o parâmetro a ele mesmo, e o atributo ficaria em zero — um bug clássico e silencioso. Já `this.vx = 0; this.vy = 0;` nascem parados de propósito: quem cria um quadrado normalmente quer posicioná-lo, não lançá-lo.

O papel do construtor é garantir que nenhum objeto exista em estado inválido: ao terminar o `new`, todos os campos já têm valor coerente.

**✅ Checkpoint:** `new Quadrado(100, 100, 30)` cria um quadrado na posição (100,100) com tamanho 30.

### PASSO 3: sobrecarga de construtores

O construtor do PASSO 2 sempre exige três argumentos. Mas há situações diferentes no jogo: um quadrado de teste no centro da tela não precisa de nada informado; um quadrado onde o usuário clicou precisa de posição e tamanho; um quadrado lançado com impulso precisa também de velocidade.

**Sobrecarga** (*overloading*) é declarar vários construtores com o mesmo nome, diferenciados pela lista de parâmetros:

```java
public class Quadrado {
    double x, y, vx, vy, tamanho;

    // Construtor completo
    public Quadrado(double x, double y, double tamanho, double vx, double vy) {
        this.x = x; this.y = y; this.tamanho = tamanho;
        this.vx = vx; this.vy = vy;
    }
```

Ainda faltam os construtores mais curtos — eles vêm no próximo passo, porque merecem uma ideia própria. Por ora, repare como o compilador escolhe qual construtor executar: pela quantidade e tipo dos argumentos passados no `new`, o que se chama *assinatura* do construtor.

```java
new Quadrado(100, 100, 30, 5, 0)      // → 5 argumentos → construtor completo
```

> ⚠️ Armadilha — o construtor padrão desaparece. Se você não escrever nenhum construtor, o Java cria um vazio automaticamente (`new Quadrado()` funciona). Mas assim que você escreve qualquer construtor, esse brinde some — `new Quadrado()` deixa de compilar até você escrever, você mesmo, um construtor sem argumentos.

**✅ Checkpoint:** `new Quadrado(100, 100, 30, 5, 0)` cria um quadrado com velocidade inicial.

### PASSO 4: use `this()` para encadear construtores

Em vez de repetir a atribuição dos cinco campos em cada construtor menor, um construtor pode chamar outro construtor da mesma classe. Complete `Quadrado` com os dois construtores que faltavam:

```java
    // Construtor padrão (centro da tela, tamanho 30)
    public Quadrado() {
        this(400, 300, 30, 0, 0);
    }

    // Construtor só posição e tamanho
    public Quadrado(double x, double y, double tamanho) {
        this(x, y, tamanho, 0, 0);
    }
}
```

`this(...)` — com parênteses e argumentos — significa "chame outro construtor desta mesma classe". Não confunda com `this.x`, visto no PASSO 2, que acessa um atributo. O padrão resultante tem um nome: **construtor telescópico**. Um único construtor faz o trabalho real; todos os outros são atalhos que delegam a ele preenchendo valores padrão.

Por que isso importa? Manutenção. Suponha que amanhã você acrescente um campo `cor`. Sem `this()`, seria preciso lembrar de inicializá-lo nos três construtores — esquecer um deles gera um bug que só aparece quando alguém usa aquela variante específica. Com `this()`, você mexe em um lugar só: o construtor completo.

> ⚠️ Armadilha — `this()` tem que ser a primeira linha. O Java exige que a chamada a `this(...)` seja o primeiro comando do construtor. Se você escrever qualquer coisa antes (nem que seja um `System.out.println`), o código não compila. A razão: o objeto precisa estar completamente inicializado antes de qualquer outra lógica rodar.

**✅ Checkpoint:** `new Quadrado()`, `new Quadrado(100,100,30)` e `new Quadrado(100,100,30,5,0)` compilam e criam quadrados coerentes — mudar a inicialização padrão exige alterar só o construtor completo.

### PASSO 5: entenda membros `static` — gravidade compartilhada

Até agora, todo atributo pertencia a um objeto. Mas pense na gravidade do jogo: ela é a mesma para todos os quadrados. Guardar uma cópia dela dentro de cada objeto seria desperdício — e pior, permitiria que dois quadrados tivessem gravidades diferentes, o que não faz sentido físico.

Membros `static` pertencem à classe, não à instância. Adicione essa linha a `Quadrado`:

```java
public class Quadrado {
    double x, y, vx, vy, tamanho;

    // GRAVIDADE: static = compartilhada por TODOS os quadrados
    public static double GRAVIDADE = 0.5;
```

Existe uma única variável `GRAVIDADE` na memória, não importa se você criou 1 ou 10.000 quadrados. Agora dê ao quadrado a física que usa essa gravidade — um método `atualizar()`:

```java
    // Aplica gravidade (usa Quadrado.GRAVIDADE ou apenas GRAVIDADE)
    public void atualizar() {
        vy += Quadrado.GRAVIDADE;  // ou apenas GRAVIDADE
        y += vy;
        x += vx;
    }
```

A ordem das três linhas importa: primeiro `vy += GRAVIDADE` acelera — a cada quadro a velocidade vertical aumenta um pouco, fazendo a queda parecer natural; depois `y += vy` move o eixo Y pela velocidade já atualizada (em JavaFX o Y cresce para baixo, então `vy` positivo é queda); por fim `x += vx` faz o mesmo no eixo horizontal, sem aceleração — não há gravidade lateral. Como `GRAVIDADE` é `static`, ela pode ser lida e alterada sem nenhum objeto existir, acessando pela classe:

```java
Quadrado.GRAVIDADE = 1.0;   // acessa pela CLASSE, não por um objeto
```

**✅ Checkpoint:** mudar `Quadrado.GRAVIDADE = 1.0` faria, em tese, todos os quadrados caírem mais rápido — você ainda vai ver isso na prática quando o jogo estiver rodando, no PASSO 7.

### PASSO 6: um contador `static` de quantos quadrados existem

Um segundo uso clássico de `static`: contar quantos objetos já foram criados. Um contador desses não pode ser de instância — cada objeto teria seu próprio contador valendo 1.

```java
    // Contador: quantos quadrados foram criados
    private static int totalQuadrados = 0;
```

`private` porque ninguém de fora deve alterar o contador diretamente; `static` porque há um só contador, compartilhado. O incremento entra no construtor completo — o único caminho por onde todo `new Quadrado(...)` passa, direta ou indiretamente, graças ao `this()` do PASSO 4:

```java
    public Quadrado(double x, double y, double tamanho, double vx, double vy) {
        this.x = x; this.y = y; this.tamanho = tamanho;
        this.vx = vx; this.vy = vy;
        totalQuadrados++;
    }
```

E um método `static` para consultar o valor:

```java
    public static int getTotalQuadrados() {
        return totalQuadrados;
    }
```

O método também é `static` — e isso é coerente: a pergunta "quantos quadrados existem?" é sobre a classe, não sobre um quadrado específico. Por isso ele é chamado sem objeto nenhum: `Quadrado.getTotalQuadrados()`.

> ⚠️ Armadilha — método `static` não enxerga atributo de instância. Um método `static` roda sem nenhum objeto associado, então dentro dele não existe `this`. Tentar acessar `x` ou `tamanho` diretamente ali dá erro de compilação ("non-static variable cannot be referenced from a static context"). A via contrária funciona: um método de instância pode ler membros `static` normalmente, como `atualizar()` já faz com `GRAVIDADE`.

**✅ Checkpoint:** `Quadrado.getTotalQuadrados()` retorna quantos objetos existem — sem criar nenhum quadrado novo para descobrir isso.

### PASSO 7: crie a classe `Circulo`, uma segunda forma

O jogo não vai ter só quadrados. Crie `src/main/java/jogo/model/Circulo.java`, seguindo a mesma estrutura de `Quadrado`, mas sem repetir `GRAVIDADE` nem o contador — por ora, cada classe tem os seus:

```java
package jogo.model;

public class Circulo {
    double x, y, vx, vy, tamanho;
    public static double GRAVIDADE = 0.5;

    public Circulo(double x, double y, double tamanho, double vx, double vy) {
        this.x = x; this.y = y; this.tamanho = tamanho;
        this.vx = vx; this.vy = vy;
    }

    public Circulo(double x, double y, double tamanho) {
        this(x, y, tamanho, 0, 0);
    }

    public void atualizar() {
        vy += GRAVIDADE;
        y += vy;
        x += vx;
    }
}
```

Repare que `Quadrado` e `Circulo` acabaram de duplicar quase todo o código: os mesmos cinco atributos, a mesma física em `atualizar()`. Isso é sinal de que falta uma classe base comum — e é exatamente esse problema que o Tópico 2 resolve, criando uma classe `Entidade` da qual as duas vão herdar. Por ora, deixe a duplicação como está: entender o problema de perto é o que vai tornar a solução do próximo tópico óbvia.

**✅ Checkpoint:** `Circulo` e `Quadrado` compilam como classes independentes.

### PASSO 8: o loop do jogo (JavaFX `AnimationTimer`)

Crie `src/main/java/jogo/Main.java` — fora do pacote `model`, porque esta classe pertence à camada de interface, não de dados. Importações e estrutura da classe:

```java
package jogo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jogo.model.Quadrado;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private List<Quadrado> quadrados = new ArrayList<>();
    private List<Rectangle> views = new ArrayList<>();
    private Pane root = new Pane();
```

`extends Application` → toda aplicação JavaFX herda dessa classe e sobrescreve o método `start()`, que o runtime do JavaFX chama automaticamente. Repare na separação em duas listas, que é o ponto arquitetural deste passo:

| Lista | Contém | Papel |
|---|---|---|
| `quadrados` | objetos `Quadrado` | o modelo: posição, velocidade, física |
| `views` | `Rectangle` do JavaFX | a visão: o que aparece na tela |

O índice liga as duas: `views.get(3)` é o desenho de `quadrados.get(3)`. Essa separação entre "os dados" e "o desenho" é a base do padrão MVC que os Tópicos 7 e 8 vão formalizar — `Quadrado` não sabe nada sobre JavaFX, e poderia ser testado sem abrir janela nenhuma (você vai fazer exatamente isso no Tópico 11).

Agora o método `start()`, criando os quadrados iniciais e seus desenhos:

```java
    @Override
    public void start(Stage stage) {
        quadrados.add(new Quadrado(100, 100, 30));
        quadrados.add(new Quadrado(300, 100, 40));
        quadrados.add(new Quadrado(500, 100, 25));

        for (Quadrado q : quadrados) {
            Rectangle rect = new Rectangle(q.x, q.y, q.tamanho, q.tamanho);
            rect.setFill(Color.BLUE);
            root.getChildren().add(rect);
            views.add(rect);
        }

        Scene scene = new Scene(root, 800, 600);
```

Três `new Quadrado(...)` já deixam o contador do PASSO 6 em 3. `new Rectangle(x, y, largura, altura)` cria o desenho correspondente a partir dos dados do objeto; `root.getChildren().add(rect)` põe o retângulo na árvore visual (sem isso ele existe na memória mas não aparece na tela); `new Scene(root, 800, 600)` monta a cena de 800×600 pixels que contém tudo.

Interação: tecla ESPAÇO faz todos pularem.

```java
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.SPACE) {
                for (Quadrado q : quadrados) {
                    q.vy = -12;  // impulso para cima
                }
            }
        });
```

`vy = -12` é negativo porque, como visto no PASSO 5, o eixo Y do JavaFX cresce para baixo — valores negativos sobem. A gravidade não é zerada: ela continua sendo somada a cada quadro, então o quadrado sobe, desacelera, para e volta a cair — o arco de um pulo real.

Interação: clique cria um novo quadrado.

```java
        root.setOnMouseClicked(e -> {
            Quadrado novo = new Quadrado(e.getX(), e.getY(), 30);
            quadrados.add(novo);
            Rectangle rect = new Rectangle(novo.x, novo.y, novo.tamanho, novo.tamanho);
            rect.setFill(Color.hsb(Math.random() * 360, 0.7, 0.9));
            root.getChildren().add(rect);
            views.add(rect);
            System.out.println("Total: " + Quadrado.getTotalQuadrados());
        });
```

Aqui os conceitos da aula se encontram: o `new` dispara o construtor, que incrementa o contador `static`; o `println` prova isso na saída, chamando o método `static` pela classe. `Color.hsb(matiz, saturação, brilho)` com matiz aleatório de 0 a 360 gera cores vivas e sempre distinguíveis, mais previsível do que sortear R, G e B separadamente.

Por fim, a janela e o game loop:

```java
        stage.setScene(scene);
        stage.setTitle("javafx-game — POO Avançada");
        stage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < quadrados.size(); i++) {
                    Quadrado q = quadrados.get(i);
                    Rectangle view = views.get(i);

                    q.atualizar();

                    if (q.y + q.tamanho > 550) {
                        q.y = 550 - q.tamanho;
                        q.vy = 0;
                    }

                    view.setX(q.x);
                    view.setY(q.y);
                }
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

`AnimationTimer` é a peça central: o JavaFX chama `handle()` automaticamente a cada quadro, cerca de 60 vezes por segundo, sincronizado com o monitor. O laço usa índice (`for (int i...)`) em vez de `for-each` porque precisa acessar as duas listas na mesma posição. A colisão com o chão em duas linhas: `q.y = 550 - q.tamanho` reposiciona o quadrado exatamente sobre o chão (testando `q.y + q.tamanho`, a *base* do quadrado, não `q.y`, que é o topo — em JavaFX a origem de um retângulo é seu canto superior esquerdo); `q.vy = 0` zera a velocidade, senão a gravidade continuaria acumulando e o quadrado "vibraria" preso no chão. As duas últimas linhas do laço são a sincronização modelo → visão: depois que a física calculou a nova posição no objeto `Quadrado`, copiamos esse valor para o `Rectangle` que está na tela. `.start()` liga o timer (sem ele, nada se move), e `launch(args)` inicializa o runtime do JavaFX, que por sua vez chama o seu `start(Stage)`.

Rode:

```bash
mvn compile
mvn exec:java
```

**✅ Checkpoint:** a janela abre. ESPAÇO faz os quadrados pularem. Clicar cria novos quadrados coloridos, e o terminal mostra "Total: N" crescendo.

### Armadilhas comuns — referência rápida

| Sintoma | Causa | Solução |
|---|---|---|
| Atributos ficam em zero mesmo passando valores | `x = x` em vez de `this.x = x` no construtor | Sempre `this.campo = parametro` quando os nomes coincidem |
| `new Quadrado()` deixou de compilar | Você escreveu outro construtor e perdeu o padrão implícito | Declare o construtor sem argumentos explicitamente |
| "call to this must be first statement" | Alguma linha vem antes do `this(...)` | Mova a chamada `this(...)` para a primeira linha |
| "non-static variable cannot be referenced..." | Método `static` tentando ler atributo de instância | Torne o método de instância, ou o atributo `static` |
| Quadrado afunda ou vibra no chão | Faltou reposicionar (`y = chão - tamanho`) ou zerar `vy` | Faça as duas coisas na colisão |
| Objeto sobe quando deveria descer | Sinal invertido: em JavaFX o eixo Y cresce para baixo | `vy` negativo sobe, positivo desce |
| Nada se move na tela | Faltou `.start()` no `AnimationTimer` | Chame `.start()` ao criar o timer |
| `import javafx...` não compila | JavaFX não declarado como dependência no `pom.xml` | Revise o PASSO 1 |

### Resumo do que você construiu

- Construtor: inicializa objeto; `this()` encadeia construtores, evitando código duplicado.
- Sobrecarga: múltiplos construtores dão mais flexibilidade na criação de objetos.
- `static`: pertence à classe, não à instância — usado aqui para constante compartilhada (`GRAVIDADE`) e contador global (`totalQuadrados`).
- `Quadrado` e `Circulo`, com duplicação de código propositalmente exposta — resolvida no próximo tópico.
- O jogo JavaFX rodando de verdade, com `AnimationTimer`, separação entre modelo (`Quadrado`) e visão (`Rectangle`), pulo e criação de quadrados por clique.

### Perguntas de fixação

1. Por que `this.x = x` é necessário no construtor de `Quadrado`, mas não seria necessário se o parâmetro se chamasse `posX` em vez de `x`?
2. `Quadrado.GRAVIDADE` e `totalQuadrados` são ambos `static`, mas um é `public` e o outro `private`. Por que essa diferença faz sentido?
3. Se você remover `q.vy = 0;` da colisão com o chão, o que acontece fisicamente com o quadrado, e por quê?
4. Por que `Main` guarda duas listas (`quadrados` e `views`) em vez de uma lista só de `Rectangle` com a física embutida dentro dele?

### DESAFIO

1. Adicione `static double RESISTENCIA_AR = 0.99` a `Quadrado` e aplique `vx *= RESISTENCIA_AR` em `atualizar()`.
2. Crie um construtor `Quadrado(double x, double y)` que sorteia um tamanho aleatório entre 20 e 50.
3. Adicione `static int MAX_QUADRADOS = 50` e impeça criar mais que isso (dica: o construtor pode lançar uma exceção — assunto do Tópico 5).
4. Faça cada quadrado nascer com uma cor própria, guardada como atributo de instância, mas continue usando `GRAVIDADE` como `static`.

## 2. Persistência com DAO (JavaFX + SQLite)

**Objetivo:** persistir as entidades do jogo em SQLite via JDBC, usando o padrão DAO — a mesma técnica de acesso a dados usada em praticamente qualquer aplicação Java que precise de um banco.

### Pré-requisitos

Projeto `javafx-game` do Tópico 1, com `Quadrado` e `Circulo` funcionando. **✅ Checkpoint:** o jogo compila e roda.

### PASSO 1: crie a classe base `Entidade`

O Tópico 1 terminou com `Quadrado` e `Circulo` duplicando os mesmos cinco atributos e o mesmo método `atualizar()`. Persistir as duas em uma única tabela de banco vai expor esse problema com ainda mais força: sem uma base em comum, o DAO precisaria de código separado para cada tipo. A solução é herança. Crie `src/main/java/jogo/model/Entidade.java`:

```java
package jogo.model;

import java.util.UUID;

public abstract class Entidade {
    private String id = UUID.randomUUID().toString();
    protected double x, y, vx, vy, tamanho;
    public static double GRAVIDADE = 0.5;

    protected Entidade(double x, double y, double tamanho) {
        this(x, y, tamanho, 0, 0);
    }

    protected Entidade(double x, double y, double tamanho, double vx, double vy) {
        this.x = x; this.y = y; this.tamanho = tamanho;
        this.vx = vx; this.vy = vy;
    }

    public void atualizar() {
        vy += GRAVIDADE;
        y += vy;
        x += vx;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public void setVy(double vy) { this.vy = vy; }
    public double getTamanho() { return tamanho; }
}
```

`abstract` → `Entidade` nunca é instanciada diretamente (`new Entidade(...)` não compilaria); ela só existe para ser estendida. Os construtores são `protected`, não `public`, pelo mesmo motivo: só uma subclasse pode chamá-los, via `super(...)`. O campo `id`, gerado automaticamente com `UUID.randomUUID()` (um identificador único, sem precisar de um banco para gerá-lo), é o que vai permitir salvar e reencontrar cada entidade especificamente — sem ele, o DAO não teria como saber "isto é o mesmo quadrado de antes" ao salvar de novo.

Agora reescreva `Quadrado` e `Circulo` para herdar de `Entidade`, em vez de repetir os atributos:

```java
package jogo.model;

public class Quadrado extends Entidade {
    public Quadrado(double x, double y, double tamanho, double vx, double vy) {
        super(x, y, tamanho, vx, vy);
    }

    public Quadrado(double x, double y, double tamanho) {
        this(x, y, tamanho, 0, 0);
    }
}
```

`extends Entidade` → `Quadrado` herda `x`, `y`, `atualizar()`, `getId()` e todo o resto; `super(...)` chama o construtor de `Entidade`, do mesmo jeito que `this(...)` chama outro construtor da própria classe. `Circulo` fica idêntico, trocando só o nome da classe. Note que `Quadrado` e `Circulo` deixaram de ter qualquer atributo próprio — por ora, a única diferença entre elas é o nome da classe, o que já é suficiente para o DAO reconstruir o tipo certo, como você vai ver no PASSO 4.

**✅ Checkpoint:** `Quadrado` e `Circulo` compilam sem repetir `x`, `y`, `vx`, `vy`, `tamanho` — e `new Entidade(...)` continua não compilando, por ser abstrata.

### PASSO 2: adicione o driver JDBC do SQLite

No `pom.xml`, dentro de `<dependencies>`:

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.46.1.3</version>
</dependency>
```

**✅ Checkpoint:** `mvn compile` baixa a dependência sem erro.

### PASSO 3: crie a conexão e a tabela

Crie `src/main/java/jogo/dao/Conexao.java` — um novo pacote, `jogo.dao`, para tudo relacionado a acesso a dados:

```java
package jogo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Conexao {
    private static final String URL = "jdbc:sqlite:jogo.db";

    public static Connection obter() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializar() throws SQLException {
        try (Connection conn = obter(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS entidade (
                    id TEXT PRIMARY KEY,
                    tipo TEXT NOT NULL,
                    x REAL, y REAL, tamanho REAL
                )
            """);
        }
    }
}
```

SQLite grava num único arquivo local (`jogo.db`) — sem servidor separado, ideal para persistência de um jogo desktop instalado na máquina do jogador. `CREATE TABLE IF NOT EXISTS` é *idempotente*: rodar esse comando várias vezes não dá erro nem apaga dados — só cria a tabela na primeira vez.

`try (Connection conn = obter(); ...)` é o **try-with-resources**: `Connection` e `Statement` implementam uma interface chamada `AutoCloseable`, e declará-los dentro dos parênteses do `try` garante que `.close()` é chamado automaticamente ao final do bloco, mesmo se algo lançar uma exceção no meio — sem isso, seria preciso um `finally` manual fechando cada recurso. Este tutorial usa try-with-resources em todo acesso a JDBC daqui em diante; o Tópico 5 aprofunda a ideia.

**✅ Checkpoint:** chamar `Conexao.inicializar()` cria o arquivo `jogo.db` com a tabela `entidade`.

### PASSO 4: crie a interface `EntidadeDAO` e sua implementação SQLite

**DAO** (*Data Access Object*) é o padrão que isola todo o SQL de acesso a um tipo de dado atrás de uma interface — o resto do código chama `salvar(entidade)` sem saber (nem precisar saber) que por trás disso existe um `INSERT` em SQLite. Crie `src/main/java/jogo/dao/EntidadeDAO.java`:

```java
package jogo.dao;

import jogo.model.Entidade;
import java.util.List;

public interface EntidadeDAO {
    void salvar(Entidade e);
    List<Entidade> listarTodas();
    void remover(String id);
}
```

Agora a implementação, em `src/main/java/jogo/dao/EntidadeDAOSQLite.java`. Comece só pelo `salvar`:

```java
package jogo.dao;

import jogo.model.Entidade;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntidadeDAOSQLite implements EntidadeDAO {
    @Override
    public void salvar(Entidade e) {
        String sql = "INSERT OR REPLACE INTO entidade (id, tipo, x, y, tamanho) VALUES (?,?,?,?,?)";
        try (Connection conn = Conexao.obter(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getId());
            ps.setString(2, e.getClass().getSimpleName());
            ps.setDouble(3, e.getX());
            ps.setDouble(4, e.getY());
            ps.setDouble(5, e.getTamanho());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Falha ao salvar entidade", ex);
        }
    }
```

`PreparedStatement` é uma consulta SQL com "buracos" (`?`), preenchidos depois de forma segura — o valor de cada `?` é enviado separado do texto do SQL, o que evita *SQL injection* (um `nome` malicioso contendo `'; DROP TABLE...` não consegue alterar a estrutura do comando, porque nunca é interpretado como parte dele). `INSERT OR REPLACE` funciona como um "upsert": insere se a chave `id` não existe, substitui se já existe — evita ter que checar antes com um `SELECT`. `e.getClass().getSimpleName()` devolve o nome da classe concreta do objeto (`"Quadrado"` ou `"Circulo"`) em tempo de execução — é assim que uma única tabela guarda os dois tipos, distinguidos pela coluna `tipo`.

Agora o `listarTodas`, que precisa fazer o caminho inverso: de uma linha genérica do banco, reconstruir o objeto polimórfico correto.

```java
    @Override
    public List<Entidade> listarTodas() {
        List<Entidade> resultado = new ArrayList<>();
        String sql = "SELECT * FROM entidade";
        try (Connection conn = Conexao.obter();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultado.add(reconstruir(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Falha ao listar entidades", ex);
        }
        return resultado;
    }

    private Entidade reconstruir(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        double x = rs.getDouble("x"), y = rs.getDouble("y"), tamanho = rs.getDouble("tamanho");
        Entidade e = tipo.equals("Quadrado") ? new Quadrado(x, y, tamanho) : new Circulo(x, y, tamanho);
        e.setId(rs.getString("id"));
        return e;
    }
```

`rs.next()` avança para a próxima linha do resultado, devolvendo `false` quando acabam — por isso o `while`. O `reconstruir` é o ponto onde o polimorfismo entra: a coluna `tipo` (texto puro, sem noção nenhuma de classes Java) decide qual construtor concreto chamar. Esse `if/else` vai reaparecer, e ser substituído por um padrão Factory, no Tópico 9 — por ora, vale sentir o problema antes de resolvê-lo.

Falta o `remover`:

```java
    @Override
    public void remover(String id) {
        String sql = "DELETE FROM entidade WHERE id = ?";
        try (Connection conn = Conexao.obter(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Falha ao remover entidade", ex);
        }
    }
}
```

Não esqueça de completar os `import`s do topo do arquivo: `java.sql.ResultSet`, `java.sql.Statement`, `java.util.ArrayList`, `java.util.List`, `jogo.model.Quadrado`, `jogo.model.Circulo`.

**✅ Checkpoint:** `salvar()` seguido de `listarTodas()` retorna a entidade salva com o tipo correto (`Quadrado` ou `Circulo`) — não um `Entidade` genérico.

### PASSO 5: crie o `GerenciadorDeJogo` e integre o DAO

O jogo ainda não tem uma classe que centralize "o estado da partida" — até agora, `Main` guarda a lista de entidades diretamente. Crie `src/main/java/jogo/model/GerenciadorDeJogo.java`:

```java
package jogo.model;

import jogo.dao.EntidadeDAO;
import jogo.dao.EntidadeDAOSQLite;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorDeJogo {
    private final EntidadeDAO dao = new EntidadeDAOSQLite();
    private List<Entidade> entidades = new ArrayList<>();

    public void carregarJogoSalvo() {
        entidades = new ArrayList<>(dao.listarTodas());
    }

    public void salvarProgresso() {
        for (Entidade e : entidades) {
            dao.salvar(e);
        }
    }

    public List<Entidade> getEntidades() {
        return entidades;
    }
}
```

O ponto central deste passo: `GerenciadorDeJogo` conhece a *interface* `EntidadeDAO`, nunca a implementação concreta `EntidadeDAOSQLite` além da única linha onde ela é criada. Se um dia o jogo trocar SQLite por outro banco, só essa linha muda — nenhuma outra classe que use `GerenciadorDeJogo` percebe a diferença. Esse desacoplamento é o valor prático do padrão DAO.

**✅ Checkpoint:** fechar o jogo (chamando `salvarProgresso()`) e reabrir (chamando `carregarJogoSalvo()`) restaura as entidades salvas na sessão anterior.

### Resumo do que você construiu

- `Entidade`, classe base `abstract`, eliminando a duplicação entre `Quadrado` e `Circulo`.
- Conexão JDBC com SQLite e criação de tabela idempotente (`CREATE TABLE IF NOT EXISTS`).
- Interface `EntidadeDAO` desacoplando o jogo da forma de persistência.
- `EntidadeDAOSQLite` com `INSERT OR REPLACE`, `SELECT` e `DELETE` via `PreparedStatement`.
- Reconstrução polimórfica de objetos a partir de linhas do banco.
- `GerenciadorDeJogo` carregando/salvando progresso via DAO, sem conhecer SQLite diretamente.

### Perguntas de fixação

1. Por que os construtores de `Entidade` são `protected` em vez de `public`?
2. O que exatamente `INSERT OR REPLACE` evita que um par `SELECT` + `if` manual teria que fazer?
3. Por que `reconstruir()` precisa da coluna `tipo`, já que a tabela também tem `id`, `x`, `y` e `tamanho`?
4. Se `GerenciadorDeJogo` chamasse `new EntidadeDAOSQLite()` toda vez que precisasse salvar, em vez de guardar uma instância no atributo `dao`, o que mudaria na prática?

### Exercícios

1. **Salvamento automático**: salve o progresso a cada 30 segundos de jogo, usando outro `AnimationTimer` ou um contador de quadros.
2. **Múltiplos slots de save**: adicione uma coluna `slot` e permita vários jogos salvos.
3. **Migração de esquema**: pesquise como adicionar uma coluna nova a uma tabela SQLite existente sem perder os dados que já estavam lá.

## 3. CRUD com Interface + DAO (JavaFX TableView e FXML)

**Objetivo:** construir uma tela JavaFX de "Gerenciador de Fases" com CRUD completo (criar, listar, editar, excluir), usando o padrão DAO com SQLite por trás de uma interface gráfica de verdade — e, com ela, a primeira View escrita em **FXML**, o formato que o resto do tutorial usa para toda tela nova.

### Pré-requisitos

Projeto `javafx-game` com `Entidade`, `Quadrado`, `Circulo` das aulas anteriores. **✅ Checkpoint:** o jogo compila e roda.

### PASSO 1: modele a entidade `Fase`

Uma "fase" do jogo é um dado bem mais simples que uma `Entidade` física — não cai, não tem posição na tela, só existe como um registro configurável. Crie `src/main/java/jogo/model/Fase.java`:

```java
package jogo.model;

public class Fase {
    private int id;
    private String nome;
    private int dificuldade; // 1 a 5
    private double gravidade;

    public Fase(String nome, int dificuldade, double gravidade) {
        this.nome = nome;
        this.dificuldade = dificuldade;
        this.gravidade = gravidade;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getDificuldade() { return dificuldade; }
    public void setDificuldade(int d) { this.dificuldade = d; }
    public double getGravidade() { return gravidade; }
    public void setGravidade(double g) { this.gravidade = g; }
}
```

Vale reparar desde já no padrão de nomes dos métodos — `getNome()`, `setNome(...)` — porque o PASSO 3 depende exatamente dessa convenção (chamada *JavaBean*) para ligar a tabela à classe automaticamente.

**✅ Checkpoint:** a classe compila.

### PASSO 2: crie `FaseDAO` e `FaseDAOImpl`

Repita o padrão DAO do Tópico 2, mas numa tabela e num arquivo de banco próprios (`fases.db`), já que fases e entidades físicas do jogo são conceitos independentes. `src/main/java/jogo/dao/FaseDAO.java`:

```java
package jogo.dao;

import jogo.model.Fase;
import java.util.List;

public interface FaseDAO {
    void salvar(Fase fase);
    List<Fase> listarTodas();
    void atualizar(Fase fase);
    void excluir(int id);
}
```

`atualizar` e `excluir` são novos em relação ao `EntidadeDAO` do Tópico 2 — ali só havia `INSERT OR REPLACE`, aqui o CRUD é explícito: cada operação corresponde a um verbo próprio. `src/main/java/jogo/dao/FaseDAOImpl.java`:

```java
package jogo.dao;

import jogo.model.Fase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FaseDAOImpl implements FaseDAO {
    private static final String URL = "jdbc:sqlite:fases.db";

    public FaseDAOImpl() {
        criarTabelaSeNaoExiste();
    }

    private void criarTabelaSeNaoExiste() {
        String sql = """
            CREATE TABLE IF NOT EXISTS fase (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                dificuldade INTEGER NOT NULL,
                gravidade REAL NOT NULL
            )
        """;
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
```

Diferente do Tópico 2, a criação da tabela acontece no **construtor** — assim que alguém faz `new FaseDAOImpl()`, a tabela já existe, sem depender de um passo de inicialização separado. `AUTOINCREMENT` delega ao próprio SQLite a geração do `id`, em vez do `UUID` manual usado em `Entidade` — uma escolha de projeto legítima quando o dado não precisa de um identificador globalmente único, só único dentro da tabela.

Agora `salvar`, `listarTodas`, `atualizar` e `excluir`, seguindo o mesmo padrão de `PreparedStatement` do Tópico 2:

```java
    @Override
    public void salvar(Fase fase) {
        String sql = "INSERT INTO fase (nome, dificuldade, gravidade) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fase.getNome());
            stmt.setInt(2, fase.getDificuldade());
            stmt.setDouble(3, fase.getGravidade());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Fase> listarTodas() {
        List<Fase> fases = new ArrayList<>();
        String sql = "SELECT * FROM fase";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fase f = new Fase(rs.getString("nome"), rs.getInt("dificuldade"), rs.getDouble("gravidade"));
                f.setId(rs.getInt("id"));
                fases.add(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fases;
    }

    @Override
    public void atualizar(Fase fase) {
        String sql = "UPDATE fase SET nome = ?, dificuldade = ?, gravidade = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fase.getNome());
            stmt.setInt(2, fase.getDificuldade());
            stmt.setDouble(3, fase.getGravidade());
            stmt.setInt(4, fase.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM fase WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

**✅ Checkpoint:** as duas classes compilam.

### PASSO 3: descreva a tela em FXML

Até aqui, toda interface deste tutorial foi montada em Java puro (`new Rectangle(...)`, `new Scene(...)`). Isso funciona para uma cena simples, mas não escala para um formulário com vários campos, tabela e botões — o código de construção da tela se mistura com o código que reage a eventos, e fica difícil enxergar a estrutura visual de relance. **FXML** resolve isso: um arquivo XML descreve só a estrutura da tela (que componentes existem, como estão organizados), separado do arquivo Java que reage aos eventos dela — a divisão View/Controller do MVC, aplicada literalmente a dois arquivos diferentes.

Crie `src/main/resources/jogo/fase.fxml` — repare que fica em `resources`, não em `java`: FXML não é código Java, é um recurso que o `FXMLLoader` lê em tempo de execução.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<VBox spacing="10" xmlns="http://javafx.com/javafx"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="jogo.controller.FaseController"
      style="-fx-padding: 10;">
    <HBox spacing="10">
        <TextField fx:id="campoNome" promptText="Nome"/>
        <TextField fx:id="campoDificuldade" promptText="Dificuldade (1-5)"/>
        <TextField fx:id="campoGravidade" promptText="Gravidade"/>
        <Button fx:id="btnSalvar" text="Salvar" onAction="#aoClicarSalvar"/>
    </HBox>
    <TableView fx:id="tabela">
        <columns>
            <TableColumn fx:id="colNome" text="Nome"/>
            <TableColumn fx:id="colDificuldade" text="Dificuldade"/>
            <TableColumn fx:id="colGravidade" text="Gravidade"/>
        </columns>
    </TableView>
    <Button fx:id="btnExcluir" text="Excluir selecionada" onAction="#aoClicarExcluir"/>
</VBox>
```

- `fx:controller="jogo.controller.FaseController"` → diz ao `FXMLLoader` qual classe Java vai controlar esta tela; você cria essa classe no próximo passo.
- `fx:id="campoNome"` → dá um nome a esse componente específico, para que o Controller consiga referenciá-lo (você vai ver isso via `@FXML` a seguir).
- `onAction="#aoClicarSalvar"` → liga o clique do botão a um método do Controller chamado exatamente `aoClicarSalvar` (o `#` indica "procure esse nome na classe do `fx:controller`").
- `VBox`/`HBox` → contêineres que empilham seus filhos verticalmente (`VBox`) ou horizontalmente (`HBox`) — o `V` e o `H` vêm de *vertical* e *horizontal*.

**✅ Checkpoint:** o arquivo FXML é um XML bem formado (sem tags fechadas incorretamente) — você confirma isso de verdade no próximo passo, quando ele for carregado.

### PASSO 4: crie o `FaseController`

Crie `src/main/java/jogo/controller/FaseController.java` — um novo pacote, `jogo.controller`, espelhando a separação MVC que o FXML já sugeriu.

```java
package jogo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jogo.dao.FaseDAO;
import jogo.dao.FaseDAOImpl;
import jogo.model.Fase;

public class FaseController {
    private final FaseDAO dao = new FaseDAOImpl();

    @FXML private TableView<Fase> tabela;
    @FXML private TableColumn<Fase, String> colNome;
    @FXML private TableColumn<Fase, Integer> colDificuldade;
    @FXML private TableColumn<Fase, Double> colGravidade;
    @FXML private TextField campoNome, campoDificuldade, campoGravidade;
    @FXML private Button btnSalvar;
```

Cada campo anotado com `@FXML` é ligado automaticamente, pelo `fx:id` de mesmo nome no FXML, assim que a tela carrega — é assim que o Controller Java "enxerga" os componentes descritos no XML, sem uma linha de código conectando-os manualmente.

```java
    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDificuldade.setCellValueFactory(new PropertyValueFactory<>("dificuldade"));
        colGravidade.setCellValueFactory(new PropertyValueFactory<>("gravidade"));
        carregarTabela();
    }

    private void carregarTabela() {
        ObservableList<Fase> dados = FXCollections.observableArrayList(dao.listarTodas());
        tabela.setItems(dados);
    }
```

`initialize()` é chamado automaticamente pelo `FXMLLoader` assim que todos os campos `@FXML` já foram injetados — é o lugar certo para configurar a tabela, nunca o construtor (que roda antes da injeção acontecer). `PropertyValueFactory<>("nome")` exige um getter no padrão JavaBean (`getNome()`) — é assim que a `TableColumn` sabe qual método chamar para preencher cada célula, sem código de "ligação" manual entre coluna e atributo. `ObservableList` é uma lista especial do JavaFX: quando seu conteúdo muda, a `TableView` que a está exibindo se atualiza sozinha, sem você chamar nenhum método de "redesenhar".

**✅ Checkpoint:** carregar essa tela mostra as fases já salvas no banco (ou uma tabela vazia, se ainda não houver nenhuma).

### PASSO 5: implemente Create e Delete

```java
    @FXML
    private void aoClicarSalvar() {
        Fase fase = new Fase(
            campoNome.getText(),
            Integer.parseInt(campoDificuldade.getText()),
            Double.parseDouble(campoGravidade.getText())
        );
        dao.salvar(fase);
        carregarTabela();
        campoNome.clear();
        campoDificuldade.clear();
        campoGravidade.clear();
    }

    @FXML
    private void aoClicarExcluir() {
        Fase selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            dao.excluir(selecionada.getId());
            carregarTabela();
        }
    }
```

`tabela.getSelectionModel().getSelectedItem()` devolve o objeto `Fase` da linha selecionada — não um índice, nem um texto: o próprio objeto, porque `TableView<Fase>` está tipada com o modelo, não com strings soltas. O `if (selecionada != null)` importa porque, sem nenhuma linha selecionada, o método devolve `null` — clicar em "Excluir" sem seleção não deve lançar exceção.

**✅ Checkpoint:** preencher os campos e clicar "Salvar" adiciona uma linha na tabela e persiste no banco; selecionar uma linha e clicar "Excluir" remove das duas.

### PASSO 6: implemente Update, via duplo clique

```java
    private Fase emEdicao;

    // dentro de initialize(), depois de carregarTabela():
    tabela.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2) {
            Fase selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada != null) {
                emEdicao = selecionada;
                campoNome.setText(selecionada.getNome());
                campoDificuldade.setText(String.valueOf(selecionada.getDificuldade()));
                campoGravidade.setText(String.valueOf(selecionada.getGravidade()));
                btnSalvar.setText("Atualizar");
            }
        }
    });
```

`e.getClickCount() == 2` distingue duplo clique de clique simples (que só seleciona a linha). O atributo `emEdicao` guarda qual `Fase` está sendo editada — sem ele, `aoClicarSalvar()` não teria como saber se deve criar uma fase nova ou atualizar uma existente. Ajuste `aoClicarSalvar()` para checar esse estado:

```java
    @FXML
    private void aoClicarSalvar() {
        if (emEdicao != null) {
            emEdicao.setNome(campoNome.getText());
            emEdicao.setDificuldade(Integer.parseInt(campoDificuldade.getText()));
            emEdicao.setGravidade(Double.parseDouble(campoGravidade.getText()));
            dao.atualizar(emEdicao);
            emEdicao = null;
            btnSalvar.setText("Salvar");
        } else {
            Fase fase = new Fase(campoNome.getText(),
                    Integer.parseInt(campoDificuldade.getText()),
                    Double.parseDouble(campoGravidade.getText()));
            dao.salvar(fase);
        }
        carregarTabela();
        campoNome.clear();
        campoDificuldade.clear();
        campoGravidade.clear();
    }
```

**✅ Checkpoint:** duplo clique em uma linha preenche o formulário e troca o texto do botão para "Atualizar"; salvar nesse estado atualiza a fase existente em vez de criar uma nova.

### PASSO 7: carregue o FXML a partir de uma `Application`

Falta o ponto de entrada. Crie `src/main/java/jogo/GerenciadorFasesApp.java` — uma segunda aplicação JavaFX independente do jogo, dentro do mesmo projeto Maven:

```java
package jogo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GerenciadorFasesApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/jogo/fase.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 520, 420));
        stage.setTitle("Gerenciador de Fases");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```

`FXMLLoader` lê o arquivo `.fxml`, instancia todos os componentes descritos nele, cria uma instância de `FaseController` (o `fx:controller` declarado no PASSO 3) e injeta os campos `@FXML` — tudo isso dentro de `loader.load()`. Rode com:

```bash
mvn exec:java -Dexec.mainClass=jogo.GerenciadorFasesApp
```

**✅ Checkpoint:** a janela "Gerenciador de Fases" abre, com o CRUD completo funcionando de ponta a ponta.

### Resumo do que você construiu

- `Fase`, entidade simples com getters/setters no padrão JavaBean.
- `FaseDAO` + `FaseDAOImpl`, persistência SQLite/JDBC com CRUD completo.
- A primeira View em **FXML** do tutorial, separada do `FaseController` que reage aos eventos dela.
- Create, Read, Update e Delete funcionando numa tela JavaFX real, ligada a um banco de verdade.

### Perguntas de fixação

1. Por que `initialize()` é o lugar certo para configurar a `TableView`, e não o construtor de `FaseController`?
2. O que `PropertyValueFactory<>("nome")` faz exatamente, e o que aconteceria se `Fase` não tivesse um método `getNome()`?
3. Por que `aoClicarSalvar()` precisa checar `emEdicao != null` em vez de sempre criar uma `Fase` nova?
4. `fase.fxml` fica em `src/main/resources`, não em `src/main/java`. Por que essa distinção existe?

### Exercícios

1. **Validação**: impeça salvar com nome vazio ou dificuldade fora do intervalo 1–5.
2. **Confirmação de exclusão**: use `Alert` (`AlertType.CONFIRMATION`) antes de excluir.
3. **Ordenação**: as colunas da `TableView` já são clicáveis para ordenar — confirme isso na prática e explique por que não precisou escrever código extra.
4. **Usar a fase no jogo**: ao selecionar uma fase na tabela, aplique sua `gravidade` em `Entidade.GRAVIDADE`.

## 4. Coleções (List, Set, Map)

**Objetivo:** trocar o uso solto de `List` por uma escolha deliberada entre `List`, `Set` e `Map`, de acordo com a pergunta que cada coleção responde melhor.

### Pré-requisitos

Projeto `javafx-game` com `Entidade`, `Quadrado`, `Circulo` das aulas anteriores. **✅ Checkpoint:** o jogo compila, usando `List<Entidade>` para guardar as entidades.

### PASSO 1: por que `List` não basta para tudo

Hoje seu jogo usa `List<Entidade>` para guardar tudo. Mas duas perguntas comuns não são bem respondidas só com `List`: "quais tipos de entidade já apareceram no jogo, sem repetir?" precisa de `Set`; "qual é a pontuação de cada jogador, buscando pelo nome dele?" precisa de `Map`. A escolha da coleção depende da pergunta que você precisa responder — não é um detalhe estético.

**✅ Checkpoint:** você consegue dar um exemplo, no seu próprio jogo, de uma pergunta que `List` sozinha não responde bem.

### PASSO 2: revise `List` — ordem importa, duplicatas permitidas

```java
List<Entidade> entidades = new ArrayList<>();
entidades.add(new Quadrado(100, 50, 30));
entidades.add(new Circulo(300, 50, 20));
entidades.add(new Quadrado(500, 50, 40)); // outro quadrado, sem problema

System.out.println(entidades.get(0)); // acesso por índice
System.out.println(entidades.size()); // 3
```

`List` preserva a ordem de inserção e permite elementos repetidos — exatamente o que o game loop precisa, já que a ordem de desenho importa (a última entidade adicionada aparece por cima das outras, no `Pane` do JavaFX).

**✅ Checkpoint:** `entidades.size()` retorna 3.

### PASSO 3: use `Set` para tipos únicos de entidade em jogo

```java
Set<String> tiposEmJogo = new HashSet<>();
for (Entidade e : entidades) {
    tiposEmJogo.add(e.getClass().getSimpleName());
}

System.out.println(tiposEmJogo); // [Quadrado, Circulo] — sem repetição, mesmo com 2 Quadrados na lista
```

`Set` não permite duplicatas: adicionar `"Quadrado"` duas vezes só mantém uma ocorrência. `HashSet` não garante ordem; se a ordem de inserção importar, existe `LinkedHashSet`, que preserva a ordem em que cada elemento entrou pela primeira vez.

**✅ Checkpoint:** `tiposEmJogo.size()` é 2, mesmo havendo 3 entidades (2 quadrados + 1 círculo).

### PASSO 4: use `Map` para associar chave e valor — pontuação por jogador

```java
Map<String, Integer> pontuacoes = new HashMap<>();
pontuacoes.put("Ana", 150);
pontuacoes.put("Bruno", 220);
pontuacoes.put("Ana", 180); // sobrescreve o valor anterior de "Ana"

System.out.println(pontuacoes.get("Ana"));    // 180
System.out.println(pontuacoes.get("Carlos")); // null (chave não existe)
```

`Map<K, V>` associa uma chave única a um valor — inserir a mesma chave de novo substitui o valor, nunca duplica a chave. Repare no `System.out.println(pontuacoes.get("Carlos"))`: como a chave não existe, o retorno é `null`, o que exige cuidado antes de usar esse valor. O método seguinte resolve isso:

```java
System.out.println(pontuacoes.getOrDefault("Carlos", 0)); // 0, sem risco de NullPointerException
```

`getOrDefault` evita checar `null` manualmente, devolvendo um valor padrão quando a chave não existe.

**✅ Checkpoint:** `pontuacoes.get("Ana")` retorna 180 (o valor mais recente), não 150.

### PASSO 5: percorra um `Map` de três formas

```java
// 1. Só as chaves
for (String jogador : pontuacoes.keySet()) {
    System.out.println(jogador);
}

// 2. Só os valores
for (int pontos : pontuacoes.values()) {
    System.out.println(pontos);
}

// 3. Chave e valor juntos (o mais usado na prática)
for (Map.Entry<String, Integer> entrada : pontuacoes.entrySet()) {
    System.out.println(entrada.getKey() + ": " + entrada.getValue());
}
```

**✅ Checkpoint:** as três formas de laço rodam sem erro e imprimem os dados esperados.

### PASSO 6: combine as três coleções em `GerenciadorDeJogo`

Volte a `GerenciadorDeJogo` (Tópico 2) e some coleções que respondem perguntas diferentes sobre a partida:

```java
public class GerenciadorDeJogo {
    private List<Entidade> entidades = new ArrayList<>();
    private Set<String> conquistasDesbloqueadas = new HashSet<>();
    private Map<String, Integer> pontuacaoPorJogador = new HashMap<>();

    public void adicionarEntidade(Entidade e) {
        entidades.add(e);
        if (entidades.size() == 10) {
            conquistasDesbloqueadas.add("DEZ_ENTIDADES");
        }
    }

    public void marcarPonto(String jogador, int pontos) {
        pontuacaoPorJogador.merge(jogador, pontos, Integer::sum);
    }
}
```

`merge(jogador, pontos, Integer::sum)` faz duas coisas em uma linha: se a chave já existe, soma o novo valor ao existente (usando `Integer::sum`, uma referência ao método estático `Integer.sum(a, b)`, como a função de combinação); se não existe, insere o valor direto. Substitui um `if (map.containsKey(...)) ... else ...` inteiro.

**✅ Checkpoint:** chamar `marcarPonto("Ana", 10)` duas vezes resulta em 20 pontos para Ana, não 10.

### Resumo do que você construiu

- `List` revisada: ordem preservada, duplicatas permitidas.
- `Set` (`HashSet`) garantindo tipos únicos, sem repetição.
- `Map` (`HashMap`) associando jogador a pontuação, com `getOrDefault` evitando `null`.
- Três formas de percorrer um `Map` (`keySet`, `values`, `entrySet`).
- `merge()` somando pontuação sem lógica condicional manual.
- `GerenciadorDeJogo` combinando as três coleções num cenário real.

### Perguntas de fixação

1. Por que `tiposEmJogo` (Passo 3) usa `Set<String>` em vez de `Set<Entidade>`?
2. O que `pontuacoes.get("Carlos")` retorna quando a chave não existe, e por que isso é perigoso sem `getOrDefault`?
3. `merge(jogador, pontos, Integer::sum)` substitui qual combinação de `if`/`else` exatamente?
4. Em que situação `LinkedHashSet` seria melhor que `HashSet` no lugar de `conquistasDesbloqueadas`?

### Exercícios

1. **TreeSet**: troque `conquistasDesbloqueadas` por `TreeSet` e observe a ordenação automática.
2. **Ranking**: ordene `pontuacaoPorJogador` do maior para o menor pontuador (dica: pesquise Stream + `sorted`, que o Tópico 6 aprofunda).
3. **removerEntidade**: implemente a remoção por referência de objeto, e explique por que `equals()` importa aqui.
4. **Map de Map**: modele `Map<String, Map<String, Integer>>` para pontuação por jogador por fase.

## 5. Generics + Tratamento de Exceções Avançado

**Objetivo:** escrever um repositório genérico reutilizável para qualquer entidade com id, e tratar erros com exceções customizadas, multi-catch e try-with-resources.

### Pré-requisitos

Projeto `javafx-game` com `Entidade`, `Quadrado`, `Circulo`, `GerenciadorDeJogo` das aulas anteriores. **✅ Checkpoint:** o jogo compila e roda.

### PASSO 1: o problema que generics resolve

Hoje `GerenciadorDeJogo` tem `List<Entidade> entidades` — funciona só para entidades. Se você quisesse um repositório reutilizável para "qualquer coisa com id" (entidades, jogadores, fases), teria que copiar a classe inteira trocando o tipo a cada vez. **Generics** permitem escrever a classe uma vez, parametrizada pelo tipo.

**✅ Checkpoint:** você entende o problema de duplicação que generics evita.

### PASSO 2: crie uma interface `ComId`

```java
public interface ComId {
    String getId();
}
```

Faça `Entidade` implementar essa interface — ela já tem `getId()` desde o Tópico 2, então basta declarar `implements ComId` na assinatura da classe.

**✅ Checkpoint:** `Entidade implements ComId` compila sem exigir nenhum método novo.

### PASSO 3: crie um repositório genérico

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Repositorio<T extends ComId> {
    private final Map<String, T> itens = new HashMap<>();

    public void salvar(T item) {
        itens.put(item.getId(), item);
    }

    public Optional<T> buscarPorId(String id) {
        return Optional.ofNullable(itens.get(id));
    }

    public void remover(String id) {
        itens.remove(id);
    }

    public int total() {
        return itens.size();
    }
}
```

`<T extends ComId>` é um *bounded type parameter*: `T` pode ser qualquer tipo, desde que implemente `ComId` — assim o repositório pode chamar `item.getId()` com segurança de tipo, sem `instanceof` nem *cast*. `Optional<T>` evita retornar `null` diretamente: força quem chama a lidar explicitamente com "pode não existir", em vez de arriscar um `NullPointerException` mais adiante.

**✅ Checkpoint:** `new Repositorio<Entidade>()` compila; `new Repositorio<String>()` não compila, porque `String` não implementa `ComId` — esse erro é o generics fazendo seu trabalho em tempo de compilação, em vez de falhar em tempo de execução.

### PASSO 4: use o repositório genérico no jogo

```java
Repositorio<Entidade> repo = new Repositorio<>();
repo.salvar(new Quadrado(100, 50, 30));
repo.salvar(new Circulo(300, 50, 20));

System.out.println("Total: " + repo.total());
```

E, para consumir o `Optional` sem checar `isPresent()` manualmente:

```java
repo.buscarPorId(algumId).ifPresentOrElse(
    e -> System.out.println("Encontrado: " + e),
    () -> System.out.println("Não encontrado")
);
```

`ifPresentOrElse` executa o primeiro lambda se o `Optional` tem valor, o segundo se está vazio — evita `if (opt.isPresent()) { ... } else { ... }` escrito manualmente.

**✅ Checkpoint:** `repo.total()` retorna 2 após salvar duas entidades.

### PASSO 5: crie exceções customizadas para o jogo

```java
public class EntidadeInvalidaException extends RuntimeException {
    public EntidadeInvalidaException(String mensagem) {
        super(mensagem);
    }
}

public class LimiteDeFaseExcedidoException extends RuntimeException {
    private final int limite;

    public LimiteDeFaseExcedidoException(int limite) {
        super("Limite de " + limite + " entidades por fase excedido");
        this.limite = limite;
    }

    public int getLimite() {
        return limite;
    }
}
```

Exceções customizadas herdam de `RuntimeException` (ou de `Exception`, para forçar tratamento obrigatório em qualquer código que as chame) e podem carregar dados extras além da mensagem — aqui, `limite`, que quem captura a exceção pode consultar para decidir o que fazer.

**✅ Checkpoint:** as duas classes compilam.

### PASSO 6: lance as exceções customizadas com validação

Volte a `GerenciadorDeJogo.adicionarEntidade` e adicione validação real:

```java
private static final int LIMITE_ENTIDADES = 50;

public void adicionarEntidade(Entidade e) {
    if (e == null) {
        throw new EntidadeInvalidaException("Entidade não pode ser nula");
    }
    if (entidades.size() >= LIMITE_ENTIDADES) {
        throw new LimiteDeFaseExcedidoException(LIMITE_ENTIDADES);
    }
    entidades.add(e);
}
```

**✅ Checkpoint:** adicionar uma entidade `null` lança `EntidadeInvalidaException` com a mensagem correta.

### PASSO 7: trate múltiplas exceções com multi-catch e try-with-resources

Primeiro, um método que grava um relatório em arquivo, reaproveitando o try-with-resources já visto no Tópico 2:

```java
import java.io.FileWriter;
import java.io.IOException;

public void salvarRelatorioDeFase(String caminho) {
    try (FileWriter escritor = new FileWriter(caminho)) {
        for (Entidade e : entidades) {
            escritor.write(e.toString() + "\n");
        }
    } catch (IOException e) {
        System.err.println("Falha ao salvar relatório: " + e.getMessage());
    }
}
```

`FileWriter` implementa `AutoCloseable`; declará-lo no `try (...)` garante `close()` automático, mesmo se `write` lançar exceção no meio do laço.

Agora um método que trata duas exceções diferentes com a mesma recuperação:

```java
public void processarFase(Entidade novaEntidade) {
    try {
        adicionarEntidade(novaEntidade);
    } catch (EntidadeInvalidaException | LimiteDeFaseExcedidoException e) {
        System.err.println("Não foi possível processar: " + e.getMessage());
    } finally {
        System.out.println("Tentativa de processamento registrada.");
    }
}
```

**multi-catch** (`EntidadeInvalidaException | LimiteDeFaseExcedidoException`) trata dois tipos de exceção não relacionados no mesmo bloco, quando a ação de recuperação é idêntica — evita duplicar o mesmo corpo de `catch` duas vezes. **`finally`** roda sempre, com ou sem exceção — aqui, registrando que uma tentativa aconteceu, independente do resultado.

**✅ Checkpoint:** `processarFase(null)` imprime a mensagem de erro e depois "Tentativa de processamento registrada." — nesta ordem.

### Resumo do que você construiu

- Interface `ComId` + `Repositorio<T extends ComId>`, genérico e reutilizável para qualquer entidade com id.
- `Optional<T>` evitando retorno de `null` explícito.
- Exceções customizadas (`EntidadeInvalidaException`, `LimiteDeFaseExcedidoException`) com dados extras.
- try-with-resources fechando `FileWriter` automaticamente.
- multi-catch tratando duas exceções não relacionadas em um único bloco.
- `finally` garantindo execução de código de limpeza/log.

### Perguntas de fixação

1. Por que `Repositorio<T extends ComId>` precisa do `extends ComId`, em vez de só `Repositorio<T>`?
2. Qual é a diferença prática entre `repo.buscarPorId(id).get()` (arriscado) e `repo.buscarPorId(id).ifPresentOrElse(...)`?
3. Em que situação um multi-catch (`A | B`) é preferível a dois blocos `catch` separados, e em que situação não é?
4. Por que `LimiteDeFaseExcedidoException` guarda o `limite` como atributo, em vez de só colocá-lo na mensagem de texto?

### Exercícios

1. **Repositorio**: crie uma classe `Jogador implements ComId` e reutilize `Repositorio<T>` sem alterá-la.
2. **Hierarquia de exceções**: crie `JogoException` abstrata e faça as outras duas herdarem dela; capture só `JogoException` em um catch único.
3. **Método genérico**: escreva `public static <T extends ComId> T maisRecente(List<T> itens)`, independente de `Repositorio`.
4. **Wildcard**: pesquise `List<? extends ComId>` e explique quando usar em vez de `List<T>`.

## 6. Manipulação de Arquivos + Streams

**Objetivo:** exportar relatórios de partida para arquivo — inclusive escolhendo o caminho com um diálogo `FileChooser` de verdade — e processar dados de forma funcional com Streams.

### Pré-requisitos

Projeto `javafx-game` com `Repositorio<T>` e `GerenciadorDeJogo`. **✅ Checkpoint:** o jogo compila e roda.

### PASSO 1: escreva um relatório em arquivo texto

```java
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public void exportarRelatorio(List<Entidade> entidades, String caminho) {
    try (PrintWriter escritor = new PrintWriter(new FileWriter(caminho))) {
        escritor.println("Relatório de Fase");
        escritor.println("Total de entidades: " + entidades.size());
        for (Entidade e : entidades) {
            escritor.printf("%s em (%.1f, %.1f)%n", e.getClass().getSimpleName(), e.getX(), e.getY());
        }
    } catch (IOException ex) {
        throw new RuntimeException("Falha ao exportar relatório", ex);
    }
}
```

`PrintWriter` envolvendo `FileWriter` adiciona métodos convenientes (`println`, `printf`) sobre a escrita bruta de caracteres. O try-with-resources garante que o arquivo é fechado mesmo se a escrita falhar no meio.

**✅ Checkpoint:** chamar `exportarRelatorio(entidades, "relatorio.txt")` cria o arquivo com o conteúdo esperado.

### PASSO 2: leia o arquivo de volta linha por linha

```java
import java.io.BufferedReader;
import java.io.FileReader;

public List<String> lerRelatorio(String caminho) {
    List<String> linhas = new ArrayList<>();
    try (BufferedReader leitor = new BufferedReader(new FileReader(caminho))) {
        String linha;
        while ((linha = leitor.readLine()) != null) {
            linhas.add(linha);
        }
    } catch (IOException ex) {
        throw new RuntimeException("Falha ao ler relatório", ex);
    }
    return linhas;
}
```

`BufferedReader` lê em blocos internos (*buffer*), evitando uma chamada de sistema por caractere — muito mais eficiente que `FileReader` puro para arquivos com várias linhas.

**✅ Checkpoint:** `lerRelatorio("relatorio.txt")` retorna a mesma quantidade de linhas escritas no Passo 1.

### PASSO 3: use a API NIO.2 moderna (`Files`) como alternativa mais simples

```java
import java.nio.file.Files;
import java.nio.file.Path;

public void exportarComNio(List<Entidade> entidades, String caminho) throws IOException {
    List<String> linhas = entidades.stream()
        .map(e -> e.getClass().getSimpleName() + " em (" + e.getX() + ", " + e.getY() + ")")
        .toList();

    Files.write(Path.of(caminho), linhas);
}
```

`Files.write` recebe diretamente uma `List<String>` e cuida de abrir, escrever e fechar o arquivo — dispensa try-with-resources manual para o caso comum de escrever tudo de uma vez.

**✅ Checkpoint:** `exportarComNio` produz um arquivo idêntico em conteúdo ao do Passo 1 (uma linha por entidade).

### PASSO 4: deixe o jogador escolher o caminho com `FileChooser`

Tudo até aqui recebeu o caminho do arquivo como parâmetro fixo. Numa aplicação desktop de verdade, quem escolhe onde salvar é o usuário — e o JavaFX tem um componente pronto para isso, o `FileChooser`, um diálogo nativo do sistema operacional.

```java
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public void exportarComDialogo(Stage stage, List<Entidade> entidades) throws IOException {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Salvar relatório de fase");
    chooser.setInitialFileName("relatorio.txt");
    chooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Arquivo de texto", "*.txt"));

    File arquivo = chooser.showSaveDialog(stage);
    if (arquivo != null) {
        exportarComNio(entidades, arquivo.getAbsolutePath());
    }
}
```

`showSaveDialog(stage)` abre o diálogo nativo de "Salvar como" do sistema operacional e bloqueia até o usuário escolher um caminho ou cancelar — se cancelar, o retorno é `null`, por isso o `if (arquivo != null)`. `getExtensionFilters()` restringe (e sugere) a extensão mostrada no diálogo, sem impedir o usuário de digitar outra se quiser. Ligue esse método a um botão "Exportar" em qualquer tela do jogo — por exemplo, em `GerenciadorFasesApp`, do Tópico 3 — passando o `Stage` da própria janela.

**✅ Checkpoint:** clicar em "Exportar" abre o diálogo nativo de salvar arquivo do seu sistema operacional, e o arquivo escolhido é criado com o conteúdo do relatório.

### PASSO 5: processe o relatório com Streams

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public long contarQuadrados(String caminho) throws IOException {
    try (Stream<String> linhas = Files.lines(Path.of(caminho))) {
        return linhas.filter(l -> l.startsWith("Quadrado")).count();
    }
}
```

`Files.lines` retorna um `Stream<String>` preguiçoso — as linhas só são lidas do disco conforme o pipeline (`filter`, `count`) as consome, sem carregar o arquivo inteiro na memória de uma vez. Streams de I/O também precisam de try-with-resources, pelo mesmo motivo de qualquer outro recurso que precisa ser fechado.

**✅ Checkpoint:** `contarQuadrados` retorna a contagem correta de linhas começando com "Quadrado".

### PASSO 6: combine Streams para uma estatística mais rica

```java
import java.util.stream.Collectors;

public Map<String, Long> contarPorTipo(List<Entidade> entidades) {
    return entidades.stream()
        .collect(Collectors.groupingBy(
            e -> e.getClass().getSimpleName(),
            Collectors.counting()
        ));
}
```

`Collectors.groupingBy` + `Collectors.counting()` substitui o laço manual com `HashMap` e `merge()` visto no Tópico 4 — a mesma tarefa, expressa de forma declarativa em uma linha: "agrupe por nome da classe, e conte quantos há em cada grupo".

**✅ Checkpoint:** `contarPorTipo(entidades)` retorna um `Map` com a contagem correta por tipo (por exemplo, `{Quadrado=2, Circulo=1}`).

### Resumo do que você construiu

- Escrita de relatório em arquivo com `PrintWriter` + try-with-resources.
- Leitura linha por linha com `BufferedReader`.
- API NIO.2 (`Files.write`) como alternativa moderna e mais simples.
- `FileChooser` deixando o usuário escolher o caminho por um diálogo nativo real.
- `Files.lines()` processando arquivo como Stream preguiçoso.
- `Collectors.groupingBy` + `counting()` como alternativa declarativa a laços manuais.

### Perguntas de fixação

1. Por que `Files.lines()` precisa de try-with-resources, se `Files.write()` (Passo 3) não precisou de nenhum `try` explícito?
2. O que `showSaveDialog` retorna quando o usuário clica em "Cancelar", e por que o código do Passo 4 trata isso explicitamente?
3. Em que sentido um `Stream<String>` de `Files.lines()` é "preguiçoso", e por que isso importa para um arquivo muito grande?
4. `Collectors.groupingBy(..., Collectors.counting())` substitui qual código do Tópico 4? Reescreva um usando o outro.

### Exercícios

1. **Exportar como CSV**: gere um arquivo `relatorio.csv` com colunas `tipo,x,y`.
2. **Importar entidades**: leia um CSV e reconstrua a lista de `Entidade` (reaproveitando a ideia de `reconstruir()` do Tópico 2).
3. **Filtro composto**: use Streams para contar entidades numa região específica do mapa (`x < 200`).

## 7. Padrão MVC em Desktop — Planejamento

**Objetivo:** entender o padrão MVC aplicado a uma aplicação desktop, e planejar como reorganizar `GerenciadorDeJogo` em Model/View/Controller. A implementação prática fica para o próximo tópico.

### Pré-requisitos

Projeto `javafx-game` com `GerenciadorDeJogo`, `Repositorio<T>` e a tela FXML de fases (Tópico 3). **✅ Checkpoint:** o jogo compila e roda.

### PASSO 1: entenda as três camadas no contexto desktop

| Camada | Responsabilidade | Hoje no seu projeto |
|---|---|---|
| **Model** | Dados e regras (`Entidade`, `Repositorio`, cálculos) | Já existe, mas parte da lógica ainda vaza para o Controller |
| **View** | Arquivos FXML | Já existe (`fase.fxml`, do Tópico 3) |
| **Controller** | Classe `@FXML` que reage a eventos e atualiza a View | Já existe (`FaseController`), mas ainda faz cálculos que deveriam ser do Model |

Se você já viu MVC num framework web, o princípio é o mesmo — só o gatilho muda: em vez de uma requisição HTTP chegando, é um evento de UI (clique, seleção) que dispara o Controller.

**✅ Checkpoint:** você consegue mapear cada arquivo do seu projeto atual (`.fxml`, `*Controller.java`, classes de `model/`) para uma das três camadas.

### PASSO 2: identifique violações do MVC no controller atual

```java
// PROBLEMA: cálculo de negócio dentro do Controller
@FXML
private void aoClicarSalvar() {
    int pontuacaoFinal = 0;
    for (Entidade e : entidades) {
        if (e instanceof Quadrado) pontuacaoFinal += 10;
        if (e instanceof Circulo) pontuacaoFinal += 5;
    }
    labelPontuacao.setText("Pontuação: " + pontuacaoFinal);
}
```

Calcular "pontuação final" dentro do Controller mistura lógica de jogo com reação a evento de UI — se a fórmula de pontuação mudar, é preciso caçar dentro de código de tela, em vez de mudar num lugar previsível.

**✅ Checkpoint:** você aponta exatamente qual trecho do seu próprio `FaseController` (ou de um exemplo equivalente) mistura Model e Controller.

### PASSO 3: planeje onde cada responsabilidade deveria morar

```
GerenciadorDeJogo (Model)
  └── calcularPontuacaoTotal(): int      ← regra de negócio pura, sem JavaFX

FaseController (Controller)
  └── aoClicarSalvar()
        └── chama gerenciador.calcularPontuacaoTotal()
        └── atualiza labelPontuacao.setText(...)

fase.fxml (View)
  └── só estrutura visual, nenhum cálculo
```

O ponto-chave: `calcularPontuacaoTotal()` não deve conhecer `Label` nem nada de JavaFX — deve ser testável chamando apenas `new GerenciadorDeJogo(...)`, sem abrir uma janela. Essa é a mesma ideia por trás de `Entidade` não importar `javafx.*`, desde o Tópico 2.

**✅ Checkpoint:** você consegue explicar por que um método de Model "não deve importar `javafx.*`".

### PASSO 4: esboce a assinatura do método antes de implementar

```java
public class GerenciadorDeJogo {
    public int calcularPontuacaoTotal() {
        // implementação no próximo tópico
        throw new UnsupportedOperationException("Ainda não implementado");
    }
}
```

Escrever a assinatura primeiro (mesmo sem corpo funcional) documenta a decisão de design tomada nesta aula — a implementação de verdade, movendo a lógica do Controller para cá, é o tema do próximo tópico.

**✅ Checkpoint:** o projeto compila com o método esqueleto, mesmo lançando exceção se chamado.

### PASSO 5: liste os benefícios concretos dessa separação para o seu projeto

- **Testável**: `calcularPontuacaoTotal()` pode ser testado sem abrir a interface gráfica (você vai fazer isso de verdade no Tópico 11).
- **Reutilizável**: a mesma regra poderia ser usada num modo "replay" sem tela.
- **Manutenção**: mudar a fórmula de pontuação não exige tocar em código de UI.

**✅ Checkpoint:** você escreve, com suas palavras, pelo menos dois desses benefícios aplicados ao seu jogo.

### Resumo do que você construiu

- Entendimento das três camadas do MVC aplicadas a uma aplicação desktop JavaFX.
- Identificação de uma violação real (cálculo de pontuação dentro do Controller).
- Planejamento de onde cada responsabilidade deveria morar.
- Esqueleto de `calcularPontuacaoTotal()` no Model, sem lógica de UI.

### Perguntas de fixação

1. O que muda, na prática, entre "o Controller reage a um clique" e "o Controller reage a uma requisição HTTP" — e o que permanece igual?
2. Por que escrever a assinatura de um método antes de implementá-lo (Passo 4) é uma prática útil, mesmo lançando `UnsupportedOperationException`?
3. Dado o Controller do Passo 2, reescreva de cabeça (sem código) qual seria a versão corrigida, dividindo entre Model e Controller.

### Exercícios

1. Liste 3 outros trechos do seu Controller atual que deveriam migrar para o Model.
2. Diagrama: desenhe as três camadas do seu projeto e as setas de comunicação entre elas.
3. Pesquisa: compare MVC com MVVM (usado em outros frameworks) e anote uma diferença.

## 8. Implementação do MVC

**Objetivo:** implementar de fato a separação MVC planejada no tópico anterior, movendo lógica do Controller para o Model.

### Pré-requisitos

Projeto `javafx-game` com o esqueleto `calcularPontuacaoTotal()` do tópico anterior. **✅ Checkpoint:** o projeto compila (o método ainda lança `UnsupportedOperationException`).

### PASSO 1: implemente a lógica no Model

```java
public class GerenciadorDeJogo {
    private List<Entidade> entidades;

    public int calcularPontuacaoTotal() {
        int total = 0;
        for (Entidade e : entidades) {
            if (e instanceof Quadrado) total += 10;
            if (e instanceof Circulo) total += 5;
        }
        return total;
    }
}
```

A lógica é exatamente a mesma que estava no Controller — a diferença é onde ela mora agora: uma classe que não importa `javafx.*`, testável isoladamente.

**✅ Checkpoint:** `new GerenciadorDeJogo(entidades).calcularPontuacaoTotal()` retorna o valor correto, sem abrir nenhuma janela.

### PASSO 2: simplifique o Controller para só orquestrar

```java
@FXML
private void aoClicarSalvar() {
    int pontuacaoFinal = gerenciador.calcularPontuacaoTotal();
    labelPontuacao.setText("Pontuação: " + pontuacaoFinal);
}
```

O Controller agora tem uma única responsabilidade nessa ação: pedir o resultado ao Model e atualizar a View — nenhum `if (e instanceof ...)` sobrou aqui.

**✅ Checkpoint:** clicar "Salvar" na interface mostra a mesma pontuação de antes, mas o Controller ficou com metade das linhas.

### PASSO 3: escreva um teste simples do Model, sem JavaFX

```java
public class GerenciadorDeJogoTest {
    public static void main(String[] args) {
        List<Entidade> entidades = List.of(new Quadrado(0,0,10), new Circulo(0,0,10));
        GerenciadorDeJogo g = new GerenciadorDeJogo(entidades);

        int resultado = g.calcularPontuacaoTotal();
        assert resultado == 15 : "Esperado 15, obtido " + resultado;
        System.out.println("Teste passou!");
    }
}
```

Rodar esse `main` (com a flag `-ea`, que habilita `assert` — desligado por padrão em Java) não abre nenhuma janela: prova concreta do benefício discutido no tópico anterior. `assert` aqui é deliberadamente rudimentar — o Tópico 11 substitui esse teste manual por JUnit de verdade, uma ferramenta própria para isso.

**✅ Checkpoint:** `java -ea GerenciadorDeJogoTest` imprime "Teste passou!".

### PASSO 4: aplique o mesmo padrão a outra ação do Controller

```java
// ANTES (no Controller): lógica misturada
@FXML
private void aoClicarReiniciar() {
    entidades.clear();
    conquistasDesbloqueadas.clear();
    pontuacaoPorJogador.clear();
    labelStatus.setText("Jogo reiniciado");
}
```

Move para o Model:

```java
// DEPOIS: Model expõe reiniciar(), Controller só chama e atualiza a View
public void reiniciar() {
    entidades.clear();
    conquistasDesbloqueadas.clear();
    pontuacaoPorJogador.clear();
}

@FXML
private void aoClicarReiniciar() {
    gerenciador.reiniciar();
    labelStatus.setText("Jogo reiniciado");
}
```

**✅ Checkpoint:** clicar "Reiniciar" continua funcionando, com a lógica de limpeza agora vivendo no Model.

### PASSO 5: revise a estrutura final do projeto

```
model/
  Entidade.java, Quadrado.java, Circulo.java
  GerenciadorDeJogo.java   ← toda a lógica de jogo, zero import javafx.*
  Repositorio.java
dao/
  Conexao.java, EntidadeDAO.java, EntidadeDAOSQLite.java
  FaseDAO.java, FaseDAOImpl.java
controller/
  FaseController.java      ← só orquestra: chama Model, atualiza View
resources/jogo/
  fase.fxml
```

**✅ Checkpoint:** nenhum arquivo em `model/` importa `javafx.scene.*` ou `javafx.fxml.*` — confirme isso revisando os `import`s de cada arquivo.

### Resumo do que você construiu

- `calcularPontuacaoTotal()` implementado no Model, sem dependência de JavaFX.
- Controller simplificado, só orquestrando Model → View.
- Teste manual do Model rodando sem abrir interface gráfica.
- `reiniciar()` movido do Controller para o Model, mesmo padrão aplicado de novo.
- Estrutura de pastas `model/dao/controller` refletindo a separação real.

### Perguntas de fixação

1. O corpo de `calcularPontuacaoTotal()` é idêntico ao que estava no Controller antes. O que exatamente mudou, então, e por que isso importa?
2. Por que o teste do Passo 3 usa `assert` em vez de simplesmente imprimir o resultado com `System.out.println` e conferir visualmente?
3. Se `GerenciadorDeJogo` importasse `javafx.scene.control.Label` para atualizar a tela sozinho, o que exatamente se perderia?

### Exercícios

1. Migre mais um método: encontre outra lógica no Controller e mova para o Model.
2. JUnit: reescreva o teste manual do Passo 3 usando JUnit 5 (`@Test`, `assertEquals`) — o Tópico 11 mostra como, se quiser adiantar.
3. Verificação automatizada: escreva um teste que falha se algum arquivo em `model/` importar `javafx.*`.

## 9. Padrões de Projeto (Singleton, Factory)

**Objetivo:** aplicar dois padrões de projeto clássicos — Singleton para um gerenciador de configurações, Factory para criar entidades sem `if/else` espalhado pelo código.

### Pré-requisitos

Projeto `javafx-game` com `Conexao`, `EntidadeDAOSQLite` e a hierarquia `Entidade`/`Quadrado`/`Circulo`. **✅ Checkpoint:** o jogo compila e salva progresso no SQLite.

### PASSO 1: identifique o problema que o Singleton resolve

Um `GerenciadorDeConfiguracoes` que lê `config.properties` (ou guarda preferências como dificuldade e volume) deveria existir uma única vez no programa inteiro — abrir múltiplas instâncias desperdiça recursos e pode causar inconsistência (duas instâncias, cada uma achando que sabe a dificuldade atual, discordando entre si).

**✅ Checkpoint:** você consegue nomear algo no seu projeto que deveria ter uma única instância global.

### PASSO 2: implemente Singleton para o gerenciador de configurações

```java
public class GerenciadorDeConfiguracoes {
    private static GerenciadorDeConfiguracoes instancia;
    private final Map<String, String> valores = new HashMap<>();

    private GerenciadorDeConfiguracoes() {
        valores.put("dificuldade", "normal");
        valores.put("volume", "80");
    }

    public static synchronized GerenciadorDeConfiguracoes getInstance() {
        if (instancia == null) {
            instancia = new GerenciadorDeConfiguracoes();
        }
        return instancia;
    }

    public String get(String chave) {
        return valores.get(chave);
    }

    public void set(String chave, String valor) {
        valores.put(chave, valor);
    }
}
```

Construtor `private` impede `new GerenciadorDeConfiguracoes()` de fora da classe — a única forma de obter a instância é via `getInstance()`, que cria uma vez e reutiliza sempre. `synchronized` evita que duas threads criem instâncias diferentes simultaneamente (um risco real, já que o JavaFX pode disparar eventos e tarefas de fundo em threads distintas).

**✅ Checkpoint:** `GerenciadorDeConfiguracoes.getInstance() == GerenciadorDeConfiguracoes.getInstance()` é `true` (mesma referência).

### PASSO 3: use o Singleton em qualquer parte do jogo, sem passar referência manualmente

```java
public class TelaOpcoes {
    public void aoMudarDificuldade(String novaDificuldade) {
        GerenciadorDeConfiguracoes.getInstance().set("dificuldade", novaDificuldade);
    }
}

public class MotorDeJogo {
    public void iniciarPartida() {
        String dificuldade = GerenciadorDeConfiguracoes.getInstance().get("dificuldade");
        System.out.println("Iniciando no modo: " + dificuldade);
    }
}
```

Nenhuma dessas duas classes precisa receber `GerenciadorDeConfiguracoes` via construtor — qualquer lugar do código acessa a mesma instância global chamando `getInstance()`.

**✅ Checkpoint:** mudar a dificuldade em `TelaOpcoes` é refletido imediatamente quando `MotorDeJogo` lê o valor.

### PASSO 4: identifique o problema que o Factory resolve

Lembre do `reconstruir()` do Tópico 2:

```java
// Espalhado pelo código, toda vez que uma entidade precisa ser criada:
Entidade e = tipo.equals("Quadrado") ? new Quadrado(x, y, tamanho) : new Circulo(x, y, tamanho);
```

Esse `if/else` está, em tese, duplicado em qualquer lugar do código que precise criar uma entidade a partir de um tipo em texto — quando um novo tipo de entidade for adicionado (por exemplo, `Triangulo`), é preciso lembrar de atualizar todos esses lugares.

**✅ Checkpoint:** você localiza, no seu próprio `EntidadeDAOSQLite`, o lugar exato onde esse `if/else` de criação aparece.

### PASSO 5: implemente uma Factory centralizando a criação

```java
public class EntidadeFactory {
    public static Entidade criar(String tipo, double x, double y, double tamanho) {
        return switch (tipo) {
            case "Quadrado" -> new Quadrado(x, y, tamanho);
            case "Circulo" -> new Circulo(x, y, tamanho);
            default -> throw new IllegalArgumentException("Tipo desconhecido: " + tipo);
        };
    }
}
```

E em `EntidadeDAOSQLite.reconstruir()`, substitua o `if/else` original:

```java
Entidade e = EntidadeFactory.criar(tipo, x, y, tamanho);
```

Agora existe um único lugar que sabe mapear string → classe concreta. Adicionar `Triangulo` no futuro significa editar só a Factory, não caçar `if/else` espalhados pelo projeto — e, se algum tipo desconhecido chegar (um bug de dados, por exemplo), o `default` lança uma exceção clara em vez de falhar silenciosamente.

**✅ Checkpoint:** substituir o `if/else` original pela chamada à Factory não muda o comportamento do jogo.

### Resumo do que você construiu

- Singleton `GerenciadorDeConfiguracoes`, com construtor `private` e `getInstance()` sincronizado.
- Acesso à mesma instância global de qualquer parte do código, sem passar referência manualmente.
- Identificação do problema de criação de objetos duplicada pelo código.
- `EntidadeFactory` centralizando a lógica de criação num único lugar.
- Substituição do `if/else` espalhado pela chamada à Factory.

### Perguntas de fixação

1. Por que o construtor de `GerenciadorDeConfiguracoes` é `private`, e o que quebraria se fosse `public`?
2. O que `synchronized` em `getInstance()` está protegendo, exatamente? Em que cenário sua ausência causaria um bug real?
3. Qual é a diferença entre "resolver o `if/else` de criação com uma Factory" e "resolver com um `switch` dentro da própria classe que precisa da entidade"?

### Exercícios

1. Adicione `Triangulo`: crie a nova classe e registre só na Factory, sem tocar em outro lugar.
2. `GerenciadorDeSom`: aplique o mesmo padrão Singleton a um gerenciador de efeitos sonoros.
3. Factory Method vs. Abstract Factory: pesquise a diferença e explique com suas palavras.

## 10. Debug e Logging

**Objetivo:** depurar com o depurador da IDE e substituir `System.out.println` por logging estruturado de verdade, com SLF4J e Logback.

### Pré-requisitos

Projeto `javafx-game` compilando normalmente numa IDE com depurador (IntelliJ ou VS Code com extensão Java). **✅ Checkpoint:** você consegue rodar o projeto em modo debug pela IDE.

### PASSO 1: coloque um breakpoint e inspecione o estado do jogo

Na IDE, clique na margem esquerda da linha `for (Entidade e : entidades) {` dentro de `calcularPontuacaoTotal()` para criar um *breakpoint*. Rode em modo debug e clique em "Salvar" na interface. Quando a execução parar, inspecione a variável `entidades` na aba de variáveis — você vê o conteúdo real da lista naquele momento exato, algo que um `println` espalhado pelo código não mostra de forma tão rica.

**✅ Checkpoint:** a execução pausa no *breakpoint* e você consegue ver o tamanho e o conteúdo de `entidades`.

### PASSO 2: use Step Over / Step Into para acompanhar a execução linha a linha

Com a execução pausada, use *Step Over* (F8 na maioria das IDEs) para avançar linha a linha dentro do mesmo método, ou *Step Into* (F7) para entrar dentro de uma chamada de método (por exemplo, `e.getClass()`). *Step Into* é útil quando você suspeita que o bug está dentro de um método chamado; *Step Over* quando você confia nesse método e só quer seguir o fluxo principal.

**✅ Checkpoint:** você consegue percorrer o laço `for` uma iteração de cada vez, observando `total` mudar.

### PASSO 3: substitua `println` por um logger de verdade

No `pom.xml`:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.16</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.12</version>
</dependency>
```

`slf4j-api` é uma *fachada* de logging: seu código chama sempre a mesma API, independente de qual biblioteca faz o trabalho de verdade por trás — nesse caso, o Logback. Em `GerenciadorDeJogo`:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GerenciadorDeJogo {
    private static final Logger logger = LoggerFactory.getLogger(GerenciadorDeJogo.class);

    public void adicionarEntidade(Entidade e) {
        logger.info("Entidade adicionada: {}", e.getClass().getSimpleName());
        entidades.add(e);
    }
}
```

`logger.info("...", e.getClass().getSimpleName())` usa placeholders (`{}`) em vez de concatenação de string — mais eficiente, porque a formatação só acontece se o nível de log estiver habilitado (concatenar strings manualmente custaria processamento mesmo quando o log não fosse usado).

**✅ Checkpoint:** rodar o jogo imprime linhas de log formatadas (com *timestamp* e nível) no console, no lugar dos `println` antigos.

### PASSO 4: use níveis de log apropriados para cada situação

```java
logger.debug("Calculando pontuação para {} entidades", entidades.size());
logger.info("Jogo salvo com sucesso");
logger.warn("Tentativa de salvar sem entidades no jogo");
logger.error("Falha ao conectar ao banco de dados", excecao);
```

`debug` é para detalhes úteis só durante desenvolvimento; `info` para eventos normais e relevantes (jogo salvo, fase iniciada); `warn` para algo estranho, mas que não quebra o jogo; `error` para falha real, geralmente acompanhada da exceção (`excecao` como último argumento inclui o *stack trace* completo automaticamente).

**✅ Checkpoint:** você consegue justificar por que uma falha de conexão com banco é `error`, não `warn`.

### PASSO 5: configure o nível mínimo de log via arquivo de configuração

Crie `src/main/resources/logback.xml`:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss} [%level] %logger{20} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

Com `level="INFO"`, chamadas a `logger.debug(...)` são ignoradas em produção sem remover o código — basta mudar esse arquivo para `DEBUG` durante uma investigação, sem recompilar nada.

**✅ Checkpoint:** trocar `level="INFO"` para `level="DEBUG"` no `logback.xml` faz as mensagens de debug aparecerem no console, sem recompilar o projeto.

### Resumo do que você construiu

- Uso de *breakpoints* e inspeção de variáveis em tempo real.
- *Step Over* vs. *Step Into* para navegar a execução linha a linha.
- SLF4J + Logback substituindo `println` por logging estruturado.
- Níveis de log (`debug`/`info`/`warn`/`error`) usados de forma apropriada.
- Configuração de nível mínimo via `logback.xml`, sem recompilar código.

### Perguntas de fixação

1. Por que `logger.info("...", variavel)` com placeholder `{}` é preferível a `logger.info("... " + variavel)`?
2. O que exatamente muda no comportamento do programa ao trocar `level="INFO"` para `level="DEBUG"` no `logback.xml`, sem recompilar?
3. Em que situação você usaria *Step Into* em vez de *Step Over* durante uma sessão de debug?

### Exercícios

1. *Conditional breakpoint*: configure um *breakpoint* que só pausa quando `entidades.size() > 5`.
2. Log em arquivo: adicione um `FileAppender` ao `logback.xml`, salvando logs em disco.
3. MDC: pesquise *Mapped Diagnostic Context* para rastrear logs de uma mesma "partida" de jogo.

## 11. Testes Unitários (JUnit + TestFX)

**Objetivo:** formalizar o teste manual feito no Tópico 8 usando JUnit 5, o framework padrão de testes em Java — e, indo além do Model, testar a própria tela JavaFX de verdade com TestFX.

### Pré-requisitos

Projeto `javafx-game` com `GerenciadorDeJogo.calcularPontuacaoTotal()` já extraído do Controller. **✅ Checkpoint:** o jogo compila.

### PASSO 1: adicione o JUnit 5 ao projeto

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.4</version>
    <scope>test</scope>
</dependency>
```

`<scope>test</scope>` garante que essa dependência só existe durante os testes — não vai para o `.jar` final do jogo. Adicione também o plugin do Maven que sabe rodar testes JUnit 5:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.5.2</version>
</plugin>
```

**✅ Checkpoint:** `mvn test` roda sem erro (mesmo sem nenhum teste ainda).

### PASSO 2: escreva seu primeiro teste

```java
// src/test/java/jogo/GerenciadorDeJogoTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GerenciadorDeJogoTest {
    @Test
    void calculaPontuacaoCorretamenteComEntidadesMistas() {
        List<Entidade> entidades = List.of(new Quadrado(0,0,10), new Circulo(0,0,10));
        GerenciadorDeJogo g = new GerenciadorDeJogo(entidades);

        int resultado = g.calcularPontuacaoTotal();

        assertEquals(15, resultado);
    }
}
```

`@Test` marca o método como um caso de teste; `assertEquals(esperado, obtido)` falha o teste (com mensagem clara, indicando os dois valores) se os valores não baterem — muito mais informativo que o `assert` manual do Tópico 8. Repare que `src/test/java` é uma pasta separada de `src/main/java` — o Maven já sabe, por convenção, que tudo ali é código de teste, compilado e rodado só quando você chama `mvn test`.

**✅ Checkpoint:** `mvn test` executa e mostra "1 test passed" (ou equivalente).

### PASSO 3: teste um caso de borda — lista vazia

```java
@Test
void pontuacaoDeListaVaziaEhZero() {
    GerenciadorDeJogo g = new GerenciadorDeJogo(List.of());
    assertEquals(0, g.calcularPontuacaoTotal());
}
```

Testar o caso vazio/limite é tão importante quanto o caso "normal" — muitos bugs reais aparecem exatamente nas bordas (lista vazia, número zero, string nula).

**✅ Checkpoint:** o novo teste passa.

### PASSO 4: teste que uma exceção é lançada corretamente

```java
@Test
void adicionarEntidadeNulaLancaExcecao() {
    GerenciadorDeJogo g = new GerenciadorDeJogo(new ArrayList<>());

    assertThrows(EntidadeInvalidaException.class, () -> {
        g.adicionarEntidade(null);
    });
}
```

`assertThrows(Classe, () -> {...})` verifica que o código dentro do lambda lança exatamente a exceção esperada — se lançar uma exceção diferente ou nenhuma, o teste falha.

**✅ Checkpoint:** o teste passa, confirmando que `EntidadeInvalidaException` é lançada corretamente.

### PASSO 5: organize múltiplos testes com `@BeforeEach`

```java
class GerenciadorDeJogoTest {
    private GerenciadorDeJogo gerenciador;

    @BeforeEach
    void configurar() {
        gerenciador = new GerenciadorDeJogo(new ArrayList<>());
    }

    @Test
    void comecaComPontuacaoZero() {
        assertEquals(0, gerenciador.calcularPontuacaoTotal());
    }

    @Test
    void adicionarQuadradoAumentaPontuacao() {
        gerenciador.adicionarEntidade(new Quadrado(0,0,10));
        assertEquals(10, gerenciador.calcularPontuacaoTotal());
    }
}
```

`@BeforeEach` roda antes de cada teste, garantindo que `gerenciador` começa "limpo" em todos eles — evita que o resultado de um teste dependa da ordem de execução dos outros, uma armadilha comum quando testes compartilham estado sem perceber.

**✅ Checkpoint:** todos os testes da classe passam, mesmo executados em ordens diferentes.

### PASSO 6: indo além — teste a tela de verdade com TestFX

Tudo até aqui testou o Model, exatamente porque o Model não depende de JavaFX. Mas a `TableView` do `GerenciadorFasesApp` (Tópico 3) também merece um teste — e testar *view* de verdade, clicando em campos e botões como um usuário faria, é o que o **TestFX** existe para fazer.

No `pom.xml`:

```xml
<dependency>
    <groupId>org.testfx</groupId>
    <artifactId>testfx-core</artifactId>
    <version>4.0.18</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testfx</groupId>
    <artifactId>testfx-junit5</artifactId>
    <version>4.0.18</version>
    <scope>test</scope>
</dependency>
```

```java
// src/test/java/jogo/GerenciadorFasesUiTest.java
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import jogo.model.Fase;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GerenciadorFasesUiTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/jogo/fase.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 520, 420));
        stage.show();
    }

    @Test
    void salvarNovaFasePelaTelaAdicionaLinhaNaTabela() {
        clickOn("#campoNome").write("Fase Teste");
        clickOn("#campoDificuldade").write("3");
        clickOn("#campoGravidade").write("0.7");
        clickOn("#btnSalvar");

        TableView<Fase> tabela = lookup("#tabela").queryTableView();
        assertTrue(tabela.getItems().stream().anyMatch(f -> f.getNome().equals("Fase Teste")));
    }
}
```

`ApplicationTest`, da própria TestFX, sobe uma instância real do runtime JavaFX antes de cada teste e a encerra depois — o `start(Stage)` aqui faz exatamente o mesmo que `GerenciadorFasesApp` faz, carregando o FXML de verdade. `clickOn("#campoNome")` localiza o componente pelo `fx:id` (o mesmo `#` usado em `onAction` no FXML) e simula um clique real nele; `.write("Fase Teste")` simula digitação real, tecla por tecla. Isso não é uma simulação superficial: é a mesma árvore de componentes, os mesmos eventos, que rodariam se um humano estivesse clicando.

Se a máquina que roda os testes não tem um monitor físico (como um servidor de integração contínua), o TestFX ainda funciona rodando contra um **X virtual** (`Xvfb`, no Linux) — um display que não desenha em tela nenhuma, mas se comporta como um display real para qualquer aplicação gráfica, JavaFX incluso. Esta é a abordagem usada para verificar este próprio tutorial: nenhum monitor conectado, mas os testes de UI rodaram de verdade, clicando e digitando, contra um X virtual.

**✅ Checkpoint:** o teste passa, confirmando que salvar pela tela de verdade adiciona a linha esperada na `TableView` — não é mais só o Model sendo testado, é a interface inteira.

### Resumo do que você construiu

- JUnit 5 configurado com `scope test`.
- Testes com `@Test` e `assertEquals`, substituindo o `assert` manual do Tópico 8.
- Teste de caso de borda (lista vazia).
- `assertThrows` verificando que a exceção correta é lançada.
- `@BeforeEach` isolando o setup entre testes, garantindo independência.
- Um teste de UI real com **TestFX**, clicando e digitando na tela `fase.fxml` de verdade.

### Perguntas de fixação

1. Por que `src/test/java` é uma pasta separada de `src/main/java`, e o que aconteceria se os testes ficassem misturados com o código principal?
2. O que `@BeforeEach` evita que aconteceria se `gerenciador` fosse criado uma única vez, fora de qualquer método de teste?
3. Qual é a diferença entre o teste de `GerenciadorDeJogoTest` (Passo 2) e o de `GerenciadorFasesUiTest` (Passo 6) em termos do que cada um realmente verifica?

### Exercícios

1. Teste do `Repositorio`: teste `salvar`/`buscarPorId`/`remover` isoladamente.
2. Teste parametrizado: pesquise `@ParameterizedTest` para testar vários tipos de entidade de uma vez.
3. Cobertura: rode `mvn test` com JaCoCo e veja a porcentagem de código coberta por testes.

## 12. Projeto Final

**Objetivo:** convergir todo o conteúdo do semestre (POO, DAO/persistência, coleções, arquivos, MVC, padrões de projeto, testes) num projeto desktop individual ou em dupla, escolhido livremente, usando JavaFX 21.

### PASSO 1: escolha o tema do app desktop

Requisitos mínimos, reaproveitando o que foi construído no jogo ao longo do semestre:

1. Hierarquia de classes com pelo menos herança + polimorfismo.
2. Persistência com DAO + SQLite.
3. Interface JavaFX com `TableView` (ou equivalente) seguindo o padrão MVC.
4. Uso de pelo menos uma Coleção não trivial (`Map` ou `Set`) em alguma regra do sistema.
5. Pelo menos 3 testes unitários (JUnit) cobrindo regras de negócio.

**✅ Checkpoint:** você tem um tema de app escrito em uma frase, cobrindo os 5 requisitos.

### PASSO 2: escreva o documento de escopo

```
# Nome do Sistema: ___________

## Problema que resolve
(1-2 frases)

## Hierarquia de classes principal
- Classe base: ...
- Subclasses: ...

## Telas principais (JavaFX)
1. ...
2. ...

## Dados persistidos (SQLite)
- ...

## Regras de negócio a testar com JUnit
- ...
```

Um escopo por escrito evita o problema mais comum de projetos finais: começar a codar sem saber exatamente onde parar.

**✅ Checkpoint:** o documento de escopo está preenchido e revisado com o professor.

### PASSO 3: crie o projeto Maven com JavaFX e JUnit

```bash
mvn archetype:generate -DgroupId=app -DartifactId=projeto-final -DarchetypeArtifactId=maven-archetype-quickstart
```

Adicione JavaFX 21, SQLite JDBC e JUnit 5, reaproveitando as mesmas dependências e versões já usadas no `javafx-game` ao longo do semestre.

**✅ Checkpoint:** `mvn test` roda sem erro (mesmo sem nenhum teste ainda).

### PASSO 4: defina a estrutura de pacotes inicial

```
model/        ← classes de domínio (hierarquia, regras de negócio)
dao/          ← interfaces e implementações de persistência
controller/   ← controllers @FXML
resources/    ← arquivos .fxml
src/test/     ← testes JUnit espelhando a estrutura de model/ e dao/
```

Essa organização segue diretamente o padrão MVC já praticado nos Tópicos 7 e 8, com testes desde o início — não como algo adicionado depois do projeto pronto.

**✅ Checkpoint:** os pacotes existem no projeto, mesmo vazios.

### PASSO 5: planeje os marcos até a apresentação

| Marco | Prazo sugerido |
|---|---|
| Hierarquia de classes + primeiros testes | 1 semana |
| DAO + persistência SQLite funcionando | 2 semanas |
| Interface JavaFX (CRUD completo) | 3 semanas |
| Cobertura de testes + polimento | véspera da apresentação |

**✅ Checkpoint:** você anota suas próprias datas previstas para cada marco.

### Apresentação

Formato sugerido: 8 minutos de apresentação + 3 minutos de perguntas por grupo.

1. **Problema** (30s): que problema o sistema resolve?
2. **Demo ao vivo** (4 min): navegue pelas telas do CRUD, mostrando o fluxo completo (criar, listar, editar, excluir).
3. **Decisões técnicas** (2 min): hierarquia de classes escolhida, como o DAO isola a persistência, quais padrões de projeto foram usados.
4. **Desafios enfrentados** (1 min): o que foi mais difícil e como foi resolvido.
5. **Perguntas** (3 min).

### Rubrica de avaliação

| Critério | Peso |
|---|---|
| CRUD completo funcionando sem crashes | 25% |
| Hierarquia de classes com herança/polimorfismo bem aplicada | 20% |
| Persistência via DAO + SQLite funcionando corretamente | 20% |
| Pelo menos 3 testes unitários (JUnit) cobrindo regras de negócio | 15% |
| Clareza da apresentação e resposta às perguntas | 20% |

### Checklist antes de apresentar

- [ ] O sistema roda a partir de um `.jar` limpo, não só pela IDE.
- [ ] Testou o CRUD completo pelo menos uma vez sem erros.
- [ ] Roda `mvn test` e todos os testes passam antes da apresentação.
- [ ] Sabe explicar, sem olhar o código, por que a lógica de negócio está no Model e não no Controller.

### Resumo do que você construiu

- Tema do sistema desktop definido, cobrindo os 5 requisitos mínimos.
- Documento de escopo escrito e revisado.
- Projeto Maven/JavaFX/JUnit criado e compilando.
- Estrutura de pacotes `model/dao/controller/test` definida.
- Marcos de progresso planejados até a apresentação.

Um exemplo completo, real e funcional deste Projeto Final — "Minha Estante", um gerenciador pessoal de livros e filmes — está disponível na implementação de referência deste tutorial, em `ds-projetos/projeto-final/`.

---

# Parte 2 — Robocode: um mini-projeto separado

Tudo que vem a seguir é um projeto novo, independente do `javafx-game` — não uma continuação dele. **Robocode** é um motor de batalha open source, também em Java: você escreve a estratégia de um robô de combate como uma classe comum, e o motor cuida de simular a arena, o movimento, os tiros e a física da batalha. O objetivo final é um torneio eliminatório entre os robôs de toda a turma.

## 13. Robocode: API — Movimento, tiros e sensoriamento

**Objetivo:** construir seu primeiro robô de combate, capaz de se mover, atirar e reagir a ser atingido.

### Pré-requisitos

Baixe o Robocode da distribuição oficial (`https://robocode.sourceforge.io`, ou o repositório `robo-code/robocode` no GitHub) e instale-o — é um instalador Java comum, que você roda com `java -jar robocode-<versão>-setup.jar`. A instalação já vem com uma pasta `robots/` cheia de robôs de exemplo (o pacote `sample`), usados como oponentes de teste ao longo deste mini-projeto.

**✅ Checkpoint:** um robô de exemplo (por exemplo, `sample.SittingDuck`) roda numa batalha de teste, aberta pela própria interface do Robocode.

### PASSO 1: crie a estrutura mínima de um robô

```java
import robocode.Robot;

public class MeuRobo extends Robot {
    public void run() {
        while (true) {
            ahead(100);
            turnGunRight(360);
        }
    }
}
```

Toda a lógica do robô vive dentro de `run()`, que roda em loop contínuo — `ahead(100)` move o robô, `turnGunRight(360)` gira o canhão procurando inimigos. Diferente do jogo JavaFX, aqui não existe `AnimationTimer`: o próprio `run()` é o loop, e cada comando de movimento (`ahead`, `turnGunRight`) bloqueia até terminar de executar, encadeando as ações uma após a outra.

**✅ Checkpoint:** o robô compila e se move em linha reta numa batalha de teste.

### PASSO 2: reaja a eventos com `onScannedRobot`

```java
public void onScannedRobot(ScannedRobotEvent e) {
    fire(1);
}
```

`onScannedRobot` é chamado automaticamente sempre que o canhão detecta um inimigo — o Robocode segue um modelo orientado a eventos, no mesmo espírito de um evento de clique no JavaFX: você não fica checando "tem inimigo à vista?" manualmente, o motor te avisa. `fire(1)` atira com potência 1.

**✅ Checkpoint:** o robô atira automaticamente sempre que avista um inimigo durante o giro do canhão.

### PASSO 3: use os dados do evento para mirar melhor

```java
public void onScannedRobot(ScannedRobotEvent e) {
    double anguloAbsoluto = getHeading() + e.getBearing();
    double giroNecessario = anguloAbsoluto - getGunHeading();

    turnGunRight(normalizeAngle(giroNecessario));
    fire(2);
}

double normalizeAngle(double angulo) {
    while (angulo > 180) angulo -= 360;
    while (angulo < -180) angulo += 360;
    return angulo;
}
```

`e.getBearing()` retorna o ângulo do inimigo relativo à *direção do seu robô* (não relativo ao mapa); somado a `getHeading()` (a direção absoluta do robô), dá o ângulo absoluto necessário para mirar com precisão. `normalizeAngle` existe porque ângulos em graus "dão a volta": girar 200° para a direita é o mesmo que girar 160° para a esquerda, e essa função sempre devolve o caminho mais curto, entre -180° e 180°.

**✅ Checkpoint:** o robô mira mais precisamente no inimigo detectado.

### PASSO 4: reaja a ser atingido com `onHitByBullet`

```java
public void onHitByBullet(HitByBulletEvent e) {
    turnRight(90 - e.getBearing());
    ahead(100);
}
```

Mudar de direção após ser atingido dificulta que o mesmo inimigo acerte tiros consecutivos, já que muitos robôs simples miram na sua última posição conhecida.

**✅ Checkpoint:** o robô muda de direção visivelmente após ser atingido.

### PASSO 5: combine tudo num robô funcional básico

```java
import robocode.*;

public class MeuRobo extends Robot {
    public void run() {
        setColors(Color.blue, Color.black, Color.cyan);
        while (true) {
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double anguloAbsoluto = getHeading() + e.getBearing();
        turnGunRight(normalizeAngle(anguloAbsoluto - getGunHeading()));
        fire(2);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnRight(90 - e.getBearing());
        ahead(100);
    }

    double normalizeAngle(double angulo) {
        while (angulo > 180) angulo -= 360;
        while (angulo < -180) angulo += 360;
        return angulo;
    }
}
```

**✅ Checkpoint:** o robô se move, mira, atira e reage a dano numa batalha de teste completa.

### Resumo do que você construiu

- Estrutura básica de robô com loop de movimento em `run()`.
- `onScannedRobot()` disparando tiro ao detectar inimigo.
- Cálculo de ângulo absoluto para mira mais precisa.
- `onHitByBullet()` reagindo a dano com mudança de direção.
- Um robô combinando movimento, mira e reação a dano numa única classe funcional.

### Perguntas de fixação

1. Por que `onScannedRobot` é um método que você sobrescreve, e não algo que você chama manualmente?
2. O que `normalizeAngle` resolve, exatamente, e o que aconteceria sem ela num giro de mais de 180°?
3. `e.getBearing()` (em `onHitByBullet`) é relativo a quê?

### Exercícios

1. Movimento evasivo aleatório: alterne entre esquerda/direita aleatoriamente.
2. Potência de tiro adaptativa: atire mais forte quando o inimigo está mais perto (`e.getDistance()`).
3. `onRobotDeath`: pesquise esse evento e reaja quando outro robô é eliminado.

## Ferramentas: batalhas via VSCode, sem o menu do Robocode

**Objetivo:** rodar `MeuRobo` contra qualquer robô de exemplo, escolhido na hora, direto do VSCode — sem abrir a interface do Robocode e montar uma batalha manualmente pelos menus (*File → New Battle*, selecionar participantes, configurar rounds).

Duas peças novas de vocabulário antes de começar. Uma **VSCode Task** é um comando externo (compilar, rodar um script, etc.) registrado no workspace e disparado pelo command palette, em vez de digitado manualmente no terminal toda vez — o mesmo papel que um "run configuration" cumpre noutras IDEs, ou um alvo de `Makefile` chamado por atalho. E um **input variable** de uma task é o que permite que ela não rode sempre o mesmo comando fixo: em vez disso, a task pausa e pergunta algo ao usuário (aqui, via um dropdown) antes de montar o comando final — é assim que a mesma task serve para testar contra qualquer oponente, sem precisar de uma task separada para cada um.

### Pré-requisitos

Robô funcional do Tópico 13 (movimento, mira, reação a dano), e a pasta `ds-projetos/robocode-robo/` aberta no VSCode como pasta do workspace (é dela que `${workspaceFolder}` nas seções seguintes vai apontar). **✅ Checkpoint:** você já rodou `MeuRobo` contra um oponente de exemplo pela GUI do Robocode ou por linha de comando pelo menos uma vez (Tópico 13).

### PASSO 1: entenda o fluxo manual que está sendo eliminado

Hoje, testar contra um novo oponente pela GUI do Robocode significa abrir o programa, ir em *Battle → New*, adicionar os dois robôs na lista, configurar o número de rounds, iniciar a batalha, esperar a janela renderizar o combate e olhar o placar na tela — e repetir esse fluxo manual inteiro toda vez que você quer testar contra outro oponente. É lento, e o resultado não fica registrado em lugar nenhum além da sua memória. Uma Task automatiza exatamente essa sequência: compilar, implantar e rodar a batalha, com a saída (o placar real) impressa no terminal integrado do VSCode.

**✅ Checkpoint:** você consegue explicar, com suas palavras, quais passos manuais da GUI a Task vai substituir.

### PASSO 2: parametrize `RodarBatalhas.java` para aceitar um oponente por argumento

`RodarBatalhas.java` (criado no Tópico 17) já roda uma bateria fixa de 4 oponentes de exemplo. Para a Task funcionar com qualquer oponente escolhido no dropdown, ele precisa aceitar esse oponente como argumento de linha de comando — sem deixar de funcionar sem argumento nenhum, já que os Tópicos 15, 16 e 17 dependem do comportamento atual (os mesmos 4 oponentes fixos) para os números que já documentaram:

```java
// Sem argumentos: os 4 oponentes fixos de sempre (comportamento original,
// usado pelos Topicos 15/16/17 do tutorial). Com um argumento: uma unica
// batalha contra a classe passada em args[0], mesmo formato de saida.
String[] oponentes = (args.length > 0)
        ? new String[]{args[0]}
        : new String[]{"sample.Corners", "sample.Crazy", "sample.Walls", "sample.RamFire"};
```

O restante do arquivo não muda: o mesmo `for` que já percorria os 4 oponentes agora percorre só 1 quando há argumento, e o tratamento de erro que já existia (`"OPONENTE NAO ENCONTRADO: " + oponenteClasse`, quando a classe não existe no repositório) continua valendo sem nenhum código novo — passar um nome de classe com erro de digitação produz a mesma mensagem clara de sempre.

**✅ Checkpoint:** `RodarBatalhas` compilado roda `RodarBatalhas sample.SittingDuck` (uma batalha só) e também `RodarBatalhas` sem argumento (os 4 de sempre) sem erro.

### PASSO 3: crie o script que compila, implanta e roda a batalha

A Task não vai chamar `javac`/`java` diretamente — ela chama um script que encadeia os mesmos passos que a seção "Como compilar e implantar" do `README.md` já documenta manualmente. Como os alunos deste curso podem estar em Linux/macOS ou em Windows, existem dois scripts equivalentes, um por sistema operacional; a Task escolhe o certo sozinha (PASSO 4).

**Linux/macOS — `tools/rodar-batalha-vscode.sh`:**

```bash
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
```

**Windows — `tools/rodar-batalha-vscode.ps1`:**

```powershell
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
# frente ao script bash, alem da sintaxe da propria linguagem.
java -DNOSECURITY=true -cp "$BuildTools;$RobocodeHome\libs\*" RodarBatalhas $Oponente
$exitCode = $LASTEXITCODE

Remove-Item $BuildTools -Recurse -Force -ErrorAction SilentlyContinue
if ($exitCode -ne 0) { exit 1 }
```

O único detalhe que realmente importa entre os dois, além da sintaxe de cada linguagem de script: **o separador de classpath**. No `-cp` do Java, Linux/macOS usa `:` entre entradas (`"$BUILD_TOOLS:$ROBOCODE_HOME/libs/*"`) e Windows usa `;` (`"$BuildTools;$RobocodeHome\libs\*"`) — é o detalhe mais fácil de copiar errado de um script para o outro sem perceber, porque o erro só aparece na hora de rodar (`ClassNotFoundException` ou "could not find or load main class"), não na hora de compilar. Fora isso, os dois scripts seguem exatamente os mesmos 4 passos, na mesma ordem, incluindo a mesma correção do `.class` da classe interna `Onda` do Wave Surfing (copiar `*.class`, não só `MeuRobo.class`) que o README já documenta.

**✅ Checkpoint:** `chmod +x tools/rodar-batalha-vscode.sh` (Linux/macOS) rodado uma vez.

### PASSO 4: crie `.vscode/tasks.json` com o dropdown de oponente

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "Robocode: batalha rápida contra oponente à escolha",
      "type": "shell",
      "command": "${workspaceFolder}/tools/rodar-batalha-vscode.sh",
      "args": ["${input:oponente}"],
      "windows": {
        "command": "powershell.exe",
        "args": [
          "-ExecutionPolicy", "Bypass",
          "-File", "${workspaceFolder}\\tools\\rodar-batalha-vscode.ps1",
          "${input:oponente}"
        ]
      },
      "group": "test",
      "presentation": {
        "reveal": "always",
        "panel": "dedicated"
      },
      "problemMatcher": []
    }
  ],
  "inputs": [
    {
      "id": "oponente",
      "type": "pickString",
      "description": "Escolha o robô de exemplo para MeuRobo enfrentar",
      "options": [
        { "label": "SittingDuck — o mais simples, parado (bom primeiro teste)", "value": "sample.SittingDuck" },
        { "label": "Corners — foge para o canto e atira parado", "value": "sample.Corners" },
        { "label": "Crazy — se move de forma errática (resultado varia bastante entre execuções)", "value": "sample.Crazy" },
        { "label": "Walls — percorre a borda da arena", "value": "sample.Walls" },
        { "label": "RamFire — tenta colidir de propósito", "value": "sample.RamFire" },
        { "label": "TrackFire — oponente do exercício do Tópico 17", "value": "sample.TrackFire" }
      ],
      "default": "sample.SittingDuck"
    }
  ]
}
```

Cada campo tem um papel específico:

- `label`: o texto que aparece na lista do "Tasks: Run Task" — puramente descritivo, **não** aceita `${input:...}` (a substituição de input variable só funciona dentro de `command`, `args` e `options` de uma task; o VSCode monta os labels antes de resolver os inputs).
- `command` / `args`: onde `${input:oponente}` de fato entra — o valor escolhido no dropdown vira o argumento passado ao script. O bloco `windows` sobrescreve só esses dois campos quando o sistema operacional é Windows; em Linux e macOS (que não têm bloco próprio) valem os `command`/`args` da task normalmente.
- `inputs[].id`: o nome usado em `${input:oponente}` — precisa bater exatamente.
- `inputs[].type: "pickString"`: um dropdown de opções fixas (as outras opções são `"promptString"`, texto livre, e `"command"`, que roda outro comando para gerar o valor).
- `inputs[].options`: cada opção pode ser uma string simples ou um objeto `{"label": "...", "value": "..."}` — usado aqui para cada oponente, onde o `label` explica o estilo do robô (reaproveitando as descrições que este mesmo tutorial já usa no Tópico 17) e o `value` é o nome real da classe que o script recebe. O dropdown mostra `label: value`.
- `inputs[].default`: a opção pré-selecionada quando o dropdown abre.

Não há aninhamento de input variables (um input não pode referenciar outro), e por isso o número de rounds continua fixo em 10 dentro de `RodarBatalhas.java` — torná-lo configurável também é um dos exercícios a seguir.

**✅ Checkpoint:** `python3 -m json.tool .vscode/tasks.json` (ou `node -e "JSON.parse(require('fs').readFileSync('.vscode/tasks.json'))"`) roda sem erro, confirmando que o JSON é válido.

### PASSO 5: rode a task de verdade

`Ctrl+Shift+P` (ou `Cmd+Shift+P` no macOS) → digite "Tasks: Run Task" → selecione a task pelo `label` ("Robocode: batalha rápida contra oponente à escolha") → o VSCode mostra o dropdown com os 6 oponentes → escolha um → o terminal integrado abre (num painel dedicado, por causa de `"panel": "dedicated"`) e mostra a saída da batalha em tempo real. Nota rápida sobre atalhos: `Ctrl+Shift+B`/`Cmd+Shift+B` roda a *build task* padrão do workspace, não esta — "Run Task" genérico (o comando acima) é o caminho normal para uma task que não é de build. Se quiser rodar esta task com `Ctrl+Shift+B`, é preciso marcá-la explicitamente como padrão com `"group": {"kind": "test", "isDefault": true}` em vez de só `"group": "test"`.

**Honestidade sobre o que foi verificado:** o comando por trás da task — `rodar-batalha-vscode.sh`, chamado com um oponente escolhido — foi rodado de verdade nesta sessão, do início ao fim (compilação, implantação, batalha), contra dois oponentes diferentes, e a saída abaixo é a saída real dessas execuções, sem edição além de remover as linhas de log de carregamento interno do Robocode (`Loaded net.sf.robocode.*`) por brevidade:

```
$ ./tools/rodar-batalha-vscode.sh sample.SittingDuck
== 1/4: compilando MeuRobo.java ==
== 2/4: implantando em /home/.../robocode/robots/meurobo ==
== 3/4: compilando RodarBatalhas.java ==
== 4/4: rodando batalha contra sample.SittingDuck ==
=== MeuRobo vs sample.SittingDuck (10 rounds) ===
meurobo.MeuRobo 1.0*      score=  1280 survival=   400 bulletDmg=   680 ramDmg=     0 1st= 8 2nd= 2 3rd= 0
sample.SittingDuck        score=   120 survival=   100 bulletDmg=     0 ramDmg=     0 1st= 2 2nd= 8 3rd= 0

$ ./tools/rodar-batalha-vscode.sh sample.TrackFire
== 1/4: compilando MeuRobo.java ==
== 2/4: implantando em /home/.../robocode/robots/meurobo ==
== 3/4: compilando RodarBatalhas.java ==
== 4/4: rodando batalha contra sample.TrackFire ==
=== MeuRobo vs sample.TrackFire (10 rounds) ===
meurobo.MeuRobo 1.0*      score=   641 survival=   100 bulletDmg=   471 ramDmg=     8 1st= 2 2nd= 8 3rd= 0
sample.TrackFire          score=  1531 survival=   400 bulletDmg=   891 ramDmg=     0 1st= 8 2nd= 2 3rd= 0
```

`MeuRobo` venceu 8/10 rounds contra `SittingDuck` (esperado — é o oponente mais simples, parado e sem mira) e apenas 2/10 contra `TrackFire`, que rastreia e atira de volta de forma mais persistente; é a mesma variação real, entre estilos de oponente, já discutida no Tópico 17.

Duas lacunas reais e específicas ficam de fora dessa verificação, e é importante ser direto sobre isso em vez de deixar implícito: este ambiente sandbox não tem interface gráfica interativa, então clicar literalmente em "Tasks: Run Task" e escolher uma opção no dropdown do VSCode não pôde ser demonstrado — o mecanismo por trás (o script, chamado com um oponente) é idêntico ao que a Task dispara, então o comportamento é o mesmo, mas a interação de clique em si não foi gravada. E o script Windows (`rodar-batalha-vscode.ps1`) foi escrito espelhando cuidadosamente a lógica já verificada do script bash — mesmos 4 passos, mesma correção da classe `Onda`, mesmo separador de classpath corrigido para `;` — mas este ambiente não tem PowerShell nem uma instalação do Robocode em layout Windows, então ele não pôde ser executado de ponta a ponta como o script bash foi. O código Java por trás dos dois (`RodarBatalhas.java`, `MeuRobo.java`) é o mesmo, já testado; a tradução do script de implantação para PowerShell, especificamente, não foi rodada de verdade.

**✅ Checkpoint:** rodar a task com pelo menos dois oponentes diferentes mostra, no terminal integrado, o placar real de cada batalha, sem precisar abrir a GUI do Robocode em nenhum momento.

### Resumo do que você construiu

- `RodarBatalhas.java` parametrizado para rodar uma única batalha contra um oponente recebido por argumento, sem quebrar o modo sem argumento (4 oponentes fixos) que os Tópicos 15–17 já usam.
- Um script por sistema operacional (`rodar-batalha-vscode.sh` para Linux/macOS, `rodar-batalha-vscode.ps1` para Windows) que encadeia compilar, implantar e rodar a batalha.
- Uma VSCode Task (`.vscode/tasks.json`) com um input `pickString`, que mostra um dropdown de oponentes e escolhe o script certo por sistema operacional via o campo `windows`.
- Duas batalhas reais rodadas de ponta a ponta contra oponentes diferentes, sem abrir a GUI do Robocode.
- Entendimento de quais campos de uma task aceitam `${input:...}` (`command`, `args`, `options`) e quais não aceitam (`label`).

### Perguntas de fixação

1. Por que só `command`, `args` e `options` suportam `${input:...}`, e não `label`?
2. O que aconteceria se você rodasse a task duas vezes seguidas com o MESMO oponente, sem alterar `MeuRobo.java` entre uma execução e outra — o resultado seria idêntico? Por quê (ou por que não)?
3. Por que o script implanta o robô (copia `.class`/`.properties` para `~/robocode/robots/`) a cada execução, em vez de só na primeira vez?

### Exercícios

1. Fácil: adicione mais um robô de exemplo ao dropdown (ex. `sample.SpinBot`, confirmando o comportamento real dele antes de descrevê-lo — rode uma batalha de teste contra ele, ou confira a documentação oficial do Robocode, antes de escrever a descrição no `label`).
2. Médio: adicione um segundo input (`promptString`) para o número de rounds, em vez de fixar 10 — repasse esse valor também para `RodarBatalhas.java`.
3. Difícil/opcional: crie uma segunda task, com `dependsOn`, que só compila e implanta (sem rodar batalha) — útil para checar erros de compilação rapidamente sem esperar 10 rounds.

## 14. Robocode: de Robot para AdvancedRobot

**Objetivo:** evoluir de `Robot` (bloqueante) para `AdvancedRobot` (assíncrono), com radar lock e movimento circular.

### Pré-requisitos

Robô básico do tópico anterior (movimento, mira, dodge) funcionando. **✅ Checkpoint:** o robô atual vence `sample.SittingDuck` na maioria das batalhas de teste.

### PASSO 1: migre de `Robot` para `AdvancedRobot`

```java
import robocode.AdvancedRobot;

public class MeuRobo extends AdvancedRobot {
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
    }
}
```

`AdvancedRobot` permite mover corpo, canhão e radar de forma independente e assíncrona — diferente de `Robot`, onde os giros bloqueiam a execução até terminar. `setAdjustGunForRobotTurn(true)` e `setAdjustRadarForGunTurn(true)` desacoplam a mira do movimento do corpo: sem isso, girar o robô giraria o canhão junto, atrapalhando qualquer mira já calculada.

**✅ Checkpoint:** o robô compila como `AdvancedRobot` sem erros.

### PASSO 2: use comandos assíncronos com `set`

```java
public void run() {
    while (true) {
        setAhead(100);
        setTurnGunRight(360);
        execute();
    }
}
```

Métodos `set*` não bloqueiam — agendam a ação, e `execute()` aplica todas as mudanças pendentes simultaneamente, permitindo mover e mirar ao mesmo tempo, em vez de uma coisa de cada vez como no `Robot` do tópico anterior.

**✅ Checkpoint:** o robô se move e gira o canhão simultaneamente.

### PASSO 3: implemente um radar de trava (*lock*) no inimigo

```java
public void onScannedRobot(ScannedRobotEvent e) {
    double anguloAbsoluto = getHeading() + e.getBearing();
    double giroRadar = normalizeAngle(anguloAbsoluto - getRadarHeading());

    setTurnRadarRight(giroRadar * 2);
    setTurnGunRight(normalizeAngle(anguloAbsoluto - getGunHeading()));
    setFire(2);
}
```

Multiplicar o giro do radar por 2 é a técnica clássica de "radar lock": garante que o radar sempre re-escaneia um pouco além da posição do último inimigo visto, na direção em que ele provavelmente está se movendo — mantendo-o continuamente detectado, em vez de perdê-lo entre um `onScannedRobot` e o próximo.

**✅ Checkpoint:** o radar do robô "gruda" no inimigo detectado.

### PASSO 4: implemente movimento circular ao redor do inimigo

```java
public void onScannedRobot(ScannedRobotEvent e) {
    double anguloAbsoluto = getHeading() + e.getBearing();
    double anguloPerpendicular = anguloAbsoluto + 90;

    setTurnRight(normalizeAngle(anguloPerpendicular - getHeading()));
    setAhead(80);
}
```

Mover-se perpendicularmente ao inimigo (em vez de diretamente em direção a ele, ou parado) dificulta a mira de robôs com mira "linear" simples — a estratégia clássica de *circling*, ou *strafing*.

**✅ Checkpoint:** o robô circula ao redor do inimigo, em vez de se mover em linha reta.

### PASSO 5: combine movimento circular, radar lock e mira num robô coeso

```java
public class MeuRobo extends AdvancedRobot {
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setTurnRadarRight(360);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double anguloAbsoluto = getHeading() + e.getBearing();

        setTurnRadarRight(normalizeAngle(anguloAbsoluto - getRadarHeading()) * 2);
        setTurnGunRight(normalizeAngle(anguloAbsoluto - getGunHeading()));
        setFire(e.getDistance() < 200 ? 3 : 1);

        setTurnRight(normalizeAngle(anguloAbsoluto + 90 - getHeading()));
        setAhead(80);

        execute();
    }

    double normalizeAngle(double angulo) {
        while (angulo > 180) angulo -= 360;
        while (angulo < -180) angulo += 360;
        return angulo;
    }
}
```

**✅ Checkpoint:** o robô combina radar lock, mira e movimento circular, vencendo consistentemente contra robôs simples como `sample.SittingDuck`.

### Resumo do que você construiu

- Migração de `Robot` para `AdvancedRobot`, com controle assíncrono.
- Uso de comandos `set` + `execute()` para ações simultâneas.
- Radar lock mantendo o inimigo continuamente detectado.
- Movimento circular perpendicular, dificultando a mira do oponente.
- Um robô coeso combinando as três técnicas.

### Perguntas de fixação

1. Qual é a diferença prática entre `turnGunRight(360)` (Tópico 13) e `setTurnGunRight(360)` seguido de `execute()`?
2. Por que multiplicar o giro do radar por 2 ajuda a mantê-lo travado no inimigo, em vez de girar exatamente o ângulo calculado?
3. `setAdjustGunForRobotTurn(true)` resolve qual problema especificamente?

### Exercícios

1. Distância adaptativa: ajuste a distância de *circling* conforme a energia restante.
2. Múltiplos alvos: pesquise como priorizar o inimigo mais fraco quando há vários na arena.
3. `onHitWall`: reaja quando o robô colide com a parede da arena (você vai formalizar isso mais adiante, no tópico de Refinamento e testes sistemáticos).

## 15. Robocode: Mira preditiva (Targeting)

**Objetivo:** substituir a mira direta (atirar na posição atual do inimigo) por mira preditiva, calculando onde o inimigo *vai estar* quando a bala chegar.

### Pré-requisitos

Robô `AdvancedRobot` com radar lock, mira direta e movimento circular do tópico anterior. **✅ Checkpoint:** o robô do Tópico 14 compila e vence a maioria das batalhas contra `sample.SittingDuck`.

### PASSO 1: por que mirar na posição atual do inimigo erra

Desde o Tópico 13, `MeuRobo` mira exatamente na posição em que o inimigo estava no instante do *scan* — mas a bala não chega instantaneamente. Ela leva `distância / velocidadeBala` *ticks* para percorrer o caminho, e a velocidade da bala no Robocode é `20 - 3 * potência` (com `potência` em `[0.1, 3.0]`). Durante esse tempo de viagem, um inimigo em movimento já saiu do lugar.

Um exemplo numérico concreto, com os números que o robô já usa: a um alvo a 400px de distância, atirando com potência 2, a velocidade da bala é `20 - 3*2 = 14` px/tick. O tempo até a bala chegar é `400 / 14 ≈ 28.6` *ticks*. Nesse tempo, um inimigo se movendo **perpendicularmente** à linha de tiro na velocidade linear máxima de um robô (8px/tick) percorre até `8 * 28.6 ≈ 228` px — mais de 12 vezes o raio de um robô (18px). Mirar onde o inimigo *estava*, e não onde ele *vai estar*, erra por uma margem enorme contra qualquer alvo que não esteja parado ou se movendo diretamente na linha de tiro.

**✅ Checkpoint:** para uma distância e potência à sua escolha, você consegue calcular quantos pixels um inimigo a 8px/tick percorre durante o tempo de viagem da bala.

### PASSO 2: Linear Targeting por refinamento iterativo

A forma "livro-texto" de resolver isso é uma equação quadrática fechada — mas há um jeito mais simples de implementar corretamente e de explicar: refinamento iterativo. A ideia é recalcular o tempo de viagem usando a posição prevista da rodada anterior, repetindo algumas vezes até estabilizar:

```java
private double[] preverPosicaoLinear(double inimigoX, double inimigoY,
                                      double velocidadeInimigo, double headingInimigoGraus,
                                      double minhaX, double minhaY, double velocidadeBala) {
    double headingRad = Math.toRadians(headingInimigoGraus);
    double vx = velocidadeInimigo * Math.sin(headingRad);
    double vy = velocidadeInimigo * Math.cos(headingRad);

    double previstoX = inimigoX;
    double previstoY = inimigoY;
    double tempo = 0;

    for (int i = 0; i < 10; i++) {
        double dx = previstoX - minhaX;
        double dy = previstoY - minhaY;
        tempo = Math.sqrt(dx * dx + dy * dy) / velocidadeBala;
        previstoX = clamp(inimigoX + vx * tempo, 18, getBattleFieldWidth() - 18);
        previstoY = clamp(inimigoY + vy * tempo, 18, getBattleFieldHeight() - 18);
    }

    return new double[]{previstoX, previstoY};
}

private double clamp(double v, double min, double max) {
    return Math.max(min, Math.min(max, v));
}
```

`vx`/`vy` decompõem a velocidade do inimigo nos eixos X/Y a partir do heading dele, na mesma convenção que o resto do arquivo já usa (`x += velocidade * sin(heading)`, `y += velocidade * cos(heading)`). Cada iteração do laço recalcula o tempo de viagem com a posição prevista mais recente, e a correção fica cada vez menor a cada passagem — na prática, converge em poucas iterações para qualquer combinação de velocidade e distância dentro da faixa normal de uma batalha, então 10 iterações é uma margem confortável, não um ajuste fino. `clamp` é um helper de min/max que evita prever uma posição fora da arena. Repare que `getBattleFieldWidth()`/`getBattleFieldHeight()` (métodos reais de `AdvancedRobot`, herdados de `Robot`) substituem qualquer largura/altura fixa — o robô continua funcionando em campos de qualquer tamanho, não só 800×600.

**✅ Checkpoint:** `preverPosicaoLinear` compila e, chamada com valores de teste (ex.: um inimigo parado, `velocidadeInimigo = 0`), devolve a própria posição do inimigo sem alteração.

### PASSO 3: generalize para Circular Targeting

Linear Targeting assume que o inimigo mantém heading e velocidade constantes — uma assunção ruim contra qualquer robô em curva (como o próprio `MeuRobo`, que já circula em torno do inimigo desde o Tópico 14). Circular Targeting simula o inimigo tick a tick, aplicando a variação de heading observada entre os dois últimos *scans*.

Primeiro, dois campos novos para guardar essa variação, atualizados a cada `onScannedRobot`:

```java
private double headingInimigoAnterior = Double.NaN; // NaN = ainda não temos um scan anterior
private double variacaoHeadingInimigo = 0;
```

Dentro de `onScannedRobot`, antes de sobrescrever `headingInimigoAnterior` com o heading atual:

```java
// Acompanha a variação de heading do inimigo entre scans (Circular Targeting).
double headingInimigoAtual = e.getHeading(); // confirmado via javap: ScannedRobotEvent.getHeading() já vem em graus
if (!Double.isNaN(headingInimigoAnterior)) {
    variacaoHeadingInimigo = normalizeAngle(headingInimigoAtual - headingInimigoAnterior);
}
headingInimigoAnterior = headingInimigoAtual;
```

(Antes de usar `e.getHeading()`, vale a pena confirmar a assinatura contra a API real instalada em vez de supor — `javap -cp ~/robocode/libs/robocode.jar robocode.ScannedRobotEvent` lista tanto `getHeading()` quanto `getHeadingRadians()`, confirmando que a versão sem "Radians" já devolve graus diretamente, na mesma convenção usada no resto do arquivo.)

Com a variação de heading em mãos, a previsão vira uma simulação passo a passo:

```java
private double[] preverPosicaoCircular(double inimigoX, double inimigoY,
                                        double velocidadeInimigo, double headingInimigoGraus,
                                        double variacaoHeadingGraus,
                                        double minhaX, double minhaY, double velocidadeBala) {
    double simX = inimigoX, simY = inimigoY;
    double simHeadingGraus = headingInimigoGraus;
    int maxPassos = 500; // margem generosa; ver explicação abaixo

    for (int passo = 1; passo <= maxPassos; passo++) {
        simHeadingGraus = normalizeAngle(simHeadingGraus + variacaoHeadingGraus);
        double rad = Math.toRadians(simHeadingGraus);
        simX = clamp(simX + velocidadeInimigo * Math.sin(rad), 18, getBattleFieldWidth() - 18);
        simY = clamp(simY + velocidadeInimigo * Math.cos(rad), 18, getBattleFieldHeight() - 18);

        double dx = simX - minhaX, dy = simY - minhaY;
        double distanciaAteSimulado = Math.sqrt(dx * dx + dy * dy);
        if (velocidadeBala * passo >= distanciaAteSimulado) {
            break; // a bala alcançaria a posição simulada neste passo
        }
    }
    return new double[]{simX, simY};
}
```

A cada passo simulado, o heading do inimigo gira pela mesma variação observada no último intervalo entre *scans* — se essa variação for 0, a trajetória simulada vira uma reta, e o resultado converge para o mesmo que o Linear Targeting do Passo 2 (Circular Targeting generaliza o caso linear, não o substitui). `maxPassos = 500` é uma margem generosa e não um ajuste fino: no pior caso, a bala mais lenta é a de potência máxima (`velocidadeBala = 20 - 3*3 = 11`), e mesmo assim 500 passos cobrem com folga a diagonal de qualquer campo Robocode razoável, a um custo de CPU irrelevante (é só um laço simples, roda em microssegundos por *tick*).

**✅ Checkpoint:** `preverPosicaoCircular` compila; com `variacaoHeadingGraus = 0`, a posição prevista bate com `preverPosicaoLinear` para os mesmos parâmetros.

### PASSO 4: combine tudo numa função `mirar()`

Uma única função decide o ângulo de mira e gira o canhão, substituindo a linha de mira direta que o robô usava desde o Tópico 13 (`setTurnGunRight(normalizeAngle(anguloAbsoluto - getGunHeading()))`). Ela usa sempre a previsão circular — como o Passo 3 mostrou, ela já cobre o caso linear quando a variação de heading é zero, então manter as duas previsões separadas em produção seria redundante:

```java
private void mirar(double inimigoX, double inimigoY, double velocidadeInimigo,
                    double headingInimigoGraus, double potencia) {
    double velocidadeBala = 20 - 3 * potencia;
    double anguloMiraGraus;

    if (velocidadeBala > 0 && !Double.isNaN(velocidadeBala)) {
        double[] previsto = preverPosicaoCircular(inimigoX, inimigoY, velocidadeInimigo,
                headingInimigoGraus, variacaoHeadingInimigo, getX(), getY(), velocidadeBala);
        anguloMiraGraus = Math.toDegrees(Math.atan2(previsto[0] - getX(), previsto[1] - getY()));
    } else {
        anguloMiraGraus = Math.toDegrees(Math.atan2(inimigoX - getX(), inimigoY - getY()));
    }

    setTurnGunRight(normalizeAngle(anguloMiraGraus - getGunHeading()));
}
```

O `if` é uma proteção defensiva, no mesmo espírito de "trate exceções defensivamente" que o tópico de Finalização já ensina: se a potência calculada produzir uma `velocidadeBala` inválida (`<= 0` ou `NaN` — não deveria acontecer com `calcularPotencia()` no intervalo `[0.1, 3.0]`, mas é barato se proteger), o robô cai de volta para mira direta na posição atual do inimigo em vez de arriscar uma previsão sem sentido. `mirar()` é chamada de dentro de `onScannedRobot`, no lugar da mira direta antiga.

**✅ Checkpoint:** o robô compila, mira à frente de um inimigo em movimento (visualmente perceptível numa batalha com interface gráfica) e continua funcionando normalmente (sem cair no fallback) durante uma batalha inteira.

### PASSO 5: bateria de batalhas reais — mira preditiva vs. mira direta

Mesmos 4 oponentes, mesmo runner `RodarBatalhas.java`, 10 *rounds* cada, comparando o baseline documentado no `README.md` do projeto (mira direta, robô do Tópico 14, uma única bateria registrada na época) com **3 baterias independentes** rodadas nesta sessão, já com Circular Targeting ativo (e ainda com o *circling* fixo do Tópico 14 como movimento — o Tópico 16 troca isso):

```
                     mira direta (baseline,     mira preditiva (Circular Targeting,
                     1 bateria registrada)      3 baterias rodadas nesta sessão)
vs sample.Corners:   0/10                       2/10 a 5/10
vs sample.Crazy:     2/10 a 5/10                 5/10 a 6/10
vs sample.Walls:     1/10                        4/10 a 5/10
vs sample.RamFire:   7/10                        6/10 a 9/10
```

**Descoberta real, e não prevista quando este material foi planejado:** rodar a mesma bateria várias vezes revelou que a variação entre execuções não é exclusividade de `Crazy` (cujo movimento é literalmente aleatório) — `Corners`, `Walls` e `RamFire` também produzem resultados diferentes de bateria para bateria, mesmo sendo oponentes com estratégia determinística. A causa mais provável é que o Robocode sorteia a posição inicial de cada robô a cada *round*, o que muda a dinâmica inicial do combate mesmo quando a lógica de ambos os lados é sempre a mesma. Isso significa que **uma única bateria de 10 *rounds*, sozinha, não é uma medida confiável** — é por isso que esta seção já reporta faixas (mínimo–máximo de 3 execuções), não um único número. Dito isso, mesmo com essa variação, a faixa inteira contra `Corners` e `Walls` fica acima do baseline de uma única bateria; contra `RamFire`, a faixa (6/10 a 9/10) chega a tocar por baixo o valor do baseline (7/10) — um lembrete de que "a mira preditiva ajuda" é verdade em geral, mas não é uma garantia absoluta em toda execução isolada. Nenhum desses números veio de estimativa — são batalhas headless reais, rodadas via `robocode.control.RobocodeEngine` nesta mesma sessão de trabalho.

**✅ Checkpoint:** você reproduz pelo menos uma dessas 4 batalhas na sua máquina e confirma uma taxa de vitória igual ou próxima à documentada aqui.

### Resumo do que você construiu

- Cálculo do tempo de viagem da bala e um exemplo numérico concreto mostrando por que mira direta erra contra alvo em movimento.
- Linear Targeting por refinamento iterativo, prevendo a posição futura do inimigo assumindo velocidade e heading constantes.
- Circular Targeting por simulação passo a passo, incorporando a variação de heading do inimigo entre *scans* — generaliza o caso linear como caso particular.
- Uma função `mirar()` única, usada em produção, com fallback defensivo para mira direta quando a velocidade de bala calculada é inválida.
- Bateria de batalhas reais comparando mira preditiva com o baseline de mira direta, com melhora real e mensurável nos 4 oponentes testados.

### Perguntas de fixação

1. Por que a previsão circular do Passo 3 cobre o caso linear do Passo 2 como um caso particular, em vez de precisar das duas implementações em produção?
2. Por que o método iterativo do Passo 2 converge em poucas iterações, em vez de exigir resolver a equação quadrática fechada?
3. O que `maxPassos = 500` está limitando, exatamente, na simulação do Passo 3 — e por que esse valor é uma margem generosa, e não um ajuste fino que precisa ser calibrado por oponente?

### Exercícios

1. Reduza `maxPassos` para um valor bem menor (ex.: 20) e rode uma bateria de testes contra `sample.RamFire`, que se aproxima rápido — o resultado piora de forma mensurável? Documente o que observar.
2. Pesquise e descreva em texto como suavizar `variacaoHeadingInimigo` com uma média móvel dos últimos *scans*, em vez de usar só a diferença entre os dois últimos — que problema isso resolveria contra um inimigo com movimento ruidoso *scan* a *scan*?
3. (Desafio opcional, não precisa estar implementado no robô de referência) Pesquise e explique em texto o que é **GuessFactor Targeting**: a técnica que substitui a simulação física do Passo 3 por uma tabela estatística de onde os disparos anteriores realmente acertaram/erraram, segmentada por faixas de distância/velocidade. Essa é exatamente a ideia central por trás do Wave Surfing do próximo tópico — só que aplicada à ofensiva (mira) em vez da defesa (movimento).

## 16. Robocode: Wave Surfing (movimento evasivo adaptativo)

**Objetivo:** substituir o *circling* fixo por um movimento evasivo que reage estatisticamente a onde os tiros do inimigo historicamente passam perto do seu robô.

Este tópico implementa uma versão **simplificada e deliberadamente escopada** de Wave Surfing, a técnica clássica de movimento evasivo do Robocode competitivo: aqui ela rastreia só o inimigo único de uma batalha 1v1, guarda uma única tabela de estatísticas não segmentada (a versão de torneio "de verdade" segmenta por faixa de distância, velocidade e proximidade de parede — fora de escopo aqui) e decide a direção olhando só um passo hipotético à frente para cada lado, em vez de simular vários *ticks* por candidato como as implementações de campeonato fazem. Isso é uma simplificação real e deliberada, feita para manter o assunto didaticamente tratável em uma aula — não um erro de implementação —, e o Passo 7 mostra, com números reais, onde essa simplificação custa desempenho.

### Pré-requisitos

Robô com radar lock e mira preditiva (Circular Targeting) do tópico anterior. **✅ Checkpoint:** a bateria de testes do Passo 5 do tópico anterior roda e reproduz números parecidos com os documentados lá.

### PASSO 1: por que um padrão de movimento fixo é previsível

Desde o Tópico 14, `MeuRobo` sempre gira 90° perpendicular ao inimigo e anda para frente — uma regra fixa, sempre no mesmo sentido. Isso já é melhor que ficar parado, mas continua sendo um **padrão**: qualquer oponente que observe algumas rodadas aprende que o robô sempre vai estar, daqui a alguns *ticks*, num ponto previsível da órbita — inclusive a própria mira preditiva que você acabou de construir no Tópico 15 seria capaz de aprender e explorar um padrão desse tipo, se estivesse do lado oposto da batalha.

A ideia central do Wave Surfing é trocar a regra fixa por uma decisão adaptativa: em vez de "sempre gire para o mesmo lado", rastrear estatisticamente onde as balas anteriores do inimigo *passariam* em relação à sua posição, e orbitar para o lado que historicamente foi mais seguro.

**✅ Checkpoint:** você consegue explicar, em uma frase, por que um padrão de movimento 100% fixo é uma fraqueza estrutural — não só teórica — contra um oponente adaptativo.

### PASSO 2: detecte disparo do inimigo pela queda de energia

O Robocode não avisa diretamente "o inimigo atirou" — mas atirar custa energia ao atirador, numa quantidade proporcional à potência (entre 0.1 e 3.0). Detectamos o disparo observando a queda de energia do inimigo entre dois *scans*:

```java
private double energiaAnteriorInimigo = 100.0;
```

```java
// Wave surfing: detecta disparo do inimigo pela queda de energia,
// antes de qualquer outra coisa (precisamos do estado "antes" do scan).
double quedaEnergia = energiaAnteriorInimigo - e.getEnergy();
if (quedaEnergia > 0.0 && quedaEnergia <= 3.0) {
    registrarNovaOnda(inimigoXAtual, inimigoYAtual, quedaEnergia);
}
energiaAnteriorInimigo = e.getEnergy();
```

O intervalo `(0.0, 3.0]` corresponde exatamente à faixa de potência válida de um tiro — uma queda fora dessa faixa não pode ter vindo de um disparo normal. Isso precisa rodar logo no início de `onScannedRobot`, antes de qualquer outro cálculo, porque depende do valor de energia "antes" deste *scan*, guardado do *scan* anterior.

Limitação honesta: uma colisão (com a parede ou com outro robô) no mesmo *tick* **também** reduz a energia do inimigo, e não é distinguível de um tiro só por esse sinal — é uma heurística conhecida e aceita na comunidade Robocode, não uma detecção perfeita. Um sinal ocasionalmente falso-positivo (registrar uma "onda" que na verdade foi uma colisão) é um custo aceito desta simplificação.

**✅ Checkpoint:** com `out.println` temporário dentro do `if`, você confirma que `registrarNovaOnda` é chamado nos momentos em que o inimigo atira (visível numa batalha com interface gráfica, comparando com o efeito visual do tiro).

### PASSO 3: classe `Onda` e rastreamento

Cada disparo detectado vira um objeto `Onda`, guardando de onde e quando ele partiu:

```java
private static class Onda {
    double origemX, origemY;
    long horaDisparo;
    double velocidadeBala;
    double anguloDiretoGraus; // ângulo absoluto da origem até minha posição no instante do disparo
}
```

```java
private final List<Onda> ondasAtivas = new ArrayList<>();

private void registrarNovaOnda(double inimigoX, double inimigoY, double quedaEnergia) {
    Onda onda = new Onda();
    onda.origemX = inimigoX;
    onda.origemY = inimigoY;
    onda.horaDisparo = getTime();
    onda.velocidadeBala = 20 - 3 * quedaEnergia;
    onda.anguloDiretoGraus = Math.toDegrees(Math.atan2(getX() - inimigoX, getY() - inimigoY));
    ondasAtivas.add(onda);
}
```

`velocidadeBala` é calculada a partir da própria queda de energia (`20 - 3 * quedaEnergia`), já que a queda de energia é exatamente a potência do tiro. `anguloDiretoGraus` é o *bearing* absoluto, em graus, da origem do tiro até a sua posição **no instante do disparo** — esse ângulo vira a "linha reta" de referência para medir, depois, o quanto você se desviou dela.

Uma onda avança fisicamente com o tempo — o raio da "bolha" de possíveis posições da bala cresce a cada *tick*. Isso precisa ser atualizado independentemente de haver um novo *scan* ou não, então roda a cada volta do laço principal de `run()`, antes de `execute()`:

```java
private void atualizarOndas() {
    Iterator<Onda> it = ondasAtivas.iterator();
    while (it.hasNext()) {
        Onda onda = it.next();
        double raioAtual = onda.velocidadeBala * (getTime() - onda.horaDisparo);
        double distanciaAteMim = Math.hypot(getX() - onda.origemX, getY() - onda.origemY);
        if (raioAtual >= distanciaAteMim) {
            registrarResultado(onda);
            it.remove();
        }
    }
}
```

```java
@Override
public void run() {
    setColors(Color.blue, Color.black, Color.cyan);
    setAdjustGunForRobotTurn(true);
    setAdjustRadarForGunTurn(true);
    setTurnRadarRight(360);

    while (true) {
        atualizarOndas();
        execute();
    }
}
```

Quando o raio da onda alcança (ou ultrapassa) a distância até você, consideramos que a bala "passou" pela sua posição atual — é o momento de registrar o resultado (Passo 5) e remover a onda da lista.

**✅ Checkpoint:** `ondasAtivas` cresce quando o inimigo atira e encolhe de volta conforme o tempo passa, sem crescer indefinidamente numa batalha longa.

### PASSO 4: GuessFactor — a régua de "quanto você se desviou"

GuessFactor é uma forma de medir, num número entre -1 e +1, o quanto você se desviou da linha reta de tiro no momento em que uma onda te alcança: `-1` significa "no limite máximo de escape para um lado", `+1` "no limite máximo para o outro lado", e `0` "exatamente na linha direta de tiro" — ou seja, teria sido um acerto se o inimigo tivesse atirado reto, sem prever seu movimento.

```java
private static final int NUM_BINS = 31; // ímpar: o bin central = acerto direto

private int calcularBin(Onda onda) {
    return calcularBinParaPosicao(onda, getX(), getY());
}

private int calcularBinParaPosicao(Onda onda, double x, double y) {
    double anguloAtualGraus = Math.toDegrees(Math.atan2(x - onda.origemX, y - onda.origemY));
    double anguloRelativoGraus = normalizeAngle(anguloAtualGraus - onda.anguloDiretoGraus);
    double anguloMaximoEscapeGraus = Math.toDegrees(Math.asin(8.0 / onda.velocidadeBala));
    double guessFactor = clamp(anguloRelativoGraus / anguloMaximoEscapeGraus, -1, 1);
    int bin = (int) Math.round((guessFactor + 1) / 2.0 * (NUM_BINS - 1));
    return Math.max(0, Math.min(NUM_BINS - 1, bin));
}
```

Já escrevemos `calcularBin` na forma parametrizada (`calcularBinParaPosicao`, recebendo `x`/`y` explícitos) que o Passo 6 vai reaproveitar para avaliar posições hipotéticas — `calcularBin(onda)` é só o caso particular "minha posição atual, agora".

`anguloMaximoEscapeGraus` vem de `asin(velocidadeMáxima / velocidadeBala)` — o maior ângulo que um alvo se movendo à velocidade linear máxima (8px/tick) consegue se desviar da linha direta antes da bala chegar. Um exemplo numérico: um tiro de potência 1 (`quedaEnergia = 1.0`) tem `velocidadeBala = 20 - 3*1 = 17`; `anguloMaximoEscapeGraus = asin(8/17) ≈ 28.07°`. Se você estiver exatamente nesse ângulo máximo de um dos lados no momento em que a onda te alcança, `guessFactor = ±1`, e o `bin` calculado é `0` ou `30` (as pontas). Se você estiver exatamente na linha de tiro, `guessFactor = 0`, e o `bin` é `15` — o bin central, dos 31.

**✅ Checkpoint:** para os números do exemplo acima, você confirma manualmente (calculadora ou script) que um desvio de 0°, 14.03° (metade do máximo) e 28.07° (o máximo) produzem, respectivamente, os bins 15, ~22-23 e 30.

### PASSO 5: tabela de estatísticas

Cada vez que uma onda te alcança (Passo 3), o bin correspondente ao seu desvio real acumula um "voto":

```java
private final double[] estatisticasPerigo = new double[NUM_BINS];

private void registrarResultado(Onda onda) {
    int bin = calcularBin(onda);
    estatisticasPerigo[bin] += 1;
}
```

Com o tempo, `estatisticasPerigo` vai acumulando em quais bins (quais desvios angulares) as balas do inimigo mais frequentemente te alcançaram — é essa tabela que orienta a decisão de movimento do Passo 6.

**✅ Checkpoint:** depois de algumas dezenas de *ticks* de batalha, pelo menos um valor de `estatisticasPerigo` é maior que zero.

### PASSO 6: para que lado orbitar

Com pelo menos uma onda ativa, o robô orbita a **origem da onda mais próxima** (não a posição atual do inimigo — a onda já partiu de um ponto fixo, e é em relação a esse ponto que o GuessFactor faz sentido), escolhendo o sentido comparando o "perigo" registrado de um pequeno passo hipotético em cada direção:

```java
private Onda ondaMaisProxima() {
    Onda maisProxima = null;
    double menorDistancia = Double.MAX_VALUE;
    for (Onda onda : ondasAtivas) {
        double distancia = Math.hypot(getX() - onda.origemX, getY() - onda.origemY);
        if (distancia < menorDistancia) {
            menorDistancia = distancia;
            maisProxima = onda;
        }
    }
    return maisProxima;
}

private double escolherAnguloOrbita(Onda ondaMaisProxima) {
    double anguloAtual = Math.toDegrees(Math.atan2(getX() - ondaMaisProxima.origemX, getY() - ondaMaisProxima.origemY));

    double passoLookahead = 20; // px, um pequeno passo hipotético para avaliar cada sentido
    double xHorario = getX() + passoLookahead * Math.sin(Math.toRadians(anguloAtual + 10));
    double yHorario = getY() + passoLookahead * Math.cos(Math.toRadians(anguloAtual + 10));
    double xAntiHorario = getX() + passoLookahead * Math.sin(Math.toRadians(anguloAtual - 10));
    double yAntiHorario = getY() + passoLookahead * Math.cos(Math.toRadians(anguloAtual - 10));

    int binHorario = calcularBinParaPosicao(ondaMaisProxima, xHorario, yHorario);
    int binAntiHorario = calcularBinParaPosicao(ondaMaisProxima, xAntiHorario, yAntiHorario);

    boolean horarioMaisSeguro = estatisticasPerigo[binHorario] <= estatisticasPerigo[binAntiHorario];
    return anguloAtual + (horarioMaisSeguro ? 90 : -90);
}
```

`calcularBinParaPosicao` (do Passo 4) permite avaliar o bin de uma posição **hipotética**, sem precisar realmente estar lá — é a peça que faltava para comparar os dois sentidos antes de decidir. Isso substitui o bloco de movimento circular fixo que hoje existe em `onScannedRobot`, com um fallback claro para quando ainda não há nenhuma onda ativa (início de batalha, ou um intervalo sem tiros): cair de volta para o comportamento do Tópico 14, circling fixo em torno da posição atual do inimigo.

```java
private void mover(double inimigoXAtual, double inimigoYAtual, double anguloAbsoluto) {
    double anguloOrbitaGraus;
    Onda maisProxima = ondaMaisProxima();
    if (maisProxima != null) {
        anguloOrbitaGraus = escolherAnguloOrbita(maisProxima);
    } else {
        anguloOrbitaGraus = anguloAbsoluto + 90;
    }
    setTurnRight(normalizeAngle(anguloOrbitaGraus - getHeading()));
    setAhead(80);
}
```

**✅ Checkpoint:** o robô orbita a origem da onda mais próxima (não a posição atual do inimigo) enquanto há onda ativa, e volta ao circling fixo do Tópico 14 quando `ondasAtivas` está vazia.

### PASSO 7: batalhas reais — isolando o efeito do Wave Surfing

Mesmos 4 oponentes, mesmo runner, 10 *rounds* cada, comparando as três versões do robô — o baseline de *circling* fixo (documentado no README, 1 bateria registrada), a versão só com mira preditiva (Tópico 15, 3 baterias rodadas nesta sessão) e a versão final, com mira preditiva **e** Wave Surfing (este tópico, **5 baterias** rodadas nesta sessão, exatamente para conseguir medir a variação em vez de confiar numa única amostra):

```
                     circling fixo        mira preditiva         mira preditiva
                     (baseline,            só (Tópico 15,        + Wave Surfing
                     1 bateria)            3 baterias)            (este tópico, 5 baterias)
vs sample.Corners:   0/10                  2/10 a 5/10             3/10 a 5/10
vs sample.Crazy:     2/10 a 5/10           5/10 a 6/10             6/10 a 10/10
vs sample.Walls:     1/10                  4/10 a 5/10             0/10 a 6/10
vs sample.RamFire:   7/10                  6/10 a 9/10             4/10 a 10/10
```

Discussão honesta, com o que os números realmente mostram — e aqui está a descoberta mais importante deste tópico: **rodar só uma bateria por versão (como as primeiras execuções deste tutorial fizeram) esconderia o quanto os resultados variam.** Só depois de rodar 5 baterias completas da versão final é que ficou claro que a variação entre execuções, historicamente atribuída só a `Crazy` (que tem movimento aleatório), também afeta `Corners`, `Walls` e `RamFire` — provavelmente porque o Robocode sorteia a posição inicial de cada robô a cada *round*. Com isso em mente:

- Contra `Corners` e `Crazy`, o Wave Surfing parece agregar uma melhora real por cima da mira preditiva sozinha: a faixa de `Corners` sobe de "2 a 5" para "3 a 5" (o piso melhora, o teto se mantém), e a de `Crazy` sobe de "5 a 6" para "6 a 10" (as faixas quase não se sobrepõem — evidência razoavelmente forte de melhora real).
- Contra `Walls`, o Wave Surfing tornou o resultado **mais instável**, não uniformemente pior nem melhor: a faixa passa de "4 a 5" (mira preditiva sozinha, relativamente estável) para "0 a 6" (com Wave Surfing) — o mínimo caiu bastante. `Walls` percorre a borda da arena de forma regular; com uma tabela de estatísticas não segmentada (a simplificação deste tópico) e poucas amostras coletadas numa única batalha, o robô às vezes orbita com base em pouquíssimos disparos registrados, o que é mais ruído estatístico que sinal confiável em algumas execuções — e um sinal razoável em outras. É exatamente o tipo de instabilidade que a versão de torneio (segmentada por distância/velocidade, com muito mais dados por segmento) existe para reduzir.
- Contra `RamFire`, o Wave Surfing também alargou a faixa em vez de deslocá-la claramente para cima: "6 a 9" virou "4 a 10". A leitura mais honesta é que o resultado contra `RamFire` já dependia fortemente de fatores fora do controle da mira ou do movimento (a posição inicial sorteada, por exemplo, tem grande efeito quando o oponente avança para colidir), e o Wave Surfing não resolveu nem piorou isso de forma decisiva.

Nenhum desses números foi estimado — são 8 baterias reais no total (1 histórica + 3 do Tópico 15 + 5 deste tópico), todas rodadas via `robocode.control.RobocodeEngine` nesta mesma sessão de trabalho.

**✅ Checkpoint:** você roda pelo menos duas baterias completas contra o mesmo oponente na sua máquina e confirma, com números próprios, que o resultado varia de uma execução para outra mesmo sem mudar uma linha de código.

### Resumo do que você construiu

- Entendimento de por que um padrão de movimento 100% fixo é uma fraqueza estrutural contra um oponente adaptativo.
- Detecção de disparo do inimigo pela queda de energia, com a limitação honesta de que colisões produzem o mesmo sinal.
- Classe `Onda` e uma lista de ondas ativas, atualizada a cada *tick* do loop principal (não só a cada *scan*).
- GuessFactor: uma régua de -1 a +1 para medir o desvio angular real de cada onda que te alcança, e uma tabela de estatísticas (`estatisticasPerigo`) não segmentada por essa régua.
- Decisão de movimento que orbita a origem da onda mais próxima pelo lado historicamente mais seguro, com fallback para o circling fixo do Tópico 14 quando não há onda ativa.
- Bateria de **múltiplas** batalhas reais (não uma só) isolando o efeito específico do Wave Surfing por cima da mira preditiva, com resultado honesto: melhora razoavelmente clara contra `Corners` e `Crazy`, mas contra `Walls` e `RamFire` o Wave Surfing alargou a faixa de resultados possíveis em vez de deslocá-la claramente para cima.
- A descoberta de que a variação entre execuções não é exclusividade de `Crazy` — `Corners`, `Walls` e `RamFire` também produzem resultados diferentes de bateria para bateria, o que reforça por que uma única bateria de 10 *rounds* não é suficiente para tirar conclusões definitivas sobre uma mudança de estratégia.

### Perguntas de fixação

1. Por que o threshold de queda de energia usado para detectar tiro é o intervalo `(0.0, 3.0]`, e não qualquer queda de energia?
2. Por que orbitar a **origem** da onda em vez da posição **atual** do inimigo?
3. A simplificação de "olhar só um passo hipotético à frente" em vez de simular vários *ticks* por candidato (como as implementações de torneio fazem) custa o quê, em termos de qualidade da decisão de movimento?
4. O Passo 7 mostrou que `Corners`, `Walls` e `RamFire` — todos com estratégia determinística, sem nenhum `Math.random()` no código deles — ainda assim produziram resultados diferentes entre baterias. Como isso é possível, e o que isso te diz sobre quantas baterias rodar antes de confiar num número?

### Exercícios

1. Mude `NUM_BINS` para um valor bem menor (ex.: 7) ou bem maior (ex.: 61) e rode a mesma bateria de 4 oponentes — o resultado muda de forma perceptível? Documente os números reais observados.
2. Pesquise e descreva em texto como segmentar `estatisticasPerigo` por faixa de distância (ex.: perto/médio/longe), em vez de uma única tabela — que problema prático isso resolveria, à luz do resultado observado contra `Walls` no Passo 7?
3. (Desafio opcional, não precisa estar implementado no robô de referência) `onPaint(java.awt.Graphics2D g)` é um método real de `Robot`/`AdvancedRobot` (confirme com `javap -cp ~/robocode/libs/robocode.jar robocode.Robot`), habilitado marcando "Paint" nas preferências/console do Robocode. Descreva como usá-lo para desenhar visualmente as ondas ativas como círculos crescentes ao redor de cada origem, o que ajudaria a depurar visualmente se o robô está de fato "surfando" as ondas certas.

## 17. Robocode: Refinamento e testes sistemáticos

**Objetivo:** gerenciar energia de forma estratégica e testar sistematicamente contra vários estilos de oponente antes do campeonato — com resultados reais, não estimados.

### Pré-requisitos

Robô `AdvancedRobot` com radar lock, mira preditiva (Linear/Circular Targeting) e Wave Surfing dos dois tópicos anteriores, além do movimento circular de fallback herdado do Tópico 14. **✅ Checkpoint:** o robô vence a maioria das batalhas contra robôs de exemplo simples.

### PASSO 1: adicione gerenciamento de energia à estratégia

```java
public void onScannedRobot(ScannedRobotEvent e) {
    double potenciaTiro = getEnergy() < 20 ? 1 : (e.getDistance() < 200 ? 3 : 2);
    setFire(potenciaTiro);
}
```

Tiros mais fortes gastam mais energia do próprio atirador — com pouca energia, atirar fraco evita ficar sem energia (e perder a batalha) rapidamente.

**✅ Checkpoint:** o robô atira com potência reduzida quando a energia está baixa.

### PASSO 2: evite ficar parado perto das paredes (`onHitWall`)

```java
public void onHitWall(HitWallEvent e) {
    setTurnRight(normalizeAngle(90 - e.getBearing()));
    setAhead(-50);
}
```

Recuar imediatamente após bater na parede mantém o robô se movendo produtivamente, em vez de desperdiçar tempo de batalha preso nela.

**✅ Checkpoint:** o robô se afasta da parede visivelmente após colidir.

### PASSO 3: teste sistematicamente contra múltiplos robôs de exemplo

Configure uma batalha contra `sample.Corners`, `sample.Crazy`, `sample.Walls` e `sample.RamFire`, um de cada vez, com pelo menos 10 *rounds* cada — pela interface gráfica do Robocode, ou por linha de comando com `-battle`/`-results`, o que é reproduzível e mais fácil de automatizar:

```bash
java -cp "robocode/libs/*" robocode.Robocode -nodisplay -nosound \
     -battle meurobo-vs-Corners.battle -results resultado.txt
```

Testar contra estilos diferentes de oponente revela pontos fracos que um único teste não mostraria — `Corners` foge para o canto e atira parado; `Crazy` se move de forma errática; `Walls` percorre a borda da arena; `RamFire` tenta colidir de propósito.

**✅ Checkpoint:** você registra a taxa de vitória do seu robô contra cada um dos 4 oponentes de exemplo, com números reais de batalhas rodadas de verdade.

### PASSO 4: registre e analise os pontos fracos encontrados

Um exemplo real, de 10 *rounds* cada bateria, rodado durante a construção deste tutorial — já com o robô incorporando mira preditiva (Tópico 15) e Wave Surfing (Tópico 16), não mais a versão simples de radar lock + mira direta + *circling* fixo do Tópico 14. O Passo 7 do tópico anterior já mostrou, com 5 baterias completas, que o resultado varia bastante de execução para execução contra **qualquer** um dos 4 oponentes (não só `Crazy`) — então a faixa observada, e não um único número, é o dado honesto aqui:

```
vs sample.Corners: 3/10 a 5/10 (5 baterias) — evolução real frente ao histórico (a versão do Tópico 14, só com mira direta e circling fixo, vencia 0/10 numa única bateria registrada)
vs sample.Crazy:   6/10 a 10/10 (6 baterias) — segue sendo o oponente com a faixa mais larga, Crazy tem movimento aleatório
vs sample.Walls:   0/10 a 6/10 (5 baterias) — o oponente com o pior "pior caso" observado, mesmo já tendo evoluído frente ao histórico (1/10)
vs sample.RamFire: 4/10 a 10/10 (5 baterias) — a faixa mais larga depois de Crazy; nem sempre acima do histórico (7/10)
```

Documentar os resultados antes de ajustar mais o código evita "otimizar no escuro" — e documentar a **faixa**, não só um número de uma bateria só, evita a armadilha mais sutil: tratar uma execução sortuda (ou azarada) como se fosse "o" resultado do robô. Repare que essa variação ampla, principalmente contra `Walls` e `RamFire`, é uma descoberta real feita durante a verificação deste material, não algo previsto de antemão — a hipótese mais provável é que o Robocode sorteia a posição inicial de cada robô a cada *round*, o que afeta até confrontos contra oponentes de estratégia 100% determinística. Os Tópicos 15 e 16 já mostraram, separadamente, o efeito de cada técnica (mira preditiva sozinha, e mira preditiva + Wave Surfing) sobre essas faixas; esta seção é sobre o robô acumulado até este ponto do currículo, não uma técnica isolada.

**✅ Checkpoint:** você tem uma lista escrita de pelo menos 2 pontos fracos identificados, com números reais.

### PASSO 5: ajuste a estratégia para o ponto fraco mais crítico

Olhando o exemplo acima, `Corners` e `Walls` — ambos os que ficam grudados na borda da arena, girando o canhão parados ou quase parados — são os piores resultados. Um robô que gira em círculo largo demais (`setAhead(80)`, como no PASSO 4 do Tópico 14) gasta tempo se afastando em vez de fechar distância contra um alvo que não foge. Uma correção possível é reduzir o raio do *circling* quando o inimigo está parado ou perto da borda:

```java
public void onHitRobot(HitRobotEvent e) {
    if (e.isMyFault()) {
        setTurnRight(90);
        setAhead(-100);
    }
}
```

`e.isMyFault()` diz se seu robô causou a colisão — afastar-se imediatamente reduz a exposição a robôs que usam *ramming* como estratégia, como `RamFire`, um dos oponentes contra os quais esse tratamento já ajuda (ver a faixa de resultados do PASSO 4).

**✅ Checkpoint:** reteste contra o oponente mais fraco identificado — o objetivo é melhorar o número em relação ao PASSO 3, não necessariamente vencer 10/10.

### Resumo do que você construiu

- Potência de tiro adaptada à energia restante.
- Recuo automático após colidir com parede.
- Bateria de testes sistemática contra 4 estilos diferentes de oponente, com resultados reais registrados.
- Registro documentado dos pontos fracos encontrados, incluindo a observação de que resultados variam entre execuções contra oponentes com movimento aleatório.
- Ajuste direcionado para o ponto fraco mais crítico.

### Perguntas de fixação

1. Por que testar contra 4 estilos diferentes de oponente revela mais que testar 40 vezes contra o mesmo oponente?
2. O resultado contra `sample.Crazy` variou entre duas rodadas de teste (2/10 e 5/10). Isso é um bug no robô, ou um comportamento esperado? Por quê?
3. Por que perder consistentemente contra `Corners` e `Walls`, mas vencer contra `RamFire`, é uma informação útil — em vez de só "meu robô não é bom o suficiente"?

### Exercícios

1. `onDeath`: registre estatísticas quando seu robô é derrotado.
2. Modo defensivo: quando energia < 10, priorize fuga sobre ataque.
3. Novo teste: rode contra `sample.TrackFire` e documente o resultado.

## 18. Robocode: Finalização e empacotamento

**Objetivo:** polimento final, empacotamento como `.jar` e checklist de submissão antes do campeonato.

### Pré-requisitos

Robô refinado e testado contra os 4 oponentes de exemplo do tópico anterior. **✅ Checkpoint:** você tem a lista de resultados dos testes da aula passada em mãos.

### PASSO 1: dê um nome e versão oficiais ao robô

```java
public class MeuRoboFinal extends AdvancedRobot {
    // implementação
}
```

```
# MeuRoboFinal.properties
robot.name=MeuRoboFinal 1.0
robot.author.name=Seu Nome
robot.description=Robô com radar lock, movimento circular e gerenciamento de energia
```

O nome e a descrição aparecem publicamente na tela de seleção de robôs do campeonato.

**✅ Checkpoint:** o robô aparece na lista de robôs disponíveis com o nome e a descrição corretos.

### PASSO 2: remova código de debug deixado no meio do desenvolvimento

```java
// REMOVER antes da versão final:
System.out.println("debug: energia = " + getEnergy());

// PREFERÍVEL, se precisar manter algum log condicional:
if (DEBUG) out.println("energia = " + getEnergy());
```

`out` é o `PrintStream` correto do Robocode — `System.out` pode ser desabilitado pelo motor de segurança em algumas configurações, então mensagens de log dentro de um robô devem usar `out`, não `System.out`.

**✅ Checkpoint:** nenhum `System.out.println` de debug esquecido permanece no código final.

### PASSO 3: trate exceções defensivamente

```java
public void onScannedRobot(ScannedRobotEvent e) {
    try {
        double anguloAbsoluto = getHeading() + e.getBearing();
        setTurnGunRight(normalizeAngle(anguloAbsoluto - getGunHeading()));
        setFire(calcularPotencia(e));
    } catch (Exception ex) {
        out.println("Erro em onScannedRobot: " + ex.getMessage());
    }
}
```

Uma exceção não tratada durante a batalha derruba o robô daquele *round* inteiro — try/catch defensivo, envolvendo cada método de evento, evita que um bug isolado custe a batalha inteira.

**✅ Checkpoint:** você identifica pelo menos um método de evento sem tratamento de exceção e o corrige.

### PASSO 4: exporte o robô como `.jar` para submissão

Pela interface do Robocode: *Robot → Package robot for upload*, selecione seu robô, gere o `.jar`. Ele empacota o `.class` compilado junto com o arquivo `.properties` — formato padrão de distribuição de robôs Robocode. Alternativa por linha de comando, empacotando você mesmo:

```bash
jar cf meurobo.jar meurobo/MeuRoboFinal.class meurobo/MeuRoboFinal.properties
```

**✅ Checkpoint:** o arquivo `.jar` é gerado e consegue ser carregado numa nova instância do Robocode.

### PASSO 5: faça o checklist final de submissão

- [ ] O robô tem nome, autor e descrição preenchidos corretamente.
- [ ] Nenhum `System.out.println` de debug esquecido.
- [ ] Métodos de evento críticos têm tratamento de exceção defensivo.
- [ ] O `.jar` foi gerado e testado numa instância limpa do Robocode.
- [ ] O robô venceu pelo menos 3 dos 4 oponentes de exemplo testados no tópico anterior — ou, se não, você sabe explicar por que os que perdeu são estruturalmente difíceis para a sua estratégia.

### Resumo do que você construiu

- Robô nomeado e documentado com um arquivo `.properties`.
- Código de debug removido/substituído por logging condicional via `out`.
- Tratamento defensivo de exceções nos métodos de evento.
- Robô exportado como `.jar`, testado numa instância limpa.
- Checklist final de submissão completo.

### Perguntas de fixação

1. Por que usar `out` em vez de `System.out` dentro de um robô Robocode?
2. O que acontece com o robô, na prática, se `onScannedRobot` lançar uma exceção não tratada no meio de uma batalha?
3. O `.jar` gerado contém o quê, exatamente, além do `.class` compilado?
