package com.ellirion.buildframework.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class HeapTest {

    @Test
    public void insert_whenEmpty_shouldInsert() {
        Heap<Integer> heap = new Heap<>();

        heap.add(1);
        assertEquals(1, (int) heap.get(0));
    }

    @Test
    public void insert_whenNotEmpty_shouldInsertSorted() {
        Heap<Integer> heap = new Heap<>();
        for (int i = 0; i < 10; i++) {
            heap.add(i + 2);
        }
        heap.add(1);

        assertEquals(1, (int) heap.get(heap.indexOf(1)));
    }

    @Test
    public void insert_whenInMiddle_shouldPercolateCorrectly() {
        Heap<Integer> heap = new Heap<>();

        // Begin with 4 layers
        for (int i = 0; i < Math.pow(2, 4) - 1; i++) {
            heap.add(i * 2);
        }

        // Insert in layer 2, which should update
        // indices for other currently stored values.
        heap.add(3);

        // Assert the resulting tree is as expected.
        assertEquals(2, (int) heap.get(heap.indexOf(2)));
        assertEquals(4, (int) heap.get(heap.indexOf(4)));
        assertEquals(3, (int) heap.get(heap.indexOf(3)));
        assertEquals(8, (int) heap.get(heap.indexOf(8)));
        assertEquals(10, (int) heap.get(heap.indexOf(10)));
        assertEquals(12, (int) heap.get(heap.indexOf(12)));
        assertEquals(6, (int) heap.get(heap.indexOf(6)));
        assertEquals(16, (int) heap.get(heap.indexOf(16)));
        assertEquals(18, (int) heap.get(heap.indexOf(18)));
        assertEquals(20, (int) heap.get(heap.indexOf(20)));
        assertEquals(22, (int) heap.get(heap.indexOf(22)));
        assertEquals(24, (int) heap.get(heap.indexOf(24)));
        assertEquals(26, (int) heap.get(heap.indexOf(26)));
        assertEquals(28, (int) heap.get(heap.indexOf(28)));
        assertEquals(14, (int) heap.get(heap.indexOf(14)));
    }

    @Test
    public void insert_whenInMiddle1_shouldKeepHashmapCorrect() {
        Heap<Integer> heap = new Heap<>();

        // Begin with 4 layers
        for (int i = 0; i < Math.pow(2, 4) - 1; i++) {
            heap.add(i * 2);
        }

        // Insert in layer 2, which should update
        // indices for other currently stored values.
        heap.add(3);

        // Assert that the indices in the internal hashmap are as expected.
        assertEquals(0, (int) heap.get(heap.indexOf(0)));
        assertEquals(2, (int) heap.get(heap.indexOf(2)));
        assertEquals(3, (int) heap.get(heap.indexOf(3)));
        assertEquals(4, (int) heap.get(heap.indexOf(4)));
        assertEquals(6, (int) heap.get(heap.indexOf(6)));
        assertEquals(8, (int) heap.get(heap.indexOf(8)));
        assertEquals(10, (int) heap.get(heap.indexOf(10)));
        assertEquals(12, (int) heap.get(heap.indexOf(12)));
        assertEquals(14, (int) heap.get(heap.indexOf(14)));
        assertEquals(16, (int) heap.get(heap.indexOf(16)));
        assertEquals(18, (int) heap.get(heap.indexOf(18)));
        assertEquals(20, (int) heap.get(heap.indexOf(20)));
        assertEquals(22, (int) heap.get(heap.indexOf(22)));
        assertEquals(24, (int) heap.get(heap.indexOf(24)));
        assertEquals(26, (int) heap.get(heap.indexOf(26)));
        assertEquals(28, (int) heap.get(heap.indexOf(28)));
    }

    @Test
    public void insert_whenInMiddle2_shouldKeepHashmapCorrect() {
        Heap<Integer> heap = new Heap<>();

        int[] data = new int[] {
                18, 44, 37, 78, 82, 27, 0, 56, 71, 28, 10, 68, 2, 59, 13, 92, 83, 32, 72, 40, 3, 46, 99, 9, 61, 16, 8,
                36, 89, 91, 74, 93, 98, 22, 49, 39, 76, 1, 29, 94, 60, 20, 81, 21, 73, 62, 24, 15, 45, 64, 70, 69, 77,
                97, 51, 12, 88, 38, 95, 100, 63, 31, 14
        };

        for (int i : data) {
            heap.add(i);
        }

        // Assert that the indices in the internal hashmap are as expected beforehand.

        // Layer 3
        assertEquals(32, (int) heap.get(heap.indexOf(32)));

        // Layer 4
        assertEquals(92, (int) heap.get(heap.indexOf(92)));
        assertEquals(49, (int) heap.get(heap.indexOf(49)));

        // Layer 5
        assertEquals(93, (int) heap.get(heap.indexOf(93)));
        assertEquals(98, (int) heap.get(heap.indexOf(98)));
        assertEquals(83, (int) heap.get(heap.indexOf(83)));
        assertEquals(78, (int) heap.get(heap.indexOf(78)));

        // Layer 6 does not exist yet.

        // Insert somewhere in layer 3 which should update
        // indices for other currently stored values.
        heap.add(31);

        // Assert that the indices in the internal hashmap are as expected afterwards.

        // Layer 3
        assertEquals(31, (int) heap.get(heap.indexOf(31)));

        // Layer 4
        assertEquals(32, (int) heap.get(heap.indexOf(32)));

        // Layer 5
        assertEquals(92, (int) heap.get(heap.indexOf(92)));

        // Layer 6
        assertEquals(93, (int) heap.get(heap.indexOf(93)));
    }

    @Test(expected = IllegalStateException.class)
    public void next_whenEmpty_shouldReturnNull() {
        Heap<Integer> heap = new Heap<>();

        heap.next();
    }

    @Test
    public void next_whenHasOneElement_shouldRemoveElement() {
        Heap<Integer> heap = new Heap<>();
        heap.add(1);

        assertEquals(1, (int) heap.next());
    }

    @Test
    public void next_whenHasMultipleElements_shouldRemoveElementAndPercolate() {
        Heap<Integer> heap = new Heap<>();

        for (int i = 0; i < 10; i++) {
            heap.add(i);
        }

        heap.next();

        assertEquals(1, (int) heap.get(0));
        assertEquals(2, (int) heap.get(1));
        assertEquals(4, (int) heap.get(2));
        assertEquals(3, (int) heap.get(3));
        assertEquals(8, (int) heap.get(4));
        assertEquals(5, (int) heap.get(5));
        assertEquals(6, (int) heap.get(6));
        assertEquals(7, (int) heap.get(7));
        assertEquals(9, (int) heap.get(8));
        assertEquals(null, heap.get(9));
    }

    @Test
    public void next_whenHasManyElements_shouldNotThrowException() {
        Heap<Integer> heap = new Heap<>();

        int j = 0;
        for (int i = 1; i < 64; i++) {
            while (heap.getLength() < i) {
                heap.add(j++);
            }
            heap.next();
        }
    }

    @Test
    public void next_whenHasManyElements_shouldAlwaysReturnLowest() {
        Heap<Integer> heap = new Heap<>();
        for (int i = 0; i < 512; i++) {
            heap.add(512 - i);
        }

        int last = 0;
        int cur;
        while (!heap.isEmpty()) {
            cur = heap.next();
            assertTrue(cur >= last);
            last = cur;
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    public void next_whenHasHashCollisions_shouldAlwaysReturnLowest() {
        Heap<Integer> heap = new Heap<>();
        for (int i = 0; i < 512; i++) {
            heap.add(i / 2);
        }

        int last = 0;
        int cur;
        while (!heap.isEmpty()) {
            cur = heap.next();
            assertTrue(cur >= last);
            last = cur;
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    public void remove_whenNotContained_shouldDoNothing() {
        Heap<Integer> heap = new Heap<>();
        for (int i = 0; i < 10; i++) {
            heap.add(i);
        }

        assertEquals(10, heap.getLength());
        assertFalse(heap.remove(11));
        assertEquals(10, heap.getLength());
    }

    @Test
    public void remove_whenContained_shouldRemove() {
        Heap<Integer> heap = new Heap<>();
        for (int i = 0; i < 10; i++) {
            heap.add(i);
        }

        assertEquals(10, heap.getLength());
        assertTrue(heap.remove(3));
        assertEquals(9, heap.getLength());
    }

    @Test
    public void remove_whenFromMiddle_shouldKeepHashmapCorrect() {
        Heap<Integer> heap = new Heap<>();

        // Begin with 4 layers
        for (int i = 0; i < Math.pow(2, 4) - 1; i++) {
            heap.add(i * 2);
        }

        // Remove from layer 2, which should update
        // indices for other currently stored values.
        heap.remove(2);

        // Assert that the indices in the internal hashmap are as expected.
        assertEquals(0, (int) heap.get(heap.indexOf(0)));
        assertEquals(4, (int) heap.get(heap.indexOf(4)));
        assertEquals(6, (int) heap.get(heap.indexOf(6)));
        assertEquals(8, (int) heap.get(heap.indexOf(8)));
        assertEquals(10, (int) heap.get(heap.indexOf(10)));
        assertEquals(12, (int) heap.get(heap.indexOf(12)));
        assertEquals(14, (int) heap.get(heap.indexOf(14)));
        assertEquals(16, (int) heap.get(heap.indexOf(16)));
        assertEquals(18, (int) heap.get(heap.indexOf(18)));
        assertEquals(20, (int) heap.get(heap.indexOf(20)));
        assertEquals(22, (int) heap.get(heap.indexOf(22)));
        assertEquals(24, (int) heap.get(heap.indexOf(24)));
        assertEquals(26, (int) heap.get(heap.indexOf(26)));
        assertEquals(28, (int) heap.get(heap.indexOf(28)));
    }

    @Test
    public void getSize_whenEmpty_shouldReturnZero() {
        Heap<Integer> heap = new Heap<>();

        assertEquals(0, heap.getLength());
    }

    @Test
    public void getSize_whenNotEmpty_shouldReturnSize() {
        Heap<Integer> heap = new Heap<>();

        for (int i = 0; i < 10; i++) {
            heap.add(i);
            assertEquals(i + 1, heap.getLength());
        }
    }
}
