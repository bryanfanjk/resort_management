package utility;

import java.util.Comparator;

/**
 * Author: <Your Name Here>
 *
 * SortUtility is a static utility class providing a hand-written
 * quicksort. It is generic over any type T and any Comparator<T>, so it
 * can sort Guest arrays by priority, by points, by name, or by any
 * other criterion the report needs - without ever calling
 * java.util.Collections.sort() (not permitted by the assignment rules).
 *
 * "Descending priority" here means: comparator.compare(a, b) > 0 implies
 * a should come BEFORE b in the sorted result.
 */
public class SortUtility {

    private SortUtility() {
        // Utility class - never instantiated.
    }

    public static <T> void quickSort(T[] array, int length, Comparator<T> comparator) {
        if (array == null || length <= 1) {
            return;
        }
        quickSort(array, 0, length - 1, comparator);
    }

    private static <T> void quickSort(T[] array, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pivotIndex = partition(array, low, high, comparator);
            quickSort(array, low, pivotIndex - 1, comparator);
            quickSort(array, pivotIndex + 1, high, comparator);
        }
    }

    private static <T> int partition(T[] array, int low, int high, Comparator<T> comparator) {
        T pivot = array[high];
        int boundary = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(array[j], pivot) > 0) {
                boundary++;
                swap(array, boundary, j);
            }
        }
        swap(array, boundary + 1, high);
        return boundary + 1;
    }

    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
