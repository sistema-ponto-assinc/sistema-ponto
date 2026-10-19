package com.sistema_ponto.persistenceConsumer.infra.inbound;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InboundPontoPayloadDTO(
        UUID mensagemId,
        String idFuncionario,
        OffsetDateTime timestampRegistro
) {
}
