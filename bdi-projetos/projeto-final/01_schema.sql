-- Projeto Final — Clínica Veterinária PetCare
-- Domínio original (não é a locadora, nem e-commerce): uma clínica veterinária
-- que atende animais de estimação, aplica vacinas e mantém um corpo de
-- veterinários com especialidades diferentes.
--
-- Requisitos do projeto final cobertos por este schema:
--  - 8 tabelas relacionadas (mínimo exigido: 4)
--  - 2 relacionamentos N:N via tabela associativa:
--      veterinario_especialidade (veterinario <-> especialidade)
--      aplicacao_vacina          (animal <-> vacina, com atributos próprios)
--  - Views, Procedure com transação e Trigger de auditoria: veja 02_views.sql,
--    03_procedure.sql e 04_trigger.sql.

CREATE DATABASE IF NOT EXISTS clinica_petcare CHARACTER SET utf8mb4;
USE clinica_petcare;

CREATE TABLE tutor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE animal (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    especie VARCHAR(50) NOT NULL,
    raca VARCHAR(80) NULL,
    data_nascimento DATE NULL,
    tutor_id INT NOT NULL,
    ultima_consulta DATETIME NULL,

    FOREIGN KEY (tutor_id) REFERENCES tutor(id)
);

CREATE TABLE veterinario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    crmv VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE especialidade (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE
);

-- N:N #1 — um veterinário pode ter várias especialidades,
-- e cada especialidade é compartilhada por vários veterinários.
CREATE TABLE veterinario_especialidade (
    veterinario_id INT NOT NULL,
    especialidade_id INT NOT NULL,

    PRIMARY KEY (veterinario_id, especialidade_id),
    FOREIGN KEY (veterinario_id) REFERENCES veterinario(id),
    FOREIGN KEY (especialidade_id) REFERENCES especialidade(id)
);

CREATE TABLE consulta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    animal_id INT NOT NULL,
    veterinario_id INT NOT NULL,
    data_consulta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    diagnostico VARCHAR(255) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (animal_id) REFERENCES animal(id),
    FOREIGN KEY (veterinario_id) REFERENCES veterinario(id)
);

CREATE TABLE vacina (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL
);

-- N:N #2 — um animal toma várias vacinas ao longo da vida, e cada vacina
-- é aplicada em vários animais. A tabela associativa carrega atributos
-- próprios (data da aplicação, próxima dose), não é uma junção "vazia".
CREATE TABLE aplicacao_vacina (
    id INT AUTO_INCREMENT PRIMARY KEY,
    animal_id INT NOT NULL,
    vacina_id INT NOT NULL,
    veterinario_id INT NOT NULL,
    data_aplicacao DATE NOT NULL,
    proxima_dose DATE NULL,

    FOREIGN KEY (animal_id) REFERENCES animal(id),
    FOREIGN KEY (vacina_id) REFERENCES vacina(id),
    FOREIGN KEY (veterinario_id) REFERENCES veterinario(id)
);

-- Tabela de auditoria usada pelo Trigger em 04_trigger.sql.
CREATE TABLE log_consulta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    consulta_id INT NOT NULL,
    acao VARCHAR(20) NOT NULL,
    registrado_em DATETIME NOT NULL
);

SHOW TABLES;
