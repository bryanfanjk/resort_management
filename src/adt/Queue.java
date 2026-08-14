package adt;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

public class Queue<T> {

    private T[] queue;
    private int front;
    private int rear;
    private int size;

    @SuppressWarnings("unchecked")
    public Queue(int capacity) {
        queue = (T[]) new Object[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }

    public void enqueue(T data) {
        if (size == queue.length) {
            System.out.println("Queue is full.");
            return;
        }

        rear = (rear + 1) % queue.length;
        queue[rear] = data;
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            return null;
        }

        T data = queue[front];
        queue[front] = null;
        front = (front + 1) % queue.length;
        size--;

        return data;
    }
    
    public T get(int index) {

        if (index < 0 || index >= size) {
            return null;
        }

        int position = (front + index) % queue.length;

        return queue[position];
    }

    public T peek() {
        if (isEmpty()) {
            return null;
        }

        return queue[front];
    }
    
    public T remove(int index) {

        if (index < 0 || index >= size) {
            return null;
        }

        int position = (front + index) % queue.length;
        T removed = queue[position];

        for (int i = index; i < size - 1; i++) {

            int current = (front + i) % queue.length;
            int next = (front + i + 1) % queue.length;

            queue[current] = queue[next];
        }

        int lastPosition = (front + size - 1) % queue.length;
        queue[lastPosition] = null;

        size--;

        if (size == 0) {
            front = 0;
            rear = -1;
        } else {
            rear = (front + size - 1) % queue.length;
        }

        return removed;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}