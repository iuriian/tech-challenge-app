-- ============================================================
-- Adiciona suporte à quantidade consumida de cada peça por serviço.
-- A antiga tabela de junção (servico_pecas) deixa de ser um simples
-- relacionamento N:N e passa a ser uma entidade de associação
-- (PecaServico = peça + quantidade), ganhando chave primária própria
-- (UUID) e a coluna quantidade.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ------------------------------------------------------------
-- 1) Chave primária própria da associação.
-- ------------------------------------------------------------
ALTER TABLE servico_pecas ADD COLUMN id UUID;
UPDATE servico_pecas SET id = gen_random_uuid() WHERE id IS NULL;
ALTER TABLE servico_pecas ALTER COLUMN id SET NOT NULL;
ALTER TABLE servico_pecas ADD CONSTRAINT servico_pecas_pkey PRIMARY KEY (id);

-- ------------------------------------------------------------
-- 2) Quantidade consumida da peça no serviço. O default 1 preenche
--    os registros já existentes (seed); novas inserções são feitas
--    explicitamente pela aplicação, por isso o default é removido.
-- ------------------------------------------------------------
ALTER TABLE servico_pecas ADD COLUMN quantidade NUMERIC(10, 2) NOT NULL DEFAULT 1;
ALTER TABLE servico_pecas ALTER COLUMN quantidade DROP DEFAULT;
