-- View exigida pelo projeto final: encapsula um JOIN de várias tabelas
-- para responder "quais vacinas estão próximas do vencimento ou já vencidas".
USE clinica_petcare;

CREATE OR REPLACE VIEW vw_vacinas_pendentes AS
SELECT t.nome AS tutor, a.nome AS animal, v.nome AS vacina,
       av.data_aplicacao, av.proxima_dose,
       DATEDIFF(av.proxima_dose, CURDATE()) AS dias_para_vencer
FROM aplicacao_vacina av
JOIN animal a ON a.id = av.animal_id
JOIN tutor t ON t.id = a.tutor_id
JOIN vacina v ON v.id = av.vacina_id
WHERE av.proxima_dose IS NOT NULL
  AND av.proxima_dose <= CURDATE() + INTERVAL 60 DAY
ORDER BY av.proxima_dose ASC;

SELECT * FROM vw_vacinas_pendentes;
