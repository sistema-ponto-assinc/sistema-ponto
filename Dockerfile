FROM python:3.12-slim AS build
WORKDIR /build

RUN apt-get update \
    && apt-get install -y --no-install-recommends build-essential \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip wheel --no-cache-dir -r requirements.txt -w /wheels

FROM python:3.12-slim
ENV PYTHONUNBUFFERED=1
WORKDIR /app

COPY --from=build /wheels /wheels
COPY requirements.txt .
RUN pip install --no-cache-dir --no-index --find-links=/wheels -r requirements.txt \
    && rm -rf /wheels

COPY producer.py .
RUN useradd --system --no-create-home app
USER app

EXPOSE 8000
CMD ["uvicorn", "producer:app", "--host", "0.0.0.0", "--port", "8000"]
