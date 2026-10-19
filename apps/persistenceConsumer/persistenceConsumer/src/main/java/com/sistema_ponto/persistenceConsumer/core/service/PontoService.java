package com.sistema_ponto.persistenceConsumer.core.service;

import com.sistema_ponto.persistenceConsumer.core.domain.Funcionario;
import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;
import com.sistema_ponto.persistenceConsumer.core.ports.FuncionarioRepository;
import com.sistema_ponto.persistenceConsumer.core.ports.PontoRepository;
import com.sistema_ponto.persistenceConsumer.infra.inbound.InboundPontoPayloadDTO;
import com.sistema_ponto.persistenceConsumer.infra.inbound.RegistrarPontoUseCase;
import org.springframework.stereotype.Service;

@Service
public class PontoService implements RegistrarPontoUseCase {
    private PontoRepository repository;
    private FuncionarioRepository funcionarioRepository;

    public PontoService(PontoRepository repository, FuncionarioRepository funcionarioRepository) {
        this.repository = repository;
        this.funcionarioRepository =funcionarioRepository;
    }


    @Override
    public void processar(InboundPontoPayloadDTO dto) {

        var ponto = repository.salvar(new Ponto(dto.mensagemId(), funcionarioRepository.buscarPorId(dto.idFuncionario()), dto.timestampRegistro()));


        System.out.println(ponto);
    }
}
