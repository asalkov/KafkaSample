package com.ansa.kafka;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.LocalDate;

public class Java8LocalDateConversion extends Conversion<LocalDate> {
    @Override
    public Class<LocalDate> getConvertedType() {
        return LocalDate.class;
    }

    @Override
    public String getLogicalTypeName() {
        //      v--- reuse the logical type `date`
        return "date";
    }

    @Override
    // convert LocalDate to Integer
    public Integer toInt(LocalDate value, Schema schema, LogicalType type) {
        return (int) value.toEpochDay();
    }

    @Override
    // parse LocalDate from Integer
    public LocalDate fromInt(Integer value, Schema schema, LogicalType type) {
        return LocalDate.ofEpochDay(value);
    }
}

