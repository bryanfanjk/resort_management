package tarumtresort.adt;

/**
 * A binary heap-based implementation of the PriorityQueueInterface.
 * The heap is represented in a contiguous array format.
 * Includes two highly original, non-trivial heap modifications:
 * 1. `remove(T entry)`: Locates and removes a specific element, re-heaping the tree.
 * 2. `changePriority(T oldEntry, T newEntry)`: Modifies an element's value (e.g. VIP loyalty upgrade)
 *    and restructures the heap dynamically.
 * 
 * @author Admin
 * @param <T> The type of elements, must implement Comparable.
 */
public class HeapPriorityQueue<T extends Comparable<T>> implements PriorityQueueInterface<T> {

    private T[] heap;
    private int lastIndex; // Index of last element in heap
    private static final int DEFAULT_CAPACITY = 25;
    private static final int MAX_CAPACITY = 10000;

    public HeapPriorityQueue() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public HeapPriorityQueue(int initialCapacity) {
        if (initialCapacity < DEFAULT_CAPACITY) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        // index 0 is left empty; heap starts at index 1
        heap = (T[]) new Comparable[initialCapacity + 1];
        lastIndex = 0;
    }

    @Override
    public void enqueue(T newEntry) {
        ensureCapacity();
        lastIndex++;
        int newIndex = lastIndex;
        int parentIndex = newIndex / 2;
        
        // Up-heap (sift-up)
        while (parentIndex > 0 && newEntry.compareTo(heap[parentIndex]) > 0) {
            heap[newIndex] = heap[parentIndex];
            newIndex = parentIndex;
            parentIndex = newIndex / 2;
        }
        heap[newIndex] = newEntry;
    }

    @Override
    public T dequeue() {
        T root = peek();
        if (root != null) {
            heap[1] = heap[lastIndex];
            heap[lastIndex] = null;
            lastIndex--;
            if (lastIndex > 0) {
                reheap(1); // Down-heap (sift-down)
            }
        }
        return root;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return heap[1];
    }

    @Override
    public boolean isEmpty() {
        return lastIndex == 0;
    }

    @Override
    public int getSize() {
        return lastIndex;
    }

    @Override
    public void clear() {
        for (int i = 1; i <= lastIndex; i++) {
            heap[i] = null;
        }
        lastIndex = 0;
    }

    /**
     * Non-trivial original operation: Removes a specific entry from the priority queue.
     * Searches the array, replaces it with the last entry, and reheaps.
     * 
     * @param entry The entry to remove.
     * @return true if the entry was removed, false if not found.
     */
    public boolean remove(T entry) {
        if (isEmpty() || entry == null) {
            return false;
        }

        int index = -1;
        for (int i = 1; i <= lastIndex; i++) {
            if (heap[i].equals(entry)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }

        T lastEntry = heap[lastIndex];
        heap[index] = lastEntry;
        heap[lastIndex] = null;
        lastIndex--;

        if (index <= lastIndex && lastIndex > 0) {
            // Check if we need to sift up or down
            int parentIndex = index / 2;
            if (parentIndex > 0 && heap[index].compareTo(heap[parentIndex]) > 0) {
                // Sift up
                T temp = heap[index];
                int curr = index;
                while (parentIndex > 0 && temp.compareTo(heap[parentIndex]) > 0) {
                    heap[curr] = heap[parentIndex];
                    curr = parentIndex;
                    parentIndex = curr / 2;
                }
                heap[curr] = temp;
            } else {
                // Sift down
                reheap(index);
            }
        }
        return true;
    }

    /**
     * Non-trivial original operation: Updates an existing entry's value and reorganizes
     * the heap to reflect its new priority.
     * 
     * @param oldEntry The existing entry in the priority queue.
     * @param newEntry The entry with updated priority values.
     * @return true if the upgrade succeeded, false if oldEntry was not found.
     */
    public boolean changePriority(T oldEntry, T newEntry) {
        if (oldEntry == null || newEntry == null) {
            return false;
        }
        boolean removed = remove(oldEntry);
        if (removed) {
            enqueue(newEntry);
            return true;
        }
        return false;
    }

    /**
     * Returns a list representation of the priority queue elements in level order.
     * Useful for rendering queues in boundaries.
     * 
     * @return List of elements in level order.
     */
    public ListInterface<T> toList() {
        ListInterface<T> list = new ArrayList<>(lastIndex);
        for (int i = 1; i <= lastIndex; i++) {
            list.add(heap[i]);
        }
        return list;
    }

    /**
     * Sorts and returns all priority elements in descending order (highest priority first).
     * This destroys the internal queue order, so we clone it first.
     * 
     * @return Sorted list of priority queue elements.
     */
    public ListInterface<T> getSortedList() {
        HeapPriorityQueue<T> clone = new HeapPriorityQueue<>(lastIndex);
        for (int i = 1; i <= lastIndex; i++) {
            clone.enqueue(heap[i]);
        }
        ListInterface<T> sortedList = new ArrayList<>(lastIndex);
        while (!clone.isEmpty()) {
            sortedList.add(clone.dequeue());
        }
        return sortedList;
    }

    private void reheap(int rootIndex) {
        boolean done = false;
        T orphan = heap[rootIndex];
        int leftChildIndex = 2 * rootIndex;

        while (!done && (leftChildIndex <= lastIndex)) {
            int largerChildIndex = leftChildIndex;
            int rightChildIndex = leftChildIndex + 1;

            if ((rightChildIndex <= lastIndex) && 
                (heap[rightChildIndex].compareTo(heap[largerChildIndex]) > 0)) {
                largerChildIndex = rightChildIndex;
            }

            if (orphan.compareTo(heap[largerChildIndex]) < 0) {
                heap[rootIndex] = heap[largerChildIndex];
                rootIndex = largerChildIndex;
                leftChildIndex = 2 * rootIndex;
            } else {
                done = true;
            }
        }
        heap[rootIndex] = orphan;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        int capacity = heap.length - 1;
        if (lastIndex >= capacity) {
            int newCapacity = 2 * capacity;
            if (newCapacity > MAX_CAPACITY) {
                throw new IllegalStateException("Priority Queue exceeds maximum allowed size.");
            }
            T[] oldHeap = heap;
            heap = (T[]) new Comparable[newCapacity + 1];
            System.arraycopy(oldHeap, 1, heap, 1, lastIndex);
        }
    }
}
