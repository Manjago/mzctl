package com.temnenkov.mzctl.model.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.PlayerSession;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import org.jetbrains.annotations.NotNull;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SerializationHelper {
    private static final ObjectMapper MESSAGE_PACK_MAPPER = createMessagePackMapper();

    private SerializationHelper() {
    }

    public static byte[] mazeToMessagePack(@NotNull Maze maze) {
        try {
            return MESSAGE_PACK_MAPPER.writeValueAsBytes(maze);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot serialize maze " + maze, e);
        }
    }

    public static byte[] playerSessionToMessagePack(@NotNull PlayerSession playerSession) {
        try {
            return MESSAGE_PACK_MAPPER.writeValueAsBytes(playerSession);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot serialize playerSession " + playerSession, e);
        }
    }

    public static byte[] facingToMessagePack(@NotNull Facing facing) {
        try {
            return MESSAGE_PACK_MAPPER.writeValueAsBytes(facing);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot serialize facing " + facing, e);
        }
    }

    public static byte[] playerStateToMessagePack(@NotNull PlayerStateND playerState) {
        try {
            return MESSAGE_PACK_MAPPER.writeValueAsBytes(playerState);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot serialize facing " + playerState, e);
        }
    }

    public static Maze mazeFromMessagePack(byte[] bytes) {
        try {
            return MESSAGE_PACK_MAPPER.readValue(bytes, Maze.class);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot deserialize maze " + bytesToString(bytes, 20), e);
        }
    }

    public static PlayerSession playerSessionFromMessagePack(byte[] bytes) {
        try {
            return MESSAGE_PACK_MAPPER.readValue(bytes, PlayerSession.class);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot deserialize playerSession " + bytesToString(bytes, 20), e);
        }
    }

    public static Facing facingFromMessagePack(byte[] bytes) {
        try {
            return MESSAGE_PACK_MAPPER.readValue(bytes, Facing.class);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot deserialize facing " + bytesToString(bytes, 20), e);
        }
    }

    public static PlayerStateND playerStateFromMessagePack(byte[] bytes) {
        try {
            return MESSAGE_PACK_MAPPER.readValue(bytes, PlayerStateND.class);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot deserialize playerState " + bytesToString(bytes, 20), e);
        }
    }

    public static void saveMazeToFile(@NotNull Maze maze, @NotNull String filename) {
        final byte[] bytes = mazeToMessagePack(maze);
        try {
            Files.write(Path.of(filename), bytes);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot save maze to file " + filename, e);
        }
    }

    public static @NotNull Maze loadMazeFromFile(@NotNull String filename) {
        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(Path.of(filename));
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot read maze from file " + filename, e);
        }
        return mazeFromMessagePack(bytes);
    }

    public static void savePlayerSessionToFile(@NotNull PlayerSession playerSession, @NotNull String filename) {
        final byte[] bytes = playerSessionToMessagePack(playerSession);
        try {
            Files.write(Path.of(filename), bytes);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot save playerSession to file " + filename, e);
        }
    }

    public static @NotNull PlayerSession loadPlayerSessionFromFile(@NotNull String filename) {
        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(Path.of(filename));
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot read playerSession from file " + filename, e);
        }
        return playerSessionFromMessagePack(bytes);
    }

    public static void saveFacingToFile(@NotNull Facing facing, @NotNull String filename) {
        final byte[] bytes = facingToMessagePack(facing);
        try {
            Files.write(Path.of(filename), bytes);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot save facing to file " + filename, e);
        }
    }

    public static @NotNull Facing loadFacingFromFile(@NotNull String filename) {
        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(Path.of(filename));
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot read facing from file " + filename, e);
        }
        return facingFromMessagePack(bytes);
    }

    public static void savePlayerStateToFile(@NotNull PlayerStateND playerState, @NotNull String filename) {
        final byte[] bytes = playerStateToMessagePack(playerState);
        try {
            Files.write(Path.of(filename), bytes);
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot save playerState to file " + filename, e);
        }
    }

    public static @NotNull PlayerStateND loadPlayerStateFromFile(@NotNull String filename) {
        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(Path.of(filename));
        } catch (IOException e) {
            throw new MazeSerializationException("Cannot read playerState from file " + filename, e);
        }
        return playerStateFromMessagePack(bytes);
    }

    private static @NotNull ObjectMapper createMessagePackMapper() {
        final ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
        final SimpleModule module = new SimpleModule();

        // Facing
        module.addSerializer(Facing.class, new FacingSerializer());
        module.addDeserializer(Facing.class, new FacingDeserializer());

        // Cell as map key
        module.addKeySerializer(Cell.class, new CellKeySerializer());
        module.addKeyDeserializer(Cell.class, new CellKeyDeserializer());

        mapper.registerModule(module);
        return mapper;
    }

    private static @NotNull String bytesToString(byte[] bytes, int maxLength) {
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