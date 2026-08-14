package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Author: <Your Name Here>
 *
 * MaxHeapPriorityQueue is a from-scratch implementation of
 * PriorityQueueADT using a binary MAX-HEAP stored inside a plain array
 * (java.util.ArrayList / PriorityQueue / any Java Collections Framework
 * class is deliberately NOT used, per assignment rules).
 *
 * A heap is logically a COMPLETE BINARY TREE: for the element stored at
 * array index i,
 *      - its left child lives at index  (2*i + 1)
 *      - its right child lives at index (2*i + 2)
 *      - its parent lives at index      ((i - 1) / 2)
 *
 * The MAX-HEAP PROPERTY guarantees every parent has priority >= both of
 * its children, which is what makes the highest-priority element always
 * sit at index 0 - retrievable in O(1) - and is exactly the behaviour
 * described in the spec ("...the guest profile with the highest tier is
 * always positioned at the front for immediate assignment").
 *
 * Two internal operations restore this property whenever it is
 * disturbed:
 *   - siftUp:   used after insert() - a new element starts at the end
 *               of the array and "bubbles up" past any parent with
 *               lower priority.
 *   - siftDown: used after removeHighestPriority()/remove() - whatever
 *               element ends up at the affected index "sinks down" past
 *               any child with higher priority.
 * Both run in O(log n) because the tree height is O(log n).
 */
public class MaxHeapPriorityQueue<T extends Comparable<T>> implements PriorityQueueADT<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private T[] heap;
    private int size;

    @SuppressWarnings("unchecked")
    public MaxHeapPriorityQueue() {
        heap = (T[]) new Comparable[DEFAULT_CAPACITY];
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public MaxHeapPriorityQueue(int initialCapacity) {
        heap = (T[]) new Comparable[initialCapacity];
        size = 0;
    }

    @Override
    public void insert(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot insert a null element.");
        }
        if (size == heap.length) {
            expandCapacity();
        }
        heap[size] = element;
        siftUp(size);
        size++;
    }

    @Override
    public T removeHighestPriority() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Priority Queue");
        }
        T highestPriority = heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return highestPriority;
    }

    @Override
    public T peekHighestPriority() throws EmptyCollectionException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Priority Queue");
        }
        return heap[0];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean isFull() {
        // The internal array grows automatically (see expandCapacity), so
        // from the client's perspective this priority queue never
        // reports full. The method is retained for ADT completeness and
        // interface consistency with other collection ADTs.
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        heap = (T[]) new Comparable[DEFAULT_CAPACITY];
        size = 0;
    }

    @Override
    public boolean contains(T target) {
        return indexOf(target) != -1;
    }

    @Override
    public boolean remove(T target) {
        int index = indexOf(target);
        if (index == -1) {
            return false;
        }
        size--;
        heap[index] = heap[size];
        heap[size] = null;
        if (index < size) {
            // The replacement value could have higher OR lower priority
            // than its new neighbours, so both directions must be
            // checked; only one of the two will actually move it.
            siftUp(index);
            siftDown(index);
        }
        return true;
    }

    @Override
    public Iterator<T> getIterator() {
        return new HeapIterator();
    }

    // ------------------------------------------------------------------
    // Private helper methods
    // ------------------------------------------------------------------

    private int indexOf(T target) {
        for (int i = 0; i < size; i++) {
            if (heap[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[index].compareTo(heap[parent]) > 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int largest = index;

            if (left < size && heap[left].compareTo(heap[largest]) > 0) {
                largest = left;
            }
            if (right < size && heap[right].compareTo(heap[largest]) > 0) {
                largest = right;
            }
            if (largest == index) {
                break;
            }
            swap(index, largest);
            index = largest;
        }
    }

    private void swap(int i, int j) {
        T temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    @SuppressWarnings("unchecked")
    private void expandCapacity() {
        T[] newHeap = (T[]) new Comparable[heap.length * 2];
        for (int i = 0; i < heap.length; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap;
    }

    // ------------------------------------------------------------------
    // Custom iterator (java.util.Iterator is an interface, not a
    // Collections Framework class, so it is allowed per the QNA)
    // ------------------------------------------------------------------

    /**
     * Iterates over the elements currently stored in the heap in
     * internal ARRAY order. IMPORTANT: this is the order that satisfies
     * the heap property (parent >= children); it is NOT a fully sorted,
     * front-to-back priority order. Whenever a fully sorted listing is
     * required (e.g. for reports), take a snapshot with this iterator
     * and sort the snapshot separately - see VIPAllocationManager.
     */
    private class HeapIterator implements Iterator<T> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return heap[currentIndex++];
        }
    }
}
