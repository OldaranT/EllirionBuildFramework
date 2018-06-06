package com.ellirion.buildframework.util;

import java.util.Arrays;
import java.util.HashMap;

public class Heap<T extends Comparable> {

    private T[] arr;
    private HashMap<T, Integer> indices;
    private int length;
    private boolean min;

    /**
     * Create a min arr of type T.
     */
    public Heap() {
        arr = (T[]) new Comparable[10];
        indices = new HashMap<>();
        length = 0;
        min = true;
    }

    /**
     * Create a min arr from an array of elements.
     * @param elements the array of elements to create a arr of
     */
    public Heap(final T[] elements) {
        arr = elements;
        indices = new HashMap<>();
        length = elements.length;
        min = true;
        heapify();
    }

    /**
     * Add an element to the arr.
     * @param element the element to add to the arr
     */
    public void add(T element) {
        if (arr.length == length) {
            resize();
        }

        arr[length] = element;
        indices.put(element, length);
        bubbleUp(length);
        length++;
    }

    /**
     * Take the top element of the arr.
     * @return the top element of the arr
     */
    public T next() {
        T data = peek();

        swap(0, length - 1);
        arr[length - 1] = null;
        indices.remove(data);
        length--;

        bubbleDown(0);

        return data;
    }

    /**
     * Look at the top element of the arr.
     * @return the top element of the arr
     */
    public T peek() {
        if (length == 0) {
            throw new IllegalStateException();
        }

        return arr[0];
    }

    /**
     * Get the depth of the arr.
     * @return the depth of the arr
     */
    public int depth() {
        int length = this.length;
        int depth = 0;
        while (length > 0) {
            depth++;
            length = length >> 1;
        }

        return depth;
    }

    /**
     * Set the arr to be min or max arr.
     * @param min whether the arr will be a min arr
     */
    public void setMinHeap(boolean min) {
        this.min = min;
        heapify();
    }

    /**
     * Remove a specific element from the arr.
     * @param data the element to remove from the arr
     * @return Whether the remove was successful
     */
    public boolean remove(T data) {
        if (!indices.containsKey(data)) {
            return false;
        }
        // Swap element to the end of the arr
        // remove element from the arr

        int index = indices.get(data);
        swap(index, length - 1);
        bubbleDown(index);

        arr[length - 1] = null;
        length--;
        indices.remove(data);

        return true;
    }

    /**
     * Get element at specific index.
     * @param index the index of the element to get
     * @return the element at index {@code index}
     */
    public T get(int index) {
        return arr[index];
    }

    /**
     * Checks whether {@code data} is stored in the heap.
     * @param data the element whose membership to check
     * @return whether the data is contained in this heap
     */
    public boolean contains(T data) {
        return indices.containsKey(data);
    }

    /**
     * Get the length of the arr.
     * @return the length of the arr
     */
    public int getLength() {
        return length;
    }

    /**
     * Check if the heap is empty.
     * @return whether the heap is empty
     */
    public boolean isEmpty() {
        return length != 0;
    }

    private void resize() {
        if (length == Integer.MAX_VALUE) {
            throw new IllegalStateException("Heap cannot be larger than Integer.MAX_VALUE elements");
        }

        int newLength = length * 2;

        if (newLength < 0) {
            newLength = Integer.MAX_VALUE;
        }

        arr = Arrays.copyOf(arr, newLength);
    }

    private void heapify() {
        // Start at last parent node (length / 2)
        // Go backwards along the arr
        // And bubble down each element
        int i = length / 2;
        while (i > 0) {
            bubbleDown(i);
            i--;
        }
        bubbleDown(0);
    }

    private void bubbleUp(int i) {
        if (i == 0) {
            // Stop bubbling up, since we've reached the root
            return;
        }

        if (min) {
            // If current is smaller than parent
            // Swap current and parent
            // BubbleUp parent
            if (arr[i].compareTo(arr[i / 2]) < 0) {
                swap(i, i / 2);
                bubbleUp(i / 2);
            }
        } else {
            // If current is greater than parent
            // Swapcurrent and parent
            // BubbleUp parent
            if (arr[i].compareTo(arr[i / 2]) > 0) {
                swap(i, i / 2);
                bubbleUp(i / 2);
            }
        }
    }

    private void bubbleDown(int i) {
        if (i * 2 >= length) {
            return;
        }

        if (arr[i * 2] != null) {
            if (arr[i * 2 + 1] != null) {
                // Depending on min, swap with one of the two children
                if (min) {
                    // Swap with child if child is lower
                    int lowest = (arr[i * 2].compareTo(arr[i * 2 + 1]) < 0) ? i * 2 : i * 2 + 1;
                    if (arr[i].compareTo(arr[lowest]) > 0) {
                        swap(i, lowest);
                        bubbleDown(lowest);
                    }
                } else {
                    // Swap with child if child is greater
                    int highest = (arr[i * 2].compareTo(arr[i * 2 + 1]) > 0) ? i * 2 : i * 2 + 1;
                    if (arr[i].compareTo(arr[highest]) < 0) {
                        swap(i, highest);
                        bubbleDown(highest);
                    }
                }
            }

            // Depending on min, swap with only child
            if (min) {
                // Swap with child if child is lower
                if (arr[i].compareTo(arr[i * 2]) > 0) {
                    swap(i, i * 2);
                    bubbleDown(i * 2);
                }
            } else {
                // Swap with child if child is greater
                if (arr[i].compareTo(arr[i * 2]) < 0) {
                    swap(i, i * 2);
                    bubbleDown(i * 2);
                }
            }
        }

        // No children means we're done bubbling down
    }

    private void swap(int a, int b) {
        T temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;

        indices.put(arr[a], a);
        indices.put(arr[b], b);
    }

    /**
     * Gets the index of a certain element within this Heap.
     * @param element The element to find the index of
     * @return The index, or -1 if it is not found
     */
    public int indexOf(T element) {
        return indices.getOrDefault(element, -1);
    }
}
