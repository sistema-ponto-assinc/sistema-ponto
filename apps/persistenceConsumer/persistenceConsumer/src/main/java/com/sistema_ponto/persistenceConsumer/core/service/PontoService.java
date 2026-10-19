package com.sistema_ponto.persistenceConsumer.core.service;

import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;
import com.sistema_ponto.persistenceConsumer.core.ports.PontoRepository;
import com.sistema_ponto.persistenceConsumer.infra.inbound.InboundPontoPayloadDTO;
import com.sistema_ponto.persistenceConsumer.infra.inbound.RegistrarPontoUseCase;
import org.springframework.stereotype.Service;

@Service
public class PontoService implements RegistrarPontoUseCase {
    private PontoRepository repository;

    public PontoService(PontoRepository repository) {
        this.repository = repository;
    }


    @Override
    public void processar(InboundPontoPayloadDTO dto) {
        var ponto = repository.salvar(new Ponto(dto.mensagemId(), dto.idFuncionario(), dto.timestampRegistro()));


        System.out.println(ponto);
    }
}
