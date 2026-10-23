-- ============================================================
-- Renomeia veiculos.motorista_id para proprietario_id, alinhando o
-- schema ao mapeamento atual de VeiculoJPA.proprietarioId e à
-- linguagem ubíqua do contexto de Veículo, onde o dono do veículo
-- passou a ser o "proprietário" (ProprietarioRef).
--
-- A coluna nasceu como motorista_id na V1 e manteve esse nome na
-- migração para UUID (V7). O PostgreSQL propaga o rename da coluna
-- para a constraint fk_veiculos_motorista, que segue válida
-- apontando para a coluna renomeada; apenas o nome da constraint
-- ficaria defasado, por isso ele também é renomeado. Nenhum índice
-- referencia a coluna.
--
-- O comentário da V14 que cita "motorista_id" permanece como
-- registro histórico: editar uma migração já aplicada invalidaria
-- o checksum do Flyway.
-- ============================================================

ALTER TABLE veiculos
    RENAME COLUMN motorista_id TO proprietario_id;

ALTER TABLE veiculos
    RENAME CONSTRAINT fk_veiculos_motorista TO fk_veiculos_proprietario;
