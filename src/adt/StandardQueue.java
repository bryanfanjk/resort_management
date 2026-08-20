package adt;

/**
 * Author: <Your Name Here>
 *
 * StandardQueue is a from-scratch, array-based FIFO queue for standard
 * (non-VIP) customers - Module 1's Linear ADT.
 *
 * Implemented as a CIRCULAR array (front index + count, wrapping around
 * with modulo) rather than shifting every remaining element down on
 * each dequeue. This keeps enqueue/dequeue/peek all O(1) - shifting the
 * whole array on every dequeue would make dequeue O(n) instead, which
 * defeats the point of a proper queue ADT.
 *
 * No java.util collection classes are used - array + manual doubling,
 * same growth strategy as ECBDemo's ArrayList.
 */
public class StandardQueue<T> implements QueueInterface<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private T[] queueArray;
    private int frontIndex;
    private int count;

    @SuppressWarnings("unchecked")
    public StandardQueue() {
        queueArray = (T[]) new Object[DEFAULT_CAPACITY];
        frontIndex = 0;
        count = 0;
    }

    @Override
    public boolean enqueue(T item) {
        if (item == null) {
            return false;
        }
        if (count == queueArray.length) {
            expandCapacity();
        }
        int rearIndex = (frontIndex + count) % queueArray.length;
        queueArray[rearIndex] = item;
        count++;
        return true;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T item = queueArray[frontIndex];
        queueArray[frontIndex] = null;
        frontIndex = (frontIndex + 1) % queueArray.length;
        count--;
        return item;
    }

    @Override
    public T peekFront() {
        if (isEmpty()) {
            return null;
        }
        return queueArray[frontIndex];
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean isFull() {
        // Dynamic array grows automatically (see expandCapacity), so this
        // never reports full. Kept for interface completeness.
        return false;
    }

    @Override
    public int size() {
        return count;
    }

    @SuppressWarnings("unchecked")
    private void expandCapacity() {
        T[] newArray = (T[]) new Object[queueArray.length * 2];
        // Copy elements out in logical FIFO order, starting fresh at
        // index 0 in the new array - this is what lets frontIndex reset
        // to 0 after resizing.
        for (int i = 0; i < count; i++) {
            newArray[i] = queueArray[(frontIndex + i) % queueArray.length];
        }
        queueArray = newArray;
        frontIndex = 0;
    }
}
