package com.deliveredtechnologies.rulebook;

import java.util.HashMap;
import java.util.Optional;

/**
 * A FactMap is an extension of {@link HashMap}; it stores facts by their name and provides convenience methods for
 * accessing {@link Fact} objects.
 */
public class FactMap<T> extends HashMap<String, Fact<T>> {
  /**
   * The method getOne() gets the value of the single Fact in the FactMap.
   * @return the value of a fact stored if only one fact is stored, otherwise null
   */
  public T getOne() {
    if (this.size() == 1) {
      return this.values().iterator().next().getValue();
    }
    return null;
  }

  /**
   * The method getValue() returns the value of the Fact associated with the name passed in.
   * @param name  the name of the Fact
   * @return      the value of the Fact
   */
  public T getValue(String name) {
    return Optional.ofNullable(this.get(name)).map(Fact::getValue).orElse(null);
  }

  /**
   * The method setValue sets the value of the Fact but its name.<br/>
   * If no Fact exists with the associated name, a new fact is created with the specified name and value.<br/>
   * @param name  the name of the Fact
   * @param value the value object of the Fact
   */
  public void setValue(String name, T value) {
    Fact<T> fact = this.get(name);
    if (fact == null) {
      fact = new Fact<T>(name, value);
      put(name, fact);
      return;
    }
    fact.setValue(value);
  }

  /**
   * The toString() method gets the FactMap converted a string.<br/>
   * If there is only one Fact, the String value is the value of the Fact.<br/>
   * Otherwise, the String value is the value of the parent {@link HashMap}.
   * @return  the String value of the FactMap
   */
  @Override
  public String toString() {
    return this.size() == 1 && this.getOne() instanceof String ? (String)this.getOne() : super.toString();
  }
}

