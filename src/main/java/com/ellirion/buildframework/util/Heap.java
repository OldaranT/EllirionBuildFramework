package com.ellirion.buildframework.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Heap<TData, TScore extends Comparable<TScore>> {

    private Node<TData, TScore>[] arr;
    private Function<TData, TScore> scorer;
    private Map<TData, Integer> indices;
    private int size;

    /**
     * Construct a new heap.
     * @param scorer The scoring function to use
     */
    public Heap(final Function<TData, TScore> scorer) {
        this.arr = (Node<TData, TScore>[]) new Node[8];
        this.scorer = scorer;
        this.indices = new HashMap<>();
        this.size = 0;
    }

    /**
     * Insert an element into the heap.
     * @param data The element
     */
    public void insert(final TData data) {
        if (size == arr.length - 1) {
            grow();
        }

        // Initially we assume the new element will be placed at
        // the end of the array (at the bottom of the binary tree).
        int index = ++size;
        Node<TData, TScore> node = new Node<>(data, scorer.apply(data));
        arr[index] = node;
        indices.put(data, index);

        // We insert the item and move the item upwards as many times as necessary.
        percolateUp(node, index);
    }

    /**
     * Remove the top element of this heap.
     * @return The removed element
     */
    public TData next() {

        // Don't do anything if there are no elements!
        if (size == 0) {
            return null;
        }

        // Extract the result first and foremost
        Node<TData, TScore> node = arr[1];
        indices.remove(node.data);
        arr[1] = arr[size];
        arr[size--] = null;

        if (arr[1] != null) {
            indices.put(arr[1].data, 1);
        }

        // Shrink the array if necessary
        //        if (size < arr.length / 2 && size > 8) {
        //            shrink();
        //         }

        // Percolate down from the new top element
        percolateDown(1);

        // Shrink the array if necessary
        return node.data;
    }

    /**
     * Remove the given element from this heap. If it was not present, nothing happens.
     * @param data The element to remove
     * @return Whether the element was removed
     */
    public boolean remove(final TData data) {
        if (!indices.containsKey(data)) {
            return false;
        }

        int index = indices.get(data);
        indices.remove(data);

        // Remove it correctly if it was the last element
        if (index == size) {
            arr[size--] = null;
            return true;
        }

        // Otherwise, we fill the empty space with the last item in the array
        // and percolate down from there.
        arr[index] = arr[size];
        arr[size--] = null;
        indices.put(arr[index].data, index);
        percolateDown(index);

        return true;
    }

    /**
     * Gets the element at index {@code i} in the array.
     * @param i The index to get the element from
     * @return The element at the given index
     */
    public TData get(final int i) {
        return arr[i].data;
    }

    /**
     * Gets the index of data {@code data} in the internal array.
     * @param data The data to get the index of
     * @return The index of the data in the array, or -1 if the
     *         data is not contained in this heap.
     */
    public int indexOf(final TData data) {
        return indices.getOrDefault(data, -1);
    }

    /**
     * Checks whether data {@code data} is stored in this heap.
     * @param data The data whose membership to check
     * @return Whether the data is contained in this heap
     */
    public boolean contains(final TData data) {
        return indexOf(data) != -1;
    }

    /**
     * Checks whether data {@code data} is stored in this heap.
     * @param data The data whose membership to check
     * @return Whether the data is contained in this heap
     */
    public boolean arrayContains(final TData data) {
        int i = indexOf(data);
        if (i == -1) {
            return false;
        }
        return arr[i].data.equals(data);
    }

    private int percolateUp(Node<TData, TScore> node, int cur) {
        // Loop as long as we haven't reached the top element (cur > 1),
        // and as long as the parent element is GREATER than the child element.
        // In this loop we move the PARENT to the CHILD element. Only AFTER
        // the loop as finished do we fill the last spot: the last PARENT.
        for (; cur > 1 && node.score.compareTo(arr[cur / 2].score) < 0; cur /= 2) {
            arr[cur] = arr[cur / 2];
            indices.put(arr[cur].data, cur);
        }

        // Lastly, we insert the new item in the resulting cur.
        arr[cur] = node;
        indices.put(arr[cur].data, cur);
        return cur;
    }

    private void percolateDown(int cur) {
        Node<TData, TScore> temp;
        int left, right, diff, lower;

        // As long as at least the left child of the current item
        // is in range, we can keep percolating down.
        while (size >= cur * 2) {

            // Grab the compare scores of the left and the right child.
            // Note that if the right child does not exist, we just
            // say that the right child is greater than the parent.
            left = arr[cur].score.compareTo(arr[cur * 2].score);
            right = (size >= cur * 2 + 1)
                    ? arr[cur].score.compareTo(arr[cur * 2 + 1].score)
                    : -1;

            // Both left and right are greater than or equal to the current item.
            // This means we've put this item in the correct location.
            if (left <= 0 && right <= 0) {
                break;
            }

            // In the event that one or two children are smaller than the
            // parent, we need to pick whichever is smallest.
            diff = (size >= cur * 2 + 1)
                   ? arr[cur * 2].score.compareTo(arr[cur * 2 + 1].score)
                   : -1;

            // Swap cur with the lower child, or the left one if they are equal
            lower = diff <= 0 ? cur * 2 : cur * 2 + 1;
            temp = arr[cur];
            arr[cur] = arr[lower];
            arr[lower] = temp;
            indices.put(arr[cur].data, cur);
            indices.put(arr[lower].data, lower);

            // Continue percolating down as the lower child
            cur = lower;
        }
    }

    private void grow() {
        arr = Arrays.copyOf(arr, arr.length * 2);
    }

    /**
     * Get the amount of elements in the heap.
     * @return The size of the heap
     */
    public int getSize() {
        return size;
    }

    private class Node<TData, TScore> {

        private TData data;
        private TScore score;

        Node(final TData data, final TScore score) {
            this.data = data;
            this.score = score;
        }

        @Override
        public String toString() {
            return this.score.toString();
        }
    }
}
