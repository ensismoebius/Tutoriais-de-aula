-- Tópico 1 — Do DER para o Modelo Lógico
-- Cria o banco locadora e as quatro tabelas do modelo lógico já unificado
-- (associado, categoria, livro, emprestimo), usado do início ao fim do tutorial.

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

SHOW TABLES;
