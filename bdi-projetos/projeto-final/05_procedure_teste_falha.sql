-- Teste isolado do caminho de erro de registrar_consulta: Dra. Bianca (id 3)
-- só tem Clínica Geral, não tem Cirurgia — a chamada abaixo deve falhar com
-- o erro customizado do SIGNAL, e a transação interna deve ser abortada sem
-- gravar nada (nem a consulta, nem o UPDATE em animal). Rode este arquivo
-- separadamente (ele termina com um erro esperado, de propósito):
--   mysql -u andre -p1234 clinica_petcare < 05_procedure_teste_falha.sql
USE clinica_petcare;

SELECT COUNT(*) AS consultas_antes FROM consulta;
CALL registrar_consulta(1, 3, 'Cirurgia', 'Remoção de nódulo', 800.00, @id_consulta_falha);
SELECT COUNT(*) AS consultas_depois FROM consulta;
