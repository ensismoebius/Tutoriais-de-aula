# Tutorial — Banco de Dados I (BDI)

SGBD de referência: MySQL 8.4 LTS (compatível com MariaDB, usado nos exemplos deste material). O projeto construído ao longo do tutorial é o banco `locadora`: uma locadora de livros, modelada do zero — do diagrama entidade-relacionamento até Views, índices, transações, Stored Procedures e Triggers, sempre sobre o mesmo schema. Ao final, um segundo projeto, com um domínio próprio (uma clínica veterinária), fecha o curso como projeto final individual.

> Todo código deste tutorial foi executado de verdade contra uma instância MariaDB local antes de ser publicado aqui — nenhuma saída de consulta foi inventada.

## Sumário

- [O básico, para quem está começando agora](#o-básico-para-quem-está-começando-agora)
- [Conceitos fundamentais antes de começar](#conceitos-fundamentais-antes-de-começar)
- [1. Do DER para o Modelo Lógico](#1-do-der-para-o-modelo-lógico)
- [2. DML — UPDATE e DELETE](#2-dml--update-e-delete)
- [3. DQL Básico — SELECT e WHERE](#3-dql-básico--select-e-where)
- [4. DQL com Funções Agregadas](#4-dql-com-funções-agregadas)
- [5. Consultas com Múltiplas Tabelas (JOINs)](#5-consultas-com-múltiplas-tabelas-joins)
- [6. JOINs Avançados (LEFT/RIGHT, SELF)](#6-joins-avançados-leftright-self)
- [7. Subconsultas](#7-subconsultas)
- [8. Views](#8-views)
- [9. Índices e Performance](#9-índices-e-performance)
- [10. Transações (ACID, COMMIT, ROLLBACK)](#10-transações-acid-commit-rollback)
- [11. Stored Procedures e Funções](#11-stored-procedures-e-funções)
- [12. Triggers](#12-triggers)
- [Projeto Final — Início](#projeto-final--início)
- [Projeto Final — Apresentações](#projeto-final--apresentações)
- [Revisão e exercícios finais](#revisão-e-exercícios-finais)
- [Projeto completo — todos os arquivos juntos](#projeto-completo--todos-os-arquivos-juntos)

Cada tópico numerado é um tutorial *build-along*: execute cada passo no seu terminal e confira o resultado antes de prosseguir. Cada seção termina com um **✅ Checkpoint** — pare e confirme que o resultado bate antes de continuar.

## O básico, para quem está começando agora

Esta seção existe para quem nunca usou um banco de dados relacional antes. Se os termos abaixo já são familiares, pule direto para "Conceitos fundamentais antes de começar".

**O que é um banco de dados relacional, e o que é um SGBD.** Um banco de dados é, de forma simples, um lugar organizado para guardar informação de forma que ela possa ser recuperada depois — o que muda de um banco para outro é *como* essa organização é feita. Num banco **relacional**, a informação é organizada em **tabelas**: uma grade de linhas e colunas, parecida com uma planilha, em que cada linha é um registro (um livro, um associado) e cada coluna é um atributo desse registro (o título, o e-mail). A diferença central em relação a uma planilha comum é que tabelas relacionais podem se **referenciar** umas às outras de um jeito formal e garantido pelo próprio banco — é exatamente esse mecanismo, chamado chave estrangeira, que este tutorial constrói já no Tópico 1. Quem gerencia esses bancos — cria as tabelas, garante que as referências entre elas continuem válidas, executa as consultas, controla quem pode acessar o quê — é o **SGBD** (Sistema Gerenciador de Banco de Dados). O MySQL é um SGBD; existem outros (PostgreSQL, SQLite, SQL Server), cada um com particularidades próprias, mas todos conversando através de uma linguagem em comum: o **SQL**.

**A diferença entre o MySQL (o servidor) e o `mysql` (o cliente de linha de comando).** "MySQL" é o nome do programa que fica rodando em segundo plano, escutando por conexões e efetivamente guardando os dados em disco — o **servidor**. `mysql` (em minúsculas, no terminal) é apenas um dos programas que sabem *conversar* com esse servidor: um **cliente** de linha de comando, que você usa para digitar comandos SQL e ver o resultado. Existem outros clientes para o mesmo servidor (o MySQL Workbench, com interface gráfica, por exemplo) — a distinção importa porque um erro de conexão ("Can't connect to MySQL server") é sempre um problema do servidor (ele não está rodando, ou está numa porta diferente), nunca do cliente em si.

**O que é SQL, e as três sublinguagens que você vai usar.** SQL (*Structured Query Language*) é a linguagem que todo SGBD relacional entende, para descrever tanto a estrutura dos dados quanto as operações sobre eles. Ela se divide, na prática, em três grupos de comandos, que reaparecem o tempo todo neste tutorial:

| Sublinguagem | Comandos | Mexe em |
|---|---|---|
| DDL (*Definition*) | `CREATE`, `ALTER`, `DROP` | Estrutura (tabelas, colunas, chaves) |
| DML (*Manipulation*) | `INSERT`, `UPDATE`, `DELETE` | Linhas de dados |
| DQL (*Query*) | `SELECT` | Leitura de dados |

O Tópico 1 usa DDL para criar as tabelas; o Tópico 2 usa DML para alterar e apagar linhas; os Tópicos 3 e 4 usam DQL para ler. A ordem não é acidental: só faz sentido alterar ou consultar dados depois que a estrutura que os guarda já existe.

**Por que SQL é declarativo, e o que isso muda no seu jeito de pensar.** Se você já programou em uma linguagem procedural (Python, Java, JavaScript), está acostumado a *descrever o processo*: um `for` que percorre uma lista item por item, decidindo o que fazer a cada volta. SQL não funciona assim. Um `SELECT` **declara o resultado que você quer**, não o processo para chegar até ele — você não escreve "para cada linha da tabela, verifique se a condição bate, e se bater, inclua no resultado"; você escreve `WHERE condição`, e é o SGBD quem decide *como* varrer os dados para satisfazer essa condição (linha por linha, usando um índice, na ordem que for mais rápida). Essa diferença é a razão de existir o Tópico 9 (Índices) deste tutorial: como você nunca escreve o "como", o SGBD é quem otimiza — e um índice é, essencialmente, uma forma de ajudá-lo a otimizar melhor.

**O terminal, e o que significa "rodar um comando".** Praticamente todo bloco de código deste tutorial dentro de uma caixa de código é para você digitar (ou colar) no terminal, dentro do cliente `mysql`, confirmando com Enter — o resultado aparece como texto logo abaixo. Comandos de shell (fora do cliente `mysql`, como o próprio `mysql -u ... -p...` que abre a conexão) são indicados explicitamente quando aparecem.

**✅ Checkpoint:** você sabe explicar, com suas próprias palavras, a diferença entre o servidor MySQL e o cliente `mysql`, e sabe dizer se um comando é DDL, DML ou DQL só de olhar o verbo (`CREATE`, `INSERT`, `SELECT`...).

## Conceitos fundamentais antes de começar

Com o básico alinhado, vale aprofundar duas ideias que aparecem o tempo todo a partir do Tópico 1.

**O que é um DER, e por que modelar antes de criar tabelas.** Um Diagrama Entidade-Relacionamento (DER) é uma representação visual, feita **antes** de escrever qualquer SQL, de quais "coisas" (entidades) o sistema precisa guardar e como elas se relacionam entre si. Pular essa etapa e ir direto para `CREATE TABLE` funciona para um sistema de brinquedo, mas em qualquer sistema real leva a retrabalho: descobrir no meio do caminho que faltou uma tabela, ou que duas entidades deveriam ter sido uma só, custa muito mais depois que já existem dados de verdade dentro das tabelas erradas. O Tópico 1 deste tutorial começa exatamente por aí — desenhando o DER da locadora no papel antes de tocar no MySQL.

**Chave primária e chave estrangeira, em uma frase cada.** Uma **chave primária** (*primary key*, `PK`) é a coluna (ou conjunto de colunas) que identifica uma linha de forma única dentro da própria tabela — duas linhas nunca podem ter a mesma. Uma **chave estrangeira** (*foreign key*, `FK`) é uma coluna que guarda o valor da chave primária de **outra** tabela, e é isso que materializa um relacionamento do DER dentro do banco: quando você vê `leitor_id` dentro da tabela `emprestimo`, apontando para o `id` de `associado`, está vendo em SQL exatamente a mesma linha "1 --- N" que apareceria no diagrama. O SGBD usa essa informação para impedir dados inconsistentes — é o que impede, por exemplo, um empréstimo apontar para um associado que não existe.

**✅ Checkpoint:** você sabe explicar por que uma FK em uma tabela sempre aponta para a PK de outra, nunca o contrário.

## 1. Do DER para o Modelo Lógico

**Objetivo:** modelar uma locadora de livros a partir de um DER conceitual e traduzi-lo em tabelas reais no MySQL, com tipos de dado e chaves estrangeiras.

### Pré-requisitos

```bash
mysql --version   # Deve mostrar 8.4+ (LTS) — ou uma MariaDB compatível
```

**✅ Checkpoint:** o comando retorna a versão sem erro.

**Passo 1 — o cenário.** Vamos modelar uma pequena locadora de livros com três entidades centrais:

```
ASSOCIADO (id, nome, email)
   |
   | 1 --- N
   |
EMPRESTIMO (id, data_emprestimo, data_prevista, data_devolucao)
   |
   | N --- 1
   |
LIVRO (id, titulo, autor, isbn)
```

Um `ASSOCIADO` pode fazer vários `EMPRÉSTIMOS` (1:N). Um `LIVRO` pode aparecer em vários `EMPRÉSTIMOS` ao longo do tempo (1:N). `EMPRÉSTIMO` é a entidade "no meio", ligando associado e livro — é assim que um relacionamento N:N (um livro pode ser emprestado por vários associados ao longo do tempo, e um associado pode pegar vários livros) vira, na prática, duas relações 1:N.

**✅ Checkpoint:** você consegue explicar, com suas próprias palavras, por que `EMPRESTIMO` existe como entidade própria em vez de uma simples tabela de junção.

**Passo 2 — identifique atributos e chaves.** Ainda no papel, antes de qualquer SQL:

| Entidade | Atributos | Chave primária |
|---|---|---|
| ASSOCIADO | id, nome, email | id |
| LIVRO | id, titulo, autor, isbn, preco, categoria, exemplares_disponiveis | id |
| EMPRESTIMO | id, associado_id (FK), livro_id (FK), data_emprestimo, data_prevista, data_devolucao | id |

Duas colunas a mais em relação ao esboço do Passo 1 já entram aqui, e valem uma palavra: uma locadora real não empresta um título "infinito" — ela tem um número físico de cópias, então `LIVRO` precisa de um `exemplares_disponiveis` desde o início (você vai usá-lo já no Tópico 10, em transações, e no Tópico 12, em Triggers). Pelo mesmo motivo, todo empréstimo tem uma **data prevista de devolução**, não só a data em que ele começou — sem isso não existe como calcular atraso, o que o Tópico 8 (Views) e o Tópico 11 (Procedures) precisam fazer. `email` e `isbn` são bons candidatos a `UNIQUE` — não fazem sentido duplicados.

**✅ Checkpoint:** você sabe apontar qual coluna em `EMPRESTIMO` aponta para qual tabela.

**Passo 3 — traduza para o modelo lógico.** Modelo lógico significa decidir o tipo exato de cada coluna, ainda em prosa, antes da sintaxe de um SGBD específico:

| Coluna | Tipo lógico |
|---|---|
| id (qualquer tabela) | Inteiro, auto-incremento |
| nome, titulo, autor | Texto curto (até 150 caracteres) |
| email | Texto curto, único |
| isbn | Texto fixo (13 caracteres) |
| preco | Número decimal com 2 casas |
| exemplares_disponiveis | Inteiro, começa em 1 |
| data_emprestimo | Data e hora |
| data_prevista | Data (sem hora) |
| data_devolucao | Data e hora, pode ser nula (livro ainda não devolvido) |

`data_devolucao` aceitar nulo é uma decisão de modelagem, não só de sintaxe: significa "o empréstimo está em aberto". Um livro também precisa de uma **categoria** (Ficção Científica, Romance...) — uma locadora de verdade organiza o catálogo por categoria desde o primeiro dia, e algumas categorias contêm subcategorias (Romance → Romance Machadiano), o que pede uma tabela própria, `CATEGORIA`, que referencia a si mesma.

**✅ Checkpoint:** você identificou pelo menos uma coluna que deve aceitar `NULL` e sabe justificar por quê.

**Passo 4 — crie o banco de dados.**

```bash
mysql -u andre -p1234 -e "CREATE DATABASE IF NOT EXISTS locadora CHARACTER SET utf8mb4;"
mysql -u andre -p1234 locadora
```

`utf8mb4` (em vez de `utf8`) suporta o conjunto completo de caracteres Unicode, incluindo emojis e todos os acentos do português — é o padrão recomendado hoje para qualquer banco novo.

**✅ Checkpoint:** você está conectado ao banco `locadora` (o prompt mudou para `MariaDB [locadora]>`).

**Passo 5 — crie `associado`, `categoria` e `livro`.**

```sql
CREATE TABLE associado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);
```

`AUTO_INCREMENT` implementa a chave primária numérica automática do modelo lógico. `VARCHAR(150)` é texto de tamanho variável, até 150 caracteres — mais eficiente que um `TEXT` para strings curtas como um nome.

```sql
CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    categoria_pai_id INT NULL,

    FOREIGN KEY (categoria_pai_id) REFERENCES categoria(id)
);
```

`categoria_pai_id` é uma FK que aponta para a própria tabela `categoria` — uma categoria opcionalmente tem uma categoria "pai" (Romance Machadiano → Romance). `NULL` aqui significa "categoria raiz, sem pai". Esse padrão de auto-relacionamento reaparece no Tópico 6, para consultar a hierarquia inteira.

```sql
CREATE TABLE livro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    isbn CHAR(13) NOT NULL UNIQUE,
    preco DECIMAL(10,2) NOT NULL,
    categoria_id INT NULL,
    exemplares_disponiveis INT NOT NULL DEFAULT 1,

    FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);
```

`CHAR(13)` é texto de tamanho **fixo**, ideal para um ISBN de 13 dígitos, que nunca varia de tamanho. `DECIMAL(10,2)` guarda até 10 dígitos no total, sendo 2 depois da vírgula — o tipo certo para dinheiro, porque evita os erros de arredondamento que um tipo de ponto flutuante (`FLOAT`) teria. `exemplares_disponiveis INT NOT NULL DEFAULT 1` garante que todo livro cadastrado já nasça com pelo menos uma cópia disponível, a menos que você informe outro valor.

**✅ Checkpoint:** `SHOW TABLES;` lista `associado`, `categoria` e `livro`.

**Passo 6 — crie `emprestimo`, com as chaves estrangeiras.**

```sql
CREATE TABLE emprestimo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    associado_id INT NOT NULL,
    livro_id INT NOT NULL,
    data_emprestimo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_prevista DATE NOT NULL,
    data_devolucao DATETIME NULL,

    FOREIGN KEY (associado_id) REFERENCES associado(id),
    FOREIGN KEY (livro_id) REFERENCES livro(id)
);
```

`FOREIGN KEY ... REFERENCES` é o que transforma a linha "1 --- N" do DER em uma restrição real: o MySQL vai recusar um `associado_id` que não exista na tabela `associado`. `data_emprestimo DEFAULT CURRENT_TIMESTAMP` preenche automaticamente com a data/hora atual se você não informar. `data_prevista DATE NOT NULL` é obrigatória — todo empréstimo precisa de um prazo desde a criação. `data_devolucao DATETIME NULL` permite nulo de propósito, representando "ainda não devolvido".

**✅ Checkpoint:** `DESCRIBE emprestimo;` mostra as três colunas de data e as duas de FK.

**Passo 7 — teste a integridade referencial.**

```sql
INSERT INTO emprestimo (associado_id, livro_id, data_prevista) VALUES (999, 1, CURDATE());
```

**✅ Checkpoint:** o MySQL recusa com `ERROR 1452 (23000): Cannot add or update a child row: a foreign key constraint fails` — não existe associado com id 999.

**Passo 8 — insira dados válidos e confirme o relacionamento.**

```sql
INSERT INTO categoria (nome, categoria_pai_id) VALUES ('Literatura Brasileira', NULL);
INSERT INTO associado (nome, email) VALUES ('Ana Souza', 'ana@email.com');
INSERT INTO livro (titulo, autor, isbn, preco, categoria_id, exemplares_disponiveis)
VALUES ('Dom Casmurro', 'Machado de Assis', '9788535910663', 39.90, 1, 2);
INSERT INTO emprestimo (associado_id, livro_id, data_prevista)
VALUES (1, 1, CURDATE() + INTERVAL 14 DAY);

SELECT associado.nome, livro.titulo, emprestimo.data_emprestimo
FROM emprestimo
JOIN associado ON emprestimo.associado_id = associado.id
JOIN livro ON emprestimo.livro_id = livro.id;
```

**✅ Checkpoint:** a consulta retorna `Ana Souza | Dom Casmurro | <timestamp>`.

### O schema deste tutorial, do início ao fim

A partir daqui, **todo o resto do tutorial usa exatamente este schema** — as quatro tabelas abaixo, sem nenhuma mudança de nome ou coluna faltando:

```
associado (id, nome, email)
categoria (id, nome, categoria_pai_id)
livro     (id, titulo, autor, isbn, preco, categoria_id, exemplares_disponiveis)
emprestimo(id, associado_id, livro_id, data_emprestimo, data_prevista, data_devolucao)
```

Vale registrar isso explicitamente porque é comum, em material de curso construído ao longo de várias semanas, uma tabela crescer aos poucos (uma coluna nova aqui, um nome mais claro ali). Este tutorial já parte da versão final: `associado` (não "leitor" — o termo usado do Tópico 5 em diante no curso original, e mais próximo da linguagem de domínio de um sistema de locação real) e as colunas `preco`, `categoria_id`, `exemplares_disponiveis` (em `livro`) e `data_prevista` (em `emprestimo`) já existem desde este primeiro tópico, mesmo que só passem a ser realmente usadas em consultas a partir dos Tópicos 5, 7, 9, 10 e 12. Sempre que um tópico adiante mencionar "o modelo definido no Tópico 1", é a este schema que ele se refere.

### Resumo do que você construiu

```
✅ DER conceitual traduzido em quatro entidades e seus relacionamentos
✅ Modelo lógico com tipos de dado definidos (VARCHAR, CHAR, DECIMAL, DATE, DATETIME)
✅ Banco de dados criado com charset utf8mb4
✅ Quatro tabelas criadas: associado, categoria, livro, emprestimo
✅ Chaves estrangeiras aplicando a integridade referencial do DER
✅ Teste confirmando que o MySQL recusa uma FK inválida
✅ JOIN entre três tabelas reconstituindo a informação do DER
```

**Exercícios:**
- *Fácil:* adicione a tabela `funcionario` (quem registrou o empréstimo), com uma FK opcional em `emprestimo`.
- *Médio:* adicione um índice em `emprestimo.data_emprestimo` para buscas rápidas por período (você vai formalizar por que isso ajuda no Tópico 9).
- *Difícil:* pesquise a cláusula `CHECK` do MySQL 8.4 e use-a para impedir `exemplares_disponiveis` negativo diretamente na definição da tabela.

**Perguntas para fixação:**
1. Por que `categoria_pai_id` aceita `NULL`, enquanto `associado_id` e `livro_id` em `emprestimo` não aceitam?
2. Se você tivesse modelado `EMPRESTIMO` como uma tabela de junção "vazia" (só `associado_id` + `livro_id`, sem `id` próprio), que informação do domínio ficaria impossível de representar?

## 2. DML — UPDATE e DELETE

**Objetivo:** alterar e apagar dados com segurança, entendendo por que DML exige mais cuidado do que DQL.

**Pré-requisitos:** banco `locadora` do Tópico 1. Este tópico é autossuficiente — o Passo 0 recria o banco do zero com uma massa de dados maior, então não é obrigatório ter seguido o Tópico 1 para acompanhar.

**✅ Checkpoint:** `mysql --version` retorna a versão sem erro.

**Passo 0 — monte o banco do zero (ou pule se já o tem populado).**

```sql
CREATE DATABASE IF NOT EXISTS locadora CHARACTER SET utf8mb4;
USE locadora;

CREATE TABLE associado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    categoria_pai_id INT NULL,
    FOREIGN KEY (categoria_pai_id) REFERENCES categoria(id)
);

CREATE TABLE livro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    isbn CHAR(13) NOT NULL UNIQUE,
    preco DECIMAL(10,2) NOT NULL,
    categoria_id INT NULL,
    exemplares_disponiveis INT NOT NULL DEFAULT 1,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE emprestimo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    associado_id INT NOT NULL,
    livro_id INT NOT NULL,
    data_emprestimo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_prevista DATE NOT NULL,
    data_devolucao DATETIME NULL,
    FOREIGN KEY (associado_id) REFERENCES associado(id),
    FOREIGN KEY (livro_id) REFERENCES livro(id)
);
```

```sql
INSERT INTO categoria (nome, categoria_pai_id) VALUES
    ('Literatura Brasileira', NULL),
    ('Romance', NULL),
    ('Ficção Científica', NULL);
INSERT INTO categoria (nome, categoria_pai_id) VALUES ('Romance Machadiano', 2);

INSERT INTO associado (nome, email) VALUES
    ('Ana Souza',    'ana@email.com'),
    ('Bruno Lima',   'bruno@gmail.com'),
    ('Carla Dias',   'carla@gmail.com'),
    ('Daniel Rocha', 'daniel@outlook.com'),
    ('Elisa Prado',  'elisa@gmail.com');

INSERT INTO livro (titulo, autor, isbn, preco, categoria_id, exemplares_disponiveis) VALUES
    ('Dom Casmurro',      'Machado de Assis',  '9788535910663', 39.90, 4, 2),
    ('Memórias Póstumas', 'Machado de Assis',  '9788535910664', 42.50, 4, 1),
    ('Iracema',           'José de Alencar',   '9788535910665', 29.90, 1, 3),
    ('A Hora da Estrela', 'Clarice Lispector', '9788535910666', 34.90, 1, 1),
    ('Fundação',          'Isaac Asimov',      '9788535910667', 54.90, 3, 2);

INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista, data_devolucao) VALUES
    (1, 1, '2026-07-05 09:00:00', '2026-07-19', NULL),
    (2, 3, '2026-07-12 14:30:00', '2026-07-26', NULL),
    (3, 2, '2026-07-01 10:00:00', '2026-07-15', '2026-07-14 11:00:00'),
    (1, 4, '2026-06-15 08:00:00', '2026-06-29', '2026-06-28 09:00:00'),
    (1, 5, '2026-07-31 16:00:00', '2026-08-14', NULL);
```

Um único `INSERT` pode inserir várias linhas, separando as tuplas de `VALUES` por vírgula — bem mais rápido do que um `INSERT` por linha.

**✅ Checkpoint:** `SELECT COUNT(*) FROM emprestimo;` retorna 5, e duas dessas linhas têm `data_devolucao` vazia (`NULL`).

**Passo 1 — a regra de ouro: sempre um SELECT antes.** Antes de qualquer `UPDATE`/`DELETE`, rode o mesmo filtro como `SELECT` primeiro. O objetivo não é "conferir a sintaxe" — é ver, com os próprios olhos, exatamente quais linhas o comando vai atingir.

```sql
SELECT * FROM associado WHERE id = 1;
```

`WHERE id = 1` é idêntico ao que você vai usar no `UPDATE`/`DELETE` logo a seguir. Se o `SELECT` retorna 1 linha, o comando destrutivo afetará 1 linha; se retornasse 500, afetaria 500.

**✅ Checkpoint:** você adotou o hábito — nunca rodar `UPDATE`/`DELETE` sem antes rodar o `SELECT` equivalente.

**Passo 2 — UPDATE simples, corrigindo um dado.**

```sql
SELECT * FROM associado WHERE email = 'ana@email.com';

UPDATE associado
SET nome = 'Ana Beatriz Souza'
WHERE email = 'ana@email.com';

SELECT * FROM associado WHERE email = 'ana@email.com';
```

`UPDATE associado` diz qual tabela terá linhas alteradas. `SET nome = '...'` diz qual coluna recebe qual valor novo (várias de uma vez, separando por vírgula: `SET nome = '...', email = '...'`). `WHERE email = '...'` diz quais linhas — sem essa cláusula, seriam todas. Esse ciclo "antes, ação, depois" (`SELECT` → `UPDATE` → `SELECT`) é o fluxo de trabalho seguro em qualquer banco de produção.

**✅ Checkpoint:** o segundo `SELECT` mostra o nome atualizado.

**Passo 3 — o perigo de esquecer o WHERE.**

```sql
-- NUNCA rode isto sem WHERE (mantido comentado de propósito):
-- UPDATE associado SET nome = 'Sem Nome';
```

O `WHERE` não é obrigatório na sintaxe do SQL — quando ausente, o banco entende "aplique a todas as linhas". O comando acima, se rodado de verdade, renomearia todo associado da tabela para `'Sem Nome'`, sem confirmação e sem desfazer (a menos que você esteja dentro de uma transação — Tópico 10). Uma armadilha real e comum em ferramentas como o MySQL Workbench: selecionar só parte do texto e mandar executar roda só o trecho selecionado — se você selecionar até o fim da linha do `SET` e deixar o `WHERE` de fora, o comando vira exatamente esse desastre.

```sql
SELECT @@sql_safe_updates;  -- 1 = trava ativa
```

Com `sql_safe_updates` ativo, o MySQL recusa qualquer `UPDATE`/`DELETE` que não filtre por chave primária ou índice, com a mensagem `You are using safe update mode...`. Trate essa mensagem como aliada: ela quase sempre significa que você esqueceu o `WHERE`.

**✅ Checkpoint:** você entende por que conferir o `WHERE` antes de executar é obrigatório, sempre.

**Passo 4 — UPDATE registrando uma devolução (regra de negócio real, e idempotente).**

```sql
SELECT * FROM emprestimo WHERE id = 2 AND data_devolucao IS NULL;

UPDATE emprestimo
SET data_devolucao = NOW()
WHERE id = 2 AND data_devolucao IS NULL;
```

`NOW()` é uma função do MySQL que devolve a data e hora atuais do servidor — usá-la, em vez de digitar a data, garante o horário real do evento e evita erro de digitação. `AND data_devolucao IS NULL` é a condição de proteção: só atualiza se o empréstimo ainda estiver em aberto. Rode o mesmo comando de novo:

```sql
UPDATE emprestimo
SET data_devolucao = NOW()
WHERE id = 2 AND data_devolucao IS NULL;
```

Na primeira execução, `data_devolucao` era `NULL`: 1 linha afetada. Na segunda, `data_devolucao` já está preenchida: o `WHERE` não casa mais, **0 linhas afetadas**. Um comando que pode ser repetido sem causar dano adicional é chamado de **idempotente**, e é o `WHERE` que torna este `UPDATE` idempotente. Sem essa proteção, a segunda execução sobrescreveria a data de devolução original.

> Note: `IS NULL`, nunca `= NULL`. Em SQL, `NULL` significa "desconhecido", e nada é igual a um desconhecido — `data_devolucao = NULL` nunca é verdadeiro, e o comando afetaria 0 linhas silenciosamente, sem erro nenhum. Sempre `IS NULL` / `IS NOT NULL`.

**✅ Checkpoint:** rodar o mesmo `UPDATE` uma segunda vez não altera nada.

**Passo 5 — DELETE, testado com SELECT antes.**

```sql
SELECT * FROM emprestimo WHERE data_devolucao IS NOT NULL AND data_devolucao < '2020-01-01';
DELETE FROM emprestimo WHERE data_devolucao IS NOT NULL AND data_devolucao < '2020-01-01';
```

`data_devolucao IS NOT NULL` restringe a empréstimos já devolvidos (um em aberto nunca deveria ser apagado por uma limpeza de histórico); `< '2020-01-01'` restringe aos antigos. Como não há registros tão antigos, o resultado é `0 rows affected` — comportamento esperado e seguro. Note que `DELETE` não tem lista de colunas: não existe "apagar uma coluna de uma linha" — `DELETE` remove a linha inteira (para "esvaziar" um campo, o comando certo é `UPDATE ... SET coluna = NULL`).

**✅ Checkpoint:** `0 rows affected`, sem erro.

**Passo 6 — DELETE e chaves estrangeiras.**

```sql
DELETE FROM associado WHERE id = 1;
```

**✅ Checkpoint:** o MySQL recusa com `ERROR 1451 (23000): Cannot delete or update a parent row: a foreign key constraint fails`. A FK do Tópico 1 (`FOREIGN KEY (associado_id) REFERENCES associado(id)`) obriga todo `emprestimo.associado_id` a apontar para um `associado.id` existente — se o associado 1 fosse apagado, os empréstimos dele ficariam apontando para ninguém (um **registro órfão**), e o banco impede isso ativamente. Você tem três saídas: apagar primeiro os filhos, depois o pai (Passo 7); declarar `ON DELETE CASCADE` na FK (perigoso — uma linha apagada pode levar centenas junto); ou não apagar, marcando como inativo (*soft delete*, no exercício 2).

**Passo 7 — use uma transação para a exclusão composta.**

```sql
START TRANSACTION;
DELETE FROM emprestimo WHERE associado_id = 5;
DELETE FROM associado WHERE id = 5;
COMMIT;

SELECT * FROM associado WHERE id = 5;  -- vazio
```

`START TRANSACTION` abre um bloco em que nada é gravado definitivamente até `COMMIT` — as alterações existem só na sua sessão. A ordem importa: os filhos (`emprestimo`) primeiro, o pai (`associado`) depois; invertida, o segundo `DELETE` cairia no mesmo erro do Passo 6. Se algo parecer errado antes do `COMMIT`, `ROLLBACK` desfaz tudo. Você vai aprofundar transações no Tópico 10 — por ora, o ponto central é: um conjunto de exclusões dependentes deve ser tratado como uma unidade atômica, tudo ou nada.

> ⚠️ Armadilha — autocommit. Por padrão o MySQL roda em *autocommit*: cada comando isolado é sua própria transação, confirmada na hora. Por isso um `DELETE` solto (fora de um `START TRANSACTION` explícito) não pode ser desfeito com `ROLLBACK` — ele já foi commitado automaticamente.

**✅ Checkpoint:** depois do `COMMIT`, `SELECT * FROM associado WHERE id = 5;` não retorna nada, e não há erro de FK.

### Armadilhas comuns — referência rápida

| Sintoma | Causa | Solução |
|---|---|---|
| UPDATE alterou a tabela inteira | WHERE ausente, ou fora da seleção executada | Sempre rode o SELECT equivalente antes; mantenha `sql_safe_updates` ligado |
| "You are using safe update mode" | O WHERE não usa chave primária/índice | Não desligue a trava por reflexo — confira se o filtro é mesmo o que você quer |
| UPDATE afetou 0 linhas sem motivo aparente | Comparação `= NULL` em vez de `IS NULL` | Use `IS NULL` / `IS NOT NULL` |
| Cannot delete or update a parent row | Existe linha filha apontando para a linha apagada | Apague os filhos primeiro (em transação), ou reveja o modelo |
| ROLLBACK "não funcionou" | O comando rodou em autocommit, fora de uma transação | Abra com `START TRANSACTION` antes do comando destrutivo |

### Resumo do que você construiu

```
✅ Hábito de sempre rodar SELECT antes de UPDATE/DELETE
✅ UPDATE simples no ciclo SELECT → UPDATE → SELECT
✅ Compreensão do UPDATE sem WHERE e da trava sql_safe_updates
✅ UPDATE idempotente, com a regra de negócio protegida dentro do WHERE
✅ DELETE testado com SELECT antes, apagando a linha inteira
✅ Confirmação de que a FK protege contra exclusões que quebrariam a integridade
✅ Transação (START TRANSACTION / COMMIT / ROLLBACK) como unidade atômica
```

**Exercícios:**
- *Fácil:* adicione uma coluna `ativo BOOLEAN DEFAULT TRUE` em `associado` e implemente soft delete com `UPDATE` em vez de `DELETE`.
- *Médio:* inicie uma transação, faça um `DELETE` errado de propósito e desfaça com `ROLLBACK` — confirme que o dado continua lá.
- *Difícil:* antes de um `DELETE` importante, copie a linha para uma tabela `associado_removido` usando `INSERT ... SELECT`.

**Perguntas para fixação:**
1. Por que `WHERE data_devolucao = NULL` nunca é verdadeiro, mesmo para uma linha em que `data_devolucao` é de fato nula?
2. O `UPDATE` do Passo 4 é idempotente. O `DELETE` do Passo 7 também é? Justifique rodando-o mentalmente uma segunda vez.

## 3. DQL Básico — SELECT e WHERE

**Objetivo:** ir além do `SELECT *`, filtrando, ordenando e limitando resultados — e entender a ordem real em que o MySQL executa uma consulta.

**Pré-requisitos:** banco `locadora`. Este tópico também é autossuficiente — veja o Passo 0.

### A ordem que você escreve ≠ a ordem que o banco executa

Guarde esta tabela: ela explica quase todas as dúvidas do resto deste tópico.

| Você escreve nesta ordem | O banco executa nesta ordem |
|---|---|
| `SELECT` colunas | 3º — escolhe as colunas |
| `FROM` tabela | 1º — localiza a tabela |
| `WHERE` filtro | 2º — descarta linhas |
| `ORDER BY` ordenação | 4º — ordena o que sobrou |
| `LIMIT` corte | 5º — corta a lista já ordenada |

O `WHERE` filtra antes de ordenar, e o `ORDER BY` sempre roda **antes** do `LIMIT` — é exatamente por isso que `ORDER BY ... LIMIT 5` significa, de forma determinística, "os 5 primeiros da lista ordenada": primeiro o banco decide a ordem inteira, só depois corta. Se fosse o contrário (cortar antes de ordenar), `LIMIT` devolveria 5 linhas arbitrárias, sem relação nenhuma com "os 5 primeiros de quê".

**Passo 0 — monte o banco, se ainda não tiver dados.** Use o mesmo `CREATE DATABASE`/`CREATE TABLE` do Tópico 2, Passo 0, e substitua o `INSERT` de `associado`, `livro` e `emprestimo` pelo bloco abaixo (mais linhas, para os exemplos de agregação do próximo tópico já saírem interessantes):

```sql
INSERT INTO associado (nome, email) VALUES
    ('Ana Souza',      'ana@email.com'),
    ('Bruno Lima',     'bruno@gmail.com'),
    ('Carla Dias',     'carla@gmail.com'),
    ('Daniel Rocha',   'daniel@outlook.com');

INSERT INTO livro (titulo, autor, isbn, preco, categoria_id, exemplares_disponiveis) VALUES
    ('Dom Casmurro',      'Machado de Assis',  '9788535910663', 39.90, 4, 2),
    ('Memórias Póstumas',  'Machado de Assis',  '9788535910664', 42.50, 4, 1),
    ('Iracema',            'José de Alencar',   '9788535910665', 29.90, 1, 3),
    ('A Hora da Estrela',  'Clarice Lispector', '9788535910666', 34.90, 1, 1);

INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista, data_devolucao) VALUES
    (1, 1, '2026-07-05 09:00:00', '2026-07-19', NULL),
    (2, 2, '2026-07-12 14:30:00', '2026-07-26', NULL),
    (3, 3, '2026-07-20 10:00:00', '2026-08-03', '2026-07-28 11:00:00'),
    (1, 4, '2026-07-31 16:00:00', '2026-08-14', NULL),
    (4, 1, '2026-06-15 08:00:00', '2026-06-29', '2026-06-30 09:00:00');
```

**✅ Checkpoint:** `SELECT COUNT(*) FROM emprestimo;` retorna 5.

**Passo 1 — SELECT: escolha só as colunas que precisa.**

```sql
SELECT * FROM livro;
SELECT titulo, autor FROM livro;
```

`FROM livro` diz de onde vêm as linhas; `SELECT *` é um curinga que significa "todas as colunas"; `SELECT titulo, autor` recebe uma lista explícita, e o banco devolve só essas duas, na ordem pedida.

| `SELECT *` | `SELECT titulo, autor` |
|---|---|
| Traz colunas que você não vai usar | Traz só o necessário |
| Mais tráfego entre banco e aplicação | Menos dados na rede |
| Quebra sem aviso se alguém reordenar/renomear colunas | Você declarou o que espera |
| Ótimo para explorar a tabela | Certo para usar em produção |

**✅ Checkpoint:** a segunda consulta retorna só `titulo` e `autor`, sem `id` nem `isbn`.

**Passo 2 — WHERE: filtre linhas com condições.**

```sql
SELECT * FROM livro WHERE autor = 'Machado de Assis';
SELECT * FROM associado WHERE email LIKE '%@gmail.com';
```

`= 'Machado de Assis'` é igualdade exata, caractere por caractere (acentos inclusive) — as aspas simples marcam um texto literal; sem elas, o MySQL acharia que `Machado` é o nome de uma coluna. `%` é o curinga do `LIKE`, e significa "qualquer sequência de caracteres, inclusive nenhuma":

| Padrão | Encontra | Exemplo que casa |
|---|---|---|
| `'%@gmail.com'` | Termina com | ana@gmail.com |
| `'ana%'` | Começa com | ana@email.com |
| `'%silva%'` | Contém em qualquer posição | joao.silva@x.com |
| `'a_a'` | `_` = exatamente um caractere | ana, ava |

> Com a *collation* padrão do MySQL (`utf8mb4_0900_ai_ci`), tanto `=` quanto `LIKE` são *case-insensitive*: `'MACHADO'` e `'machado'` dão o mesmo resultado. Em outros bancos (PostgreSQL, por exemplo) isso não vale — não conte com esse comportamento se o código precisar ser portável entre SGBDs.

**✅ Checkpoint:** a segunda consulta retorna só associados com e-mail do Gmail.

**Passo 3 — combine condições com AND / OR.**

```sql
SELECT * FROM emprestimo WHERE data_devolucao IS NULL AND associado_id = 1;
SELECT * FROM livro WHERE autor = 'Machado de Assis' OR autor = 'José de Alencar';
```

`AND` exige as duas condições verdadeiras — cada `AND` que você acrescenta diminui (ou mantém) o número de linhas. `OR` exige só uma — cada `OR` aumenta (ou mantém). Note também o `IS NULL` na primeira: `WHERE data_devolucao = NULL` nunca é verdadeiro (nada é igual a um desconhecido) e retornaria 0 linhas silenciosamente — um dos erros mais traiçoeiros do SQL, porque a consulta roda, não acusa nada, e simplesmente devolve vazio.

> ⚠️ Armadilha — precedência entre AND e OR. `AND` é avaliado antes de `OR`, como `×` antes de `+` na matemática:
> ```sql
> -- Provavelmente NÃO é o que você quis dizer:
> WHERE autor = 'Machado' OR autor = 'Alencar' AND ano > 2000
> -- O banco entende: autor='Machado' OR (autor='Alencar' AND ano>2000)
>
> -- Use parênteses para deixar explícito:
> WHERE (autor = 'Machado' OR autor = 'Alencar') AND ano > 2000
> ```
> Na dúvida, parentize — não custa nada e elimina a ambiguidade.

**✅ Checkpoint:** você sabe prever o resultado antes de rodar cada consulta.

**Passo 4 — IN, BETWEEN e comparações.**

```sql
SELECT * FROM livro WHERE autor IN ('Machado de Assis', 'José de Alencar', 'Clarice Lispector');
```

`IN (lista)` equivale a vários `OR` encadeados — a vantagem é só de legibilidade: acrescentar um autor é acrescentar um item na lista, sem risco de errar a precedência.

```sql
SELECT * FROM emprestimo
WHERE data_emprestimo >= '2026-07-01' AND data_emprestimo < '2026-08-01';
```

Para faixas, o atalho equivalente é `BETWEEN a AND b`, inclusivo nas duas pontas. Mas há uma armadilha com colunas `DATETIME` (como `data_emprestimo`): `BETWEEN '2026-07-01' AND '2026-07-31'` interpreta `'2026-07-31'` como `'2026-07-31 00:00:00'` — um empréstimo feito às 14h do dia 31 ficaria de fora. Por isso a consulta acima usa `>= início AND < dia seguinte`, em vez de `BETWEEN`, para o mês inteiro de julho.

**✅ Checkpoint:** a consulta retorna só os empréstimos de julho, incluindo o do dia 31 às 16h.

**Passo 5 — ORDER BY: ordene os resultados.** Sem `ORDER BY`, a ordem das linhas é indefinida — o banco devolve na ordem que for mais rápida para ele, e essa ordem pode mudar entre duas execuções da mesma consulta.

```sql
SELECT titulo, autor FROM livro ORDER BY titulo ASC;
SELECT * FROM emprestimo ORDER BY data_emprestimo DESC;
SELECT titulo, autor FROM livro ORDER BY autor ASC, titulo ASC;
```

`ASC` (o padrão, se você omitir) é crescente; `DESC` é decrescente — em datas, mais recente primeiro, o formato usual de um feed ou histórico. Você pode ordenar por mais de uma coluna: a segunda serve como critério de desempate (dentro de cada autor, ordena por título).

**✅ Checkpoint:** a segunda consulta mostra o empréstimo mais recente na primeira linha.

**Passo 6 — LIMIT: restrinja a quantidade de linhas.**

```sql
SELECT * FROM emprestimo ORDER BY data_emprestimo DESC LIMIT 3;
```

Lendo na ordem em que o banco de fato executa (a tabela do início deste tópico): primeiro `FROM emprestimo` traz todas as linhas; depois `ORDER BY data_emprestimo DESC` coloca a mais recente no topo; só então `LIMIT 3` devolve as 3 primeiras dessa lista já ordenada. Juntas, `ORDER BY` + `LIMIT` formam a combinação clássica "os N mais...": `ORDER BY` decide o significado de "mais", `LIMIT` decide o "N". `LIMIT` sozinho, sem `ORDER BY`, devolveria linhas arbitrárias — sem `ORDER BY`, não existe "as primeiras" com garantia nenhuma. Há também um motivo de desempenho: sem `LIMIT`, uma tabela com milhões de linhas seria inteiramente transferida para a aplicação; toda paginação na web é `ORDER BY` + `LIMIT` por trás.

**✅ Checkpoint:** a consulta retorna no máximo 3 linhas, mesmo que existam mais empréstimos.

**Passo 7 — combine tudo: uma consulta real de relatório.** Pergunta de negócio: "quais são os empréstimos em aberto há mais tempo?" — o relatório de cobrança da locadora. O problema: `emprestimo` só guarda `associado_id` e `livro_id` (números); para mostrar nomes, é preciso três tabelas ao mesmo tempo.

```sql
SELECT * FROM emprestimo e;
```

O `e` depois do nome da tabela é um **alias** (apelido): a partir daí você escreve `e.data_emprestimo` em vez de `emprestimo.data_emprestimo`. Obrigatório quando duas tabelas têm colunas de mesmo nome (aqui, todas têm `id`).

```sql
SELECT a.nome, l.titulo, e.data_emprestimo
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
JOIN livro l ON e.livro_id = l.id
WHERE e.data_devolucao IS NULL
ORDER BY e.data_emprestimo ASC
LIMIT 10;
```

`JOIN associado a` traz a tabela junto (com alias `a`); `ON e.associado_id = a.id` diz como casar as linhas — a FK do empréstimo aponta para a PK do associado, exatamente a relação desenhada no DER do Tópico 1. `WHERE e.data_devolucao IS NULL` filtra só os em aberto. `ORDER BY e.data_emprestimo ASC` — repare que aqui o `ASC` é proposital e diferente do Passo 6: para "atrasados", o que interessa é o empréstimo mais **antigo** ainda aberto, não o mais recente.

> ⚠️ Armadilha — esquecer o `ON`. Um `JOIN` sem `ON` (ou com uma condição errada) produz um **produto cartesiano**: cada linha de uma tabela combinada com todas as da outra. Com 1.000 empréstimos e 1.000 associados, o resultado teria 1.000.000 de linhas. O sintoma é uma consulta que trava ou devolve uma quantidade absurda de linhas repetidas.

**✅ Checkpoint:** a consulta retorna nome do associado, título do livro e data, ordenados do mais antigo em aberto para o mais recente.

### Armadilhas comuns — referência rápida

| Sintoma | Causa | Solução |
|---|---|---|
| Consulta devolve 0 linhas sem erro | Comparação `= NULL` em vez de `IS NULL` | Use `IS NULL` / `IS NOT NULL` |
| AND/OR combinados dão resultado inesperado | AND tem precedência sobre OR | Use parênteses |
| BETWEEN não pega registros do último dia | Coluna DATETIME; `'...-31'` vira `00:00:00` | Use `>= início AND < dia seguinte` |
| Ordem das linhas muda entre execuções | Sem ORDER BY, a ordem é indefinida | Declare ORDER BY sempre que a ordem importar |
| LIMIT traz linhas "aleatórias" | LIMIT sem ORDER BY não tem critério | Combine sempre ORDER BY + LIMIT |
| Consulta com JOIN explode em linhas repetidas | ON ausente ou errado → produto cartesiano | Confira se cada JOIN tem seu ON ligando FK → PK |
| `Column 'id' in field list is ambiguous` | Duas tabelas do JOIN têm coluna de mesmo nome | Prefixe com o alias: `a.id`, `l.id` |

### Resumo do que você construiu

```
✅ Diferença entre a ordem de escrita e a ordem de execução das cláusulas
✅ SELECT nomeando colunas específicas em vez de SELECT *
✅ WHERE com igualdade, LIKE e os curingas % e _
✅ Combinação de condições com AND / OR e o uso de parênteses
✅ IN e BETWEEN como atalhos legíveis para OR e comparações de faixa
✅ Tratamento correto de NULL com IS NULL / IS NOT NULL
✅ ORDER BY ascendente, descendente e com critério de desempate
✅ LIMIT restringindo a quantidade, sempre junto de ORDER BY
✅ Consulta de relatório combinando JOIN + WHERE + ORDER BY + LIMIT
```

**Exercícios:**
- *Fácil:* encontre livros cujo título contenha "casa" (a busca já é *case-insensitive* por padrão no MySQL).
- *Médio:* liste associados sem nenhum empréstimo — pesquise `NOT IN` ou `LEFT JOIN ... IS NULL` (você vai formalizar esse padrão no Tópico 6).
- *Difícil:* empréstimos dos últimos 7 dias, usando `CURDATE() - INTERVAL 7 DAY`.

**Perguntas para fixação:**
1. Por que `ORDER BY ... LIMIT N` é determinístico, mas `LIMIT N` sozinho não é?
2. No Passo 4, por que a consulta de julho usou `>= '2026-07-01' AND < '2026-08-01'` em vez de `BETWEEN '2026-07-01' AND '2026-07-31'`?

## 4. DQL com Funções Agregadas

**Objetivo:** responder perguntas como "quantos empréstimos por associado?" e "qual livro foi mais emprestado?" usando funções agregadas.

**Pré-requisitos:** banco `locadora` do Tópico 3, com múltiplos empréstimos por associado.

**✅ Checkpoint:** `SELECT COUNT(*) FROM emprestimo;` retorna mais de 3.

**Passo 1 — COUNT: quantas linhas.**

```sql
SELECT COUNT(*) FROM emprestimo;
SELECT COUNT(*) FROM emprestimo WHERE data_devolucao IS NULL;
```

`COUNT(*)` conta linhas; combinado com `WHERE`, conta só as que atendem à condição — aqui, empréstimos ainda em aberto.

**✅ Checkpoint:** o segundo `COUNT` é menor ou igual ao primeiro.

**Passo 2 — GROUP BY: agrupe e agregue por categoria.**

```sql
SELECT associado_id, COUNT(*) AS total_emprestimos
FROM emprestimo
GROUP BY associado_id;
```

`GROUP BY associado_id` faz o MySQL agrupar todas as linhas com o mesmo `associado_id` em um único resultado, e `COUNT(*)` conta quantas linhas caíram em cada grupo.

**✅ Checkpoint:** a consulta retorna uma linha por associado, com o total de empréstimos de cada um.

**Passo 3 — combine GROUP BY com JOIN, para nomes legíveis.**

```sql
SELECT a.nome, COUNT(*) AS total_emprestimos
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
GROUP BY a.id, a.nome
ORDER BY total_emprestimos DESC;
```

⚠️ Regra importante: toda coluna no `SELECT` que não é uma função agregada precisa estar no `GROUP BY` (aqui, `a.id` e `a.nome`). O MySQL, em modo estrito (`ONLY_FULL_GROUP_BY`, padrão desde a versão 5.7), recusa a consulta se essa regra for violada.

**✅ Checkpoint:** a lista aparece ordenada do associado com mais empréstimos para o com menos.

**Passo 4 — o livro mais emprestado ("Top 1" com agregação).**

```sql
SELECT l.titulo, COUNT(*) AS vezes_emprestado
FROM emprestimo e
JOIN livro l ON e.livro_id = l.id
GROUP BY l.id, l.titulo
ORDER BY vezes_emprestado DESC
LIMIT 1;
```

**✅ Checkpoint:** a consulta retorna exatamente 1 linha: o título mais emprestado e sua contagem.

**Passo 5 — HAVING: filtre depois de agregar.**

```sql
SELECT associado_id, COUNT(*) AS total
FROM emprestimo
GROUP BY associado_id
HAVING total > 1;
```

`WHERE` filtra linhas **antes** de agrupar; `HAVING` filtra grupos **depois** de agregar. Não daria para escrever `WHERE COUNT(*) > 1` — o `COUNT(*)` só existe depois do agrupamento, e é exatamente por isso que `HAVING` existe como cláusula separada.

**✅ Checkpoint:** só aparecem associados com mais de 1 empréstimo.

**Passo 6 — outras funções agregadas: MIN, MAX, AVG.**

```sql
SELECT
  MIN(data_emprestimo) AS emprestimo_mais_antigo,
  MAX(data_emprestimo) AS emprestimo_mais_recente,
  COUNT(*) AS total
FROM emprestimo;
```

`MIN`/`MAX` retornam o menor e o maior valor da coluna (funcionam com números, datas e texto). Todas essas funções (`COUNT`, `SUM`, `AVG`, `MIN`, `MAX`) ignoram valores `NULL` automaticamente.

**✅ Checkpoint:** a consulta retorna as três colunas em uma única linha — sem `GROUP BY`, o "grupo" é a tabela inteira.

**Passo 7 — relatório completo: associados mais ativos, com filtro.**

```sql
SELECT a.nome, COUNT(*) AS total_emprestimos, MAX(e.data_emprestimo) AS ultimo_emprestimo
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
GROUP BY a.id, a.nome
HAVING total_emprestimos >= 1
ORDER BY total_emprestimos DESC
LIMIT 5;
```

Pergunta de negócio real: "quem são os associados mais ativos, e quando foi a última vez que pegaram um livro?".

**✅ Checkpoint:** a consulta combina `JOIN` + `GROUP BY` + `HAVING` + `ORDER BY` + `LIMIT` em uma única query coerente.

### Resumo do que você construiu

```
✅ COUNT(*) simples e com WHERE
✅ GROUP BY agrupando linhas por coluna (associado, livro)
✅ Regra do ONLY_FULL_GROUP_BY
✅ HAVING filtrando grupos depois da agregação (diferente de WHERE)
✅ MIN, MAX, AVG como outras funções agregadas
✅ Relatório completo combinando JOIN + GROUP BY + HAVING + ORDER BY + LIMIT
```

**Exercícios:**
- *Fácil:* livros nunca emprestados — pesquise `LEFT JOIN ... IS NULL` combinado com o que você já sabe.
- *Médio:* empréstimos por mês, agrupando por `MONTH(data_emprestimo)`.
- *Difícil:* associados com mais de 1 empréstimo **e** que têm pelo menos 1 em aberto (dica: `HAVING` com duas condições, uma delas sobre uma soma condicional).

**Perguntas para fixação:**
1. Por que a consulta do Passo 4 não funcionaria com `WHERE vezes_emprestado > 1` no lugar de `HAVING`?
2. `COUNT(*)` e `COUNT(coluna)` não são sempre equivalentes — em que situação eles dariam resultados diferentes? (Você vai ver um caso concreto disso no Tópico 6, Passo 2.)

## 5. Consultas com Múltiplas Tabelas (JOINs)

**Objetivo:** dominar os tipos de JOIN — `INNER`, `LEFT`, `RIGHT` — e saber qual deles responde a pergunta certa em cada situação.

**Pré-requisitos:** banco `locadora` dos tópicos anteriores. Adicione um associado sem nenhum empréstimo, para os exemplos de `LEFT JOIN` fazerem sentido:

```sql
INSERT INTO associado (nome, email) VALUES ('Fábio Teixeira', 'fabio@email.com');
```

**✅ Checkpoint:** `SELECT COUNT(*) FROM associado;` é maior que `SELECT COUNT(DISTINCT associado_id) FROM emprestimo;`.

**Passo 1 — INNER JOIN: só o que existe nos dois lados.**

```sql
SELECT a.nome, e.id, e.data_devolucao
FROM associado a
INNER JOIN emprestimo e ON a.id = e.associado_id;
```

`INNER JOIN` retorna só associados que têm pelo menos um empréstimo — Fábio, que não tem nenhum, some do resultado.

**✅ Checkpoint:** Fábio não aparece.

**Passo 2 — LEFT JOIN: tudo do lado esquerdo, mesmo sem correspondência.**

```sql
SELECT a.nome, e.id, e.data_devolucao
FROM associado a
LEFT JOIN emprestimo e ON a.id = e.associado_id;
```

`LEFT JOIN` mantém **todos** os associados, preenchendo as colunas de empréstimo com `NULL` quando não há correspondência — é por isso que `COUNT(e.id)` (não `COUNT(*)`) é a forma certa de contar empréstimos por associado num `LEFT JOIN`: `COUNT(*)` contaria a linha mesmo com tudo `NULL`, `COUNT(e.id)` não.

**✅ Checkpoint:** Fábio aparece agora, com `NULL` nas colunas de empréstimo.

**Passo 3 — use LEFT JOIN para responder "quem nunca pegou um livro emprestado?"**

```sql
SELECT a.nome, a.email
FROM associado a
LEFT JOIN emprestimo e ON a.id = e.associado_id
WHERE e.id IS NULL;
```

Esse é o padrão **anti-join**: `LEFT JOIN` + `WHERE ... IS NULL` encontra exatamente os associados sem nenhum empréstimo — algo impossível de responder com `INNER JOIN` sozinho, já que o `INNER JOIN` descarta justamente as linhas sem correspondência que você está tentando encontrar.

**✅ Checkpoint:** retorna só o Fábio.

**Passo 4 — RIGHT JOIN: o espelho do LEFT.**

```sql
SELECT a.nome, e.id
FROM emprestimo e
RIGHT JOIN associado a ON e.associado_id = a.id;
```

Equivalente ao `LEFT JOIN` do Passo 2, invertendo qual tabela vem primeiro na escrita. A maioria prefere sempre `LEFT JOIN` por consistência (mais fácil de ler "de qual tabela eu quero tudo" quando ela é sempre a primeira).

**✅ Checkpoint:** compare com o resultado do Passo 2 — são iguais.

**Passo 5 — JOIN de três tabelas: relatório de empréstimos ativos.**

```sql
SELECT a.nome AS associado, l.titulo AS livro, e.data_emprestimo, e.data_devolucao
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
JOIN livro l ON e.livro_id = l.id
WHERE e.data_devolucao IS NULL;
```

`WHERE e.data_devolucao IS NULL` filtra apenas empréstimos em andamento — combinado com o JOIN de três tabelas, forma um relatório operacional real da locadora.

**✅ Checkpoint:** a consulta retorna uma linha por empréstimo ativo, com nome do associado e título do livro.

**Passo 6 — SELF JOIN: livros do mesmo autor.**

```sql
SELECT l1.titulo AS livro1, l2.titulo AS livro2, l1.autor
FROM livro l1
JOIN livro l2 ON l1.autor = l2.autor AND l1.id < l2.id;
```

Um **SELF JOIN** é a mesma tabela referenciada duas vezes, com aliases diferentes (`l1`, `l2`). `l1.id < l2.id` evita comparar um livro consigo mesmo e evita pares duplicados (Dom Casmurro/Memórias Póstumas e Memórias Póstumas/Dom Casmurro seriam o mesmo par, contado duas vezes sem essa condição).

**✅ Checkpoint:** se dois livros têm o mesmo autor, a consulta retorna esse par uma única vez.

### Resumo do que você construiu

```
✅ INNER JOIN retornando só correspondências dos dois lados
✅ LEFT JOIN mantendo todos os associados, com NULL onde não há empréstimo
✅ Padrão "anti-join" para achar associados sem empréstimo
✅ RIGHT JOIN entendido como espelho do LEFT JOIN
✅ JOIN de três tabelas filtrando empréstimos em andamento
✅ SELF JOIN comparando livros do mesmo autor
```

**Exercícios:**
- *Fácil:* livros nunca emprestados — anti-join entre `livro` e `emprestimo`.
- *Médio:* associados atrasados — `LEFT JOIN` filtrando `data_devolucao IS NULL AND data_prevista < CURDATE()`.
- *Difícil:* self join de categoria — junte `categoria` com ela mesma usando `categoria_pai_id`, listando cada categoria raiz com suas subcategorias (você vai formalizar isso no próximo tópico).

**Perguntas para fixação:**
1. Por que o anti-join do Passo 3 não funcionaria trocando `LEFT JOIN` por `INNER JOIN`?
2. No Passo 6, o que aconteceria com o resultado se a condição fosse `l1.id != l2.id` em vez de `l1.id < l2.id`?

## 6. JOINs Avançados (LEFT/RIGHT, SELF)

**Objetivo:** aprofundar `LEFT`/`RIGHT JOIN` e `SELF JOIN` em cenários de relatório mais próximos do que aparece num sistema real.

**Pré-requisitos:** banco `locadora` com os JOINs básicos do Tópico 5 já praticados.

**✅ Checkpoint:** a consulta de "associados sem empréstimo" do Tópico 5 ainda funciona.

**Passo 1 — LEFT JOIN com múltiplas condições no ON.**

```sql
SELECT a.nome, e.id, e.data_devolucao
FROM associado a
LEFT JOIN emprestimo e ON a.id = e.associado_id AND e.data_devolucao IS NULL;
```

Colocar `AND e.data_devolucao IS NULL` dentro do `ON` (não no `WHERE`) mantém **todos** os associados no resultado, mas só "conecta" empréstimos ainda em andamento — diferente de filtrar no `WHERE`, que eliminaria associados sem nenhum empréstimo ativo (incluindo os que só têm empréstimos já devolvidos).

**✅ Checkpoint:** um associado com empréstimo já devolvido aparece com `NULL` nas colunas de empréstimo, porque o único empréstimo dele não bate na condição do `ON`.

**Passo 2 — combine LEFT JOIN com GROUP BY: quantos empréstimos por associado, incluindo zero.**

```sql
SELECT a.nome, COUNT(e.id) AS total_emprestimos
FROM associado a
LEFT JOIN emprestimo e ON a.id = e.associado_id
GROUP BY a.id, a.nome
ORDER BY total_emprestimos ASC;
```

`COUNT(e.id)` — não `COUNT(*)` — conta só linhas onde `e.id` não é `NULL`: associados sem empréstimo aparecem com **0**, em vez de sumir do relatório como aconteceria com `INNER JOIN`. Esta é a diferença entre `COUNT(*)` e `COUNT(coluna)` levantada na pergunta de fixação do Tópico 4.

**✅ Checkpoint:** associados sem empréstimo aparecem no topo, com `total_emprestimos = 0`.

**Passo 3 — SELF JOIN hierárquico: categorias e subcategorias.**

```sql
SELECT c.nome AS categoria, sub.nome AS subcategoria
FROM categoria c
LEFT JOIN categoria sub ON sub.categoria_pai_id = c.id
WHERE c.categoria_pai_id IS NULL;
```

`categoria_pai_id` referenciando a própria tabela `categoria` (declarada assim desde o Tópico 1) é o padrão clássico de hierarquia de um nível. `LEFT JOIN` garante que categorias sem subcategoria ainda apareçam no relatório, com `subcategoria = NULL`.

**✅ Checkpoint:** categorias raiz sem subcategoria aparecem com `subcategoria = NULL`; "Romance" aparece com "Romance Machadiano".

**Passo 4 — RIGHT JOIN onde ele é mais natural que reescrever com LEFT.**

```sql
SELECT l.titulo, e.id AS emprestimo_id
FROM emprestimo e
RIGHT JOIN livro l ON e.livro_id = l.id
WHERE e.id IS NULL;
```

A consulta já começa de `emprestimo`; `RIGHT JOIN livro` (mantendo todo livro) evita reescrever a consulta inteira trocando a ordem das tabelas. Encontra livros que nunca foram emprestados.

**✅ Checkpoint:** a consulta retorna livros com zero empréstimos.

**Passo 5 — JOIN triplo com SELF JOIN: associados que pegaram o mesmo livro.**

```sql
SELECT DISTINCT a1.nome AS associado1, a2.nome AS associado2, l.titulo
FROM emprestimo e1
JOIN emprestimo e2 ON e1.livro_id = e2.livro_id AND e1.associado_id < e2.associado_id
JOIN associado a1 ON a1.id = e1.associado_id
JOIN associado a2 ON a2.id = e2.associado_id
JOIN livro l ON l.id = e1.livro_id;
```

`e1.associado_id < e2.associado_id` no SELF JOIN de `emprestimo` evita pares duplicados e a comparação de um associado consigo mesmo — o mesmo padrão do Tópico 5, agora combinado com mais dois JOINs.

**✅ Checkpoint:** a consulta retorna pares de associados que emprestaram o mesmo título, sem repetir o mesmo par invertido.

### Resumo do que você construiu

```
✅ Condição extra dentro do ON de um LEFT JOIN (diferente de filtrar no WHERE)
✅ LEFT JOIN + GROUP BY contando "zero" corretamente para quem não tem correspondência
✅ SELF JOIN hierárquico para categoria/subcategoria
✅ RIGHT JOIN aplicado quando é mais natural que reescrever a consulta
✅ JOIN triplo combinando SELF JOIN de emprestimo com duas junções de associado
```

**Exercícios:**
- *Fácil:* reescreva o Passo 4 com `LEFT JOIN` e confirme que o resultado é idêntico.
- *Médio:* adicione uma subcategoria-de-subcategoria (uma hierarquia de 2 níveis) e ajuste a consulta do Passo 3.
- *Difícil:* inverta a lógica do Passo 5 — associados que **nunca** pegaram o mesmo livro que outro associado.

**Perguntas para fixação:**
1. No Passo 1, por que colocar a condição extra no `ON` (em vez do `WHERE`) muda o conjunto de associados retornados?
2. O relatório do Passo 2 usaria `INNER JOIN` corretamente se a pergunta fosse "só os associados que já pegaram pelo menos um livro"? Por quê?

## 7. Subconsultas

**Objetivo:** usar consultas dentro de consultas como alternativa a JOINs em cenários em que elas são mais diretas ou mais legíveis.

**Pré-requisitos:** banco `locadora` com os JOINs já praticados.

**✅ Checkpoint:** `SELECT COUNT(*) FROM emprestimo;` funciona.

**Passo 1 — subconsulta escalar: comparar com um valor agregado.**

```sql
SELECT titulo, preco
FROM livro
WHERE preco > (SELECT AVG(preco) FROM livro);
```

A subconsulta `(SELECT AVG(preco) FROM livro)` roda primeiro, retorna um único valor (**escalar**), e a consulta externa usa esse valor no `WHERE` — encontra livros acima do preço médio.

**✅ Checkpoint:** todos os livros retornados têm preço maior que a média geral.

**Passo 2 — subconsulta com IN: associados que já pegaram emprestado.**

```sql
SELECT nome, email
FROM associado
WHERE id IN (SELECT DISTINCT associado_id FROM emprestimo);
```

`IN (subconsulta)` verifica se o `id` do associado aparece na lista retornada pela subconsulta — equivalente a um `INNER JOIN`, mas às vezes mais legível quando você só precisa confirmar existência, não trazer colunas da outra tabela.

**✅ Checkpoint:** a consulta retorna só associados que têm ao menos um empréstimo.

**Passo 3 — subconsulta com NOT IN: o "anti-join" via subconsulta.**

```sql
SELECT nome, email
FROM associado
WHERE id NOT IN (SELECT associado_id FROM emprestimo WHERE associado_id IS NOT NULL);
```

Alternativa ao `LEFT JOIN` + `WHERE ... IS NULL` do Tópico 5. O `WHERE associado_id IS NOT NULL` dentro da subconsulta evita um problema clássico: se qualquer linha retornada pela subconsulta for `NULL`, `NOT IN` passa a não retornar **nenhuma** linha — um comportamento do `NULL` em SQL fácil de esquecer e difícil de depurar, porque a consulta não dá erro, só devolve vazio sem explicação.

**✅ Checkpoint:** a consulta retorna os mesmos associados do anti-join com `LEFT JOIN` feito no Tópico 5.

**Passo 4 — subconsulta correlacionada: livros acima da média da própria categoria.**

```sql
SELECT l1.titulo, l1.categoria_id, l1.preco
FROM livro l1
WHERE l1.preco > (
    SELECT AVG(l2.preco)
    FROM livro l2
    WHERE l2.categoria_id = l1.categoria_id
);
```

Diferente do Passo 1, esta subconsulta é **correlacionada**: ela referencia `l1` da consulta externa, então roda uma vez para cada linha de `l1` — mais lenta que uma subconsulta simples, mas resolve uma pergunta que um JOIN sozinho não responde com a mesma naturalidade: "acima da média, mas da própria categoria, não da média geral".

**✅ Checkpoint:** um livro caro numa categoria barata aparece; um livro barato numa categoria cara não aparece.

**Passo 5 — subconsulta no FROM (tabela derivada).**

```sql
SELECT categoria_id, media_categoria
FROM (
    SELECT categoria_id, AVG(preco) AS media_categoria
    FROM livro
    GROUP BY categoria_id
) AS medias_por_categoria
WHERE media_categoria > 35;
```

Uma subconsulta no `FROM` (chamada **tabela derivada**) é tratada como se fosse uma tabela normal — permite filtrar com `WHERE` sobre um resultado já agregado, algo que `HAVING` sozinho deixaria menos legível numa consulta mais complexa que a deste exemplo.

**✅ Checkpoint:** a consulta retorna só categorias cuja média de preço passa de R$ 35.

### Resumo do que você construiu

```
✅ Subconsulta escalar comparando com um valor agregado (AVG)
✅ Subconsulta com IN como alternativa a INNER JOIN
✅ Subconsulta com NOT IN (com cuidado contra NULL) como anti-join
✅ Subconsulta correlacionada resolvendo "acima da média do próprio grupo"
✅ Subconsulta no FROM (tabela derivada) filtrando sobre dado já agregado
```

**Exercícios:**
- *Fácil:* reescreva o Passo 2 usando `WHERE EXISTS (...)` e compare a legibilidade.
- *Médio:* adicione uma coluna com o total de empréstimos de cada associado, via subconsulta escalar dentro do `SELECT`.
- *Difícil:* pesquise por que subconsultas correlacionadas costumam ser mais lentas que JOINs equivalentes — relacione com o Tópico 9 (Índices).

**Perguntas para fixação:**
1. Por que a subconsulta do Passo 3 precisa do `WHERE associado_id IS NOT NULL` interno, mas a do Passo 2 (com `IN`) não teria o mesmo problema?
2. O que muda, de fato, entre a subconsulta do Passo 1 (escalar simples) e a do Passo 4 (correlacionada), em termos de quantas vezes cada uma roda?

## 8. Views

**Objetivo:** encapsular consultas complexas e repetidas em Views — consultas salvas que se comportam como tabelas virtuais.

**Pré-requisitos:** banco `locadora` com JOINs e subconsultas já praticados.

**✅ Checkpoint:** a consulta de "empréstimos ativos" do Tópico 5 funciona.

**Passo 1 — entenda o problema que Views resolvem.** A consulta de empréstimos ativos (JOIN de três tabelas + `WHERE`) tende a se repetir em vários relatórios diferentes. Copiar e colar o mesmo SQL em cada lugar significa que uma correção precisa ser replicada manualmente em todos eles — um convite a esquecer um.

**✅ Checkpoint:** você lembra qual consulta de tópicos anteriores seria uma boa candidata a virar View.

**Passo 2 — crie sua primeira View.**

```sql
CREATE VIEW vw_emprestimos_ativos AS
SELECT a.nome AS associado, l.titulo AS livro, e.data_emprestimo, e.data_prevista
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
JOIN livro l ON e.livro_id = l.id
WHERE e.data_devolucao IS NULL;
```

`CREATE VIEW` salva a **definição** da consulta, não os dados — toda vez que a View é consultada, o SQL por trás roda de novo, sobre os dados atuais.

**✅ Checkpoint:** `SELECT * FROM vw_emprestimos_ativos;` retorna os mesmos resultados da consulta manual de tópicos anteriores.

**Passo 3 — consulte a View como se fosse uma tabela normal.**

```sql
SELECT * FROM vw_emprestimos_ativos WHERE associado = 'Ana Beatriz Souza';

SELECT associado, COUNT(*) AS total
FROM vw_emprestimos_ativos
GROUP BY associado
ORDER BY total DESC;
```

`WHERE`, `GROUP BY`, `ORDER BY` funcionam sobre a View exatamente como funcionariam sobre uma tabela — o JOIN complexo fica escondido atrás de um nome simples.

**✅ Checkpoint:** a segunda consulta retorna o ranking de associados com mais empréstimos ativos, sem repetir o JOIN em lugar nenhum.

**Passo 4 — crie uma View com agregação: relatório de atraso.**

```sql
CREATE VIEW vw_emprestimos_atrasados AS
SELECT a.nome AS associado, l.titulo AS livro, e.data_prevista,
       DATEDIFF(CURDATE(), e.data_prevista) AS dias_atraso
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
JOIN livro l ON e.livro_id = l.id
WHERE e.data_devolucao IS NULL AND e.data_prevista < CURDATE();
```

`DATEDIFF(CURDATE(), e.data_prevista)` calcula dias de atraso dinamicamente, a cada consulta — a View sempre reflete o "hoje" real, sem precisar recalcular ou re-salvar nada.

**✅ Checkpoint:** `SELECT * FROM vw_emprestimos_atrasados;` mostra só empréstimos vencidos, com a coluna de dias de atraso.

**Passo 5 — entenda a limitação: Views não são cache.**

```sql
SELECT COUNT(*) FROM vw_emprestimos_ativos;  -- roda o JOIN de novo
SELECT COUNT(*) FROM vw_emprestimos_ativos;  -- roda o JOIN de novo, outra vez
```

Diferente de uma tabela materializada ou de um cache, uma View comum não armazena resultado — ela só guarda a consulta. Para dados que mudam pouco e são consultados com muita frequência, isso pode significar reprocessamento desnecessário — assunto do próximo tópico, Índices.

**✅ Checkpoint:** você consegue explicar por que uma View não é a mesma coisa que salvar um resultado em cache.

### Resumo do que você construiu

```
✅ CREATE VIEW encapsulando um JOIN de três tabelas repetido em vários relatórios
✅ Consultas com WHERE/GROUP BY/ORDER BY funcionando normalmente sobre a View
✅ View com cálculo dinâmico (DATEDIFF) sempre refletindo a data atual
✅ Entendimento de que uma View reexecuta a consulta por trás, não é um cache
```

**Exercícios:**
- *Fácil:* `vw_livros_disponiveis` — View mostrando livros com `exemplares_disponiveis > 0`.
- *Médio:* crie uma consulta que use `vw_emprestimos_atrasados` com um filtro adicional (por exemplo, só atrasos acima de 30 dias).
- *Difícil:* pesquise `DROP VIEW` e recrie `vw_emprestimos_ativos` com uma coluna a mais, sem apagar e recriar manualmente (dica: `CREATE OR REPLACE VIEW`).

**Perguntas para fixação:**
1. Se a tabela `emprestimo` ganhar uma linha nova, a View `vw_emprestimos_ativos` precisa ser recriada para refletir isso? Por quê?
2. Por que `DATEDIFF(CURDATE(), e.data_prevista)` dentro de uma View continua correto mesmo dias depois de ela ter sido criada, mas o mesmo cálculo guardado como uma coluna comum (preenchida uma vez, no momento do `INSERT`) ficaria desatualizado?

## 9. Índices e Performance

**Objetivo:** entender como o banco encontra linhas rapidamente, e quando um índice ajuda ou atrapalha.

**Pré-requisitos:** banco `locadora` com volume razoável de dados.

**✅ Checkpoint:** `SELECT COUNT(*) FROM livro;` retorna um número razoável de linhas.

**Passo 1 — meça uma consulta sem índice com EXPLAIN.**

```sql
EXPLAIN SELECT * FROM livro WHERE autor = 'Machado de Assis';
```

`EXPLAIN` mostra o plano de execução sem rodar a consulta de verdade — se `type = ALL` aparecer, o MySQL está fazendo um *full table scan*, examinando linha por linha. (Note: não usamos `isbn` neste exemplo porque ele já é `UNIQUE` desde o Tópico 1, e todo `UNIQUE` já cria um índice automaticamente — o `EXPLAIN` nele já sairia otimizado, mesmo sem nenhum `CREATE INDEX` explícito. `autor`, sem nenhuma restrição desse tipo, é o exemplo correto de "ainda sem índice".)

**✅ Checkpoint:** `type = ALL` aparece no resultado.

**Passo 2 — crie um índice e compare.**

```sql
CREATE INDEX idx_livro_autor ON livro(autor);
EXPLAIN SELECT * FROM livro WHERE autor = 'Machado de Assis';
```

`type` muda para `ref` — o índice B-tree permite ao MySQL pular direto às linhas com aquele autor, em vez de examinar a tabela inteira.

**✅ Checkpoint:** o `EXPLAIN` mostra `type = ref` depois de criar o índice.

**Passo 3 — entenda o trade-off: índices não são "grátis".**

```sql
INSERT INTO livro (titulo, autor, isbn, preco, categoria_id, exemplares_disponiveis)
VALUES ('Novo Livro', 'Autor X', '9788535910699', 49.90, 3, 1);
```

Índices aceleram `SELECT`, mas desaceleram `INSERT`/`UPDATE`/`DELETE` (o banco precisa manter a estrutura do índice atualizada a cada mudança) e ocupam espaço extra em disco.

**✅ Checkpoint:** você explica por que uma tabela de log com milhões de inserções diárias deveria ter poucos índices.

**Passo 4 — crie um índice composto para uma consulta com múltiplos filtros.**

```sql
EXPLAIN SELECT * FROM emprestimo WHERE associado_id = 1 AND data_devolucao IS NULL;

CREATE INDEX idx_emprestimo_associado_devolucao ON emprestimo(associado_id, data_devolucao);

EXPLAIN SELECT * FROM emprestimo WHERE associado_id = 1 AND data_devolucao IS NULL;
```

A ordem das colunas no índice composto importa: `(associado_id, data_devolucao)` otimiza consultas por `associado_id` sozinho ou pelos dois juntos, mas não ajudaria uma consulta que filtrasse só por `data_devolucao`. Repare também que, mesmo antes deste índice composto, o `EXPLAIN` já usava algum índice em `associado_id` — o MySQL cria automaticamente um índice para toda coluna com `FOREIGN KEY`, e `associado_id` referencia `associado(id)` desde o Tópico 1. O índice composto ainda melhora o plano: sem ele, o MySQL localiza as linhas do associado usando o índice da FK e depois varre-as (`Using where`) verificando a condição de data uma a uma; com ele, a própria busca do índice já usa as duas colunas.

**✅ Checkpoint:** o plano de execução usa as duas colunas do índice composto (menos linhas verificadas "na mão" pelo `Using where`).

**Passo 5 — identifique quando um índice NÃO ajuda.**

```sql
EXPLAIN SELECT * FROM livro WHERE preco > 30;
```

Índices em colunas de baixa seletividade (poucos valores distintos possíveis) ou consultas com `LIKE '%termo%'` (o `%` inicial impede o banco de "entrar" no índice por um prefixo) geralmente não são bem aproveitados por um índice B-tree comum. Uma faixa ampla como `preco > 30`, quando pega uma fração grande da tabela, também pode levar o otimizador a preferir um *table scan* direto, mesmo que exista um índice disponível — percorrer a tabela inteira sequencialmente às vezes é mais barato do que ficar saltando entre páginas do índice.

**✅ Checkpoint:** você cita um exemplo, fora do banco `locadora`, de coluna onde um índice provavelmente não ajudaria.

### Resumo do que você construiu

```
✅ EXPLAIN revelando o plano de execução e o tipo de acesso
✅ Índice simples acelerando a busca por autor
✅ Entendimento do trade-off: índices aceleram leitura, desaceleram escrita
✅ Índice composto e a importância da ordem das colunas
✅ Reconhecimento de cenários onde um índice não ajuda
```

**Exercícios:**
- *Fácil:* crie um índice em `emprestimo(livro_id)` e confirme com `EXPLAIN` que ele melhora o JOIN mais usado nos tópicos anteriores.
- *Médio:* pesquise `ANALYZE TABLE` e explique quando rodá-lo.
- *Difícil:* crie um índice `UNIQUE` numa coluna que ainda não tem um e explique a diferença prática para um índice comum.

**Perguntas para fixação:**
1. Por que o índice em `isbn` já existia antes de qualquer `CREATE INDEX` explícito neste tutorial?
2. Se uma tabela tem 10 milhões de linhas e uma coluna booleana (`ativo`, só `TRUE`/`FALSE`), por que um índice simples nessa coluna provavelmente não ajudaria uma consulta `WHERE ativo = TRUE`?

## 10. Transações (ACID, COMMIT, ROLLBACK)

**Objetivo:** garantir que operações compostas (registrar um empréstimo e baixar o exemplar disponível) aconteçam por completo, ou não aconteçam de jeito nenhum.

**Pré-requisitos:** banco `locadora` com `livro`, `associado`, `emprestimo`.

**✅ Checkpoint:** `SELECT exemplares_disponiveis FROM livro WHERE id = 3;` retorna um valor.

**Passo 1 — identifique o problema sem transação.**

```sql
UPDATE livro SET exemplares_disponiveis = exemplares_disponiveis - 1 WHERE id = 3;
INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista)
VALUES (2, 3, NOW(), CURDATE() + INTERVAL 14 DAY);  -- imagine que este falhasse
```

Se a segunda instrução falhasse (uma queda de conexão, um erro de validação), o exemplar já teria sido marcado como emprestado sem nenhum registro de empréstimo correspondente — um dado inconsistente, e silencioso: nada no banco acusaria o problema sozinho.

**✅ Checkpoint:** você explica por que essas duas instruções precisam ser "tudo ou nada".

**Passo 2 — entenda ACID rapidamente.**

| Propriedade | Significado |
|---|---|
| Atomicidade | Tudo ou nada |
| Consistência | O banco sai de um estado válido para outro estado válido |
| Isolamento | Transações concorrentes não veem o trabalho incompleto uma da outra |
| Durabilidade | Depois do COMMIT, os dados sobrevivem mesmo a uma queda de energia |

**✅ Checkpoint:** você associa "Atomicidade" ao problema do Passo 1.

**Passo 3 — use START TRANSACTION, COMMIT e ROLLBACK.**

```sql
START TRANSACTION;
UPDATE livro SET exemplares_disponiveis = exemplares_disponiveis - 1 WHERE id = 3;
INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista)
VALUES (2, 3, NOW(), CURDATE() + INTERVAL 14 DAY);
COMMIT;
```

Nenhuma mudança é permanente até o `COMMIT` — se algo falhasse antes disso, nada teria sido de fato persistido.

**✅ Checkpoint:** rodar o bloco completo aplica as duas mudanças normalmente; `exemplares_disponiveis` do livro 3 caiu em 1.

**Passo 4 — simule uma falha e use ROLLBACK.**

```sql
START TRANSACTION;
UPDATE livro SET exemplares_disponiveis = exemplares_disponiveis - 1 WHERE id = 3;
-- verificação de negócio: numa aplicação real, aqui entraria a checagem
-- "o valor ficou negativo? se sim, cancele" antes do COMMIT
SELECT exemplares_disponiveis FROM livro WHERE id = 3;
ROLLBACK;
SELECT exemplares_disponiveis FROM livro WHERE id = 3; -- valor original, sem alteração
```

`ROLLBACK` desfaz todas as mudanças desde o `START TRANSACTION`.

**✅ Checkpoint:** depois do `ROLLBACK`, o valor volta ao que era antes do bloco.

**Passo 5 — como essa lógica vive numa aplicação real.** Os quatro passos acima foram digitados diretamente no cliente `mysql`, mas numa aplicação de verdade (um sistema web, um app de balcão da locadora) essas mesmas instruções `START TRANSACTION` / `COMMIT` / `ROLLBACK` não desaparecem — elas passam a ser chamadas pelo driver ou ORM que a aplicação usa para falar com o banco (em Java, `Connection.setAutoCommit(false)` e `connection.commit()`/`connection.rollback()`; em outras linguagens, uma API equivalente), envolvendo exatamente o mesmo SQL que você acabou de escrever à mão. A verificação de negócio do Passo 4 ("o valor ficou negativo? cancele") também não precisa ficar só no código da aplicação: o MySQL permite embutir essa mesma checagem **dentro do próprio banco**, usando uma estrutura condicional (`IF ... THEN ... END IF`) e um comando chamado `SIGNAL`, que interrompe a transação com um erro customizado antes que ela chegue a gravar algo inválido. Você vai construir exatamente essa validação, com essa sintaxe, em duas etapas: uma versão dentro de uma Procedure no Tópico 11, e uma versão automática, disparada por um `INSERT`, dentro de um Trigger no Tópico 12.

**✅ Checkpoint:** você entende por que a validação de negócio do Passo 4 pode viver tanto na aplicação quanto dentro do próprio banco, e por que as duas abordagens usam o mesmo mecanismo de transação por baixo.

### Resumo do que você construiu

```
✅ Identificação do problema de inconsistência sem transação
✅ Entendimento das quatro propriedades ACID
✅ START TRANSACTION / COMMIT persistindo mudanças em conjunto
✅ ROLLBACK desfazendo mudanças de uma transação incompleta
✅ Entendimento de como a aplicação/ORM e o próprio banco (via Procedure/Trigger) usam o mesmo mecanismo
```

**Exercícios:**
- *Fácil:* modele a devolução (incrementar `exemplares_disponiveis` + atualizar `data_devolucao`) como uma transação atômica.
- *Médio:* pesquise `SAVEPOINT` e o rollback parcial dentro de uma transação maior.
- *Difícil:* pesquise a diferença prática entre os níveis de isolamento `READ COMMITTED` e `REPEATABLE READ`.

**Perguntas para fixação:**
1. Por que o `ROLLBACK` é necessário mesmo quando a aplicação já validou os dados antes de enviá-los ao banco?
2. No Passo 1, em que ponto exato dado ficaria inconsistente se a segunda instrução falhasse de verdade, sem transação nenhuma envolvida?

## 11. Stored Procedures e Funções

**Objetivo:** encapsular regras de negócio reutilizáveis dentro do próprio banco, com Functions e Stored Procedures.

**Pré-requisitos:** banco `locadora` com transações já praticadas.

**✅ Checkpoint:** a consulta de "empréstimos ativos" ainda funciona.

**Passo 1 — crie uma Function simples: calcular multa por atraso.**

```sql
DELIMITER //

CREATE FUNCTION calcular_multa(dias_atraso INT)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN dias_atraso * 1.50;
END //

DELIMITER ;
```

`DETERMINISTIC` informa que a função sempre retorna o mesmo resultado para a mesma entrada. `DELIMITER //` evita conflito entre o `;` interno do corpo da função e o fim da instrução `CREATE FUNCTION` — sem trocar o delimitador, o cliente `mysql` interpretaria o primeiro `;` de dentro do `BEGIN...END` como "acabou o comando", cortando a função no meio.

**✅ Checkpoint:** `SELECT calcular_multa(5);` retorna `7.50`.

**Passo 2 — use a Function dentro de uma consulta normal.**

```sql
SELECT a.nome, e.data_prevista, DATEDIFF(CURDATE(), e.data_prevista) AS dias_atraso,
       calcular_multa(GREATEST(DATEDIFF(CURDATE(), e.data_prevista), 0)) AS multa
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
WHERE e.data_devolucao IS NULL AND e.data_prevista < CURDATE();
```

A regra de cálculo de multa vive num único lugar, reutilizável por qualquer consulta ou aplicação que acesse este banco.

**✅ Checkpoint:** a consulta retorna o valor da multa junto com o relatório de atraso.

**Passo 3 — crie uma Stored Procedure: registrar devolução, com validação.**

```sql
DELIMITER //

CREATE PROCEDURE registrar_devolucao(
    IN p_emprestimo_id INT,
    OUT p_multa DECIMAL(10,2)
)
BEGIN
    DECLARE v_livro_id INT;
    DECLARE v_dias_atraso INT;

    SELECT livro_id, GREATEST(DATEDIFF(CURDATE(), data_prevista), 0)
    INTO v_livro_id, v_dias_atraso
    FROM emprestimo WHERE id = p_emprestimo_id;

    SET p_multa = calcular_multa(v_dias_atraso);

    START TRANSACTION;
        UPDATE emprestimo SET data_devolucao = NOW() WHERE id = p_emprestimo_id;
        UPDATE livro SET exemplares_disponiveis = exemplares_disponiveis + 1 WHERE id = v_livro_id;
    COMMIT;
END //

DELIMITER ;
```

`GREATEST(..., 0)` evita multa negativa quando a devolução é adiantada. A Procedure combina cálculo, validação e a transação do Tópico 10 num único ponto de entrada — reutilizável por qualquer sistema que acesse este banco, exatamente a promessa feita no fim do Tópico 10.

**✅ Checkpoint:**

```sql
CALL registrar_devolucao(1, @multa);
SELECT @multa;
```

retorna o valor da multa (maior que zero, se o empréstimo 1 estiver vencido).

**Passo 4 — teste a Procedure com um empréstimo em dia.**

```sql
INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista)
VALUES (2, 5, NOW(), CURDATE() + INTERVAL 14 DAY);

CALL registrar_devolucao(LAST_INSERT_ID(), @multa_em_dia);
SELECT @multa_em_dia; -- 0.00, sem atraso
```

`LAST_INSERT_ID()` recupera o `id` gerado pelo `INSERT` imediatamente anterior, na mesma sessão — útil para encadear um teste sem precisar descobrir o `id` manualmente.

**✅ Checkpoint:** a devolução em dia retorna multa zero, e o exemplar volta a ficar disponível.

**Passo 5 — entenda quando usar Procedure vs. lógica na aplicação.**

| Cenário | Onde colocar a lógica |
|---|---|
| Regra usada por múltiplos sistemas acessando o mesmo banco | Stored Procedure |
| Regra que muda com frequência, específica de um app | Código da aplicação |
| Cálculo simples e reutilizável | Function |
| Lógica com testes automatizados robustos e versionamento fácil | Código da aplicação |

**✅ Checkpoint:** você justifica por que `registrar_devolucao` faz sentido no banco, mas uma regra de "enviar e-mail de lembrete" não faria (o banco não sabe conversar com um serviço de e-mail).

### Resumo do que você construiu

```
✅ Function calcular_multa() reutilizável dentro de consultas SQL
✅ Stored Procedure registrar_devolucao() com parâmetros IN/OUT
✅ Transação completa dentro da própria Procedure
✅ GREATEST evitando multa negativa em devolução adiantada
✅ Critério para decidir entre Procedure e lógica na aplicação
```

**Exercícios:**
- *Fácil:* crie `calcular_prazo_maximo()`, baseada num tipo de associado (comum vs. premium — você precisaria adicionar essa coluna).
- *Médio:* crie `renovar_emprestimo()`, que estende `data_prevista` em 7 dias, só se o empréstimo ainda estiver em aberto.
- *Difícil:* pesquise por que Procedures podem ser mais rápidas que várias idas e voltas separadas da aplicação ao banco.

**Perguntas para fixação:**
1. Por que `calcular_multa` é declarada `DETERMINISTIC`, mas `registrar_devolucao` (que usa `NOW()` internamente) não poderia ser?
2. O que aconteceria com `exemplares_disponiveis` se a Procedure não envolvesse os dois `UPDATE` numa transação, e o segundo `UPDATE` falhasse por algum motivo?

## 12. Triggers

**Objetivo:** garantir que uma regra rode sempre que uma tabela for alterada, não importa por qual caminho a alteração chegou.

**Pré-requisitos:** banco `locadora` com Stored Procedures já praticadas.

**✅ Checkpoint:** `SELECT * FROM livro LIMIT 1;` retorna uma linha.

**Passo 1 — entenda o problema que Triggers resolvem.** Toda vez que um empréstimo é registrado, alguém precisa lembrar de decrementar `exemplares_disponiveis`. Se essa lógica ficar espalhada em múltiplos pontos (um script de importação, uma tela administrativa, a Procedure do Tópico 11), é fácil esquecer um deles. Um **Trigger** garante que a regra roda sempre, não importa como o `INSERT` aconteceu — inclusive um `INSERT` direto no cliente `mysql`, sem passar por nenhuma Procedure.

**✅ Checkpoint:** você explica um cenário em que esquecer de atualizar o contador manualmente causaria inconsistência.

**Passo 2 — crie uma tabela de auditoria (log de empréstimos).**

```sql
CREATE TABLE log_emprestimo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    emprestimo_id INT,
    acao VARCHAR(20),
    registrado_em DATETIME
);
```

**✅ Checkpoint:** a tabela é criada sem erro.

**Passo 3 — crie um Trigger AFTER INSERT.**

```sql
DELIMITER //

CREATE TRIGGER trg_log_emprestimo
AFTER INSERT ON emprestimo
FOR EACH ROW
BEGIN
    INSERT INTO log_emprestimo (emprestimo_id, acao, registrado_em)
    VALUES (NEW.id, 'CRIADO', NOW());
END //

DELIMITER ;
```

`NEW` é a pseudo-tabela que representa a linha recém-inserida — disponível em Triggers `AFTER`/`BEFORE INSERT` e `UPDATE`.

**✅ Checkpoint:** `INSERT INTO emprestimo (...) VALUES (...);` cria automaticamente uma linha em `log_emprestimo`, sem `INSERT` explícito.

**Passo 4 — crie um Trigger BEFORE INSERT para validação de estoque.**

```sql
DELIMITER //

CREATE TRIGGER trg_validar_exemplares
BEFORE INSERT ON emprestimo
FOR EACH ROW
BEGIN
    DECLARE v_disponiveis INT;
    SELECT exemplares_disponiveis INTO v_disponiveis FROM livro WHERE id = NEW.livro_id;

    IF v_disponiveis <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Nenhum exemplar disponível para empréstimo';
    END IF;
END //

DELIMITER ;
```

`BEFORE INSERT` roda antes da linha ser gravada — este é exatamente o `IF`/`SIGNAL` prometido no fechamento do Tópico 10, agora rodando automaticamente, e bloqueando o registro se não houver exemplar disponível, mesmo que o `INSERT` venha de fora de qualquer aplicação.

**✅ Checkpoint:**

```sql
UPDATE livro SET exemplares_disponiveis = 0 WHERE id = 4;
INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista)
VALUES (2, 4, NOW(), CURDATE() + INTERVAL 14 DAY);
```

falha com `ERROR 1644 (45000): Nenhum exemplar disponível para empréstimo`.

**Passo 5 — entenda o risco de Triggers em cascata, e quando evitá-los.** Triggers são poderosos, mas invisíveis no código da aplicação — cadeias de Triggers disparando outros Triggers tornam o sistema difícil de depurar, porque uma única instrução SQL pode desencadear uma sequência inteira de efeitos que não aparecem em lugar nenhum do código que você está lendo. Regra prática: use Triggers para auditoria e validações de integridade simples (como as duas construídas aqui); evite para regras de negócio complexas, que ficam mais claras e mais testáveis vivendo no código da aplicação.

**✅ Checkpoint:** você lista um cenário em que preferiria colocar a regra na aplicação em vez de num Trigger.

### Resumo do que você construiu

```
✅ Entendimento do problema que Triggers resolvem
✅ Trigger AFTER INSERT registrando histórico de empréstimos
✅ Uso de NEW para acessar a linha recém-inserida
✅ Trigger BEFORE INSERT validando disponibilidade antes de gravar
✅ Entendimento dos riscos de Triggers em cascata
```

**Exercícios:**
- *Fácil:* pesquise `SHOW TRIGGERS` e liste todos os Triggers ativos no banco `locadora`.
- *Médio:* crie um Trigger `AFTER UPDATE`, registrando em `log_emprestimo` quando um empréstimo é devolvido (`data_devolucao` deixa de ser `NULL`).
- *Difícil:* crie um Trigger que decremente `exemplares_disponiveis` automaticamente ao inserir um empréstimo (em vez de deixar essa responsabilidade só com a Procedure do Tópico 11) — e reflita sobre o risco de ter a mesma regra em dois lugares diferentes.

**Perguntas para fixação:**
1. Por que `trg_validar_exemplares` precisa ser `BEFORE INSERT`, e não `AFTER INSERT`?
2. Se você chamar `registrar_devolucao` (Tópico 11) e ela fizer um `UPDATE` em `emprestimo`, o Trigger `trg_log_emprestimo` (que reage a `INSERT`) dispara? Por quê?

## Projeto Final — Início

Todo o conteúdo deste tutorial — modelagem, DML/DQL, JOINs, subconsultas, Views, índices, transações, Procedures, Triggers — converge agora num projeto final de banco de dados, individual ou em dupla, com um domínio **escolhido por você**, diferente da locadora usada até aqui.

**Passo 1 — escolha o domínio do banco.** Requisitos mínimos:
- Pelo menos 4 tabelas relacionadas, com FKs corretas.
- Pelo menos 2 relacionamentos N:N, via tabela associativa.
- Pelo menos 1 View encapsulando uma consulta complexa.
- Pelo menos 1 Stored Procedure com transação.
- Pelo menos 1 Trigger de auditoria ou validação.

**✅ Checkpoint:** você tem um domínio de banco escrito em uma frase, cobrindo os 5 requisitos.

**Passo 2 — desenhe o DER.** Use papel, MySQL Workbench ou uma ferramenta como dbdiagram.io para desenhar entidades, atributos, relacionamentos e cardinalidades. Assim como no Tópico 1, desenhar antes de criar as tabelas evita retrabalho de migração no meio do projeto.

**✅ Checkpoint:** o DER está desenhado e revisado.

**Passo 3 — escreva os CREATE TABLE do modelo lógico.**

```sql
CREATE DATABASE projeto_final CHARACTER SET utf8mb4;
USE projeto_final;
-- suas tabelas, seguindo o DER aprovado
```

**✅ Checkpoint:** `SHOW TABLES;` lista todas as tabelas planejadas no DER.

**Passo 4 — popule o banco com dados de teste realistas.** Pelo menos 10-15 linhas por tabela principal. Dados de teste realistas tornam a apresentação final mais convincente, e ajudam a encontrar problemas de modelagem cedo — um nome genérico como "teste1" não revela se um campo de texto é curto demais para um nome real, por exemplo.

**✅ Checkpoint:** cada tabela principal tem pelo menos 10 linhas de dados plausíveis.

**Passo 5 — planeje os marcos até a apresentação.**

| Marco | Prazo sugerido |
|---|---|
| DER + tabelas criadas e populadas | 1 semana |
| Consultas principais (JOINs, subconsultas, Views) | 2 semanas |
| Procedures + Triggers | 3 semanas |
| Relatório final + polimento | véspera da apresentação |

**✅ Checkpoint:** você anota suas próprias datas previstas para cada marco.

Este tutorial inclui, como referência, uma implementação completa e verificada desse projeto final — uma clínica veterinária — em [`bdi-projetos/projeto-final/`](bdi-projetos/projeto-final/), com seu próprio README explicando o domínio, o DER e como rodar cada parte.

### Resumo do que você construiu

```
✅ Domínio do banco definido, cobrindo os 5 requisitos mínimos
✅ DER desenhado e revisado
✅ Tabelas criadas a partir do modelo lógico
✅ Banco populado com dados de teste realistas
✅ Marcos de progresso planejados até a apresentação
```

## Projeto Final — Apresentações

**Formato:** 8 minutos de apresentação + 3 minutos de perguntas por grupo.

### Roteiro sugerido

1. Domínio (30s): que sistema o banco modela?
2. DER (2 min): apresente o diagrama, explicando cardinalidades e por que foram escolhidas.
3. Demo ao vivo (3 min): rode consultas reais (JOIN, View, Procedure) mostrando resultados sobre dados populados.
4. Decisões técnicas (1,5 min): por que criou os índices/Triggers que criou.
5. Perguntas (3 min).

### Rubrica de avaliação

| Critério | Peso |
|---|---|
| DER coerente com as tabelas implementadas | 20% |
| Pelo menos 2 relacionamentos N:N corretamente modelados | 15% |
| View encapsulando consulta complexa, funcionando | 15% |
| Stored Procedure com transação, testada com caso de erro | 20% |
| Trigger funcionando (auditoria ou validação) | 15% |
| Clareza da apresentação e resposta às perguntas | 15% |

### Checklist antes de apresentar

- [ ] O script SQL completo roda do zero num banco limpo, sem erros.
- [ ] Testou a Procedure com um caso de sucesso e um caso de erro.
- [ ] Sabe explicar, sem olhar o código, por que escolheu os índices que escolheu.
- [ ] Os dados de teste são realistas, não "teste1", "teste2".

## Revisão e exercícios finais

Sem conteúdo novo — resolva os exercícios abaixo sem consultar o material, depois confira.

### Escopo revisado

Todo o conteúdo do tutorial: modelagem (DER), DDL/DML/DQL, JOINs, subconsultas, Views, índices, transações, Stored Procedures/Functions, Triggers.

### Exercícios de revisão

1. **Modelagem:** dado um enunciado (biblioteca, escola, clínica), desenhe um DER com pelo menos um relacionamento N:N.
2. **JOINs:** escreva de memória a diferença entre `INNER`, `LEFT` e `SELF JOIN`, com um exemplo de cada.
3. **Agregação:** escreva uma consulta com `GROUP BY` + `HAVING` respondendo "quais associados têm mais de 3 empréstimos".
4. **Transações:** explique por que `ROLLBACK` é necessário mesmo com validação prévia no código da aplicação.
5. **Procedures vs. Triggers:** explique quando usar cada um, com um exemplo do próprio projeto final.
6. **Índices:** explique por que criar índice em toda coluna "por garantia" é uma má prática.

### Autoavaliação rápida

| Tópico | Confiante | Preciso revisar |
|---|---|---|
| Modelagem (DER → modelo lógico) | | |
| JOINs (INNER/LEFT/RIGHT/SELF) | | |
| Agregação e subconsultas | | |
| Views | | |
| Transações (ACID) | | |
| Procedures, Functions e Triggers | | |
| Índices e performance | | |

Marque os tópicos que precisam de revisão, e volte aos tópicos correspondentes antes da avaliação final.

## Projeto completo — todos os arquivos juntos

A estrutura final do projeto cumulativo `locadora`, com cada script marcado pelo tópico que o introduziu — a implementação real, executada e verificada, está em [`bdi-projetos/locadora/`](bdi-projetos/locadora/):

```
locadora/
├── 01_schema.sql        ← Tópico 1 (associado, categoria, livro, emprestimo)
├── 02_seed.sql           ← Tópicos 1-4 (massa de dados de teste)
├── 03_dml.sql             ← Tópico 2 (UPDATE, DELETE, transação)
├── 04_dql.sql              ← Tópicos 3-4 (SELECT/WHERE, funções agregadas)
├── 05_joins.sql             ← Tópicos 5-6 (INNER/LEFT/RIGHT/SELF JOIN)
├── 06_subconsultas.sql       ← Tópico 7
├── 07_views.sql                ← Tópico 8
├── 08_indices.sql                ← Tópico 9
├── 09_transacoes.sql               ← Tópico 10
├── 10_procedures.sql                 ← Tópico 11 (calcular_multa, registrar_devolucao)
└── 11_triggers.sql                     ← Tópico 12 (trg_log_emprestimo, trg_validar_exemplares)
```

O schema final é o mesmo desde o Tópico 1, sem nenhuma mudança de nome de tabela ou coluna pelo caminho:

```sql
associado (id, nome, email)
categoria (id, nome, categoria_pai_id)
livro     (id, titulo, autor, isbn, preco, categoria_id, exemplares_disponiveis)
emprestimo(id, associado_id, livro_id, data_emprestimo, data_prevista, data_devolucao)
log_emprestimo (id, emprestimo_id, acao, registrado_em)   -- criada no Tópico 12
```

Um administrador da locadora navegando pelo sistema completo, e onde cada tópico entra em ação: cadastra um livro novo, com preço, categoria e exemplares disponíveis (Tópico 1); registra um empréstimo, que o Trigger `trg_validar_exemplares` (Tópico 12) recusaria automaticamente se não houvesse exemplar livre; consulta o painel de "empréstimos em aberto há mais tempo" através da View `vw_emprestimos_atrasados` (Tópico 8), que por baixo é o mesmo JOIN de três tabelas do Tópico 5, com a função `calcular_multa` (Tópico 11) já somando o valor a cobrar; ao registrar a devolução, chama `registrar_devolucao` (Tópico 11), que atualiza `emprestimo` e `livro` dentro de uma única transação (Tópico 10) — e cada uma dessas consultas roda rápido porque as colunas certas têm índice (Tópico 9), decisão que só faz sentido porque, antes de tudo isso, o modelo inteiro nasceu de um DER desenhado no papel (Tópico 1) e passou por DML e DQL bem entendidos (Tópicos 2 a 4) antes de qualquer View, Procedure ou Trigger ser construída em cima dele.

O segundo projeto, o projeto final (clínica veterinária PetCare), com seu próprio schema de 9 tabelas, 2 relacionamentos N:N, View, Procedure com transação e Trigger de auditoria, está em [`bdi-projetos/projeto-final/`](bdi-projetos/projeto-final/) — veja o README daquela pasta para o DER completo e as instruções de execução.
