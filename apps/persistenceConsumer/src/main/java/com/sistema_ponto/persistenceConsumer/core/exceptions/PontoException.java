package com.sistema_ponto.persistenceConsumer.core.exceptions;

public abstract class PontoException extends RuntimeException {
    protected PontoException(String message, Throwable causa) {
        super(message, causa);
    }
    public abstract boolean isRecuperavel();

}

