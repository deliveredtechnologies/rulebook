package com.deliveredtechnologies.rulebook;

import java.util.Map;

/**
 * Created by clong on 4/2/17.
 */
public interface NameValueReferableMap<T> extends Map<String, NameValueReferable<T>> {
  T getOne();
  T getValue(String name);
  void setValue(String name, T obj);
  NameValueReferable<T> put(NameValueReferable<T> ref);
}
