package com.sistema_ponto.persistenceConsumer.core.exceptions;

public class PersistenciaIndisponivelException extends PontoException {
    public PersistenciaIndisponivelException(Throwable causa) {
        super("Falha ao acessar ao banco de dados:" + causa, null);
    }

    @Override
    public boolean isRecuperavel() {
        return true;
    }
}
