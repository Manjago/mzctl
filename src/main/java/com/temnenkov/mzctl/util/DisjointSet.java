package com.temnenkov.mzctl.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DisjointSet<T> {

    private final Map<T, Node<T>> map = new HashMap<>();

    private static class Node<T> {
        private final T data;
        private Node<T> parent;
        private int rank;

        Node(@NotNull T data) {
            this.data = data;
            this.parent = this; // изначально родитель - сам узел
            this.rank = 0;      // начальный ранг (глубина дерева) = 0
        }
    }

    /**
     * Создает новое множество, содержащее один элемент data
     */
    public void makeSet(@NotNull T data) {
        SimplePreconditions.checkNotNull(data, "data", "DisjointSet.makeSet");
        map.put(data, new Node<>(data));
    }

    /**
     * Объединяет множества, в которых находятся элементы data1 и data2.
     * Если элементы уже в одном множестве, ничего не делает.
     */
    public void union(@NotNull T data1, @NotNull T data2) {
        SimplePreconditions.checkNotNull(data1, "data1", "DisjointSet.union");
        SimplePreconditions.checkNotNull(data2, "data2", "DisjointSet.union");

        Node<T> node1 = SimplePreconditions.checkContains(map.get(data1), "data1", "DisjointSet.find", data1);
        Node<T> node2 = SimplePreconditions.checkContains(map.get(data2), "data2", "DisjointSet.find", data2);

        node1 = findSet(node1);
        node2 = findSet(node2);

        if (node1 == node2) return;

        if (node1.rank >= node2.rank) {
            node2.parent = node1;
            if (node1.rank == node2.rank) {
                node1.rank++;
            }
        } else {
            node1.parent = node2;
        }
    }

    /**
     * Возвращает представителя множества, которому принадлежит элемент data.
     * Выполняет path compression для ускорения последующих вызовов.
     */
    public T find(@NotNull T data) {
        SimplePreconditions.checkNotNull(data, "data", "DisjointSet.find");

        Node<T> node = map.get(data);
        SimplePreconditions.checkState(node != null, "Element not found in disjoint-set: " + data);

        return findSet(node).data;
    }

    private Node<T> findSet(Node<T> node) {
        if (node.parent != node) {
            node.parent = findSet(node.parent); // path compression
        }
        return node.parent;
    }
}