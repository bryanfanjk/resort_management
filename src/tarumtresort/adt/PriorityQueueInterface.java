package tarumtresort.adt;

/**
 * An interface for the ADT Priority Queue.
 * Keeps items sorted according to their natural ordering (max-priority at the front).
 * 
 * @author Admin
 * @param <T> The type of elements in the priority queue, which must implement Comparable.
 */
public interface PriorityQueueInterface<T extends Comparable<T>> {

    /**
     * Adds a new entry to this priority queue.
     * The priority queue is reorganized automatically.
     * 
     * @param newEntry An object to be added.
     */
    void enqueue(T newEntry);

    /**
     * Removes and returns the entry with the highest priority.
     * 
     * @return The object with the highest priority, or null if empty.
     */
    T dequeue();

    /**
     * Retrieves the entry with the highest priority without removing it.
     * 
     * @return The object with the highest priority, or null if empty.
     */
    T peek();

    /**
     * Sees whether this priority queue is empty.
     * 
     * @return true if the priority queue is empty, or false if not.
     */
    boolean isEmpty();

    /**
     * Gets the number of entries currently in this priority queue.
     * 
     * @return The integer number of entries.
     */
    int getSize();

    /**
     * Removes all entries from this priority queue.
     */
    void clear();
}
