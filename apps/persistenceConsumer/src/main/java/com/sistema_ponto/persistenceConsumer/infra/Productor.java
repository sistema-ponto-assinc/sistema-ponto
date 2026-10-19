package com.sistema_ponto.persistenceConsumer.infra;

import com.sistema_ponto.persistenceConsumer.core.DTO.OutboundNotificationPayloadDTO;
import com.sistema_ponto.persistenceConsumer.core.ports.PontoNotificador;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class Productor implements PontoNotificador {
    private ObjectMapper objectMapper;
    private JmsTemplate template;

    private static final String FILA_NOTIFICACAO = "fila.ponto.notificacao";

    public Productor(ObjectMapper objectMapper, JmsTemplate template) {
        this.objectMapper = objectMapper;
        this.template = template;
    }

    @Override
    public void notificar(OutboundNotificationPayloadDTO outboundDTO) {

        try {
            template.convertAndSend(FILA_NOTIFICACAO,criarJsonPayloadTemplate(outboundDTO));

        }catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            System.out.println(outboundDTO.emailFuncionario());

        }


    }

    private String criarJsonPayloadTemplate(OutboundNotificationPayloadDTO outboundDTO){
        return objectMapper.writeValueAsString(outboundDTO);
    }
}
