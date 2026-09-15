# projeto-final — Clínica Veterinária PetCare

Implementação de referência do "Projeto Final" descrito no `Tutorial_BDI.md`
(Tópicos finais, a partir de "Projeto Final — Início"). É um domínio
**original**, escolhido à parte da `locadora` usada no resto do tutorial,
cobrindo os 5 requisitos mínimos do projeto final do curso.

## Domínio, em uma frase

Um sistema de banco de dados para uma clínica veterinária: cadastra tutores e
seus animais, o corpo de veterinários e suas especialidades, registra
consultas e o histórico de vacinação de cada animal.

## DER (textual)

```
TUTOR (id, nome, telefone, email)
   |
   | 1 --- N
   |
ANIMAL (id, nome, especie, raca, data_nascimento, tutor_id FK, ultima_consulta)
   |                                      |
   | 1 --- N                              | N --- N (via aplicacao_vacina)
   |                                      |
CONSULTA                              VACINA (id, nome, fabricante)
(id, animal_id FK, veterinario_id FK,     |
 data_consulta, diagnostico, valor)       | aplicacao_vacina (id, animal_id FK,
   |                                        vacina_id FK, veterinario_id FK,
   | N --- 1                                data_aplicacao, proxima_dose)
   |
VETERINARIO (id, nome, crmv)
   |
   | N --- N (via veterinario_especialidade)
   |
ESPECIALIDADE (id, nome)

LOG_CONSULTA (id, consulta_id FK, acao, registrado_em)  — auditoria (Trigger)
```

## Os 5 requisitos, e onde cada um está

| Requisito | Onde |
|---|---|
| ≥ 4 tabelas relacionadas com FKs | 9 tabelas em `01_schema.sql` |
| ≥ 2 relacionamentos N:N via tabela associativa | `veterinario_especialidade` (veterinário ↔ especialidade) e `aplicacao_vacina` (animal ↔ vacina, com atributos próprios) |
| ≥ 1 View encapsulando consulta complexa | `vw_vacinas_pendentes`, em `03_view.sql` |
| ≥ 1 Stored Procedure com transação | `registrar_consulta`, em `04_procedure.sql`, testada com sucesso e com falha |
| ≥ 1 Trigger de auditoria ou validação | `trg_log_consulta`, em `06_trigger.sql` |

## Como rodar

```bash
mysql -u andre -p1234 < 01_schema.sql
mysql -u andre -p1234 < 02_seed.sql
mysql -u andre -p1234 < 03_view.sql
mysql -u andre -p1234 < 04_procedure.sql
mysql -u andre -p1234 clinica_petcare < 05_procedure_teste_falha.sql   # termina com erro esperado, de propósito
mysql -u andre -p1234 < 06_trigger.sql
```

`05_procedure_teste_falha.sql` é intencionalmente o único script que termina
com um erro — ele existe justamente para provar que a Procedure recusa e
desfaz a operação quando o veterinário não tem a especialidade exigida.
Rode-o separadamente (não misture com um `for f in *.sql` genérico).

## O que foi verificado de verdade, com saída real

- `01_schema.sql` → `SHOW TABLES` lista as 9 tabelas.
- `02_seed.sql` → 3 tutores, 4 animais, 3 veterinários, 4 aplicações de vacina, 3 consultas.
- `03_view.sql` → `vw_vacinas_pendentes` retorna a vacina do Bidu já vencida (`dias_para_vencer = -36`) e a da Luna a vencer em 47 dias.
- `04_procedure.sql` → `CALL registrar_consulta(2, 1, 'Clínica Geral', ...)` com a Dra. Renata (que tem Clínica Geral) cria a consulta e atualiza `animal.ultima_consulta`.
- `05_procedure_teste_falha.sql` → `CALL registrar_consulta(1, 3, 'Cirurgia', ...)` com a Dra. Bianca (que só tem Clínica Geral) falha com `ERROR 1644 (45000): Veterinário não possui a especialidade exigida para esta consulta`, e a contagem de `consulta` não muda (a transação interna foi desfeita pelo `SIGNAL`, antes mesmo do `START TRANSACTION`).
- `06_trigger.sql` → antes do teste, `log_consulta` está vazia (as 4 consultas anteriores foram inseridas antes do Trigger existir); depois de uma nova `CALL registrar_consulta(...)`, aparece exatamente 1 linha nova em `log_consulta`, com o `consulta_id` correto.
- Contagem final conferida à parte: 5 consultas (3 do seed + 1 do teste de sucesso + 1 do teste do Trigger) e 1 log — a tentativa que falhou não deixou nenhum rastro em `consulta`.
