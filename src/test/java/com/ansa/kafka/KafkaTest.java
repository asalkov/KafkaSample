package com.ansa.kafka;

import com.ansa.model.CustomRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.test.TestUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SenderConfig.class, MockSerdeConfig.class})

public class KafkaTest {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(KafkaTest.class);

    @Autowired
    public KafkaTemplate<String, CustomRecord> template;

    @Autowired
    public Sender sender;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka =
            new EmbeddedKafkaRule(1, false, "foo");

    @BeforeClass
    public static void setup() {
        System.setProperty("spring.kafka.bootstrap-servers",
                embeddedKafka.getEmbeddedKafka().getBrokersAsString());
        System.setProperty("kafka.bootstrap-servers",
                embeddedKafka.getEmbeddedKafka().getBrokersAsString());
    }

    @Test
    public void test() throws InterruptedException {


        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(embeddedKafka.getEmbeddedKafka().getBrokersAsString(),
                "GR",
                "true");
        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<String, String>(
                consumerProps);
        Consumer<String, String> consumer = cf.createConsumer();
        embeddedKafka.getEmbeddedKafka().consumeFromAllEmbeddedTopics(consumer);

        CustomRecord record = new CustomRecord();
        record.setDate(LocalDate.now());
        record.setName("First");
        sender.send(record);

//        ConsumerRecord record = KafkaTestUtils.getSingleRecord(consumer, "foo");
//
//        System.out.println(record);
    }

    @After
    public void tearDown() {

    }

}
