package com.ellirion.buildframework.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class HeapTest {

    @Test
    public void insert_whenEmpty_shouldInsert() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        heap.insert(1);
        assertEquals(1, heap.getSize());
    }

    @Test
    public void insert_whenNotEmpty_shouldInsertSorted() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);
        for (int i = 0; i < 10; i++) {
            heap.insert(i + 2);
        }
        heap.insert(1);

        assertEquals(1, (int) heap.get(1));
    }

    @Test
    public void next_whenEmpty_shouldReturnNull() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        assertNull(heap.next());
    }

    @Test
    public void next_whenHasOneElement_shouldRemoveElement() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);
        heap.insert(1);

        assertEquals(1, (int) heap.next());
    }

    @Test
    public void next_whenHasMultipleElements_shouldRemoveElementAndPercolate() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        for (int i = 0; i < 10; i++) {
            heap.insert(i);
        }

        heap.next();

        assertEquals(1, (int) heap.get(1));
        assertEquals(3, (int) heap.get(2));
        assertEquals(2, (int) heap.get(3));
        assertEquals(7, (int) heap.get(4));
        assertEquals(4, (int) heap.get(5));
        assertEquals(5, (int) heap.get(6));
        assertEquals(6, (int) heap.get(7));
        assertEquals(9, (int) heap.get(8));
        assertEquals(8, (int) heap.get(9));
        assertEquals(9, heap.getSize());
    }

    @Test
    public void next_whenHasManyElements_shouldNotThrowException() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        int j = 0;
        for (int i = 0; i < 64; i++) {
            while (heap.getSize() < i) {
                heap.insert(j++);
            }
            heap.next();
        }
    }

    @Test
    public void next_whenHasManyElements_shouldAlwaysReturnLowest() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);
        for (int i = 0; i < 512; i++) {
            heap.insert(512 - i);
        }

        int last = 0;
        int cur;
        while (heap.getSize() > 0) {
            cur = heap.next();
            assertTrue(cur >= last);
            last = cur;
        }
    }

    @Test
    public void remove_whenNotContained_shouldDoNothing() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);
        for (int i = 0; i < 10; i++) {
            heap.insert(i);
        }

        assertEquals(10, heap.getSize());
        assertFalse(heap.remove(11));
        assertEquals(10, heap.getSize());
    }

    @Test
    public void remove_whenContained_shouldRemove() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);
        for (int i = 0; i < 10; i++) {
            heap.insert(i);
        }

        assertEquals(10, heap.getSize());
        assertTrue(heap.remove(3));
        assertEquals(9, heap.getSize());
    }

    @Test
    public void getSize_whenEmpty_shouldReturnZero() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        assertEquals(0, heap.getSize());
    }

    @Test
    public void getSize_whenNotEmpty_shouldReturnSize() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        for (int i = 0; i < 10; i++) {
            heap.insert(i);
            assertEquals(i + 1, heap.getSize());
        }
    }
}
