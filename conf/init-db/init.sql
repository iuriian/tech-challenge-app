-- Este script é executado apenas na primeira inicialização do container
-- O banco 'oficina' já é criado automaticamente pela variável POSTGRES_DB
-- Este arquivo pode ser usado para criar tabelas, extensões ou dados iniciais

-- Exemplo: garantir que o banco existe (redundante, mas seguro)
-- SELECT 'CREATE DATABASE oficina'
-- WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'oficina')\gexec

SELECT 'CREATE DATABASE oficina'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'oficina')\gexec

\c oficina
