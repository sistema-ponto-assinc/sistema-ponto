import json

from proton.handlers import MessagingHandler
from proton.reactor import Container

from notificacao_dto import NotificacaoDTO

URL = "amqp://localhost:5672"
FILA = "fila.ponto.notificacao"


class NotificacaoConsumer(MessagingHandler):

    def __init__(self):
        super().__init__(auto_accept=False)

    def on_start(self, event):
        conexao = event.container.connect(URL, user="admin", password="admin", allow_insecure_mechs=True)
        event.container.create_receiver(conexao, FILA)
        print(f"Escutando a fila {FILA}... (Ctrl+C para parar)")

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
    