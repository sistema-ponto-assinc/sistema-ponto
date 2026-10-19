package com.sistema_ponto.persistenceConsumer.core.service;

import com.sistema_ponto.persistenceConsumer.core.DTO.OutboundNotificationPayloadDTO;
import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;
import com.sistema_ponto.persistenceConsumer.core.ports.FuncionarioRepository;
import com.sistema_ponto.persistenceConsumer.core.ports.PontoNotificador;
import com.sistema_ponto.persistenceConsumer.core.ports.PontoRepository;
import com.sistema_ponto.persistenceConsumer.core.DTO.InboundPontoPayloadDTO;
import com.sistema_ponto.persistenceConsumer.core.ports.RegistrarPontoUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PontoService implements RegistrarPontoUseCase {
    private PontoRepository repository;
    private FuncionarioRepository funcionarioRepository;
    private PontoNotificador notificador;

    public PontoService(PontoRepository repository, FuncionarioRepository funcionarioRepository,PontoNotificador notificador) {
        this.repository = repository;
        this.funcionarioRepository =funcionarioRepository;
        this.notificador = notificador;
    }


    @Override
    public void processar(InboundPontoPayloadDTO dto) {

        if(verificarIdempotencia(dto.mensagemId())){
            return;
        }
        try{


            var ponto = repository.salvar(new Ponto(dto.mensagemId(), funcionarioRepository.buscarPorId(dto.idFuncionario()), dto.timestampRegistro()));

            var outboundPayload  = new OutboundNotificationPayloadDTO(ponto.getMessagemId(),ponto.getFuncionario().getId(),ponto.getFuncionario().getNome(),ponto.getFuncionario().getEmail(), ponto.getTimestampRegistro());

            notificador.notificar(outboundPayload);


        }catch (Exception e){
            throw new RuntimeException(e);
        }





    }
    private boolean verificarIdempotencia(UUID mensagemId){
        return repository.verificarIdempotencia(mensagemId);
    }

}
