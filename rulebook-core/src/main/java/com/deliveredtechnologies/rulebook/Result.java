package com.deliveredtechnologies.rulebook;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * This is a wrapper class for the actual result.
 * This is needed to ensure that an instance is available and that a reference is maintained to the actual that
 * can be manipulated in the then {@link java.util.function.BiFunction}.
 * The <code>Result</code> object can then contain a pointer to any instance, which can be shared with lamda calls
 * and across other objects (i.e. DecisionBook and Decision objects)
 */
public class Result<T> implements Referable<T> {
  private final Map<Long, T> _valueMap = new HashMap<>();
  private final ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
  private T _defaultValue = null;
  private Supplier<T> _supplier = null;

  public Result() {}

  /**
   * Creates an instance of Result with a default value.
   *
   * @param value the default value.
   */
  public Result(T value) {
    _defaultValue = value;
  }

  /**
   * Creates an instance of Result with a default supplier function.
   *
   * @param supplier the default value.
   */
  public Result(Supplier<T> supplier) {
    if (supplier != null) {
      _supplier = supplier;
      _defaultValue = supplier.get();
    }
  }


  /**
   * Resets the value of the Result to its default value.
   */
  public void reset() {
    _lock.readLock().lock();
    try {
      if (_defaultValue == null) {
        return;
      }
    } finally {
      _lock.readLock().unlock();
    }
    if (_supplier == null) {
      setValue(_defaultValue);
    } else {
      // Create new reference
      setValue(_supplier.get());
    }
  }

  /**
   * The method getValue() returns the object contained in the Result object.
   *
   * @return the object stored in the Result object
   */
  @Override
  public T getValue() {
    _lock.readLock().lock();
    try {
      long key = Thread.currentThread().getId();
      if (_valueMap.containsKey(key)) {
        return _valueMap.get(Thread.currentThread().getId());
      }
      return _defaultValue;
    } finally {
      _lock.readLock().unlock();
    }
  }

  /**
   * The method setValue() sets the object to be contained in the Result object.
   *
   * @param value the object to be stored in the Result object
   */
  @Override
  public void setValue(T value) {
    _lock.writeLock().lock();
    try {
      _valueMap.put(Thread.currentThread().getId(), value);
    } finally {
      _lock.writeLock().unlock();
    }
  }

  @Override
  public String toString() {
    _lock.readLock().lock();
    try {
      long key = Thread.currentThread().getId();
      if (_valueMap.containsKey(key)) {
        return _valueMap.get(key).toString();
      }
      if (_defaultValue != null) {
        return _defaultValue.toString();
      }
      return "";
    } finally {
      _lock.readLock().unlock();
    }
  }
}
