package com.sistema_ponto.persistenceConsumer.core.ports;

import com.sistema_ponto.persistenceConsumer.core.domain.Funcionario;

public interface FuncionarioRepository {
    Funcionario buscarPorId(String id);

}
