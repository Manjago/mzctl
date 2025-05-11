package com.temnenkov.mzctl.context;

import com.temnenkov.mzctl.game.MazeManager;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleContext {
    private final AtomicReference<MazeManager> mazeManagerHolder = new AtomicReference<>();

    public MazeManager getMazeManager() {
        return mazeManagerHolder.get();
    }

    public void setMazeManager(MazeManager mazeManager) {
        this.mazeManagerHolder.set(mazeManager);
    }

}
