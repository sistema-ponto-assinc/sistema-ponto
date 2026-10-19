package com.sistema_ponto.persistenceConsumer.infra.outbound.database;

import com.sistema_ponto.persistenceConsumer.core.domain.Funcionario;
import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataJpaFuncionarioRepository extends JpaRepository<Funcionario, String> {
}
