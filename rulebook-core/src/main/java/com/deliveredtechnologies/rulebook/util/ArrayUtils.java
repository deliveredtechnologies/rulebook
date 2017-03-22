package com.deliveredtechnologies.rulebook.util;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Stream;

/**
 * ArrayUtils is a utility class for arrays.
 */
public final class ArrayUtils {

  private ArrayUtils() { }

  /**
   * The combine() static method combines the contents of two arrays into a single array of the same type.
   * @param array1  the array to be concatenated on the left
   * @param array2  the array to be concatenated on the right
   * @param <T>     the type of array
   * @return        a concatenation of array1 and array2
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] combine(T[] array1, T[] array2) {
    return combine(array1, array2, array1.length + array2.length);
  }

  /**
   * The combine() static method combines the contents of two arrays into a single array of the same type that contains
   * no more than the maxElements number of elements.
   * @param array1      the array to be concatenated on the left
   * @param array2      the array to be concatenated on the right
   * @param maxElements the maximum number of elements in the resulting array
   * @param <T>         the type of array
   * @return            a concatenation of array1 and array2
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] combine(T[] array1, T[] array2, int maxElements) {
    if (array1.length == maxElements) {
      return array1;
    } else if (array1.length > maxElements) {
      T[] combinedArray = (T[])Array.newInstance(array1.getClass().getComponentType(), maxElements);
      System.arraycopy(array1, 0, combinedArray, 0, maxElements);
      return combinedArray;
    }

    maxElements = array1.length + array2.length >= maxElements ? maxElements : array1.length + array2.length;
    T[] combinedArray = (T[])Array.newInstance(array1.getClass().getComponentType(), maxElements);
    System.arraycopy(array1, 0, combinedArray, 0, array1.length);
    System.arraycopy(array2, 0, combinedArray, array1.length, maxElements - array1.length);
    return combinedArray;
  }
}