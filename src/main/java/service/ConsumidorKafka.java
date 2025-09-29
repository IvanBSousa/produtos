package service;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dto.ResponseDTO;
import io.quarkus.logging.Log;

public class ConsumidorKafka {

    @Incoming("produto-topic-in")
    public void consume(String mensagem) {
       try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // Para lidar com tipos Java 8 como LocalDateTime
            ResponseDTO produto = mapper.readValue(mensagem, ResponseDTO.class);

            Log.info("Produto recebido do Kafka: " + produto);

        } catch (JsonProcessingException e) {
            Log.error("Erro ao desserializar mensagem do Kafka", e);
        }
    }
}
