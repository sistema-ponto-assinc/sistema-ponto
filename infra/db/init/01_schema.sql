-- Schema inicial (seção 9 do doc de arquitetura) - usado apenas em QA
-- Executado somente na primeira subida do container (volume vazio).

CREATE TABLE IF NOT EXISTS funcionario (
    id VARCHAR(50) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS registro_ponto (
    id UUID PRIMARY KEY,
    mensagem_id UUID NOT NULL UNIQUE, -- ID da fila (Idempotência)
    id_funcionario VARCHAR(50) NOT NULL,
    timestamp_registro TIMESTAMP WITH TIME ZONE NOT NULL,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario(id)
);

-- Seed para testes (funcionario é somente leitura no MVP)
INSERT INTO funcionario (id, nome, email) VALUES
    ('98765', 'João da Silva', 'joao@empresa.com')
ON CONFLICT (id) DO NOTHING;
