-- Adiciona colunas de rastreio de tempo à tabela de serviços.
-- data_abertura: preenchida com NOW() para registros existentes (backfill);
-- o DEFAULT é removido após a migração — novos registros são inseridos
-- com o valor fornecido pela aplicação.
-- data_inicio_execucao e data_finalizacao permanecem NULL até as
-- transições de status correspondentes (EM_EXECUCAO e FINALIZADA).

ALTER TABLE servicos
    ADD COLUMN data_abertura      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    ADD COLUMN data_inicio_execucao TIMESTAMP WITH TIME ZONE,
    ADD COLUMN data_finalizacao   TIMESTAMP WITH TIME ZONE;

ALTER TABLE servicos ALTER COLUMN data_abertura DROP DEFAULT;
