package tarumtresort.adt;

/**
 * An interface for the ADT Stack.
 * 
 * @author Admin
 * @param <T> The type of elements in the stack.
 */
public interface StackInterface<T> {

    /**
     * Adds a new entry to the top of this stack.
     * 
     * @param newEntry An object to be added.
     */
    void push(T newEntry);

    /**
     * Removes and returns this stack's top entry.
     * 
     * @return The object at the top of the stack, or null if the stack is empty.
     */
    T pop();

    /**
     * Retrieves this stack's top entry without removing it.
     * 
     * @return The object at the top of the stack, or null if empty.
     */
    T peek();

    /**
     * Sees whether this stack is empty.
     * 
     * @return true if the stack is empty, or false if not.
     */
    boolean isEmpty();

    /**
     * Removes all entries from this stack.
     */
    void clear();
}
