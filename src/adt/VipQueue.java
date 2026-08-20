package adt;

/**
 * Author: <Your Name Here>
 *
 * VipQueue is a from-scratch, array-based FIFO queue for VIP customers -
 * Module 2's Phase A structure.
 *
 * Deliberately a SEPARATE class from StandardQueue, not a subclass or a
 * shared instance, even though the logic is currently identical. This
 * is intentional: Phase B replaces this class's internals (queue -> a
 * tier-aware list) without touching StandardQueue or Module 1 at all.
 * If this were one shared class or an inheritance relationship, that
 * migration would risk disturbing Module 1's already-working code.
 *
 * Implementation notes are the same as StandardQueue - circular array
 * (front index + count), O(1) enqueue/dequeue/peek, manual doubling
 * on growth, no java.util collection classes.
 */
public class VipQueue<T> implements ListInterface<T> {

    private static final int DEFAULT_CAPACITY = 10;

    private T[] queueArray;
    private int frontIndex;
    private int count;

    @SuppressWarnings("unchecked")
    public VipQueue() {
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
        return false;
    }

    @Override
    public int size() {
        return count;
    }

    @SuppressWarnings("unchecked")
    private void expandCapacity() {
        T[] newArray = (T[]) new Object[queueArray.length * 2];
        for (int i = 0; i < count; i++) {
            newArray[i] = queueArray[(frontIndex + i) % queueArray.length];
        }
        queueArray = newArray;
        frontIndex = 0;
    }
}
