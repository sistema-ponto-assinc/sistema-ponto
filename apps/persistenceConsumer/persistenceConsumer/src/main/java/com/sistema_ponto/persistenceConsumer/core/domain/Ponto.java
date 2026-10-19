package com.sistema_ponto.persistenceConsumer.core.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "registro_ponto")
public class Ponto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "mensagem_id", nullable = false, unique = true)
    private UUID messagemId;

    @Column(name = "id_funcionario", nullable = false)
    private String funcionarioId;

    @Column(name = "timestamp_registro", nullable = false)
    private OffsetDateTime timestampRegistro;

    public Ponto(UUID messagemId, String funcionarioId, OffsetDateTime timestampRegistro) {
        this.id = id;
        this.messagemId = messagemId;
        this.funcionarioId = funcionarioId;
        this.timestampRegistro = timestampRegistro;
    }

    protected Ponto() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getMessagemId() {
        return messagemId;
    }

    public String getFuncionarioId() {
        return funcionarioId;
    }

    public OffsetDateTime getTimestampRegistro() {
        return timestampRegistro;
    }

    @Override
    public String toString() {
        return "Ponto{" +
                "id=" + id +
                ", messagemId=" + messagemId +
                ", funcionarioId='" + funcionarioId + '\'' +
                ", timestampRegistro=" + timestampRegistro +
                '}';
    }
}
