package com.temnenkov.mzctl.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.temnenkov.mzctl.model.Cell;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CellKeySerializer extends JsonSerializer<Cell> {
    @Override
    public void serialize(Cell cell, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String key = cell.coordinates().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        jsonGenerator.writeFieldName(key);
    }
}

