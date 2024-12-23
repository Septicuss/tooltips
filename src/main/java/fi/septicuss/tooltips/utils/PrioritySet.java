package fi.septicuss.tooltips.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PriorityQueue;

public class PrioritySet<T> implements Iterable<T> {

    private static class WeightedItem<T> implements Comparable<WeightedItem<T>> {
        T item;
        int weight;

        public WeightedItem(T item, int weight) {
            this.item = item;
            this.weight = weight;
        }

        @Override
        public int compareTo(WeightedItem<T> other) {
            return Integer.compare(other.weight, this.weight);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WeightedItem<?> that = (WeightedItem<?>) o;
            return item.equals(that.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item);
        }
    }

    private final PriorityQueue<WeightedItem<T>> maxHeap;
    private final Map<T, WeightedItem<T>> itemMap;

    public PrioritySet() {
        this.maxHeap = new PriorityQueue<>();
        this.itemMap = new HashMap<>();
    }

    public boolean add(T item, int weight) {
        if (itemMap.containsKey(item)) {
            return false;
        }
        WeightedItem<T> weightedItem = new WeightedItem<>(item, weight);
        maxHeap.add(weightedItem);
        itemMap.put(item, weightedItem);
        return true;
    }

    public boolean contains(T item) {
        return itemMap.containsKey(item);
    }

    public int size() {
        return maxHeap.size();
    }

    public boolean isEmpty() {
        return maxHeap.isEmpty();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        // Create a copy of the heap to iterate over, ensuring elements remain in the original structure
        PriorityQueue<WeightedItem<T>> copyHeap = new PriorityQueue<>(maxHeap);

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return !copyHeap.isEmpty();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return copyHeap.poll().item;
            }
        };
    }

    public List<T> toList() {
        // Returns a list of elements ordered by weight without altering the original structure
        List<T> result = new ArrayList<>();
        for (T item : this) {
            result.add(item);
        }
        return result;
    }
}