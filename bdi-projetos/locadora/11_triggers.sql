-- Tópico Triggers.
USE locadora;

CREATE TABLE log_emprestimo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    emprestimo_id INT,
    acao VARCHAR(20),
    registrado_em DATETIME
);

DELIMITER //

CREATE TRIGGER trg_log_emprestimo
AFTER INSERT ON emprestimo
FOR EACH ROW
BEGIN
    INSERT INTO log_emprestimo (emprestimo_id, acao, registrado_em)
    VALUES (NEW.id, 'CRIADO', NOW());
END //

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

-- Testa o Trigger AFTER INSERT: cria um empréstimo e confirma o log automático.
INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista)
VALUES (3, 2, NOW(), CURDATE() + INTERVAL 14 DAY);

SELECT * FROM log_emprestimo ORDER BY id DESC LIMIT 1;

-- Zera o estoque de um livro para testar o Trigger BEFORE INSERT de validação.
UPDATE livro SET exemplares_disponiveis = 0 WHERE id = 4;

-- A linha abaixo deve falhar com o erro customizado do SIGNAL SQLSTATE '45000'.
-- (mantida comentada porque interromperia a execução em lote deste script;
-- descomente para observar o erro isoladamente)
-- INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista)
-- VALUES (2, 4, NOW(), CURDATE() + INTERVAL 14 DAY);

-- Devolve o estoque do livro 4 ao valor original para não deixar o banco inconsistente.
UPDATE livro SET exemplares_disponiveis = 1 WHERE id = 4;
