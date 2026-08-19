package tarumtresort.adt;

/** Operations provided by the project's array-based list ADT. */
public interface ListInterface<T> {

    void add(T item);

    T get(int index);

    T remove(int index);

    boolean contains(T item);

    int size();

    boolean isEmpty();
}
