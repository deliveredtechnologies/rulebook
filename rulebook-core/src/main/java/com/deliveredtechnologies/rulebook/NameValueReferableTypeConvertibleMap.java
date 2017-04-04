package com.deliveredtechnologies.rulebook;

/**
 * Created by clong on 4/3/17.
 */
public interface NameValueReferableTypeConvertibleMap<T> extends NameValueReferableMap<T> {
  String getStrVal(String name);
  Integer getIntVal(String name);
  Double getDblVal(String name);
}
