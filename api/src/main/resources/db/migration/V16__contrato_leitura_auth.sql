-- ============================================================
-- Contrato de leitura do servico de autenticacao (tech-challenge-auth).
--
-- O auth consulta cliente(id, nome, cpf, status) e
-- funcionario(id, nome, cpf, cargo). Estas views publicam exatamente esse
-- formato sobre as tabelas da app, e o role auth_ro recebe acesso apenas
-- a elas, nunca as tabelas.
--
-- Mudancas em clientes/funcionarios devem preservar estas colunas.
-- ============================================================

-- clientes nao tem coluna de status: todo cliente pessoa fisica pode
-- autenticar. Se um dia houver inativacao, troque a constante pela coluna.
CREATE OR REPLACE VIEW cliente AS
SELECT id,
       nome,
       documento_numero AS cpf,
       'ATIVO'::text    AS status
FROM clientes
WHERE tipo_pessoa = 'PESSOA_FISICA';

CREATE OR REPLACE VIEW funcionario AS
SELECT id, nome, cpf, cargo
FROM funcionarios;

-- O role auth_ro so existe no Cloud SQL. Local, kind e o smoke test do CI
-- nao o tem, e a migration nao pode falhar nesses ambientes.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'auth_ro') THEN
        GRANT SELECT (id, nome, cpf, status) ON cliente TO auth_ro;
GRANT SELECT (id, nome, cpf, cargo) ON funcionario TO auth_ro;
END IF;
END $$;