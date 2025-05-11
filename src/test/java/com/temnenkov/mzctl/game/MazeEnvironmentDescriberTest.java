package com.temnenkov.mzctl.game;

import com.temnenkov.mzctl.game.model.Facing;
import com.temnenkov.mzctl.game.model.MazeEnvironmentDescriber;
import com.temnenkov.mzctl.game.model.PlayerStateND;
import com.temnenkov.mzctl.model.Cell;
import com.temnenkov.mzctl.model.Maze;
import com.temnenkov.mzctl.model.MazeDim;
import com.temnenkov.mzctl.model.MazeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MazeEnvironmentDescriberTest {

    private Maze maze;
    private MazeEnvironmentDescriber describer;

    @BeforeEach
    void setUp() {
        MazeDim dim = MazeDim.of(3, 3);
        maze = MazeFactory.createNotConnectedMaze(dim);

        // Создаем простой лабиринт:
        //   0   1   2
        // 0 x---x---x
        //           |
        // 1 x   x   x
        //           |
        // 2 x---x---x

        maze.addPass(Cell.ofColumnAndRow(0, 0), Cell.ofColumnAndRow(1, 0));
        maze.addPass(Cell.ofColumnAndRow(1, 0), Cell.ofColumnAndRow(2, 0));
        maze.addPass(Cell.ofColumnAndRow(2, 0), Cell.ofColumnAndRow(2, 1));
        maze.addPass(Cell.ofColumnAndRow(2, 1), Cell.ofColumnAndRow(2, 2));
        maze.addPass(Cell.ofColumnAndRow(2, 2), Cell.ofColumnAndRow(1, 2));
        maze.addPass(Cell.ofColumnAndRow(1, 2), Cell.ofColumnAndRow(0, 2));

        describer = new MazeEnvironmentDescriber(maze);
    }

    @Test
    void testDescribeEnvironment_CenterFacingEast() {
        PlayerStateND player = new PlayerStateND(Cell.ofColumnAndRow(1, 1), Facing.EAST);

        String description = describer.describeEnvironment(player);

        String expectedDescription = """
    Вы находитесь в комнате.
    - Впереди: стена
    - Слева: стена
    - Справа: стена
    - Сзади: стена
    """;

        System.out.println("Actual description: \n" + description);
        System.out.println("Expected description: \n" + expectedDescription);

        assertEquals(expectedDescription.trim(), description.trim());
    }

    @Test
    void testDescribeEnvironment_CornerFacingNorth() {
        PlayerStateND player = new PlayerStateND(Cell.ofColumnAndRow(0, 0), Facing.NORTH);

        String description = describer.describeEnvironment(player);

        String expectedDescription = """
            Вы находитесь в комнате.
            - Впереди: граница лабиринта
            - Слева: граница лабиринта
            - Справа: проход
            - Сзади: стена
            """;

        System.out.println("Actual description: \n" + description);
        System.out.println("Expected description: \n" + expectedDescription);

        assertEquals(expectedDescription.trim(), description.trim());
    }

    @Test
    void testDescribeEnvironment_EdgeFacingSouth() {
        PlayerStateND player = new PlayerStateND(Cell.ofColumnAndRow(2, 0), Facing.SOUTH);

        String description = describer.describeEnvironment(player);

        String expectedDescription = """
    Вы находитесь в комнате.
    - Впереди: проход
    - Слева: граница лабиринта
    - Справа: проход
    - Сзади: граница лабиринта
    """;

        System.out.println("Actual description: \n" + description);
        System.out.println("Expected description: \n" + expectedDescription);

        assertEquals(expectedDescription.trim(), description.trim());
    }
}