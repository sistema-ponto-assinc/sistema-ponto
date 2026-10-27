"""
Producer API
 - POST /apontar -> publica na FILA   (ponto a ponto: um consumidor processa cada mensagem)
 - POST /avisos  -> publica no TÓPICO (pub/sub: todos os consumidores inscritos recebem)
"""
import json
import logging
import os
import uuid
from datetime import datetime, timezone
from typing import Literal, Optional

import stomp
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

BROKER_HOST = os.getenv("BROKER_HOST", "localhost")
BROKER_PORT = int(os.getenv("BROKER_PORT", "61613"))  # STOMP (ActiveMQ Classic)
BROKER_USER = os.getenv("BROKER_USER", "admin")
BROKER_PASSWORD = os.getenv("BROKER_PASSWORD", "admin")
QUEUE_NAME = os.getenv("QUEUE_NAME", "/queue/fila.ponto.processamento")
TOPIC_NAME = os.getenv("TOPIC_NAME", "/topic/avisos")

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
log = logging.getLogger("producer-api")

app = FastAPI(title="Producer API - Ponto", version="1.1.0")


class Apontamento(BaseModel):
    matricula: str = Field(..., min_length=1, examples=["12345"])
    tipo: Literal["ENTRADA", "SAIDA"] = Field(..., examples=["ENTRADA"])
    data_hora: Optional[datetime] = Field(
        default=None, description="ISO 8601. Se omitido, usa o horário atual (UTC)."
    )


class Aviso(BaseModel):
    mensagem: str = Field(..., min_length=1, examples=["Sistema entra em manutenção às 22h"])
    origem: Optional[str] = Field(default=None, examples=["RH"])


def publicar(destination: str, payload: dict, persistente: bool = False) -> None:
    """Abre conexão STOMP, publica o JSON no destino (fila ou tópico) e desconecta."""
    headers = {"persistent": "true"} if persistente else {}
    conn = stomp.Connection([(BROKER_HOST, BROKER_PORT)])
    try:
        conn.connect(BROKER_USER, BROKER_PASSWORD, wait=True)
        conn.send(
            destination=destination,
            body=json.dumps(payload, ensure_ascii=False),
            content_type="application/json",
            headers=headers,
        )
    finally:
        if conn.is_connected():
            conn.disconnect()


def publicar_ou_503(destination: str, payload: dict, persistente: bool = False) -> None:
    try:
        publicar(destination, payload, persistente)
    except Exception as e:
        log.error("Falha ao publicar no broker: %s", e)
        raise HTTPException(status_code=503, detail="Broker de mensageria indisponível")
    log.info("[producer] Enviada para %s -> %s", destination, payload)


@app.post("/apontar", status_code=202)
def apontar(apontamento: Apontamento):
    payload = {
        "id": str(uuid.uuid4()),
        "matricula": apontamento.matricula,
        "tipo": apontamento.tipo,
        "data_hora": (apontamento.data_hora or datetime.now(timezone.utc)).isoformat(),
    }
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
