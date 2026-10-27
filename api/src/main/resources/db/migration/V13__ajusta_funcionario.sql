-- ============================================================
-- Ajuste do schema de funcionários para refletir a entidade JPA
-- atual (FuncionarioJPA):
--   * nova coluna cpf VARCHAR(11) NOT NULL, única (idx_cpf)
--   * cargo passa a ser persistido como EnumType.STRING
--     (ATENDENTE/MECANICO) com tamanho 20, restrito por CHECK
-- ============================================================

-- ------------------------------------------------------------
-- CPF: adiciona como nullable, preenche e só então aplica NOT NULL
-- ------------------------------------------------------------
ALTER TABLE funcionarios
    ADD COLUMN cpf VARCHAR(11);

UPDATE funcionarios SET cpf = '52998224725' WHERE id = '3f5f33b0-4f1f-4a76-9ef8-1dc8b8d1a1b3';
UPDATE funcionarios SET cpf = '11144477735' WHERE id = '8f7d6a4e-9b17-49dd-8f8d-5c1d4d1ab923';
UPDATE funcionarios SET cpf = '39053344705' WHERE id = 'd2f9f58d-32f5-48aa-a4f5-b4dc5e4f6a74';

UPDATE funcionarios f
SET cpf = LPAD(n.rn::TEXT, 11, '0')
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
      FROM funcionarios
      WHERE cpf IS NULL) n
WHERE f.id = n.id;

ALTER TABLE funcionarios
    ALTER COLUMN cpf SET NOT NULL;

CREATE UNIQUE INDEX idx_cpf ON funcionarios (cpf);

-- ------------------------------------------------------------
-- Cargo: converte o id numérico do enum para o nome (EnumType.STRING)
-- ------------------------------------------------------------
UPDATE funcionarios
SET cargo = CASE cargo
                WHEN '1' THEN 'ATENDENTE'
                WHEN '2' THEN 'MECANICO'
                ELSE cargo
            END;

ALTER TABLE funcionarios
    ALTER COLUMN cargo TYPE VARCHAR(20);

ALTER TABLE funcionarios
    ADD CONSTRAINT ck_funcionarios_cargo CHECK (cargo IN ('ATENDENTE', 'MECANICO'));
