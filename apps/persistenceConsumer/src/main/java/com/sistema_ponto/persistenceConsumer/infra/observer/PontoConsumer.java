package com.sistema_ponto.persistenceConsumer.infra.observer;

import com.sistema_ponto.persistenceConsumer.core.DTO.InboundPontoPayloadDTO;
import com.sistema_ponto.persistenceConsumer.core.exceptions.PontoException;
import com.sistema_ponto.persistenceConsumer.core.ports.RegistrarPontoUseCase;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class PontoConsumer {

    private RegistrarPontoUseCase pontoUseCase;
    private ObjectMapper objectMapper;

    private static final String FILA_PROCESSAMENTO  = "fila.ponto.processamento";

    public PontoConsumer(RegistrarPontoUseCase pontoUseCase, ObjectMapper mapper) {
        this.pontoUseCase = pontoUseCase;
        this.objectMapper = mapper;
    }

    @JmsListener(destination = "fila.ponto.processamento")
    public void receber(String messagePayload){
        InboundPontoPayloadDTO dtoToSend;

        try{
             dtoToSend = deserializer(messagePayload);
        }catch (JacksonException e){
            System.out.println("Payload Invalido, descarando{}" + messagePayload + e);
            return;
        }



        try {

            pontoUseCase.processar(dtoToSend);


        }catch (PontoException e){
            if(!e.isRecuperavel()){
                System.out.println("Mensagem descartada, messagemId:," + dtoToSend.mensagemId() + e.getMessage());
                return;
            }
            throw e;
        }


    }

    private InboundPontoPayloadDTO deserializer(String message){
        return objectMapper.readValue(message,InboundPontoPayloadDTO.class);

    }

}
