-- ============================================================
-- Seed complementar - 3 peças ainda não cadastradas.
-- ============================================================

INSERT INTO pecas (id, codigo, nome, descricao, fabricante, fornecedor, preco_de_compra, preco_de_venda, qtd_estoque, ativo) VALUES
    (4, 'PEC004', 'Filtro de Ar', 'Filtro de ar do motor', 'Mann-Filter', 'AutoParts Ltda', 35.00, 65.00, 40, true),
    (5, 'PEC005', 'Vela de Ignição', 'Vela de ignição resistiva', 'NGK', 'Peças Express', 18.00, 32.00, 80, true),
    (6, 'PEC006', 'Correia Dentada', 'Correia dentada para motor flex', 'Gates', 'Peças Express', 95.00, 160.00, 25, true);

SELECT setval(pg_get_serial_sequence('pecas', 'id'), (SELECT MAX(id) FROM pecas));
