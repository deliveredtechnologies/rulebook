package com.deliveredtechnologies.rulebook;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.Collection;

/**
 * A FactMap decorates {@link Map}; it stores facts by their name and provides convenience methods for
 * accessing {@link Fact} objects.
 */
public class FactMap<T> implements NameValueReferableMap<T> {

  private Map<String, NameValueReferable<T>> _facts;

  public FactMap(Map<String, NameValueReferable<T>> facts) {
    _facts = facts;
  }

  /**
   * Constructor allows a FactMap to be created by specifying NameValueReferable facts.
   * @param facts an array of NameValueReferable facts
   */
  @SafeVarargs
  public FactMap(NameValueReferable<T>... facts) {
    this();

    for (NameValueReferable<T> fact : facts) {
      this.put(fact);
    }
  }

  public FactMap() {
    _facts = new HashMap<>();
  }

  /**
   * The method getOne() gets the value of the single Fact in the FactMap.
   * @return the value of a fact stored if only one fact is stored, otherwise null
   */
  @Override
  public T getOne() {
    if (_facts.size() == 1) {
      return _facts.values().iterator().next().getValue();
    }
    return null;
  }

  /**
   * The method getValue() returns the value of the Fact associated with the name passed in.
   * @param name  the name of the Fact
   * @return      the value of the Fact
   */
  @Override
  public T getValue(String name) {
    return Optional.ofNullable(_facts.get(name)).map(NameValueReferable::getValue).orElse(null);
  }

  /**
   * Method getStrVal() gets the String value of a Fact in the FactMap.
   * @param name  the name of the Fact in the FactMap
   * @return      the String value of the Fact specified
   */
  @Deprecated
  public String getStrVal(String name) {
    if (getValue(name) instanceof String) {
      return (String)getValue(name);
    }
    return String.valueOf(getValue(name));
  }

  /**
   * Method getIntVal() gets the Integer value of a Fact in the FactMap.
   * @param name  the name of the Fact in the FactMap
   * @return      the Integer value of the Fact specified
   */
  @Deprecated
  public Integer getIntVal(String name) {
    Object value = getValue(name);
    if (value != null) {
      if (Integer.class == value.getClass()) {
        return (Integer)value;
      }
      if (value.getClass() == String.class) {
        return Integer.valueOf((String) value);
      }
    }
    return null;
  }

  /**
   * Method getDblVal() gets the Double value of a Fact in the FactMap.
   * @param name  the name of the Fact in the FactMap
   * @return      the Double value of the Fact specified
   */
  @Deprecated
  public Double getDblVal(String name) {
    Object value = getValue(name);
    if (value != null) {
      if (Float.class == value.getClass()) {
        return Double.valueOf((Float) value);
      }
      if (Double.class == value.getClass()) {
        return (Double)value;
      }
      if (Integer.class == value.getClass()) {
        return Double.valueOf((Integer) value);
      }
      if (Long.class == value.getClass()) {
        return Double.valueOf((Long) value);
      }
      if (String.class == value.getClass()) {
        return Double.parseDouble((String)value);
      }
    }
    return null;
  }

  /**
   * The method setValue sets the value of the Fact by its name.<br/>
   * If no Fact exists with the associated name, a new fact is created with the specified name and value.<br/>
   * @param name  the name of the Fact
   * @param value the value object of the Fact
   */
  public void setValue(String name, T value) {
    NameValueReferable<T> fact = _facts.get(name);
    if (fact == null) {
      fact = new Fact<>(name, value);
      _facts.put(name, fact);
      return;
    }
    fact.setValue(value);
  }

  /**
   * This put() method is a convenience method for adding a Fact to a FactMap.<br/>
   * It uses the name of the Fact as the key and the Fact as the value.
   * @param fact  the Fact to be added to the FactMap
   * @return      the Fact that was just added
   */
  @Override
  public Fact<T> put(NameValueReferable<T> fact) {
    return put(fact.getName(), fact);
  }

  @Override
  public Fact<T> put(String key, NameValueReferable<T> fact) {
    Optional<NameValueReferable<T>> prev = Optional.ofNullable(_facts.put(key, fact));
    return prev.map(obj -> obj instanceof Fact ? (Fact<T>)obj : new Fact<>((NameValueReferable<T>)obj)).orElse(null);
  }

  @Override
  public int size() {
    return _facts.size();
  }

  @Override
  public boolean isEmpty() {
    return _facts.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return _facts.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return _facts.containsValue(value);
  }

  @Override
  public Fact<T> get(Object key) {
    NameValueReferable<T> obj = _facts.get(key);
    if (obj == null) {
      return null;
    }
    if (obj instanceof Fact) {
      return (Fact<T>)obj;
    }
    return new Fact<>(obj);
  }

  @Override
  public Fact<T> remove(Object key) {
    NameValueReferable<T> obj = _facts.remove(key);
    if (obj == null) {
      return null;
    }
    if (obj instanceof Fact) {
      return (Fact<T>)obj;
    }
    return new Fact<>(obj);
  }

  @Override
  public void putAll(Map<? extends String, ? extends NameValueReferable<T>> map) {
    _facts.putAll(map);
  }

  @Override
  public void clear() {
    _facts.clear();
  }

  @Override
  public Set<String> keySet() {
    return _facts.keySet();
  }

  @Override
  public Collection<NameValueReferable<T>> values() {
    return _facts.values();
  }

  @Override
  public Set<Entry<String, NameValueReferable<T>>> entrySet() {
    return _facts.entrySet();
  }

  /**
   * The toString() method gets the FactMap converted a string.<br/>
   * If there is only one Fact, the String value is the String value of the Fact.<br/>
   * Otherwise, the String value is the value of the parent {@link HashMap}.
   * @return  the String value of the FactMap
   */
  @Override
  public String toString() {
    return _facts.size() == 1 ? this.getOne().toString() : _facts.toString();
  }
}

