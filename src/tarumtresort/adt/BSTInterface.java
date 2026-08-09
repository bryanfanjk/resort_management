package tarumtresort.adt;

/**
 * An interface for the Binary Search Tree (BST) mapping keys to values.
 * Represents a non-linear data structure suitable for fast hierarchical operations 
 * and log-time searching.
 * 
 * @author Admin
 * @param <K> The type of keys, which must implement Comparable.
 * @param <V> The type of values.
 */
public interface BSTInterface<K extends Comparable<K>, V> {

    /**
     * Inserts a key-value mapping into the tree.
     * If the key already exists, updates the value.
     * 
     * @param key The key to map. Precondition: key is not null.
     * @param value The value mapped to the key.
     */
    void insert(K key, V value);

    /**
     * Searches for the value mapped to a specific key.
     * 
     * @param key The search key.
     * @return The mapped value, or null if key does not exist.
     */
    V search(K key);

    /**
     * Sees whether the tree contains a mapping for a specific key.
     * 
     * @param key The search key.
     * @return true if the key is found, or false if not.
     */
    boolean contains(K key);

    /**
     * Removes the mapping for a key from the tree.
     * 
     * @param key The key to remove.
     * @return The removed value, or null if the key was not found.
     */
    V delete(K key);

    /**
     * Sees whether this tree is empty.
     * 
     * @return true if empty, or false if not.
     */
    boolean isEmpty();

    /**
     * Gets the total number of nodes in this tree.
     * 
     * @return Number of nodes.
     */
    int getSize();

    /**
     * Removes all nodes from the tree.
     */
    void clear();

    /**
     * Retrieves all values in in-order traversal sequence.
     * 
     * @return List of all values sorted by key.
     */
    ListInterface<V> getInOrderValues();

    /**
     * Non-trivial original operation: Retrieves all values whose keys fall within
     * the closed range [startKey, endKey] in-order.
     * 
     * @param startKey The lower bound key.
     * @param endKey The upper bound key.
     * @return List of values in the key range.
     */
    ListInterface<V> getInOrderRange(K startKey, K endKey);
}
