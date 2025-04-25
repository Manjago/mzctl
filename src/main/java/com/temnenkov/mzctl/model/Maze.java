package com.temnenkov.mzctl.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Maze {
    private final Map<Cell, Set<Cell>> passes;

    @JsonCreator
    public Maze(@JsonProperty("passes") Map<Cell, Set<Cell>> passes) {
        this.passes = passes;
    }

    public Maze() {
        this.passes = new HashMap<>();
    }

    public void addPass(Cell cell, Set<Cell> neighbors) {
        passes.computeIfAbsent(cell, k -> new HashSet<>()).addAll(neighbors);

        for (Cell neighbor : neighbors) {
            passes.computeIfAbsent(neighbor, k -> new HashSet<>()).add(cell);
        }
    }

    public Set<Cell> getNeighbors(Cell cell) {
        return passes.getOrDefault(cell, Set.of());
    }

    public boolean canPass(Cell from, Cell to) {
        return passes.getOrDefault(from, Set.of()).contains(to);
    }
}
