package com.sistema_ponto.persistenceConsumer.core.exceptions;

public class FilaIndisponivelException extends PontoException {
    public FilaIndisponivelException(Throwable causa) {
        super("Falha ao acessar a fila de mensageria:" + causa, null);
    }

    @Override
    public boolean isRecuperavel() {
        return true;
    }
}
