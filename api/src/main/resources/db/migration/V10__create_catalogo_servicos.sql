CREATE TABLE catalogo_servicos
(
    id        UUID PRIMARY KEY,
    descricao VARCHAR(100)   NOT NULL,
    valor     NUMERIC(10, 2) NOT NULL,
    ativo     BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_catalogo_servicos_valor_nao_negativo
        CHECK (valor >= 0)
);