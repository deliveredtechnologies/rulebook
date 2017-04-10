package com.deliveredtechnologies.rulebook;

/**
 * An object with a reference to another object.
 */
public interface Referable<T> {

  /**
   * Gets the object referred to.
   * @return  the object
   */
  T getValue();

  /**
   * Sets the object to be referred to.
   * @param obj the object
   */
  void setValue(T obj);
}
