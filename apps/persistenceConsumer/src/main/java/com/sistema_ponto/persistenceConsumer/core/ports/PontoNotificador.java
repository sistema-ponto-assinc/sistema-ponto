package com.sistema_ponto.persistenceConsumer.core.ports;

import com.sistema_ponto.persistenceConsumer.core.DTO.OutboundNotificationPayloadDTO;

public interface PontoNotificador {
    void notificar(OutboundNotificationPayloadDTO outboundDTO);
}
