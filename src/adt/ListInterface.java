package adt;

/**
 * Author: <Your Name Here>
 *
 * QueueInterface specifies standard FIFO queue behaviour. Style matches
 * the ECBDemo reference project adopted for this project: operations
 * signal failure/empty via boolean or null returns, not custom
 * exceptions.
 *
 * @param <T> the element type stored
 */
public interface ListInterface<T> {

    /** Adds an item to the back of the queue. */
    boolean enqueue(T item);

    /** Removes and returns the item at the front of the queue, or null if empty. */
    T dequeue();

    /** Returns (without removing) the item at the front of the queue, or null if empty. */
    T peekFront();

    /** @return true if the queue contains no elements */
    boolean isEmpty();

    /** @return true if the queue cannot accept more elements (always false here - see StandardQueue/VipQueue) */
    boolean isFull();

    /** @return the number of elements currently in the queue */
    int size();
}
