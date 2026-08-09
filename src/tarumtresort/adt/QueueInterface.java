package tarumtresort.adt;

/**
 * An interface for the ADT Queue.
 * 
 * @author Admin
 * @param <T> The type of elements in the queue.
 */
public interface QueueInterface<T> {

    /**
     * Adds a new entry to the back of this queue.
     * 
     * @param newEntry An object to be added.
     */
    void enqueue(T newEntry);

    /**
     * Removes and returns the entry at the front of this queue.
     * 
     * @return The object at the front of the queue, or null if the queue is empty.
     */
    T dequeue();

    /**
     * Retrieves the entry at the front of this queue.
     * 
     * @return The object at the front of the queue, or null if the queue is empty.
     */
    T getFront();

    /**
     * Sees whether this queue is empty.
     * 
     * @return true if the queue is empty, or false if not.
     */
    boolean isEmpty();

    /**
     * Removes all entries from this queue.
     */
    void clear();
}
