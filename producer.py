"""
Producer API
 - POST /pontos -> recebe idFuncionario e publica um RegistrarPontoCommand na fila
 - POST /avisos -> publica avisos no tópico
"""
import json
import logging
import os
import uuid
from datetime import datetime, timezone
from typing import Optional


from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from proton import Message
from proton.utils import BlockingConnection

BROKER_HOST = os.getenv("BROKER_HOST", "localhost")
BROKER_PORT = int(os.getenv("BROKER_PORT", "5672"))  # AMQP 1.0 (ActiveMQ Classic)
BROKER_USER = os.getenv("BROKER_USER", "admin")
BROKER_PASSWORD = os.getenv("BROKER_PASSWORD", "admin")
QUEUE_NAME = os.getenv("QUEUE_NAME", "queue://fila.ponto.processamento")
TOPIC_NAME = os.getenv("TOPIC_NAME", "topic://avisos")

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
log = logging.getLogger("producer-api")

app = FastAPI(title="Producer API - Ponto", version="1.2.0")


class RegistrarPontoRequest(BaseModel):
    idFuncionario: str = Field(..., min_length=1, examples=["98765"])


class RegistrarPontoCommand(BaseModel):
    mensagemId: str
    idFuncionario: str
    timestampRegistro: str

class Aviso(BaseModel):
    mensagem: str = Field(..., min_length=1, examples=["Sistema entra em manutenção às 22h"])
    origem: Optional[str] = Field(default=None, examples=["RH"])


def publicar(destination: str, payload: dict, persistente: bool = False) -> None:
    """Abre conexão AMQP 1.0, publica o JSON no destino e desconecta."""
    conn = BlockingConnection(
        f"amqp://{BROKER_HOST}:{BROKER_PORT}",
        timeout=10,
        user=BROKER_USER,
        password=BROKER_PASSWORD,
        allow_insecure_mechs=True,
    )
    try:
        sender = conn.create_sender(destination)
        mensagem = Message(body=json.dumps(payload, ensure_ascii=False))
        mensagem.content_type = "application/json"
        mensagem.durable = persistente
        sender.send(mensagem)
    finally:
        conn.close()


def publicar_ou_503(destination: str, payload: dict, persistente: bool = False) -> None:
    try:
        publicar(destination, payload, persistente)
    except Exception as e:
        log.error("Falha ao publicar no broker: %s", e)
        raise HTTPException(status_code=503, detail="Broker de mensageria indisponível")
    log.info("[producer] Enviada para %s -> %s", destination, payload)


@app.post("/pontos", status_code=202)
def registrar_ponto(ponto: RegistrarPontoRequest):
    timestamp_registro = (
        datetime.now(timezone.utc)
        .replace(microsecond=0)
        .isoformat()
        .replace("+00:00", "Z")
    )
    command = RegistrarPontoCommand(
        mensagemId=str(uuid.uuid4()),
        idFuncionario=ponto.idFuncionario,
        timestampRegistro=timestamp_registro,
    )
    payload = command.model_dump()
    publicar_ou_503(QUEUE_NAME, payload, persistente=True)
    return {"status": "enfileirado", "fila": QUEUE_NAME, "mensagem": payload}


@app.post("/avisos", status_code=202)
def avisos(aviso: Aviso):
    payload = {
        "id": str(uuid.uuid4()),
        "mensagem": aviso.mensagem,
        "origem": aviso.origem,
        "data_hora": datetime.now(timezone.utc).isoformat(),
    }
    publicar_ou_503(TOPIC_NAME, payload)
    return {"status": "publicado", "topico": TOPIC_NAME, "mensagem": payload}


@app.get("/health")
def health():
    return {"status": "ok"}
