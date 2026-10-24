# Imagem do notificationConsumer (build a partir do código do repositório)
# Configuração só por variáveis de ambiente: ACTIVEMQ_URL, ACTIVEMQ_USER, ACTIVEMQ_PASS, FILA_NOTIFICACAO

# Etapa 1: compila as dependências (a proton tem parte em C e não tem pacote pronto para Linux)
FROM python:3.13-slim AS build
WORKDIR /build

RUN apt-get update && apt-get install -y --no-install-recommends build-essential

COPY requirements.txt .
RUN pip wheel --no-cache-dir -r requirements.txt -w /wheels

# Etapa 2: imagem final só com o Python e os pacotes já compilados
FROM python:3.13-slim
ENV PYTHONUNBUFFERED=1
WORKDIR /app

COPY --from=build /wheels /wheels
COPY requirements.txt .
RUN pip install --no-cache-dir --no-index --find-links=/wheels -r requirements.txt && rm -rf /wheels

COPY . .

RUN useradd --system --no-create-home app
USER app

ENTRYPOINT ["python", "notification_handler.py"]
