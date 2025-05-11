package com.temnenkov.mzctl.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.temnenkov.mzctl.model.Cell;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

class CellKeySerializer extends JsonSerializer<Cell> {
    @Override
    public void serialize(@NotNull Cell cell,
            @NotNull JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        String key = Arrays.stream(cell.getCoordinates()).mapToObj(Integer::toString).collect(Collectors.joining(","));
        jsonGenerator.writeFieldName(key);
    }
}
