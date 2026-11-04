package com.sistema_ponto.persistenceConsumer.core.exceptions;


public class PayloadInvalidoException extends PontoException {
    public PayloadInvalidoException(String motivo) {
        super("Payload inválido: " + motivo, null);
    }

    @Override
    public boolean isRecuperavel() {
        return false;
    }
}
