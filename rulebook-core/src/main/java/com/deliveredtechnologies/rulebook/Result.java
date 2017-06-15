package com.deliveredtechnologies.rulebook;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This is a wrapper class for the actual result.
 * This is needed to ensure that an instance is available and that a reference is maintained to the actual that
 * can be manipulated in the then {@link java.util.function.BiFunction}.
 * The <code>Result</code> object can then contain a pointer to any instance, which can be shared with lamda calls
 * and across other objects (i.e. DecisionBook and Decision objects)
 */
public class Result<T> implements Referable<T> {
  private Map<Long, T> _valueMap = new HashMap<>();
  private final ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();

  public Result() {}

  public Result(T value) {
    _valueMap.put(Thread.currentThread().getId(), value);
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
      return _valueMap.get(Thread.currentThread().getId());
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
      if (_valueMap.containsKey(Thread.currentThread().getId())) {
        return _valueMap.get(Thread.currentThread().getId()).toString();
      }
      return "";
    } finally {
      _lock.readLock().unlock();
    }
  }
}
