package service;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.logging.Log;

public class ConsumidorKafka {

  

    /**
     * Método que consome mensagens do tópico "produto-topic-out".
     * Cada mensagem publicada será recebida aqui.
     */
    @Incoming("produto-topic-in")
    public void consume(String mensagem) {
        // Aqui você processa a mensagem recebida
        Log.info("Mensagem recebida do Kafka: " + mensagem);
    }

   

}
