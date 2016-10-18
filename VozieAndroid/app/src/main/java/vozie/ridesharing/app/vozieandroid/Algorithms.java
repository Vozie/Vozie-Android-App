package vozie.ridesharing.app.vozieandroid;

import java.util.List;

/**************************************************
* File Name: Algorithms.java
*
* Description: This class holds static algorithmic functions
* that are required in Vozie's primary activities.
**************************************************/
public class Algorithms {

    /**************************************************
    * Title: Quicksort
    *
    * Description: This class handles a quicksort of any type of list, based
    * on a separate integer array that should match the order of corresponding list component.
    **************************************************/
    public static class Quicksort<T> {
        private List<T> list;
        private int[] intArr;

        /* Title:                   sort
        * Description:             Calling function for Quicksort class.
        *
        * @param   listToSort      List to be sorted in unison with integer array.
        * @param   valuesToSortBy  Values to dictate the sorting of the list; listToSort will
        *                          have its indexes shifted in unison with valuesToSortBy.
        * @return                  List of objects sorted.
        */
        public List<T> sort(List<T> listToSort, int[] valuesToSortBy) {
            if (valuesToSortBy == null
                    || valuesToSortBy.length == 0
                    || listToSort == null
                    || listToSort.size() == 0
                    || listToSort.size() != valuesToSortBy.length)
                return null;

            this.list = listToSort;
            this.intArr = valuesToSortBy;

            quicksort(0, valuesToSortBy.length - 1);

            return list;
        }

        private void quicksort(int low, int high) {
            int i = low, j = high;

            int pivot = intArr[low + (high-low)/2];

            while (i <= j) {
                while (intArr[i] < pivot) {
                    i++;
                }
                while (intArr[j] > pivot) {
                    j--;
                }

                if (i <= j) {
                    exchange(i, j);
                    i++;
                    j--;
                }
            }

            if (low < j)
                quicksort(low, j);
            if (i < high)
                quicksort(i, high);
        }

        private void exchange(int i, int j) {
            int temp = intArr[i];

            list.set(i, list.get(j));
            intArr[i] = intArr[j];

            list.set(j, list.get(i));
            intArr[j] = temp;
        }
    }
}
