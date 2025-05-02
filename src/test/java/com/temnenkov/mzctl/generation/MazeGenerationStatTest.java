package com.temnenkov.mzctl.generation;

import com.temnenkov.mzctl.analysis.MazeExplorer;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.util.SimpleStopWatch;
import com.temnenkov.mzctl.visualization.MazeAsciiVisualizer;
import com.temnenkov.mzctl.visualization.MazeImageVisualizer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class MazeGenerationStatTest {
    private final Random random = new Random(1L);
    private final MazeGeneratorFactory mazeGeneratorFactory = new MazeGeneratorFactory(random);

    /*
    1. Маленький двумерный лабиринт:
    Размерность: 2D
    Размер: 5x5
    Цель: Проверка базовой корректности и удобства визуализации.
    Хорошо подойдет для быстрого анализа и отладки.
     */
    @Test
    void testSmall2D() throws IOException {
        final MazeDim mazeDim = MazeDim.of(5, 5);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report();
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            new MazeAsciiVisualizer(maze).printMaze();
            new MazeImageVisualizer(maze).saveMazeImage("target/"+algo+"-small2D.png");
        }
    }

    /*
    2. Большой двумерный лабиринт:
    Размерность: 2D
    Размер: 50x50
    Цель: Оценка производительности и того, как методы справляются с масштабированием.
    Позволит увидеть различия в показателях (например, средняя длина пути, количество тупиков и т.д.)
    между алгоритмами.
     */
    @Test
    void testBig2D() throws IOException {
        final MazeDim mazeDim = MazeDim.of(50, 50);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report();
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            // в ASCII не выводим, очень большой, неудобно смотреть
            new MazeImageVisualizer(maze).saveMazeImage("target/"+algo+"-big2D.png");
        }
    }

    /*
    3. Маленький трехмерный лабиринт:
    - Размерность: 3D
    - Размер: 5x5x5
    - Цель: Оценка работы алгоритмов в многомерном случае, проверка корректности и визуализации в 3D.
     */
    @Test
    void testSmall3D() {
        final MazeDim mazeDim = MazeDim.of(5, 5, 5);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report();
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            // в ASCII не выводим, не умеем рисовать 3D
            // в PNG не выводим, не умеем рисовать 3D
        }
    }

    /*
    4. Большой трехмерный лабиринт:
    - Размерность: 3D
    - Размер: 20x20x20
    - Цель: Сравнение работы алгоритмов в многомерном случае при существенном увеличении сложности. Можно увидеть, как меняются характеристики лабиринта при переходе в многомерное пространство.     */
    @Test
    void testBig3D() {
        final MazeDim mazeDim = MazeDim.of(20, 20, 20);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report();
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            // в ASCII не выводим, не умеем рисовать 3D
            // в PNG не выводим, не умеем рисовать 3D
        }
    }

    /*
    5. Прямоугольный лабиринт (не квадратный):
    - Размерность: 2D
    - Размер: 50x10 (сильно вытянутый прямоугольник)
    - Цель: Проверить, как генераторы справляются с лабиринтами нестандартной формы. Может выявить проблемы или особенности алгоритмов, связанные с неравномерностью размеров по разным осям.
    */
    @Test
    void testNonSquare2D() throws IOException {
        final MazeDim mazeDim = MazeDim.of(50, 10);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report();
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            new MazeAsciiVisualizer(maze).printMaze();
            new MazeImageVisualizer(maze).saveMazeImage("target/"+algo+"-nonSquare2D.png");
        }
    }

    /*
    6. Экстремально вытянутый лабиринт
    - Размерность: 2D
    - Размер: 5x100 (сильно-сильно вытянутый прямоугольник)
    - Цель: Интересно протестировать лабиринт с еще более экстремальным отношением сторон (например, 5×100), чтобы увидеть, как Recursive Division будет вести себя в таком случае.
    */
    @Test
    void testTooNonSquare2D() throws IOException {
        final MazeDim mazeDim = MazeDim.of(5, 100);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report();
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            // ASCII будет смотреться плохо
            new MazeImageVisualizer(maze).saveMazeImage("target/"+algo+"-tooNonSquare2D.png");
        }
    }

    /*
    7. Совсем экстремально вытянутый лабиринт
    - Размерность: 2D
    - Размер: 3x200 (совсем вытянутый прямоугольник)
    - Цель: Интересно протестировать лабиринт с еще более экстремальным отношением сторон (например, 5×100), чтобы увидеть, как Recursive Division будет вести себя в таком случае.
    */
    @Test
    void testVeryNonSquare2D() throws IOException {
        final MazeDim mazeDim = MazeDim.of(3, 200);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report();
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            // ASCII будет смотреться плохо
            new MazeImageVisualizer(maze).saveMazeImage("target/"+algo+"-veryNonSquare2D.png");
        }
    }

    /*
   8. Маленький четырехмерный лабиринт:
    - Размерность: 4D
    - Размер: 4x4x4x4
    - Цель: Проверка того, как алгоритмы справляются с высокой размерностью. Полезно, если твои алгоритмы поддерживают произвольную размерность.
    */
    @Test
    void testSmall4D() {
        final MazeDim mazeDim = MazeDim.of(4 ,4 ,4, 4);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report();
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            // не умеем 4D рисовать в ASCII
            // не умеем 4D рисовать в PNG
        }
    }

    /*
   9. Средний четырехмерный лабиринт
    - Размерность: 4D
    - Размер: 10x10x10x10
    - Цель: Проверка того, как алгоритмы справляются с высокой размерностью. Полезно, если твои алгоритмы поддерживают произвольную размерность.
    */
    @Test
    void testMedium4D() {
        final MazeDim mazeDim = MazeDim.of(10, 10, 10, 10);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report();
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            // не умеем 4D рисовать в ASCII
            // не умеем 4D рисовать в PNG
        }
    }

    /*
   10. Большой четырехмерный лабиринт (для интереса):
    - Размерность: 4D
    - Размер: 50x50x50x50
    - Цель: Проверка того, как алгоритмы справляются с высокой размерностью. Полезно, если твои алгоритмы поддерживают произвольную размерность.
    */
    @Test
    void testBig4D() {
        final MazeDim mazeDim = MazeDim.of(50, 50, 50, 50);
        for(MazeGeneratorFactory.Algo algo : MazeGeneratorFactory.Algo.values()) {
            System.out.println("Testing algorithm: " + algo);

            final MazeGenerator mazeGenerator = mazeGeneratorFactory.create(algo, mazeDim);
            final SimpleStopWatch genWatch = SimpleStopWatch.createStarted();
            final Maze maze = mazeGenerator.generateMaze();
            final long genDurationMs = genWatch.elapsed();

            final MazeExplorer mazeExplorer = new MazeExplorer(maze, random);
            final SimpleStopWatch exploreWatch = SimpleStopWatch.createStarted();
            assertTrue(mazeExplorer.isPerfect());
            final String report = mazeExplorer.report(10);
            final long exploreDurationMs = exploreWatch.elapsed();

            System.out.printf("""
                    Algorithm: %s
                    Generation time: %d ms
                    Exploration time: %d ms
                    %s
                    %n""", algo, genDurationMs, exploreDurationMs, report);

            // не умеем 4D рисовать в ASCII
            // не умеем 4D рисовать в PNG
        }
    }
}
