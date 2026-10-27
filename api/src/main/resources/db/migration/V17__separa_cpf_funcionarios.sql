-- ============================================================
-- CPFs de funcionarios nao podem coincidir com os de clientes: o servico de
-- autenticacao (tech-challenge-auth) consulta a view cliente primeiro e
-- emitiria o papel CLIENTE.
--
-- Antes desta migration, 39053344705 e 11144477735 estavam nas duas tabelas,
-- e nenhum MECANICO conseguia obter token.
-- ============================================================
UPDATE funcionarios SET cpf = '28625587887'
WHERE id = '8f7d6a4e-9b17-49dd-8f8d-5c1d4d1ab923'; -- Maria Souza, MECANICO

UPDATE funcionarios SET cpf = '45317828791'
WHERE id = 'd2f9f58d-32f5-48aa-a4f5-b4dc5e4f6a74'; -- Carlos Lima, ATENDENTE