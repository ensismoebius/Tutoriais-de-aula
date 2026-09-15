-- Stored Procedure exigida pelo projeto final: registra uma consulta dentro
-- de uma transação, validando antes que o veterinário tem a especialidade
-- necessária para aquele tipo de consulta. Se não tiver, a Procedure recusa
-- com SIGNAL e nada é gravado (nem a consulta, nem a atualização em animal).
USE clinica_petcare;

DELIMITER //

CREATE PROCEDURE registrar_consulta(
    IN p_animal_id INT,
    IN p_veterinario_id INT,
    IN p_especialidade_necessaria VARCHAR(80),
    IN p_diagnostico VARCHAR(255),
    IN p_valor DECIMAL(10,2),
    OUT p_consulta_id INT
)
BEGIN
    DECLARE v_tem_especialidade INT;

    SELECT COUNT(*) INTO v_tem_especialidade
    FROM veterinario_especialidade ve
    JOIN especialidade e ON e.id = ve.especialidade_id
    WHERE ve.veterinario_id = p_veterinario_id
      AND e.nome = p_especialidade_necessaria;

    IF v_tem_especialidade = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Veterinário não possui a especialidade exigida para esta consulta';
    END IF;

    START TRANSACTION;
        INSERT INTO consulta (animal_id, veterinario_id, data_consulta, diagnostico, valor)
        VALUES (p_animal_id, p_veterinario_id, NOW(), p_diagnostico, p_valor);

        SET p_consulta_id = LAST_INSERT_ID();

        UPDATE animal SET ultima_consulta = NOW() WHERE id = p_animal_id;
    COMMIT;
END //

DELIMITER ;

-- Teste de sucesso: Dra. Renata (id 1) tem Clínica Geral, consulta é registrada.
CALL registrar_consulta(2, 1, 'Clínica Geral', 'Vacinação em dia, peso normal', 110.00, @id_consulta_sucesso);
SELECT @id_consulta_sucesso AS consulta_criada;
SELECT ultima_consulta FROM animal WHERE id = 2;

-- O teste do caminho de erro (SIGNAL interrompendo a transação) fica em
-- 05_procedure_teste_falha.sql, à parte, porque o próprio objetivo desse
-- teste é gerar um erro — mantê-lo aqui pararia a execução em lote deste
-- script no meio, antes de qualquer statement seguinte.
