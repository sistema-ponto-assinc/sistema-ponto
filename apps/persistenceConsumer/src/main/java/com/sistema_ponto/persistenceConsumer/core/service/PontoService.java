package com.sistema_ponto.persistenceConsumer.core.service;

import com.sistema_ponto.persistenceConsumer.core.DTO.OutboundNotificationPayloadDTO;
import com.sistema_ponto.persistenceConsumer.core.domain.Ponto;
import com.sistema_ponto.persistenceConsumer.core.exceptions.FuncionarioApontouEmMenosDeSessentaSegundosException;
import com.sistema_ponto.persistenceConsumer.core.exceptions.PayloadInvalidoException;
import com.sistema_ponto.persistenceConsumer.core.ports.FuncionarioRepository;
import com.sistema_ponto.persistenceConsumer.core.ports.PontoNotificador;
import com.sistema_ponto.persistenceConsumer.core.ports.PontoRepository;
import com.sistema_ponto.persistenceConsumer.core.DTO.InboundPontoPayloadDTO;
import com.sistema_ponto.persistenceConsumer.core.ports.RegistrarPontoUseCase;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
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
        validarPayload(dto);

        var ponto = repository.buscarPorMensagemId(dto.mensagemId()).orElseGet( () ->{
            validarApontamento60s(dto);
            return salvarPonto(dto);
        });

        var payloadDeNotificacao  = criarPayloadDeNotificacao(ponto);
        notificador.notificar(payloadDeNotificacao);
    }


    private void validarApontamento60s(InboundPontoPayloadDTO dto ){
        repository.buscarUltimoPorFuncionario(dto.idFuncionario()).ifPresent(( ultimoPonto -> {
            var segundos = Duration.between(ultimoPonto.getTimestampRegistro(), dto.timestampRegistro()).getSeconds();
            if(Math.abs(segundos) < 60 ){
                throw new FuncionarioApontouEmMenosDeSessentaSegundosException(ultimoPonto.getId());
            }
        }));
    }

    private void validarPayload(InboundPontoPayloadDTO dto) {
        if (dto.mensagemId() == null) {
            throw new PayloadInvalidoException("mensagemId ausente");
        }
        if (dto.idFuncionario() == null || dto.idFuncionario().isBlank()) {
            throw new PayloadInvalidoException("idFuncionario ausente, mensagemId: " + dto.mensagemId());
        }
        if (dto.timestampRegistro() == null) {
            throw new PayloadInvalidoException("timestampRegistro ausente, mensagemId: " + dto.mensagemId());
        }
    }

    private OutboundNotificationPayloadDTO criarPayloadDeNotificacao(Ponto ponto) {
        return new OutboundNotificationPayloadDTO(
                ponto.getMessagemId(),
                ponto.getFuncionario().getId(),
                ponto.getFuncionario().getNome(),
                ponto.getFuncionario().getEmail(),
                ponto.getTimestampRegistro());
    }

    private Ponto salvarPonto(InboundPontoPayloadDTO dto) {
        var funcionario = funcionarioRepository.buscarPorId(dto.idFuncionario());
        var ponto = repository.salvar(new Ponto(dto.mensagemId(),funcionario, dto.timestampRegistro()));
        System.out.println("ponto salvo com sucesso");
        return ponto;
    }


}
