/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tarumtresort.adt;

public class List<T> implements ListInterface<T> {

    private T[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public List(int capacity) {
        data = (T[]) new Object[capacity];
        size = 0;
    }

    @Override
    public void add(T item) {

        if (size == data.length) {
            System.out.println("List is full.");
            return;
        }

        data[size] = item;
        size++;
    }

    @Override
    public T get(int index) {

        if (index < 0 || index >= size) {
            return null;
        }

        return data[index];
    }

    @Override
    public T remove(int index) {

        if (index < 0 || index >= size) {
            return null;
        }

        T removed = data[index];

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }

        data[size - 1] = null;
        size--;

        return removed;
    }

    @Override
    public boolean contains(T item) {

        for (int i = 0; i < size; i++) {

            if (data[i].equals(item)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }
}
