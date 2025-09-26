package service;

import org.apache.kafka.clients.consumer.Consumer;

import io.smallrye.reactive.messaging.annotations.Channel;
import jakarta.inject.Inject;

public class ConsumidorKafka {

    @Inject
    @Channel("produto-topic-in")
    Consumer<String, String> consumidor;

   

}
