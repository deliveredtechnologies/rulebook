package com.deliveredtechnologies.rulebook;

/**
 * A Fact is a single piece of data that can be supplied to a {@link Rule}.
 * Facts are not immutable; they may be changed by rules and used to derive a result state.
 */
public class Fact<T> implements NameValueReferable<T> {
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

  public Fact(NameValueReferable<T> fact) {
    _name = fact.getName();
    _value = fact.getValue();
  }

  /**
   * The method getName() gets the name of the Fact.
   * @return the name of the Fact
   */
  @Override
  public String getName() {
    return _name;
  }

  /**
   * The method setName() sets the name of the Fact.
   * @param name the name of the Fact
   */
  @Override
  public void setName(String name) {
    this._name = name;
  }

  /**
   * The method getValue() gets the value of the Fact.
   * @return the value of the Fact
   */
  @Override
  public T getValue() {
    return _value;
  }

  /**
   * The method setValue() sets the value of the Fact.
   * @param value the value of the Fact
   */
  @Override
  public void setValue(T value) {
    this._value = value;
  }

  /**
   * The toString() method returns the toString of the object contained in the Fact.
   * @return  the toString() of the object contained in the Fact
   */
  @Override
  public String toString() {
    return _value.toString();
  }

  @Override
  public int hashCode() {
    return (_name.hashCode() + _value.hashCode()) % Integer.MAX_VALUE;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Fact) {
      Fact fact = (Fact)obj;
      return fact.getName().equals(_name) && fact.getValue().equals(_value);
    }
    return false;
  }
}
