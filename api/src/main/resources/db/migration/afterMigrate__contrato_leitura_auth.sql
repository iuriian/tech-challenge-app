-- ============================================================
-- Reaplica os GRANT do contrato de leitura do servico de autenticacao
-- (tech-challenge-auth) descrito no V16.
--
-- Callback afterMigrate: roda em todo migrate, isto e, em todo boot da
-- aplicacao, e nao apenas quando existe migration pendente. E essa diferenca
-- que importa aqui.
--
-- Os GRANT nasceram no V16 dentro de um IF EXISTS sobre pg_roles. Quando a
-- instancia do Cloud SQL e recriada e a aplicacao sobe antes de o role auth_ro
-- existir, o V16 cria as views, pula os grants em silencio e fica registrado
-- como aplicado — o Flyway nunca o executa de novo, e o auth passa a receber
-- permission denied nas views mesmo depois de o role aparecer. Foi o que
-- derrubou a emissao de token na staging em 2026-11-04 (503
-- temporarily_unavailable).
--
-- O V16 nao pode ser corrigido no lugar: editar uma migration aplicada
-- invalidaria o checksum do Flyway (ver o comentario da V15). Por isso o
-- contrato passa a viver aqui, onde a reaplicacao e idempotente e nao depende
-- do historico do Flyway.
--
-- O IF EXISTS continua necessario: local, kind e o smoke test do CI nao tem o
-- role, e um callback que falha derruba o boot da aplicacao.
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'auth_ro') THEN
        GRANT SELECT (id, nome, cpf, status) ON cliente TO auth_ro;
        GRANT SELECT (id, nome, cpf, cargo) ON funcionario TO auth_ro;
    END IF;
END $$;
