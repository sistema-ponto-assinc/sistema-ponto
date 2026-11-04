package com.sistema_ponto.persistenceConsumer.core.DTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OutboundNotificationPayloadDTO (
        UUID mensagemId,
        String idFuncionario,
        String nomeFuncionario,
        String emailFuncionario,
        OffsetDateTime timestampRegistro

){
    public OutboundNotificationPayloadDTO(UUID mensagemId, String idFuncionario, String nomeFuncionario, String emailFuncionario, OffsetDateTime timestampRegistro) {
        this.mensagemId = mensagemId;
        this.idFuncionario = idFuncionario;
        this.nomeFuncionario = nomeFuncionario;
        this.emailFuncionario = emailFuncionario;
        this.timestampRegistro = timestampRegistro;
    }
}
