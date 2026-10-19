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
    private UUID mensagemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_funcionario", nullable = false)

    private Funcionario funcionario;

    @Column(name = "timestamp_registro", nullable = false)
    private OffsetDateTime timestampRegistro;

    public Ponto(UUID messagemId, Funcionario funcionario, OffsetDateTime timestampRegistro) {
        this.mensagemId = messagemId;
        this.funcionario = funcionario;
        this.timestampRegistro = timestampRegistro;
    }

    protected Ponto() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getMessagemId() {
        return mensagemId;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public OffsetDateTime getTimestampRegistro() {
        return timestampRegistro;
    }

    @Override
    public String toString() {
        return "Ponto{" +
                "id=" + id +
                ", messagemId=" + mensagemId +
                ", funcionario='" + funcionario + '\'' +
                ", timestampRegistro=" + timestampRegistro +
                '}';
    }
}
