package com.temnenkov.mzctl.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.temnenkov.mzctl.game.model.Facing;

import java.io.IOException;

public class FacingSerializer extends JsonSerializer<Facing> {
    @Override
    public void serialize(Facing facing, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeArrayFieldStart("direction");
        for (Facing.AxisDirection dir : facing.getDirections()) {
            gen.writeNumber(dir.value());
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}