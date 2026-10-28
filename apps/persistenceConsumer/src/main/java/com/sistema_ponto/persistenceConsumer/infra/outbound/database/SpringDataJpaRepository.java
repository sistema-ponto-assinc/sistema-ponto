package com.sistema_ponto.persistenceConsumer.infra.outbound.database;

import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaRepository extends JpaRepository<Ponto, UUID> {
    @EntityGraph(attributePaths = "funcionario")
    Optional<Ponto> findByMensagemId(UUID mensagemId);

    Optional<Ponto> findTopByFuncionarioIdOrderByTimestampRegistroDesc(String idFuncionario);
}
