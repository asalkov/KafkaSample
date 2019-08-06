package com.ansa.kafka;


import com.ansa.model.CustomRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class Sender {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Sender.class);

    @Autowired
    private KafkaTemplate<String, CustomRecord> kafkaTemplate;

    public void send(CustomRecord payload) {
        LOGGER.info("sending payload='{}'", payload);
        kafkaTemplate.send("foo", payload);
    }
}
