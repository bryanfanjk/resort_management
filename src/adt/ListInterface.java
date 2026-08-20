package adt;

/**
 * Custom List ADT.
 * This is not the Java Collections Framework.
 * It supports insertion/removal in the middle, which a queue cannot do.
 */
public interface ListInterface<T> {

    boolean add(T item);

    boolean add(int index, T item);

    T get(int index);

    T set(int index, T item);

    T remove(int index);

    boolean removeItem(T item);

    int indexOf(T item);

    boolean contains(T item);

    boolean isEmpty();

    int size();

    void clear();
}