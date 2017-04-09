package com.deliveredtechnologies.rulebook;

/**
 * A name value pair where the value refers to an object.
 * The object reference in the value allows a reference to this object to remain
 * consistent, while the referred object in the value can change.
 */
public interface NameValueReferable<T> {

  /**
   * Gets the name.
   * @return  the name
   */
  String getName();

  /**
   * Sets the name.
   * @param name  the name
   */
  void setName(String name);

  /**
   * Gets the value.
   * @return  the value object reference
   */
  T getValue();

  /**
   * Sets the vaue.
   * @param obj the value object [reference]
   */
  void setValue(T obj);
}
