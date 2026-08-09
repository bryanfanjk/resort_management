package tarumtresort.util;

import tarumtresort.adt.ListInterface;
import java.util.Comparator;

/**
 * Utility class providing custom explicit sorting algorithms.
 * Implements a generic Merge Sort algorithm working directly on custom ListInterface instances.
 * Crucial for report ordering without using the Java Collections framework.
 * 
 * @author Admin
 */
public class Sorter {

    /**
     * Sorts the specified ListInterface using a custom Comparator.
     * Uses Merge Sort algorithm which guarantees O(n log n) performance.
     * 
     * @param <T> The element type.
     * @param list The custom list to sort.
     * @param c The comparator to define the sorting criteria.
     */
    public static <T> void sort(ListInterface<T> list, Comparator<? super T> c) {
        if (list == null || list.getLength() <= 1) {
            return;
        }
        mergeSort(list, 1, list.getLength(), c);
    }

    private static <T> void mergeSort(ListInterface<T> list, int first, int last, Comparator<? super T> c) {
        if (first < last) {
            int mid = first + (last - first) / 2;
            mergeSort(list, first, mid, c);
            mergeSort(list, mid + 1, last, c);
            merge(list, first, mid, last, c);
        }
    }

    private static <T> void merge(ListInterface<T> list, int first, int mid, int last, Comparator<? super T> c) {
        int leftSize = mid - first + 1;
        int rightSize = last - mid;

        @SuppressWarnings("unchecked")
        T[] leftArray = (T[]) new Object[leftSize];
        @SuppressWarnings("unchecked")
        T[] rightArray = (T[]) new Object[rightSize];

        // Copy data to temp arrays
        for (int i = 0; i < leftSize; i++) {
            leftArray[i] = list.getEntry(first + i);
        }
        for (int j = 0; j < rightSize; j++) {
            rightArray[j] = list.getEntry(mid + 1 + j);
        }

        // Merge back into list
        int i = 0, j = 0;
        int k = first;
        while (i < leftSize && j < rightSize) {
            if (c.compare(leftArray[i], rightArray[j]) <= 0) {
                list.replace(k, leftArray[i]);
                i++;
            } else {
                list.replace(k, rightArray[j]);
                j++;
            }
            k++;
        }

        // Copy remaining elements
        while (i < leftSize) {
            list.replace(k, leftArray[i]);
            i++;
            k++;
        }
        while (j < rightSize) {
            list.replace(k, rightArray[j]);
            j++;
            k++;
        }
    }
}
