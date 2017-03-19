package com.deliveredtechnologies.rulebook;

/**
 * This is a wrapper class for the actual result.
 * This is needed to ensure that an instance is available and that a reference is maintained to the actual that
 * can be manipulated in the then {@link java.util.function.BiFunction}.
 * The <code>Result</code> object can then contain a pointer to any instance, which can be shared with lamda calls
 * and across other objects (i.e. DecisionBook and Decision objects)
 */
public class Result<T> {
  private T _value;

  public Result() {}

  public Result(T value) {
    this._value = value;
  }

  /**
   * The method getValue() returns the object contained in the Result object.
   *
   * @return the object stored in the Result object
   */
  public T getValue() {
    return _value;
  }

  /**
   * The method setValue() sets the object to be contained in the Result object.
   *
   * @param value the object to be stored in the Result object
   */
  public void setValue(T value) {
    this._value = value;
  }
}