package com.ansa.kafka;

import com.ansa.model.CustomRecord;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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
    private KafkaProperties props;

    public MockSerdeConfig(KafkaProperties kafkaProperties) {
        props = kafkaProperties;
    }

    /**
     * Mock schema registry bean used by Kafka Avro Serde since
     * the @EmbeddedKafka setup doesn't include a schema registry.
     * @return MockSchemaRegistryClient instance
     */
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

    /**
     * KafkaAvroDeserializer that uses the MockSchemaRegistryClient.
     * The props must be provided so that specific.avro.reader: true
     * is set. Without this, the consumer will receive GenericData records.
     * @return KafkaAvroDeserializer instance
     */
    @Bean
    public KafkaAvroDeserializer kafkaAvroDeserializer() {
        return new KafkaAvroDeserializer(schemaRegistryClient(), props.buildConsumerProperties());
    }

    /**
     * Configures the kafka producer factory to use the overridden
     * KafkaAvroDeserializer so that the MockSchemaRegistryClient
     * is used rather than trying to reach out via HTTP to a schema registry
     * @return DefaultKafkaProducerFactory instance
     */
    @Bean
    public ProducerFactory<String, CustomRecord> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
//        return new DefaultKafkaProducerFactory(
//                props.buildProducerProperties(),
//                new StringSerializer(),
//                kafkaAvroSerializer()
//        );
    }

    private Map<String, Object> producerConfig() {
        return new HashMap<>();
    }

    /**
     * Configures the kafka consumer factory to use the overridden
     * KafkaAvroSerializer so that the MockSchemaRegistryClient
     * is used rather than trying to reach out via HTTP to a schema registry
     * @return DefaultKafkaConsumerFactory instance
     */
    @Bean
    public DefaultKafkaConsumerFactory consumerFactory() {
        return new DefaultKafkaConsumerFactory(
                props.buildConsumerProperties(),
                new StringDeserializer(),
                kafkaAvroDeserializer()
        );
    }

    /**
     * Configure the ListenerContainerFactory to use the overridden
     * consumer factory so that the MockSchemaRegistryClient is used
     * under the covers by all consumers when deserializing Avro data.
     * @return ConcurrentKafkaListenerContainerFactory instance
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
