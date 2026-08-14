package adt;

import java.util.Iterator;

/**
 * Author: <Your Name Here>
 *
 * PriorityQueueADT specifies the standard behaviour of a priority queue
 * collection ADT. A priority queue is a NON-LINEAR Abstract Data Type:
 * conceptually, its elements are organised as a complete binary tree (a
 * heap) connected by parent/child relationships, rather than a simple
 * front-to-back sequence like a list or a standard queue.
 *
 * The element with the highest priority is always retrievable in
 * constant time from the "front" of the structure, and the structure
 * automatically reorganises itself on every insertion and removal so
 * that this property always holds.
 *
 * This interface lists ALL standard priority queue operations for
 * completeness/reusability, even though not every operation is
 * necessarily exercised by every client class.
 *
 * @param <T> the element type, which must be Comparable so that
 *            relative priority between two elements can be determined
 */
public interface PriorityQueue<T extends Comparable<T>> {

    /**
     * Inserts an element into the priority queue. The structure
     * reorganises itself so the heap property is maintained.
     */
    void insert(T element);

    /**
     * Removes and returns the highest-priority element.
     * @throws EmptyCollectionException if the priority queue is empty
     */
    T removeHighestPriority() throws EmptyCollectionException;

    /**
     * Returns (without removing) the highest-priority element.
     * @throws EmptyCollectionException if the priority queue is empty
     */
    T peekHighestPriority() throws EmptyCollectionException;

    /** @return true if the priority queue contains no elements */
    boolean isEmpty();

    /** @return true if the priority queue cannot accept more elements */
    boolean isFull();

    /** @return the number of elements currently stored */
    int size();

    /** Removes all elements from the priority queue. */
    void clear();

    /** @return true if an element equal to target exists in the queue */
    boolean contains(T target);

    /**
     * Removes a specific element (matched via equals()), wherever it
     * happens to sit in the heap, re-balancing the structure afterwards.
     * @return true if the element was found and removed
     */
    boolean remove(T target);

    /**
     * @return an iterator over the elements in internal heap-array
     * order (i.e. satisfying the heap property, NOT a fully sorted
     * priority order).
     */
    Iterator<T> getIterator();
}