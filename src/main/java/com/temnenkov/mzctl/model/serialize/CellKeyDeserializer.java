package com.temnenkov.mzctl.model.serialize;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.temnenkov.mzctl.model.Cell;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

class CellKeyDeserializer extends KeyDeserializer {
    @Override
    public @NotNull Cell deserializeKey(@NotNull String key, DeserializationContext ctxt) {
        int[] coordinates = Arrays.stream(key.split(",")).mapToInt(Integer::parseInt).toArray();
        return new Cell(coordinates);
    }
}
