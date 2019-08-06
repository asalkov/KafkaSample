package com.ansa.kafka;

import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

public class SchemaGenerator {
    public static void main(String[] args) {

        Schema local = LogicalTypes.date().addToSchema(Schema.create(Schema.Type.INT));
        Schema schema = SchemaBuilder.record("app")
                .fields()
                .name("name").type(local)
                .noDefault()
                .endRecord();

        System.out.println(schema.toString(true));
    }
}
