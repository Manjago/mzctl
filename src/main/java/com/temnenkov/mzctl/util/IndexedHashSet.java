package com.temnenkov.mzctl.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Множество, поддерживающее доступ по индексу, быстрое удаление и добавление элементов,
 * а также случайный доступ. Порядок элементов сохраняется.
 *
 * @param <T> тип элементов множества
 */
public class IndexedHashSet<T> implements Iterable<T> {

    private static final String CTOR = ".ctor";
    private final List<T> elements;
    private final Map<T, Integer> indexes;

    /**
     * Создает пустое множество.
     */
    public IndexedHashSet() {
        this.elements = new ArrayList<>();
        this.indexes = new HashMap<>();
    }

    /**
     * Создает множество по данным из переданной коллекции.
     * Если в коллекции есть повторяющиеся элементы, они добавляются один раз.
     *
     * @param initialData коллекция элементов для инициализации множества
     */
    public IndexedHashSet(@NotNull Collection<T> initialData) {
        SimplePreconditions.checkNotNull(initialData, "initialData", CTOR);
        this.elements = new ArrayList<>(initialData.size());
        this.indexes = HashMap.newHashMap(initialData.size());
        for (T element : initialData) {
            SimplePreconditions.checkNotNull(element, "element in initialData", CTOR);
            if (!indexes.containsKey(element)) { // исключаем повторяющиеся элементы
                indexes.put(element, elements.size());
                elements.add(element);
            }
        }
    }

    /**
     * Добавляет элемент в множество.
     * Если элемент уже присутствует, возвращает false.
     *
     * @param element добавляемый элемент
     * @return true, если элемент был добавлен, false, если уже присутствует
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
     *
     * @param element удаляемый элемент
     * @return true, если элемент был удален, false, если отсутствовал
     */
    public boolean remove(@NotNull T element) {
        SimplePreconditions.checkNotNull(element, "element", "remove");
        final Integer index = indexes.get(element);
        if (index == null) {
            return false;
        }

        elements.remove((int)index); // удаляем элемент по индексу, сохраняя порядок
        indexes.remove(element);

        // обновляем индексы элементов, следующих за удалённым
        for (int i = index; i < elements.size(); i++) {
            indexes.put(elements.get(i), i);
        }

        return true;
    }

    /**
     * Проверяет, содержится ли элемент в множестве.
     *
     * @param element проверяемый элемент
     * @return true, если элемент содержится в множестве
     */
    public boolean contains(@NotNull T element) {
        SimplePreconditions.checkNotNull(element, "element", "contains");
        return indexes.containsKey(element);
    }

    /**
     * Возвращает случайный элемент из множества.
     *
     * @param random генератор случайных чисел
     * @return случайный элемент из множества
     * @throws IllegalStateException если множество пусто
     */
    public T getRandom(@NotNull Random random) {
        SimplePreconditions.checkState(!elements.isEmpty(), "IndexedHashSet is empty");
        return elements.get(random.nextInt(elements.size()));
    }

    /**
     * Возвращает последний элемент множества.
     *
     * @return последний элемент множества
     * @throws IllegalStateException если множество пусто
     */
    public T getLast() {
        SimplePreconditions.checkState(!elements.isEmpty(), "IndexedHashSet is empty");
        return elements.getLast();
    }

    /**
     * Возвращает первый элемент множества.
     *
     * @return первый элемент множества
     * @throws IllegalStateException если множество пусто
     */
    public T getFirst() {
        SimplePreconditions.checkState(!elements.isEmpty(), "IndexedHashSet is empty");
        return elements.getFirst();
    }

    /**
     * Возвращает количество элементов в множестве.
     *
     * @return количество элементов
     */
    public int size() {
        return elements.size();
    }

    /**
     * Проверяет, пусто ли множество.
     *
     * @return true, если множество пусто
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Очищает множество, удаляя все элементы.
     */
    public void clear() {
        elements.clear();
        indexes.clear();
    }

    /**
     * Возвращает немодифицируемый список элементов множества.
     * Полезно для перебора элементов.
     *
     * @return немодифицируемый список элементов
     */
    public List<T> asList() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * Перемешивает элементы множества в случайном порядке.
     *
     * @param random генератор случайных чисел
     */
    public void shuffle(@NotNull Random random) {
        SimplePreconditions.checkNotNull(random, "random", "shuffle");
        Collections.shuffle(elements, random);
        // пересоздаем индексы после shuffle
        for (int i = 0; i < elements.size(); i++) {
            indexes.put(elements.get(i), i);
        }
    }

    /**
     * Возвращает итератор по элементам множества.
     * Порядок перебора соответствует текущему порядку элементов.
     *
     * @return итератор по элементам множества
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return asList().iterator();
    }

    /**
     * Проверяет внутреннюю консистентность структуры данных.
     * Полезно для отладки и тестирования.
     *
     * @return true, если структура данных внутренне консистентна
     */
    boolean isInternallyConsistent() {
        if (elements.size() != indexes.size()) {
            return false;
        }
        for (int i = 0; i < elements.size(); i++) {
            T element = elements.get(i);
            final Integer index = indexes.get(element);
            if (index == null || index != i) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "IndexedHashSet{" + "elements=" + elements + ", indexes=" + indexes + '}';
    }
}