package adt;

/**
 * Author: <Your Name Here>
 *
 * Thrown when an operation that requires at least one element (such as
 * removing or peeking the highest-priority element) is attempted on an
 * empty collection ADT. Declared as a checked exception so that callers
 * (the control layer) are forced to explicitly handle the empty case
 * rather than risk a silent null or an unchecked crash.
 */
public class EmptyCollectionException extends Exception {

    public EmptyCollectionException(String collectionName) {
        super(collectionName + " is empty.");
    }
}