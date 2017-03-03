package com.deliveredtechnologies.rulebook;

/**
 * Created by clong on 2/6/17.
 * A Fact is a single piece of data that can be supplied to a {@link Rule}
 * Facts are not immutable; they may be changed by rules and used to derive a result state
 */
public class Fact<T> {
  private String _name;
  private T _value;

  public Fact(String name, T value) {
    _name = name;
    _value = value;
  }

  public Fact(T obj) {
    _name = obj.toString();
    _value = obj;
  }

  /**
   * The method getName() gets the name of the Fact.
   *
   * @return the name of the Fact
   */
  public String getName() {
    return _name;
  }

  /**
   * The method setName() sets the name of the Fact.
   *
   * @param name the name of the Fact
   */
  public void setName(String name) {
    this._name = name;
  }

  /**
   * The method getValue() gets the value of the Fact.
   * @return the value of the Fact
   */
  public T getValue() {
    return _value;
  }

  /**
   * The method setValue() sets the value of the Fact.
   *
   * @param value the value of the Fact
   */
  public void setValue(T value) {
    this._value = value;
  }
}
