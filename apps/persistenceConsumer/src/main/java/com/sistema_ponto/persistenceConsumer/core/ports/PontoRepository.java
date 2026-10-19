package com.sistema_ponto.persistenceConsumer.core.ports;

import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;

import java.util.UUID;

public interface PontoRepository {
    Ponto salvar(Ponto ponto);
    Boolean verificarIdempotencia(UUID mensagemId);
}
