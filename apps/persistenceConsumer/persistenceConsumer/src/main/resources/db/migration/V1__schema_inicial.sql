-- Schema inicial (seção 9 do doc de arquitetura)
-- SQL compatível com PostgreSQL (QA) e H2 em MODE=PostgreSQL (DEV).

CREATE TABLE funcionario (
    id    VARCHAR(50)  PRIMARY KEY,
    nome  VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE registro_ponto (
    id                 UUID PRIMARY KEY,
    mensagem_id        UUID NOT NULL UNIQUE, -- ID da fila (Idempotência)
    id_funcionario     VARCHAR(50) NOT NULL,
    timestamp_registro TIMESTAMP WITH TIME ZONE NOT NULL,
    criado_em          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario (id)
);
