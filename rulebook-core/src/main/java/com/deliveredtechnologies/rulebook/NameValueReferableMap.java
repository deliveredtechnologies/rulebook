package com.deliveredtechnologies.rulebook;

import java.util.Map;

/**
 * A Map with String keys and NameValueReferable objects with some convenience methods.
 */
public interface NameValueReferableMap<T> extends Map<String, NameValueReferable<T>> {
  /**
   * Gets the value of the the single NameValueReferable object in the map if there is only one object
   * contained in the map.
   * @return  the value of the NameValueReferable object if there is only one, otherwise null
   */
  T getOne();

  /**
   * Gets the value of the NameValueReferable object associated with the specified name.
   * @param name  the name of the NameValueReferable object
   * @return      the NameValueReferable object associated with the name
   */
  T getValue(String name);

  /**
   * Sets the value of NameValueReferable object.
   * @param name  the name
   * @param obj   the value
   */
  void setValue(String name, T obj);

  /**
   * Puts a NameValueReferable object into the Map.
   * @param ref the NameValueReferable object to be put into the Map
   * @return    the NameValueReferable object put into the Map
   */
  NameValueReferable<T> put(NameValueReferable<T> ref);
}
