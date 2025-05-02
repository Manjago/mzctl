package com.temnenkov.mzctl.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IndexedHashSet<T> {
    private static final String ELEMENT_CANNOT_BE_NULL = "element cannot be null";
    private final List<T> elements;
    private final Map<T, Integer> indexes;

    public IndexedHashSet() {
        this.elements = new ArrayList<>();
        this.indexes = new HashMap<>();
    }

    /**
     * Добавляет элемент в множество.
     * Если элемент уже присутствует, возвращает false.
     */
    public boolean add(@NotNull T element) {
        SimplePreconditions.checkNotNull(element, "element", "add");
        if (contains(element)) {
            return false;
        }
        elements.add(element);
        indexes.put(element, elements.size() - 1);
        return true;
    }

    /**
     * Удаляет элемент из множества.
     * Если элемент отсутствует, возвращает false.
     */
    public boolean remove(@NotNull T element) {
        SimplePreconditions.checkNotNull(element, "element", "remove");
        final Integer index = indexes.get(element);
        if (index == null) {
            return false;
        }

        // Меняем местами удаляемый элемент и последний элемент списка
        int lastIndex = elements.size() - 1;
        final T lastElement = elements.get(lastIndex);
        Collections.swap(elements, index, lastIndex);

        // Удаляем последний элемент (ранее он был удаляемым)
        elements.remove(lastIndex);
        indexes.remove(element);

        // Обновляем индекс перемещенного элемента, если это не тот же элемент
        if (index < elements.size()) {
            indexes.put(lastElement, index);
        }

        return true;
    }

    /**
     * Проверяет, содержится ли элемент в множестве.
     */
    public boolean contains(@NotNull T element) {
        SimplePreconditions.checkNotNull(element, "element", "contains");
        return indexes.containsKey(element);
    }

    /**
     * Возвращает случайный элемент из множества.
     */
    public T getRandom(@NotNull Random random) {
        SimplePreconditions.checkState(!elements.isEmpty(), "IndexedHashSet is empty");
        return elements.get(random.nextInt(elements.size()));
    }

    /**
     * Возвращает количество элементов в множестве.
     */
    public int size() {
        return elements.size();
    }

    /**
     * Проверяет, пусто ли множество.
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Очищает множество.
     */
    public void clear() {
        elements.clear();
        indexes.clear();
    }

    /**
     * Возвращает немодифицируемый список элементов.
     * Полезно для перебора элементов.
     */
    public List<T> asList() {
        return Collections.unmodifiableList(elements);
    }
}
