package com.temnenkov.mzctl.model.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.temnenkov.mzctl.game.model.Facing;

import java.io.IOException;

public class FacingDeserializer extends JsonDeserializer<Facing> {
    @Override
    public Facing deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode directionsNode = node.get("direction");
        if (directionsNode == null || !directionsNode.isArray()) {
            throw new IOException("Expected 'direction' array");
        }
        Facing.AxisDirection[] directions = new Facing.AxisDirection[directionsNode.size()];
        for (int i = 0; i < directionsNode.size(); i++) {
            directions[i] = Facing.AxisDirection.fromValue(directionsNode.get(i).intValue());
        }
        return Facing.of(directions);
    }
}