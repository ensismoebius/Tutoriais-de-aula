-- Trigger de auditoria exigida pelo projeto final: toda consulta registrada
-- (por INSERT direto ou através da Procedure) gera uma linha em log_consulta,
-- sem depender de quem chamou o INSERT lembrar de fazer isso manualmente.
USE clinica_petcare;

DELIMITER //

CREATE TRIGGER trg_log_consulta
AFTER INSERT ON consulta
FOR EACH ROW
BEGIN
    INSERT INTO log_consulta (consulta_id, acao, registrado_em)
    VALUES (NEW.id, 'CRIADA', NOW());
END //

DELIMITER ;

-- Confirma que consultas já existentes (inseridas antes do Trigger existir)
-- não geraram log — só o Trigger, a partir de agora, gera auditoria.
SELECT COUNT(*) AS logs_antes_do_teste FROM log_consulta;

CALL registrar_consulta(3, 2, 'Cirurgia', 'Consulta de retorno pós-operatório', 90.00, @id_consulta_trigger);

SELECT * FROM log_consulta WHERE consulta_id = @id_consulta_trigger;
