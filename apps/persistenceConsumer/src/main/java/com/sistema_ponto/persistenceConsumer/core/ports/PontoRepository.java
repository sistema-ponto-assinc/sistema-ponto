package com.sistema_ponto.persistenceConsumer.core.ports;

import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;

import java.util.Optional;
import java.util.UUID;

public interface PontoRepository {
    Ponto salvar(Ponto ponto);
    Optional<Ponto> buscarPorMensagemId(UUID mensagemId);
    Optional<Ponto> buscarUltimoPorFuncionario(String idFuncionario);
}
