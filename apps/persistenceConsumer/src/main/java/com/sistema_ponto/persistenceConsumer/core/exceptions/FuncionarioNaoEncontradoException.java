package com.sistema_ponto.persistenceConsumer.core.exceptions;


public class FuncionarioNaoEncontradoException extends PontoException {
    public FuncionarioNaoEncontradoException(String id) {
        super("Funcionário não encontrado com id:" + id, null );
    }

    @Override
    public boolean isRecuperavel() {
        return false;
    }
}
