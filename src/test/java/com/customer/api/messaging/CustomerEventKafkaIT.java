package com.customer.api.messaging;

import com.customer.api.CustomerApiApplication;
import com.customer.api.application.port.in.CreateCustomerCommand;
import com.customer.api.application.port.in.CreateCustomerUseCase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CustomerApiApplication.class, properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "messaging.customer.topic=customer.events.v1"
})
@EmbeddedKafka(partitions = 1, topics = {"customer.events.v1"})
@DirtiesContext
class CustomerEventKafkaIT {

    @Autowired
    CreateCustomerUseCase createCustomerUseCase;

    @Autowired
    EmbeddedKafkaBroker broker;

    @Test
    void publicaEventoDeCriacao() {
        createCustomerUseCase.execute(new CreateCustomerCommand("Teste Kafka", "52998224725", "kafka@test.com", "11911111111"));

        Map<String, Object> props = KafkaTestUtils.consumerProps("test-group", "false", broker);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, CustomerEvent.class.getName());

        try (var consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, Object>(props)) {
            broker.consumeFromAnEmbeddedTopic(consumer, "customer.events.v1");
            ConsumerRecord<String, Object> record = KafkaTestUtils.getSingleRecord(consumer, "customer.events.v1");
            assertNotNull(record);
            assertTrue(record.value().toString().contains("Teste Kafka"));
        }
    }
}

