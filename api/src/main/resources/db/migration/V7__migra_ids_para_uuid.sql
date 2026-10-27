-- ============================================================
-- Migra os identificadores de enderecos, clientes, contatos,
-- veiculos e servicos de BIGINT para UUID, preservando todos os
-- relacionamentos existentes (incluindo os dados de seed).
-- Acompanha a refatoração das entidades de domínio para o padrão
-- de identidade baseado no value object Id (UUID), já adotado em
-- pecas (ver V6).
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ------------------------------------------------------------
-- 1) Remove as constraints de chave estrangeira que serão
--    reconstruídas após a troca de tipo das colunas.
-- ------------------------------------------------------------
ALTER TABLE clientes      DROP CONSTRAINT fk_clientes_endereco;
ALTER TABLE contatos      DROP CONSTRAINT fk_contatos_cliente;
ALTER TABLE veiculos      DROP CONSTRAINT fk_veiculos_motorista;
ALTER TABLE servicos      DROP CONSTRAINT fk_servicos_cliente;
ALTER TABLE servicos      DROP CONSTRAINT fk_servicos_veiculo;
ALTER TABLE servico_pecas DROP CONSTRAINT fk_servico_pecas_servico;

-- ------------------------------------------------------------
-- 2) Gera um UUID para cada linha das tabelas que possuem PK.
-- ------------------------------------------------------------
ALTER TABLE enderecos ADD COLUMN id_uuid UUID;
UPDATE enderecos SET id_uuid = gen_random_uuid();
ALTER TABLE enderecos ALTER COLUMN id_uuid SET NOT NULL;

ALTER TABLE clientes ADD COLUMN id_uuid UUID;
UPDATE clientes SET id_uuid = gen_random_uuid();
ALTER TABLE clientes ALTER COLUMN id_uuid SET NOT NULL;

ALTER TABLE contatos ADD COLUMN id_uuid UUID;
UPDATE contatos SET id_uuid = gen_random_uuid();
ALTER TABLE contatos ALTER COLUMN id_uuid SET NOT NULL;

ALTER TABLE veiculos ADD COLUMN id_uuid UUID;
UPDATE veiculos SET id_uuid = gen_random_uuid();
ALTER TABLE veiculos ALTER COLUMN id_uuid SET NOT NULL;

ALTER TABLE servicos ADD COLUMN id_uuid UUID;
UPDATE servicos SET id_uuid = gen_random_uuid();
ALTER TABLE servicos ALTER COLUMN id_uuid SET NOT NULL;

-- ------------------------------------------------------------
-- 3) Cria as colunas de FK em UUID e popula a partir dos
--    relacionamentos antigos (BIGINT), antes de removê-los.
-- ------------------------------------------------------------
ALTER TABLE clientes ADD COLUMN endereco_uuid UUID;
UPDATE clientes c
SET endereco_uuid = e.id_uuid
FROM enderecos e
WHERE c.endereco_id = e.id;

ALTER TABLE contatos ADD COLUMN cliente_uuid UUID;
UPDATE contatos co
SET cliente_uuid = c.id_uuid
FROM clientes c
WHERE co.cliente_id = c.id;

ALTER TABLE veiculos ADD COLUMN motorista_uuid UUID;
UPDATE veiculos v
SET motorista_uuid = c.id_uuid
FROM clientes c
WHERE v.motorista_id = c.id;

ALTER TABLE servicos ADD COLUMN cliente_uuid UUID;
UPDATE servicos s
SET cliente_uuid = c.id_uuid
FROM clientes c
WHERE s.cliente_id = c.id;

ALTER TABLE servicos ADD COLUMN veiculo_uuid UUID;
UPDATE servicos s
SET veiculo_uuid = v.id_uuid
FROM veiculos v
WHERE s.veiculo_id = v.id_veiculo;

ALTER TABLE servico_pecas ADD COLUMN servico_uuid UUID;
UPDATE servico_pecas sp
SET servico_uuid = s.id_uuid
FROM servicos s
WHERE sp.servico_id = s.id;

