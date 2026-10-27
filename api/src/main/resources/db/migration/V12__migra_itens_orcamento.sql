CREATE TABLE itens_orcamento
(
    id                UUID PRIMARY KEY,
    ordem_servico_id  UUID           NOT NULL,
    tipo              VARCHAR(20)    NOT NULL,
    referencia_id     UUID           NOT NULL,
    descricao         VARCHAR(100)   NOT NULL,
    valor_unitario    NUMERIC(10, 2) NOT NULL,
    quantidade        NUMERIC(10, 2) NOT NULL,
    codigo_referencia VARCHAR(50),

    CONSTRAINT fk_itens_orcamento_ordem_servico
        FOREIGN KEY (ordem_servico_id)
            REFERENCES servicos (id)
            ON DELETE CASCADE,

    CONSTRAINT ck_itens_orcamento_tipo
        CHECK (tipo IN ('PECA', 'SERVICO')),

    CONSTRAINT ck_itens_orcamento_valor_unitario
        CHECK (valor_unitario >= 0),

    CONSTRAINT ck_itens_orcamento_quantidade
        CHECK (quantidade > 0)
);

INSERT INTO itens_orcamento (id,
                             ordem_servico_id,
                             tipo,
                             referencia_id,
                             descricao,
                             valor_unitario,
                             quantidade,
                             codigo_referencia)
SELECT sp.id,
       sp.servico_id,
       'PECA',
       p.id,
       p.nome,
       p.preco_de_venda,
       sp.quantidade,
       p.codigo
FROM servico_pecas sp
         JOIN pecas p
              ON p.id = sp.peca_id;

DROP TABLE servico_pecas;
