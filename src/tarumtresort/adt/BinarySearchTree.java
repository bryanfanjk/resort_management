package tarumtresort.adt;

/**
 * Node-based implementation of the BSTInterface.
 * Implements a Binary Search Tree mapping comparable keys to values.
 * Includes a highly original, non-trivial `getInOrderRange` operation
 * which retrieves items within a range of keys efficiently.
 * 
 * @author Admin
 * @param <K> The type of keys, must implement Comparable.
 * @param <V> The type of values.
 */
public class BinarySearchTree<K extends Comparable<K>, V> implements BSTInterface<K, V> {

    private TreeNode root;
    private int size;

    public BinarySearchTree() {
        root = null;
        size = 0;
    }

    @Override
    public void insert(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }
        root = insertHelper(root, key, value);
    }

    private TreeNode insertHelper(TreeNode node, K key, V value) {
        if (node == null) {
            size++;
            return new TreeNode(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.leftChild = insertHelper(node.leftChild, key, value);
        } else if (cmp > 0) {
            node.rightChild = insertHelper(node.rightChild, key, value);
        } else {
            node.value = value; // Update value if key already exists
        }
        return node;
    }

    @Override
    public V search(K key) {
        if (key == null) {
            return null;
        }
        TreeNode node = searchHelper(root, key);
        return (node != null) ? node.value : null;
    }

    private TreeNode searchHelper(TreeNode node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return searchHelper(node.leftChild, key);
        } else if (cmp > 0) {
            return searchHelper(node.rightChild, key);
        } else {
            return node;
        }
    }

    @Override
    public boolean contains(K key) {
        return search(key) != null;
    }

    @Override
    public V delete(K key) {
        if (key == null) {
            return null;
        }
        // Use a wrapper to retrieve the deleted value
        ValueWrapper deletedValue = new ValueWrapper();
        root = deleteHelper(root, key, deletedValue);
        if (deletedValue.value != null) {
            size--;
        }
        return deletedValue.value;
    }

    private TreeNode deleteHelper(TreeNode node, K key, ValueWrapper wrapper) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.leftChild = deleteHelper(node.leftChild, key, wrapper);
        } else if (cmp > 0) {
            node.rightChild = deleteHelper(node.rightChild, key, wrapper);
        } else {
            // Found the node to delete
            wrapper.value = node.value;

            // Case 1: No children or 1 child
            if (node.leftChild == null) {
                return node.rightChild;
            } else if (node.rightChild == null) {
                return node.leftChild;
            }

            // Case 2: Two children
            // Find the in-order predecessor (maximum in left subtree)
            TreeNode maxLeft = findMax(node.leftChild);
            node.key = maxLeft.key;
            node.value = maxLeft.value;
            // Delete the predecessor from left subtree
            node.leftChild = deleteHelper(node.leftChild, maxLeft.key, new ValueWrapper());
        }
        return node;
    }

    private TreeNode findMax(TreeNode node) {
        while (node.rightChild != null) {
            node = node.rightChild;
        }
        return node;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public ListInterface<V> getInOrderValues() {
        ListInterface<V> list = new ArrayList<>(size);
        inOrderHelper(root, list);
        return list;
    }

    private void inOrderHelper(TreeNode node, ListInterface<V> list) {
        if (node != null) {
            inOrderHelper(node.leftChild, list);
            list.add(node.value);
            inOrderHelper(node.rightChild, list);
        }
    }

    @Override
    public ListInterface<V> getInOrderRange(K startKey, K endKey) {
        ListInterface<V> list = new ArrayList<>();
        if (startKey == null || endKey == null || startKey.compareTo(endKey) > 0) {
            return list;
        }
        rangeHelper(root, startKey, endKey, list);
        return list;
    }

    private void rangeHelper(TreeNode node, K startKey, K endKey, ListInterface<V> list) {
        if (node == null) {
            return;
        }

        // If current key is greater than startKey, left subtree might have keys in range
        if (node.key.compareTo(startKey) > 0) {
            rangeHelper(node.leftChild, startKey, endKey, list);
        }

        // If current key is in range, add to list
        if (node.key.compareTo(startKey) >= 0 && node.key.compareTo(endKey) <= 0) {
            list.add(node.value);
        }

        // If current key is less than endKey, right subtree might have keys in range
        if (node.key.compareTo(endKey) < 0) {
            rangeHelper(node.rightChild, startKey, endKey, list);
        }
    }

    // Helper classes
    private class TreeNode {
        private K key;
        private V value;
        private TreeNode leftChild;
        private TreeNode rightChild;

        private TreeNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.leftChild = null;
            this.rightChild = null;
        }
    }

    private class ValueWrapper {
        private V value = null;
    }
}
