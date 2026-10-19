package com.sistema_ponto.persistenceConsumer.core.ports;

import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;

public interface PontoRepository {
    Ponto salvar(Ponto ponto);
}
