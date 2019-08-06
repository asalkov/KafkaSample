package com.ansa.kafka;

import com.ansa.model.CustomRecord;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MockSerdeConfig {
    @Bean
    public MockSchemaRegistryClient schemaRegistryClient() {
        return new MockSchemaRegistryClient();
    }

    /**
     * KafkaAvroSerializer that uses the MockSchemaRegistryClient
     * @return KafkaAvroSerializer instance
     */
    @Bean
    public KafkaAvroSerializer kafkaAvroSerializer() {
        return new KafkaAvroSerializer(schemaRegistryClient());
    }

    @Bean
    public ProducerFactory<String, CustomRecord> producerFactory() {
        return new DefaultKafkaProducerFactory(producerConfig(),
                new StringSerializer(),
                kafkaAvroSerializer());
    }

    private Map<String, Object> producerConfig() {
        return new HashMap<>();
    }


    /**
     * KafkaAvroDeserializer that uses the MockSchemaRegistryClient.
     * The props must be provided so that specific.avro.reader: true
     * is set. Without this, the consumer will receive GenericData records.
     * @return KafkaAvroDeserializer instance
     */
    @Bean
    public KafkaAvroDeserializer kafkaAvroDeserializer() {
        return new KafkaAvroDeserializer(schemaRegistryClient());
    }
}
