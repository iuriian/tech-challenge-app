-- Adiciona identificação de negócio e prazo estimado à Ordem de Serviço.
--
-- A sequence fornece somente um valor sequencial único.
-- A formatação do número da OS é responsabilidade da aplicação.
--
-- os_number permanece nullable para compatibilidade com registros legados.
-- Novas ordens terão o número gerado pelo backend.
--
-- prazo_minutos representa a duração estimada da OS em minutos.

CREATE SEQUENCE ordem_servico_numero_seq
    START WITH 1
    INCREMENT BY 1;

ALTER TABLE servicos
    ADD COLUMN os_number VARCHAR(50),
    ADD COLUMN prazo_minutos BIGINT;

ALTER TABLE servicos
    ADD CONSTRAINT uk_servicos_os_number UNIQUE (os_number),
    ADD CONSTRAINT ck_servicos_prazo_minutos_positivo
        CHECK (prazo_minutos IS NULL OR prazo_minutos > 0);
