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

    private MazeEnvironmentDescriber describer;

    @BeforeEach
    void setUp() {
        final MazeDim dim = MazeDim.of(3, 3);
        final Maze maze = MazeFactory.createNotConnectedMaze(dim);

        // Создаем простой лабиринт:
        //   0   1   2
        // 0 x---x---x
        //           |
        // 1 x   x   x
        //           |
        // 2 x---x---x

        maze.addPass(Cell.ofRowAndColumn(0, 0), Cell.ofRowAndColumn(0, 1));
        maze.addPass(Cell.ofRowAndColumn(0, 1), Cell.ofRowAndColumn(0, 2));
        maze.addPass(Cell.ofRowAndColumn(0, 2), Cell.ofRowAndColumn(1, 2));
        maze.addPass(Cell.ofRowAndColumn(1, 2), Cell.ofRowAndColumn(2, 2));
        maze.addPass(Cell.ofRowAndColumn(2, 2), Cell.ofRowAndColumn(2, 1));
        maze.addPass(Cell.ofRowAndColumn(2, 1), Cell.ofRowAndColumn(2, 0));

        describer = new MazeEnvironmentDescriber(maze);
    }

    @Test
    void testDescribeEnvironment_CenterFacingEast() {
        PlayerStateND player = new PlayerStateND(Cell.ofRowAndColumn(1, 1), Facing.EAST);

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
        PlayerStateND player = new PlayerStateND(Cell.ofRowAndColumn(0, 0), Facing.NORTH);

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
        PlayerStateND player = new PlayerStateND(Cell.ofRowAndColumn(2, 0), Facing.SOUTH);

        String description = describer.describeEnvironment(player);

        String expectedDescription = """
Вы находитесь в комнате.
- Впереди: граница лабиринта
- Слева: проход
- Справа: граница лабиринта
- Сзади: стена
""";

        System.out.println("Actual description: \n" + description);
        System.out.println("Expected description: \n" + expectedDescription);

        assertEquals(expectedDescription.trim(), description.trim());
    }
}