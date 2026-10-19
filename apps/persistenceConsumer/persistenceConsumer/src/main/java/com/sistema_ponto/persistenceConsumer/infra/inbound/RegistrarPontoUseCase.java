package com.sistema_ponto.persistenceConsumer.infra.inbound;

public interface RegistrarPontoUseCase {
    void processar(InboundPontoPayloadDTO dto);
}
