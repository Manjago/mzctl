package com.temnenkov.mzctl.model.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class SerializationHelper {
    private static final ObjectMapper MESSAGE_PACK_MAPPER = createMessagePackMapper();

    private SerializationHelper() {
    }

    public static byte[] mazeToMessagePack(@NotNull Maze maze) {
        try {
            return MESSAGE_PACK_MAPPER.writeValueAsBytes(maze);
        } catch (JsonProcessingException e) {
            throw new MazeSerializationException("Cannot serialize maze " + maze, e);
        }
    }

    public static Maze mazeFromMessagePack(byte[] bytes) {
        try {
            return MESSAGE_PACK_MAPPER.readValue(bytes, Maze.class);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot deserialize maze " + bytesToString(bytes, 20), e);
        }
    }

    private static @NotNull ObjectMapper createMessagePackMapper() {
        final ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
        final SimpleModule module = new SimpleModule();
        module.addKeySerializer(Cell.class, new CellKeySerializer());
        module.addKeyDeserializer(Cell.class, new CellKeyDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    private static class CellKeyDeserializer extends KeyDeserializer {
        @Override
        public @NotNull Cell deserializeKey(@NotNull String key, DeserializationContext ctxt) throws IOException {
            List<Integer> coordinates = Arrays.stream(key.split(",")).map(Integer::parseInt).toList();
            return new Cell(coordinates);
        }
    }

    private static class CellKeySerializer extends JsonSerializer<Cell> {
        @Override
        public void serialize(@NotNull Cell cell,
                @NotNull JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider) throws IOException {
            String key = cell.coordinates().stream().map(Object::toString).collect(Collectors.joining(","));
            jsonGenerator.writeFieldName(key);
        }
    }

    private static String bytesToString(byte[] bytes, int maxLength) {
        if (bytes == null) {
            return "null";
        }
        int length = Math.min(bytes.length, maxLength);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < length; i++) {
            sb.append(bytes[i]);
            if (i < length - 1) {
                sb.append(", ");
            }
        }
        if (bytes.length > maxLength) {
            sb.append(", …");
        }
        sb.append("]");
        return sb.toString();
    }
}

