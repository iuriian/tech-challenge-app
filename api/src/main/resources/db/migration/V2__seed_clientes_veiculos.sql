-- ============================================================
-- Seed de dados - 3 clientes (pessoa física) com endereço,
-- contato e ao menos um veículo cada.
-- Os CPFs possuem dígitos verificadores válidos, de modo a
-- passar na validação de domínio (ValidadorCpf).
-- ============================================================

-- ------------------------------------------------------------
-- Endereços
-- ------------------------------------------------------------
INSERT INTO enderecos (id, logradouro, numero, complemento, bairro, cidade, estado, cep) VALUES
    (1, 'Rua das Flores',       '120',  'Apto 31', 'Centro',     'São Paulo', 'SP', '01001000'),
    (2, 'Avenida Paulista',     '1578', NULL,      'Bela Vista', 'São Paulo', 'SP', '01310200'),
    (3, 'Rua Sete de Setembro', '45',   'Casa',    'Aldeota',    'Fortaleza', 'CE', '60160230');

-- ------------------------------------------------------------
-- Clientes (CPFs válidos, somente dígitos)
-- ------------------------------------------------------------
INSERT INTO clientes (id, nome, documento_numero, tipo_pessoa, email, endereco_id) VALUES
    (1, 'João da Silva',  '39053344705', 'PESSOA_FISICA', 'joao.silva@example.com',     1),
    (2, 'Maria Oliveira', '11144477735', 'PESSOA_FISICA', 'maria.oliveira@example.com', 2),
    (3, 'Carlos Pereira', '52704472661', 'PESSOA_FISICA', 'carlos.pereira@example.com', 3);

-- ------------------------------------------------------------
-- Contatos (um por cliente)
-- ------------------------------------------------------------
INSERT INTO contatos (id, tipo, nome, telefone, cliente_id) VALUES
    (1, 'CELULAR', 'João da Silva',  '11999990001', 1),
    (2, 'CELULAR', 'Maria Oliveira', '11999990002', 2),
    (3, 'CELULAR', 'Carlos Pereira', '85999990003', 3);

-- ------------------------------------------------------------
-- Veículos (cada cliente com ao menos um; João possui dois)
-- ------------------------------------------------------------
INSERT INTO veiculos (id_veiculo, marca, nome, modelo, ano, placa, motorista_id) VALUES
    (1, 'Volkswagen', 'Gol do João',       'Gol 1.6',        '2018', 'ABC1D23', 1),
    (2, 'Fiat',       'Strada de Trabalho', 'Strada Freedom', '2022', 'DEF2G34', 1),
    (3, 'Chevrolet',  'Onix da Maria',      'Onix LT 1.0',    '2021', 'GHI3J45', 2),
    (4, 'Toyota',     'Corolla do Carlos',  'Corolla XEI',    '2020', 'JKL4M56', 3);

-- ------------------------------------------------------------
-- Funcionários — cargo armazenado como ID numérico (Int):
-- 1 = ATENDENTE, 2 = MECANICO (vide enum Cargo)
-- ------------------------------------------------------------
INSERT INTO funcionarios (id, nome, cargo) VALUES
    ('3f5f33b0-4f1f-4a76-9ef8-1dc8b8d1a1b3', 'João Silva',   '1'),
    ('8f7d6a4e-9b17-49dd-8f8d-5c1d4d1ab923', 'Maria Souza',  '2'),
    ('d2f9f58d-32f5-48aa-a4f5-b4dc5e4f6a74', 'Carlos Lima',  '1');

-- ------------------------------------------------------------
-- Ajusta as sequências de identidade para o próximo valor após
-- os IDs inseridos manualmente, evitando colisão com inserções
-- futuras feitas pela aplicação.
-- ------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('enderecos', 'id'),         (SELECT MAX(id) FROM enderecos));
SELECT setval(pg_get_serial_sequence('clientes',  'id'),         (SELECT MAX(id) FROM clientes));
SELECT setval(pg_get_serial_sequence('contatos',  'id'),         (SELECT MAX(id) FROM contatos));
SELECT setval(pg_get_serial_sequence('veiculos',  'id_veiculo'), (SELECT MAX(id_veiculo) FROM veiculos));