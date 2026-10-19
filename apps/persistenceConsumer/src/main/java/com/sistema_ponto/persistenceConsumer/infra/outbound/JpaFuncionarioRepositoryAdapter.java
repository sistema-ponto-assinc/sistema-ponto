package com.sistema_ponto.persistenceConsumer.infra.outbound;

import com.sistema_ponto.persistenceConsumer.core.domain.Funcionario;
import com.sistema_ponto.persistenceConsumer.core.ports.FuncionarioRepository;
import com.sistema_ponto.persistenceConsumer.infra.outbound.database.SpringDataJpaFuncionarioRepository;
import com.sistema_ponto.persistenceConsumer.infra.outbound.database.SpringDataJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaFuncionarioRepositoryAdapter implements FuncionarioRepository {

    private final SpringDataJpaFuncionarioRepository funcionarioRepository;

    public JpaFuncionarioRepositoryAdapter(SpringDataJpaFuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }


    @Override
    public Funcionario buscarPorId(String id) {
        return funcionarioRepository.findById(id).orElseThrow();
    }
}
