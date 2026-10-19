package com.sistema_ponto.persistenceConsumer.core.DTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OutboundNotificationPayloadDTO (
        UUID mensagemId,
        String funcionarioId,
        String nomeFuncionário,
        String emailFuncionario,
        OffsetDateTime timestampRegistro

){
    public OutboundNotificationPayloadDTO(UUID mensagemId, String funcionarioId, String nomeFuncionário, String emailFuncionario, OffsetDateTime timestampRegistro) {
        this.mensagemId = mensagemId;
        this.funcionarioId = funcionarioId;
        this.nomeFuncionário = nomeFuncionário;
        this.emailFuncionario = emailFuncionario;
        this.timestampRegistro = timestampRegistro;
    }
}
