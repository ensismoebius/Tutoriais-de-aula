-- Massa de dados de teste realista para a clínica PetCare.
USE clinica_petcare;

INSERT INTO tutor (nome, telefone, email) VALUES
    ('Marcos Andrade',  '11 98888-1111', 'marcos@email.com'),
    ('Juliana Ferraz',  '11 98888-2222', 'juliana@email.com'),
    ('Patrícia Nunes',  '11 98888-3333', 'patricia@email.com');

INSERT INTO animal (nome, especie, raca, data_nascimento, tutor_id) VALUES
    ('Thor',    'Cachorro', 'Labrador',        '2021-03-10', 1),
    ('Mimi',    'Gato',     'Siamês',          '2022-07-22', 1),
    ('Bidu',    'Cachorro', 'Vira-lata',       '2019-11-02', 2),
    ('Luna',    'Gato',     'Persa',           '2023-01-15', 3);

INSERT INTO veterinario (nome, crmv) VALUES
    ('Dra. Renata Costa',  'CRMV-SP 12345'),
    ('Dr. Felipe Aragão',  'CRMV-SP 23456'),
    ('Dra. Bianca Souza',  'CRMV-SP 34567');

INSERT INTO especialidade (nome) VALUES
    ('Clínica Geral'),
    ('Cirurgia'),
    ('Dermatologia');

-- Dra. Renata: Clínica Geral e Dermatologia. Dr. Felipe: Cirurgia.
-- Dra. Bianca: Clínica Geral apenas.
INSERT INTO veterinario_especialidade (veterinario_id, especialidade_id) VALUES
    (1, 1), (1, 3),
    (2, 2),
    (3, 1);

INSERT INTO vacina (nome, fabricante) VALUES
    ('V10',       'Zoetis'),
    ('Antirrábica', 'MSD Saúde Animal'),
    ('Giardia',   'Boehringer Ingelheim');

INSERT INTO aplicacao_vacina (animal_id, vacina_id, veterinario_id, data_aplicacao, proxima_dose) VALUES
    (1, 1, 1, '2026-06-01', '2027-06-01'),
    (1, 2, 3, '2026-06-01', '2027-06-01'),
    (3, 2, 3, '2025-08-10', '2026-08-10'),
    (4, 3, 1, '2026-08-01', '2026-11-01');

INSERT INTO consulta (animal_id, veterinario_id, data_consulta, diagnostico, valor) VALUES
    (1, 1, '2026-06-01 09:00:00', 'Check-up de rotina, sem alterações',        120.00),
    (3, 3, '2025-08-10 14:00:00', 'Otite leve, prescrito colírio',              150.00),
    (4, 1, '2026-08-01 10:30:00', 'Consulta pré-vacinal, animal saudável',      120.00);

SELECT COUNT(*) AS total_tutores FROM tutor;
SELECT COUNT(*) AS total_animais FROM animal;
SELECT COUNT(*) AS total_veterinarios FROM veterinario;
SELECT COUNT(*) AS total_aplicacoes_vacina FROM aplicacao_vacina;
SELECT COUNT(*) AS total_consultas FROM consulta;
