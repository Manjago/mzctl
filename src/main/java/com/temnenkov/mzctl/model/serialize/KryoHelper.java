package com.temnenkov.mzctl.model.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class KryoHelper {
    private static final Kryo kryo = new Kryo();

    private KryoHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    static {
        kryo.setRegistrationRequired(false); // автоматическая регистрация классов
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
    }

    public static <T> void saveToFile(T object, String filePath) throws IOException {
        try (Output output = new Output(new FileOutputStream(filePath))) {
            kryo.writeObject(output, object);
        }
    }

    public static <T> T loadFromFile(Class<T> type, String filePath) throws IOException {
        try (Input input = new Input(new FileInputStream(filePath))) {
            return kryo.readObject(input, type);
        }
    }

    public static <T> T loadFromResource(Class<T> type, String resourceName) throws IOException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourceName);
            }
            try (Input input = new Input(is)) {
                return kryo.readObject(input, type);
            }
        }
    }
}