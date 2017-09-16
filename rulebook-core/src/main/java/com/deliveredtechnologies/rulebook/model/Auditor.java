package com.deliveredtechnologies.rulebook.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by clong on 9/2/17.
 */
public abstract class Auditor {
  private Map<String, Map<Long, RuleStatus>> _auditMap = new HashMap<>();
  private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();

  public void registerRule(Auditable rule) {
    _lock.writeLock().lock();
    try {
      _auditMap.put(rule.getName(), new HashMap<>());
    } finally {
      _lock.writeLock().unlock();
    }
  }

  public void updateRuleStatus(Auditable rule, RuleStatus status) {
    _lock.readLock().lock();
    try {
      if (_auditMap.containsKey(rule.getName())) {
        _lock.readLock().unlock();
        _lock.writeLock().lock();
        try {
          _auditMap.get(rule.getName()).put(Thread.currentThread().getId(), status);
          _lock.readLock().lock();
        } finally {
          _lock.writeLock().unlock();
        }
      }
    } finally {
      _lock.readLock().unlock();
    }
  }

  public RuleStatus getRuleStatus(String name) {
    return getRuleStatusMap().getOrDefault(name, RuleStatus.NONE);
  }

  public Map<String, RuleStatus> getRuleStatusMap() {
    _lock.readLock().lock();
    try {
      return _auditMap.keySet().stream()
          .collect(
              Collectors.toMap(key -> key,
                  key -> _auditMap.get(key).getOrDefault(Thread.currentThread().getId(), RuleStatus.PENDING)));
    } finally {
      _lock.readLock().unlock();
    }
  }
}
