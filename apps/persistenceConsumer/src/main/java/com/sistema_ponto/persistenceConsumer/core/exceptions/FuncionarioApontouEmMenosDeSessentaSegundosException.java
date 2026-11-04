package com.sistema_ponto.persistenceConsumer.core.exceptions;


import java.util.UUID;

public class FuncionarioApontouEmMenosDeSessentaSegundosException extends PontoException {
    public FuncionarioApontouEmMenosDeSessentaSegundosException(UUID idFuncionario) {
        super("Funcionário apontou em menos de 60s de regra de negócio. indentificador do funcionário: " + idFuncionario, null );
    }

    @Override
    public boolean isRecuperavel() {
        return false;
    }
}
