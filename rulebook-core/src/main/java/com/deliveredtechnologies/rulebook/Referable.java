package com.deliveredtechnologies.rulebook;

/**
 * Created by clong on 4/2/17.
 */
public interface Referable<T> {
  T getValue();
  void setValue(T obj);
}
