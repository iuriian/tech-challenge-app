-- ============================================================
-- Ajuste do schema de veículos para refletir a entidade JPA
-- atual (VeiculoJPA), que mapeia a chave primária como "id".
--
-- A coluna nasceu como id_veiculo na V1 e manteve esse nome na
-- migração para UUID (V7). O PostgreSQL propaga o rename para a
-- PRIMARY KEY e para a FK fk_servicos_veiculo, que referencia
-- esta coluna, então nenhuma constraint precisa ser recriada.
--
-- As demais colunas (marca, nome, modelo, ano, placa e
-- motorista_id) já correspondem ao mapeamento da entidade, assim
-- como a constraint uk_veiculos_placa, que garante a unicidade
-- da placa usada por CriarVeiculo e AtualizarVeiculo.
-- ============================================================

ALTER TABLE veiculos
    RENAME COLUMN id_veiculo TO id;
