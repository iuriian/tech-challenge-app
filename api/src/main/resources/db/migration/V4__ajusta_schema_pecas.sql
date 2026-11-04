-- ============================================================
-- Ajuste do schema de peças para refletir a entidade JPA atual.
-- ============================================================

ALTER TABLE pecas
    ALTER COLUMN codigo TYPE VARCHAR(10),
    ALTER COLUMN descricao TYPE VARCHAR(255),
    ALTER COLUMN qtd_estoque SET DEFAULT 0,
    ALTER COLUMN ativo SET DEFAULT true;
