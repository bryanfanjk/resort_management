package tarumtresort.adt;

/**
 * An interface for the ADT List.
 * Entries in a list have positions that begin with 1.
 * 
 * @author Admin
 * @param <T> The type of elements in the list.
 */
public interface ListInterface<T> {

    /**
     * Adds a new entry to the end of this list.
     * Entries currently in the list are unaffected.
     * The list's size is increased by 1.
     * 
     * @param newEntry The object to be added as a new entry.
     */
    void add(T newEntry);

    /**
     * Adds a new entry at a specified position within this list.
     * Entries originally at and above the specified position are moved to the next higher position.
     * The list's size is increased by 1.
     * 
     * @param newPosition An integer that specifies the desired position of the new entry.
     *                    Precondition: 1 <= newPosition <= getLength() + 1.
     * @param newEntry The object to be added as a new entry.
     * @return true if the addition is successful, or false if not.
     */
    boolean add(int newPosition, T newEntry);

    /**
     * Removes the entry at a given position from this list.
     * Entries originally at positions higher than the given position are moved to the next lower position.
     * The list's size is decreased by 1.
     * 
     * @param givenPosition An integer that specifies the position of the entry to be removed.
     *                      Precondition: 1 <= givenPosition <= getLength().
     * @return The removed entry if successful, or null if position is invalid.
     */
    T remove(int givenPosition);

    /**
     * Removes all entries from this list.
     */
    void clear();

    /**
     * Replaces the entry at a given position in this list.
     * 
     * @param givenPosition An integer that specifies the position of the entry to be replaced.
     *                      Precondition: 1 <= givenPosition <= getLength().
     * @param newEntry The object to replace the old entry.
     * @return The original entry that was replaced.
     */
    T replace(int givenPosition, T newEntry);

    /**
     * Retrieves the entry at a given position in this list.
     * 
     * @param givenPosition An integer that specifies the position of the desired entry.
     *                      Precondition: 1 <= givenPosition <= getLength().
     * @return A reference to the indicated entry, or null if position is invalid.
     */
    T getEntry(int givenPosition);

    /**
     * Sees whether this list contains a given entry.
     * 
     * @param anEntry The object that is the desired entry.
     * @return true if the list contains anEntry, or false if not.
     */
    boolean contains(T anEntry);

    /**
     * Gets the number of entries currently in this list.
     * 
     * @return The integer number of entries currently in the list.
     */
    int getLength();

    /**
     * Sees whether this list is empty.
     * 
     * @return true if the list is empty, or false if not.
     */
    boolean isEmpty();

    /**
     * Sees whether this list is full.
     * 
     * @return true if the list is full, or false if not.
     */
    boolean isFull();
}
