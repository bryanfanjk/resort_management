package tarumtresort.adt;

/**
 * A linked node-based implementation of the ADT Stack.
 * Includes a non-trivial, original operation `popMany(int count)` to support 
 * batch undo or multiple rollback levels in log structures.
 * 
 * @author Admin
 * @param <T> The type of elements in the stack.
 */
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

    /**
     * Non-trivial original operation: Pops multiple elements from the stack 
     * in a single operation.
     * 
     * @param count The number of elements to pop. Must be greater than 0.
     * @return A list containing the popped elements in LIFO order (first popped element first).
     */
    public ListInterface<T> popMany(int count) {
        ListInterface<T> poppedList = new ArrayList<>(count);
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

    /**
     * Gets the current depth of the stack.
     * 
     * @return Number of elements in the stack.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns a list of elements from top to bottom (for display purposes).
     * 
     * @return List representing the stack elements.
     */
    public ListInterface<T> toList() {
        ListInterface<T> list = new ArrayList<>(size);
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
