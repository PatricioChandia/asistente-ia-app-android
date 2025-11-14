package com.devst.voicegpt.network;

// El JSON que ENVIAMOS a /api/consulta
public class ConsultaRequest {
    String prompt;

    public ConsultaRequest(String prompt) {
        this.prompt = prompt;
    }
}