package adt;

public class VipList<T> implements ListInterface<T> {

    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;

    public VipList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    @Override
    public boolean add(T item) {
        if (item == null) {
            return false;
        }
        ensureCapacity();
        elements[size] = item;
        size++;
        return true;
    }

    @Override
    public boolean add(int index, T item) {
        if (item == null || index < 0 || index > size) {
            return false;
        }

        ensureCapacity();

        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }

        elements[index] = item;
        size++;
        return true;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return (T) elements[index];
    }

    @Override
    public T set(int index, T item) {
        if (index < 0 || index >= size || item == null) {
            return null;
        }
        T oldValue = (T) elements[index];
        elements[index] = item;
        return oldValue;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        T removed = (T) elements[index];

        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        elements[size - 1] = null;
        size--;

        return removed;
    }

    @Override
    public boolean removeItem(T item) {
        int index = indexOf(item);
        if (index == -1) {
            return false;
        }
        remove(index);
        return true;
    }

    @Override
    public int indexOf(T item) {
        for (int i = 0; i < size; i++) {
            if (item.equals(elements[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(T item) {
        return indexOf(item) != -1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    private void ensureCapacity() {
        if (size < elements.length) {
            return;
        }

        Object[] newArray = new Object[elements.length * 2];
        for (int i = 0; i < elements.length; i++) {
            newArray[i] = elements[i];
        }
        elements = newArray;
    }
}