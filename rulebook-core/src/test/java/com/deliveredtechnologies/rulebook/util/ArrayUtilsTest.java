package com.deliveredtechnologies.rulebook.util;

import org.junit.Assert;
import org.junit.Test;

import static com.deliveredtechnologies.rulebook.util.ArrayUtils.combine;

/**
 * Tests for {@link ArrayUtils}
 */
public class ArrayUtilsTest {
  @Test
  public void combineMethodShouldCombineTwoArrays () {
    String[] array1 = {"one", "two", "three"};
    String[] array2 = {"four", "five", "six"};
    String[] combinedArray = combine(array1, array2);

    for (int i = 0; i < array1.length; i++) {
      Assert.assertEquals(array1[i], combinedArray[i]);
    }

    for (int i = 0; i < combinedArray.length - array1.length; i++) {
      Assert.assertEquals(array2[i], combinedArray[i + array1.length]);
    }

    Assert.assertEquals(array1.length + array2.length, combinedArray.length);
  }

  @Test
  public void combineMethodShouldCombineTwoArraysUpToMaxSizeWhenMaxSizeLessThanArray1Elements() {
    String[] array1 = {"one", "two", "three"};
    String[] array2 = {"four", "five", "six"};
    String[] combinedArray = combine(array1, array2, 2);

    for (int i = 0; i < combinedArray.length; i++) {
      Assert.assertEquals(array1[i], combinedArray[i]);
    }

    Assert.assertEquals(2, combinedArray.length);
  }

  @Test
  public void combineMethodShouldCombineTwoArraysUpToMaxSizeWhenMaxSizeGreaterThanArray1Elements() {
    String[] array1 = {"one", "two", "three"};
    String[] array2 = {"four", "five", "six"};
    String[] combinedArray = combine(array1, array2, 5);

    for (int i = 0; i < array1.length; i++) {
      Assert.assertEquals(array1[i], combinedArray[i]);
    }

    for (int i = 0; i < combinedArray.length - array1.length; i++) {
      Assert.assertEquals(array2[i], combinedArray[i + array1.length]);
    }

    Assert.assertEquals(5, combinedArray.length);
  }

  @Test
  public void combineMethodShouldCombineTwoArraysUpToMaxSizeWhenMaxSizeGreaterThanArrayAllElements() {
    String[] array1 = {"one", "two", "three"};
    String[] array2 = {"four", "five", "six"};
    String[] combinedArray = combine(array1, array2, 10);

    for (int i = 0; i < array1.length; i++) {
      Assert.assertEquals(array1[i], combinedArray[i]);
    }

    for (int i = 0; i < combinedArray.length - array1.length; i++) {
      Assert.assertEquals(array2[i], combinedArray[i + array1.length]);
    }

    Assert.assertEquals(array1.length + array2.length, combinedArray.length);
  }
}
