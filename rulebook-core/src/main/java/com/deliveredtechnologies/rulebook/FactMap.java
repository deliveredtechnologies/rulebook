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
   *
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
   *
   * @param name the name of the Fact
   * @return the value of the Fact
   */
  public T getValue(String name) {
    return Optional.ofNullable(this.get(name)).map(Fact::getValue).orElse(null);
  }
}

