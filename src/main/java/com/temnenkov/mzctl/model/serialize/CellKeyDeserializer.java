package com.temnenkov.mzctl.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.temnenkov.mzctl.model.Cell;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CellKeyDeserializer extends KeyDeserializer {
    @Override
    public Cell deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        List<Integer> coordinates = Arrays.stream(key.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return new Cell(coordinates);
    }
}
