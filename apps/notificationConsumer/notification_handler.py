import json
import os
from dotenv import load_dotenv


from proton.handlers import MessagingHandler
from proton.reactor import Container

from notificacao_dto import NotificacaoDTO


APP_ENV = os.getenv("APP_ENV", "dev")
load_dotenv(f".env.{APP_ENV}")

URL = os.getenv("ACTIVEMQ_URL", "amqp://localhost:5672")
USUARIO = os.environ["ACTIVEMQ_USER"]
SENHA = os.environ["ACTIVEMQ_PASS"]
FILA = os.getenv("FILA_NOTIFICACAO", "fila.ponto.notificacao")


class NotificacaoConsumer(MessagingHandler):

    def __init__(self):
        super().__init__(auto_accept=False)

    def on_start(self, event):
        conexao = event.container.connect(URL, user=USUARIO, password=SENHA, allow_insecure_mechs=True)
        event.container.create_receiver(conexao, FILA)
        print(f"Escutando a fila -> {FILA}")

    def on_message(self, event):
        try:
            dados = event.message.body
            dto = NotificacaoDTO.from_json(dados)
        except (json.JSONDecodeError, KeyError) as e:
            print(f"Erro ao decodificar JSON: {e}")
            self.reject(event.delivery)                   # rejeite a entrega
            return

        print(f"Bom dia, {dto.nome_funcionario}! Você tem uma nova notificação: {dto.mensagem_id} seu ponto foi registrado com sucesso em {dto.timestamp_registro}.")
        self.accept(event.delivery)                          # confirme a entrega


Container(NotificacaoConsumer()).run()
    