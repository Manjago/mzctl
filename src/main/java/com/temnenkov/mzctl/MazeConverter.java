package com.temnenkov.mzctl;

import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.serialize.KryoHelper;
import com.temnenkov.mzctl.model.serialize.SerializationHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class MazeConverter {
    public static void main(String[] args) throws IOException {
        Path sourceDir = Paths.get("src/test/resources"); // MessagePack
        Path targetDir = Paths.get("src/test/resources/2"); // Kryo
        Files.createDirectories(targetDir);

        try (Stream<Path> files = Files.walk(sourceDir)) {
            files.filter(path -> path.toString().endsWith(".mzpack"))
                    .forEach(path -> {
                        try {
                            System.out.println("Конвертируем файл: " + path);
                            // Сначала загружаем через MessagePack
                            Maze maze = SerializationHelper.loadMazeFromFile(path.toString());

                            // Определяем путь для нового файла
                            Path relativePath = sourceDir.relativize(path);
                            Path targetPath = targetDir.resolve(relativePath);
                            Files.createDirectories(targetPath.getParent());

                            // Затем сохраняем через Kryo
                            KryoHelper.saveToFile(maze, targetPath.toString());

                            System.out.println("Сохранено в Kryo: " + targetPath);
                        } catch (Exception e) {
                            System.err.println("Ошибка при конвертации файла " + path + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
        }
    }
}