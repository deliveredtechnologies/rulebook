package com.deliveredtechnologies.rulebook.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by clong on 3/18/17.
 */
public class ArrayUtils {
  public static <T> T[] combine(T[] array1, T[] array2) {
    T[] combinedArray = (T[])Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
    System.arraycopy(array1, 0, combinedArray, 0, array1.length);
    System.arraycopy(array2, 0, combinedArray, array1.length, array2.length);
    return combinedArray;
  }
}
