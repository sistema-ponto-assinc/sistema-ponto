package com.sistema_ponto.persistenceConsumer.infra.observer;

import com.sistema_ponto.persistenceConsumer.core.DTO.InboundPontoPayloadDTO;
import com.sistema_ponto.persistenceConsumer.core.ports.RegistrarPontoUseCase;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class PontoConsumer {

    private RegistrarPontoUseCase pontoUseCase;
    private ObjectMapper objectMapper;

    public PontoConsumer(RegistrarPontoUseCase pontoUseCase, ObjectMapper mapper) {
        this.pontoUseCase = pontoUseCase;
        this.objectMapper = mapper;
    }

    @JmsListener(destination = "fila.ponto.processamento")
    public void receber(String messagePayload){

        try {

            pontoUseCase.processar(deserializer(messagePayload));


        }catch (Exception e){
            throw new RuntimeException("Falha ao serializar a mensagem da fila" + e.getMessage());
        }


    }

    private InboundPontoPayloadDTO deserializer(String message){
        return objectMapper.readValue(message,InboundPontoPayloadDTO.class);

    }

}
