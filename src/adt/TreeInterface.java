package adt;

public interface TreeInterface<K extends Comparable<K>, V> {

    void insert(K key, V value);

    V search(K key);

    boolean contains(K key);

    int size();

    Object[] getAllValues();

    boolean isEmpty();
}
