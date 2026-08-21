package adt;

/* author: Ho Jia Ming */
public class LinkedStack<T> implements StackInterface<T> {

    private Node topNode; // References node at top of stack
    private int size;

    public LinkedStack() {
        topNode = null;
        size = 0;
    }

    @Override
    public void push(T newEntry) {
        Node newNode = new Node(newEntry, topNode);
        topNode = newNode;
        size++;
    }

    @Override
    public T pop() {
        T top = peek();
        if (topNode != null) {
            topNode = topNode.next;
            size--;
        }
        return top;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return topNode.data;
    }

    @Override
    public boolean isEmpty() {
        return topNode == null;
    }

    @Override
    public void clear() {
        topNode = null;
        size = 0;
    }

    public ListInterface<T> popMany(int count) {
        ListInterface<T> poppedList = new List<>(count);
        if (count <= 0) {
            return poppedList;
        }

        int actualPopped = 0;
        while (!isEmpty() && actualPopped < count) {
            poppedList.add(pop());
            actualPopped++;
        }
        return poppedList;
    }

    public int getSize() {
        return size;
    }

    public ListInterface<T> toList() {
        ListInterface<T> list = new List<>(size);
        Node current = topNode;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }

    private class Node {

        private T data;
        private Node next;

        private Node(T dataPortion) {
            this(dataPortion, null);
        }

        private Node(T dataPortion, Node linkPortion) {
            data = dataPortion;
            next = linkPortion;
        }
    }
}