-- ------------------------------------------------------------
-- 4) Substitui as colunas de FK antigas pelas novas em UUID.
-- ------------------------------------------------------------
ALTER TABLE clientes DROP COLUMN endereco_id;
ALTER TABLE clientes RENAME COLUMN endereco_uuid TO endereco_id;

ALTER TABLE contatos DROP COLUMN cliente_id;
ALTER TABLE contatos RENAME COLUMN cliente_uuid TO cliente_id;

ALTER TABLE veiculos DROP COLUMN motorista_id;
ALTER TABLE veiculos RENAME COLUMN motorista_uuid TO motorista_id;
ALTER TABLE veiculos ALTER COLUMN motorista_id SET NOT NULL;

ALTER TABLE servicos DROP COLUMN cliente_id;
ALTER TABLE servicos RENAME COLUMN cliente_uuid TO cliente_id;
ALTER TABLE servicos ALTER COLUMN cliente_id SET NOT NULL;

ALTER TABLE servicos DROP COLUMN veiculo_id;
ALTER TABLE servicos RENAME COLUMN veiculo_uuid TO veiculo_id;
ALTER TABLE servicos ALTER COLUMN veiculo_id SET NOT NULL;

ALTER TABLE servico_pecas DROP COLUMN servico_id;
ALTER TABLE servico_pecas RENAME COLUMN servico_uuid TO servico_id;
ALTER TABLE servico_pecas ALTER COLUMN servico_id SET NOT NULL;

-- ------------------------------------------------------------
-- 5) Substitui as chaves primárias BIGINT pelas novas em UUID.
-- ------------------------------------------------------------
ALTER TABLE enderecos DROP CONSTRAINT enderecos_pkey;
ALTER TABLE enderecos DROP COLUMN id;
ALTER TABLE enderecos RENAME COLUMN id_uuid TO id;
ALTER TABLE enderecos ADD CONSTRAINT enderecos_pkey PRIMARY KEY (id);

ALTER TABLE clientes DROP CONSTRAINT clientes_pkey;
ALTER TABLE clientes DROP COLUMN id;
ALTER TABLE clientes RENAME COLUMN id_uuid TO id;
ALTER TABLE clientes ADD CONSTRAINT clientes_pkey PRIMARY KEY (id);

ALTER TABLE contatos DROP CONSTRAINT contatos_pkey;
ALTER TABLE contatos DROP COLUMN id;
ALTER TABLE contatos RENAME COLUMN id_uuid TO id;
ALTER TABLE contatos ADD CONSTRAINT contatos_pkey PRIMARY KEY (id);

ALTER TABLE veiculos DROP CONSTRAINT veiculos_pkey;
ALTER TABLE veiculos DROP COLUMN id_veiculo;
ALTER TABLE veiculos RENAME COLUMN id_uuid TO id_veiculo;
ALTER TABLE veiculos ADD CONSTRAINT veiculos_pkey PRIMARY KEY (id_veiculo);

ALTER TABLE servicos DROP CONSTRAINT servicos_pkey;
ALTER TABLE servicos DROP COLUMN id;
ALTER TABLE servicos RENAME COLUMN id_uuid TO id;
ALTER TABLE servicos ADD CONSTRAINT servicos_pkey PRIMARY KEY (id);

-- ------------------------------------------------------------
-- 6) Recria as constraints de chave estrangeira.
-- ------------------------------------------------------------
ALTER TABLE clientes
    ADD CONSTRAINT fk_clientes_endereco FOREIGN KEY (endereco_id) REFERENCES enderecos (id);
ALTER TABLE contatos
    ADD CONSTRAINT fk_contatos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id);
ALTER TABLE veiculos
    ADD CONSTRAINT fk_veiculos_motorista FOREIGN KEY (motorista_id) REFERENCES clientes (id);
ALTER TABLE servicos
    ADD CONSTRAINT fk_servicos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id);
ALTER TABLE servicos
    ADD CONSTRAINT fk_servicos_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos (id_veiculo);
ALTER TABLE servico_pecas
    ADD CONSTRAINT fk_servico_pecas_servico FOREIGN KEY (servico_id) REFERENCES servicos (id);
