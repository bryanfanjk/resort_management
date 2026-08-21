/*
 * Author: Antigravity
 * 
 * Non-Linear ADT: Binary Search Tree (BST)
 * Adapted from standard Data Structures and Algorithms Binary Search Tree concepts.
 */
package adt;

public class BinarySearchTree<K extends Comparable<K>, V> {

    private class Node {
        K key;
        V value;
        Node left;
        Node right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;
    private int size;

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    public void insert(K key, V value) {
        if (key == null) return;
        root = insertRecursive(root, key, value);
    }

    private Node insertRecursive(Node current, K key, V value) {
        if (current == null) {
            size++;
            return new Node(key, value);
        }

        int cmp = key.compareTo(current.key);
        if (cmp < 0) {
            current.left = insertRecursive(current.left, key, value);
        } else if (cmp > 0) {
            current.right = insertRecursive(current.right, key, value);
        } else {
            // Update existing value if key already exists
            current.value = value;
        }

        return current;
    }

    public V search(K key) {
        if (key == null) return null;
        return searchRecursive(root, key);
    }

    private V searchRecursive(Node current, K key) {
        if (current == null) {
            return null;
        }

        int cmp = key.compareTo(current.key);
        if (cmp == 0) {
            return current.value;
        } else if (cmp < 0) {
            return searchRecursive(current.left, key);
        } else {
            return searchRecursive(current.right, key);
        }
    }

    public boolean contains(K key) {
        return search(key) != null;
    }

    public int size() {
        return size;
    }

    public Object[] getAllValues() {
        Object[] result = new Object[size];
        if (root != null) {
            inOrderTraversal(root, result, new int[]{0});
        }
        return result;
    }

    private void inOrderTraversal(Node current, Object[] result, int[] index) {
        if (current == null) return;
        inOrderTraversal(current.left, result, index);
        result[index[0]++] = current.value;
        inOrderTraversal(current.right, result, index);
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
