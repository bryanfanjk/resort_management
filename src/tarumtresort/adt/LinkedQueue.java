package tarumtresort.adt;

/**
 * A linked node-based implementation of the ADT Queue.
 * Includes a non-trivial, original operation `moveToFront(T entry)` to expedite 
 * specific items (e.g. urgent room requests or vip bypasses) within standard queues.
 * 
 * @author Admin
 * @param <T> The type of elements in the queue.
 */
public class LinkedQueue<T> implements QueueInterface<T> {

    private Node firstNode; // References node at front of queue
    private Node lastNode;  // References node at back of queue
    private int count;

    public LinkedQueue() {
        firstNode = null;
        lastNode = null;
        count = 0;
    }

    @Override
    public void enqueue(T newEntry) {
        Node newNode = new Node(newEntry, null);
        if (isEmpty()) {
            firstNode = newNode;
        } else {
            lastNode.next = newNode;
        }
        lastNode = newNode;
        count++;
    }

    @Override
    public T dequeue() {
        T front = getFront();
        if (front != null) {
            firstNode = firstNode.next;
            if (firstNode == null) {
                lastNode = null;
            }
            count--;
        }
        return front;
    }

    @Override
    public T getFront() {
        if (isEmpty()) {
            return null;
        }
        return firstNode.data;
    }

    @Override
    public boolean isEmpty() {
        return firstNode == null;
    }

    @Override
    public void clear() {
        firstNode = null;
        lastNode = null;
        count = 0;
    }

    /**
     * Non-trivial original operation: Moves the first occurrence of a specific 
     * entry to the absolute front of the queue, allowing urgent items to bypass 
     * the FIFO wait time.
     * 
     * @param entry The entry to expedite.
     * @return true if the entry was found and moved, false otherwise.
     */
    public boolean moveToFront(T entry) {
        if (isEmpty() || entry == null) {
            return false;
        }

        // If it's already the first node, no movement needed
        if (firstNode.data.equals(entry)) {
            return true;
        }

        Node prev = firstNode;
        Node curr = firstNode.next;

        while (curr != null) {
            if (curr.data.equals(entry)) {
                // If it is the last node, update lastNode reference
                if (curr == lastNode) {
                    lastNode = prev;
                }
                // Unlink current node
                prev.next = curr.next;
                
                // Link current node to the front
                curr.next = firstNode;
                firstNode = curr;
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    /**
     * Gets the number of elements in the queue.
     * 
     * @return Number of elements.
     */
    public int size() {
        return count;
    }

    /**
     * Retrieves all entries in the queue as a ListInterface representation.
     * Useful for boundaries to display current queue elements.
     * 
     * @return List containing all queue elements.
     */
    public ListInterface<T> toList() {
        ListInterface<T> list = new ArrayList<>(count);
        Node current = firstNode;
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
