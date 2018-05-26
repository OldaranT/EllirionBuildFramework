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
    public void insert_whenInMiddle_shouldPercolateCorrectly() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        // Begin with 4 layers
        for (int i = 0; i < Math.pow(2, 4) - 1; i++) {
            heap.insert(i * 2);
        }

        // Insert in layer 2, which should update
        // indices for other currently stored values.
        heap.insert(3);

        // Assert the resulting tree is as expected.
        assertEquals(2, (int) heap.get(2));
        assertEquals(4, (int) heap.get(3));
        assertEquals(3, (int) heap.get(4));
        assertEquals(8, (int) heap.get(5));
        assertEquals(10, (int) heap.get(6));
        assertEquals(12, (int) heap.get(7));
        assertEquals(6, (int) heap.get(8));
        assertEquals(16, (int) heap.get(9));
        assertEquals(18, (int) heap.get(10));
        assertEquals(20, (int) heap.get(11));
        assertEquals(22, (int) heap.get(12));
        assertEquals(24, (int) heap.get(13));
        assertEquals(26, (int) heap.get(14));
        assertEquals(28, (int) heap.get(15));
        assertEquals(14, (int) heap.get(16));
    }

    @Test
    public void insert_whenInMiddle1_shouldKeepHashmapCorrect() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        // Begin with 4 layers
        for (int i = 0; i < Math.pow(2, 4) - 1; i++) {
            heap.insert(i * 2);
        }

        // Insert in layer 2, which should update
        // indices for other currently stored values.
        heap.insert(3);

        // Assert that the indices in the internal hashmap are as expected.
        assertEquals(1, heap.indexOf(0));
        assertEquals(2, heap.indexOf(2));
        assertEquals(3, heap.indexOf(4));
        assertEquals(4, heap.indexOf(3));
        assertEquals(5, heap.indexOf(8));
        assertEquals(6, heap.indexOf(10));
        assertEquals(7, heap.indexOf(12));
        assertEquals(8, heap.indexOf(6));
        assertEquals(9, heap.indexOf(16));
        assertEquals(10, heap.indexOf(18));
        assertEquals(11, heap.indexOf(20));
        assertEquals(12, heap.indexOf(22));
        assertEquals(13, heap.indexOf(24));
        assertEquals(14, heap.indexOf(26));
        assertEquals(15, heap.indexOf(28));
        assertEquals(16, heap.indexOf(14));
    }

    @Test
    public void insert_whenInMiddle2_shouldKeepHashmapCorrect() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        int[] data = new int[] {
                18, 44, 37, 78, 82, 27, 0, 56, 71, 28, 10, 68, 2, 59, 13, 92, 83, 32, 72, 40, 3, 46, 99, 9, 61, 16, 8,
                36, 89, 91, 74, 93, 98, 22, 49, 39, 76, 1, 29, 94, 60, 20, 81, 21, 73, 62, 24, 15, 45, 64, 70, 69, 77,
                97, 51, 12, 88, 38, 95, 100, 63, 31, 14
        };

        for (int i : data) {
            heap.insert(i);
        }

        // Assert that the indices in the internal hashmap are as expected beforehand.

        // Layer 3
        assertEquals(8, heap.indexOf(32));

        // Layer 4
        assertEquals(16, heap.indexOf(92));
        assertEquals(17, heap.indexOf(49));

        // Layer 5
        assertEquals(32, heap.indexOf(93));
        assertEquals(33, heap.indexOf(98));
        assertEquals(34, heap.indexOf(83));
        assertEquals(35, heap.indexOf(78));

        // Layer 6 does not exist yet.

        // Insert somewhere in layer 3 which should update
        // indices for other currently stored values.
        heap.insert(31);

        // Assert that the indices in the internal hashmap are as expected afterwards.

        // Layer 3
        assertEquals(8, heap.indexOf(31));
        assertEquals(31, (int) heap.get(8));

        // Layer 4
        assertEquals(16, heap.indexOf(32));
        assertEquals(32, (int) heap.get(16));

        // Layer 5
        assertEquals(32, heap.indexOf(92));
        assertEquals(92, (int) heap.get(32));

        // Layer 6
        assertEquals(64, heap.indexOf(93));
        assertEquals(93, (int) heap.get(64));
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
        assertEquals(0, heap.getSize());
    }

    @Test
    public void next_whenHasHashCollisions_shouldAlwaysReturnLowest() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);
        for (int i = 0; i < 512; i++) {
            heap.insert(i / 2);
        }

        int last = 0;
        int cur;
        while (heap.getSize() > 0) {
            cur = heap.next();
            assertTrue(cur >= last);
            last = cur;
        }
        assertEquals(0, heap.getSize());
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
    public void remove_whenFromMiddle_shouldKeepHashmapCorrect() {
        Heap<Integer, Integer> heap = new Heap<>(a -> a);

        // Begin with 4 layers
        for (int i = 0; i < Math.pow(2, 4) - 1; i++) {
            heap.insert(i * 2);
        }

        // Remove from layer 2, which should update
        // indices for other currently stored values.
        heap.remove(2);

        // Assert that the indices in the internal hashmap are as expected.
        assertEquals(1, heap.indexOf(0));
        assertEquals(2, heap.indexOf(6));
        assertEquals(3, heap.indexOf(4));
        assertEquals(4, heap.indexOf(14));
        assertEquals(5, heap.indexOf(8));
        assertEquals(6, heap.indexOf(10));
        assertEquals(7, heap.indexOf(12));
        assertEquals(8, heap.indexOf(28));
        assertEquals(9, heap.indexOf(16));
        assertEquals(10, heap.indexOf(18));
        assertEquals(11, heap.indexOf(20));
        assertEquals(12, heap.indexOf(22));
        assertEquals(13, heap.indexOf(24));
        assertEquals(14, heap.indexOf(26));
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
