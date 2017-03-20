package com.deliveredtechnologies.rulebook.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * ArrayUtils is a utility class for arrays.
 */
public class ArrayUtils {

  private ArrayUtils() { }

  /**
   * The combine() static method combines the contents of two arrays into a single array of the same type.
   * @param array1  the array to be concatenated on the left
   * @param array2  the array to be concatendated on the right
   * @param <T>     the type of array
   * @return        a concatenation of array1 and array2
   */
  public static <T> T[] combine(T[] array1, T[] array2) {
    T[] combinedArray = (T[])Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
    System.arraycopy(array1, 0, combinedArray, 0, array1.length);
    System.arraycopy(array2, 0, combinedArray, array1.length, array2.length);
    return combinedArray;
  }
}
