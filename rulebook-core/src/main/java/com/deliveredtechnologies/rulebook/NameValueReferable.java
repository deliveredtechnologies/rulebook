package com.deliveredtechnologies.rulebook;

/**
 * Created by clong on 4/2/17.
 */
public interface NameValueReferable<T> {
  String getName();
  void setName(String name);
  T getValue();
  void setValue(T obj);
}
