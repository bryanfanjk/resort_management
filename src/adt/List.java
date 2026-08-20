/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adt;

public class List<T> implements ListInterface<T> {

    private T[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public List(int capacity) {
        data = (T[]) new Object[capacity];
        size = 0;
    }


    @Override
    public boolean add(T item) {
        if (item == null || size == data.length) {
            return false;
        }

        data[size] = item;
        size++;
        return true;
    }

    @Override
    public boolean add(int index, T item) {
        if (item == null
                || index < 0
                || index > size
                || size == data.length) {
            return false;
        }

        for (int currentIndex = size;
             currentIndex > index;
             currentIndex--) {
            data[currentIndex] = data[currentIndex - 1];
        }

        data[index] = item;
        size++;
        return true;
    }
    
    @Override
    public T set(int index, T item) {
        if (index < 0 || index >= size || item == null) {
            return null;
        }

        T oldItem = data[index];
        data[index] = item;
        return oldItem;
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
