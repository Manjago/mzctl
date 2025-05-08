package com.temnenkov.mzctl.visualization;

import com.temnenkov.mzctl.model.CellularAutomataMaze;

public interface MazeVisualizer {
    /**
     * Визуализирует (или выводит информацию о) текущем состоянии лабиринта.
     *
     * @param maze лабиринт для визуализации
     * @param step текущий шаг генерации (0 - начальное состояние, 1..N - шаги)
     */
    void visualize(CellularAutomataMaze maze, int step);
}