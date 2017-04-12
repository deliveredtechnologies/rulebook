package com.deliveredtechnologies.rulebook;

/**
 * Decorator interface for NameValueReferableMap that adds common type conversions.
 */
public interface NameValueReferableTypeConvertibleMap<T> extends NameValueReferableMap<T> {

  /**
   * Converts the value contained in the specified object to a String.
   * @param name  the name of the NameValueReferable object
   * @return      the value converted to String
   */
  String getStrVal(String name);

  /**
   * Converts the value contained in the specified object to an Integer.
   * @param name  the name of the NameValueReferable object
   * @return      the value converted to Integer
   */
  Integer getIntVal(String name);

  /**
   * Converts the value contained in the specified object to a Double.
   * @param name  the name of the NameValueReferable object
   * @return      the value converted to Double
   */
  Double getDblVal(String name);
}
