-- ============================================================
-- Migra o identificador de peças de BIGINT para UUID.
-- Mantém os relacionamentos existentes em servico_pecas.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE servico_pecas
    DROP CONSTRAINT fk_servico_pecas_peca;

ALTER TABLE pecas
    ADD COLUMN id_uuid UUID;

UPDATE pecas
SET id_uuid = gen_random_uuid()
WHERE id_uuid IS NULL;

ALTER TABLE pecas
    ALTER COLUMN id_uuid SET NOT NULL;

ALTER TABLE servico_pecas
    ADD COLUMN peca_uuid UUID;

UPDATE servico_pecas servico_peca
SET peca_uuid = peca.id_uuid
FROM pecas peca
WHERE servico_peca.peca_id = peca.id;

ALTER TABLE servico_pecas
    ALTER COLUMN peca_uuid SET NOT NULL;

ALTER TABLE servico_pecas
    DROP COLUMN peca_id;

ALTER TABLE servico_pecas
    RENAME COLUMN peca_uuid TO peca_id;

ALTER TABLE pecas
    DROP CONSTRAINT pecas_pkey;

ALTER TABLE pecas
    DROP COLUMN id;

ALTER TABLE pecas
    RENAME COLUMN id_uuid TO id;

ALTER TABLE pecas
    ADD CONSTRAINT pecas_pkey PRIMARY KEY (id);

ALTER TABLE servico_pecas
    ADD CONSTRAINT fk_servico_pecas_peca FOREIGN KEY (peca_id) REFERENCES pecas (id);
