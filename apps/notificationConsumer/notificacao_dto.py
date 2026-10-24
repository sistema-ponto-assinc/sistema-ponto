from dataclasses import dataclass
import json

@dataclass(frozen=True)
class NotificacaoDTO:
    mensagem_id: str
    id_funcionario: str
    nome_funcionario: str
    email_funcionario: str
    timestamp_registro: str
    
    @classmethod
    def from_json(cls, data: str) -> "NotificacaoDTO":
        dados = json.loads(data)
        return cls(
            mensagem_id = dados["mensagemId"],
            id_funcionario = dados["idFuncionario"],
            nome_funcionario = dados["nomeFuncionario"],
            email_funcionario = dados["emailFuncionario"],
            timestamp_registro = dados["timestampRegistro"]
        )