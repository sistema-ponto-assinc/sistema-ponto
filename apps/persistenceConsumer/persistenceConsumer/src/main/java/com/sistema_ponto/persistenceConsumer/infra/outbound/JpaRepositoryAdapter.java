package com.sistema_ponto.persistenceConsumer.infra.outbound;

import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;
import com.sistema_ponto.persistenceConsumer.core.ports.PontoRepository;
import com.sistema_ponto.persistenceConsumer.infra.outbound.database.SpringDataJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaRepositoryAdapter implements PontoRepository {

    private final SpringDataJpaRepository repository;

    public JpaRepositoryAdapter(SpringDataJpaRepository repository) {
        this.repository = repository;
    }


    @Override
    public Ponto salvar(Ponto ponto) {
        return repository.save(ponto);
    }
}
