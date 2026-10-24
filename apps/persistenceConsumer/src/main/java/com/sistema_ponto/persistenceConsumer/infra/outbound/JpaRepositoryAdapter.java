package com.sistema_ponto.persistenceConsumer.infra.outbound;

import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;
import com.sistema_ponto.persistenceConsumer.core.exceptions.PersistenciaIndisponivelException;
import com.sistema_ponto.persistenceConsumer.core.ports.PontoRepository;
import com.sistema_ponto.persistenceConsumer.infra.outbound.database.SpringDataJpaRepository;
import org.springframework.transaction.TransactionException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaRepositoryAdapter implements PontoRepository {

    private final SpringDataJpaRepository repository;

    public JpaRepositoryAdapter(SpringDataJpaRepository repository) {
        this.repository = repository;
    }


    @Override
    public Ponto salvar(Ponto ponto) {
        try{
            return repository.save(ponto);

        }catch (DataAccessException | TransactionException e){
            throw new PersistenciaIndisponivelException(e);
        }
    }

    @Override
    public Optional<Ponto> buscarPorMensagemId(UUID mensagemId) {
        try{
            return repository.findByMensagemId(mensagemId);

        }catch (DataAccessException | TransactionException e){
            throw new PersistenciaIndisponivelException(e);
        }
    }



}
