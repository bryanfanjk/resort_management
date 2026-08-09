package tarumtresort.adt;

/**
 * An array-based implementation of the ADT List.
 * This class automatically resizes its array when capacity is reached.
 * 
 * @author Admin
 * @param <T> The type of elements in the list.
 */
public class ArrayList<T> implements ListInterface<T> {

    private T[] list;
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 25;
    private static final int MAX_CAPACITY = 10000;

    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public ArrayList(int initialCapacity) {
        if (initialCapacity < DEFAULT_CAPACITY) {
            initialCapacity = DEFAULT_CAPACITY;
        } else if (initialCapacity > MAX_CAPACITY) {
            throw new IllegalStateException("Attempt to create a list whose capacity exceeds allowed maximum.");
        }

        list = (T[]) new Object[initialCapacity];
        numberOfEntries = 0;
    }

    @Override
    public void add(T newEntry) {
        ensureCapacity();
        list[numberOfEntries] = newEntry;
        numberOfEntries++;
    }

    @Override
    public boolean add(int newPosition, T newEntry) {
        if (newPosition >= 1 && newPosition <= numberOfEntries + 1) {
            ensureCapacity();
            makeRoom(newPosition);
            list[newPosition - 1] = newEntry;
            numberOfEntries++;
            return true;
        }
        return false;
    }

    @Override
    public T remove(int givenPosition) {
        if (givenPosition >= 1 && givenPosition <= numberOfEntries) {
            T result = list[givenPosition - 1];
            if (givenPosition < numberOfEntries) {
                removeGap(givenPosition);
            }
            list[numberOfEntries - 1] = null;
            numberOfEntries--;
            return result;
        }
        return null;
    }

    @Override
    public void clear() {
        for (int i = 0; i < numberOfEntries; i++) {
            list[i] = null;
        }
        numberOfEntries = 0;
    }

    @Override
    public T replace(int givenPosition, T newEntry) {
        if (givenPosition >= 1 && givenPosition <= numberOfEntries) {
            T original = list[givenPosition - 1];
            list[givenPosition - 1] = newEntry;
            return original;
        }
        return null;
    }

    @Override
    public T getEntry(int givenPosition) {
        if (givenPosition >= 1 && givenPosition <= numberOfEntries) {
            return list[givenPosition - 1];
        }
        return null;
    }

    @Override
    public boolean contains(T anEntry) {
        boolean found = false;
        int index = 0;
        while (!found && (index < numberOfEntries)) {
            if (anEntry.equals(list[index])) {
                found = true;
            }
            index++;
        }
        return found;
    }

    @Override
    public int getLength() {
        return numberOfEntries;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public boolean isFull() {
        return false; // Dynamic array is never permanently full
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        int capacity = list.length;
        if (numberOfEntries >= capacity) {
            int newCapacity = 2 * capacity;
            if (newCapacity > MAX_CAPACITY) {
                throw new IllegalStateException("List capacity exceeds allowed maximum.");
            }
            T[] oldList = list;
            list = (T[]) new Object[newCapacity];
            System.arraycopy(oldList, 0, list, 0, numberOfEntries);
        }
    }

    private void makeRoom(int newPosition) {
        int newIndex = newPosition - 1;
        int lastIndex = numberOfEntries - 1;
        for (int index = lastIndex; index >= newIndex; index--) {
            list[index + 1] = list[index];
        }
    }

    private void removeGap(int givenPosition) {
        int removedIndex = givenPosition - 1;
        int lastIndex = numberOfEntries - 1;
        for (int index = removedIndex; index < lastIndex; index++) {
            list[index] = list[index + 1];
        }
    }
}
