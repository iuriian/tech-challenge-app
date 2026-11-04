-- ============================================================
-- Seed de dados - Peças, Serviços e Relacionamentos
-- ============================================================

-- ------------------------------------------------------------
-- Peças
-- ------------------------------------------------------------
INSERT INTO pecas (id, codigo, nome, descricao, fabricante, fornecedor, preco_de_compra, preco_de_venda, qtd_estoque, ativo) VALUES
    (1, 'PEC001', 'Filtro de Óleo', 'Filtro de óleo de motor padrão', 'Bosch', 'AutoParts Ltda', 25.00, 45.00, 50, true),
    (2, 'PEC002', 'Óleo Sintético 5W30', 'Óleo de motor sintético 1L', 'Castrol', 'Lubrificantes SA', 30.00, 55.00, 100, true),
    (3, 'PEC003', 'Pastilha de Freio Dianteira', 'Jogo de pastilhas de freio', 'Cobreq', 'AutoParts Ltda', 70.00, 120.00, 20, true);

-- ------------------------------------------------------------
-- Serviços
-- ------------------------------------------------------------
-- João da Silva (cliente 1) trouxe o Gol (veículo 1) para troca de óleo (finalizado)
-- Maria Oliveira (cliente 2) trouxe o Onix (veículo 3) para troca de pastilha (em diagnóstico)
INSERT INTO servicos (id, descricao, status, funcionario_id, cliente_id, veiculo_id) VALUES
    (1, 'Troca de Óleo e Filtro', 'FINALIZADA', '3f5f33b0-4f1f-4a76-9ef8-1dc8b8d1a1b3', 1, 1),
    (2, 'Troca de Pastilha de Freio', 'EM_DIAGNOSTICO', '8f7d6a4e-9b17-49dd-8f8d-5c1d4d1ab923', 2, 3);

-- ------------------------------------------------------------
-- Relacionamento Serviços - Peças
-- ------------------------------------------------------------
-- O serviço 1 utilizou o Filtro (1) e o Óleo (2)
-- O serviço 2 vai utilizar a Pastilha (3)
INSERT INTO servico_pecas (servico_id, peca_id) VALUES
    (1, 1),
    (1, 2),
    (2, 3);

-- ------------------------------------------------------------
-- Ajusta as sequências de identidade para o próximo valor
-- ------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('pecas', 'id'), (SELECT MAX(id) FROM pecas));
SELECT setval(pg_get_serial_sequence('servicos', 'id'), (SELECT MAX(id) FROM servicos));
