package adt;

/** Operations provided by the project's array-based list ADT. */
public interface ListInterface<T> {

    boolean add(T item);

    boolean add(int index, T item);

    T get(int index);
    
    T set(int index, T item);

    T remove(int index);

    boolean contains(T item);

    int size();

    boolean isEmpty();
}
