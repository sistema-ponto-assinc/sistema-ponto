package com.sistema_ponto.persistenceConsumer.core.ports;

import com.sistema_ponto.persistenceConsumer.core.DTO.InboundPontoPayloadDTO;

public interface RegistrarPontoUseCase {
    void processar(InboundPontoPayloadDTO dto);
}
