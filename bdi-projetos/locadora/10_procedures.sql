-- Tópico Stored Procedures e Funções.
USE locadora;

DELIMITER //

CREATE FUNCTION calcular_multa(dias_atraso INT)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    RETURN dias_atraso * 1.50;
END //

DELIMITER ;

SELECT calcular_multa(5) AS multa_5_dias;

SELECT a.nome, e.data_prevista, DATEDIFF(CURDATE(), e.data_prevista) AS dias_atraso,
       calcular_multa(GREATEST(DATEDIFF(CURDATE(), e.data_prevista), 0)) AS multa
FROM emprestimo e
JOIN associado a ON e.associado_id = a.id
WHERE e.data_devolucao IS NULL AND e.data_prevista < CURDATE();

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

-- Empréstimo em dia, criado só para exercitar o caso "multa zero" da Procedure.
INSERT INTO emprestimo (associado_id, livro_id, data_emprestimo, data_prevista)
VALUES (2, 5, NOW(), CURDATE() + INTERVAL 14 DAY);

-- Teste 1: devolução com atraso (multa > 0). O empréstimo 1 (Dom Casmurro,
-- associado 1) está em aberto com data_prevista em 2026-07-19, já vencida.
CALL registrar_devolucao(1, @multa_atraso);
SELECT @multa_atraso AS multa_com_atraso;

-- Teste 2: devolução em dia (multa = 0), usando o empréstimo recém-criado acima.
SET @id_emprestimo_em_dia = LAST_INSERT_ID();
CALL registrar_devolucao(@id_emprestimo_em_dia, @multa_em_dia);
SELECT @multa_em_dia AS multa_sem_atraso;
