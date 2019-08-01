package com.deliveredtechnologies.rulebook.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Auditors are used for auditing rules. They maintain a record of each Rule and their state.
 */
public abstract class Auditor {
  protected Map<String, Map<Long, RuleStatus>> _auditMap = new HashMap<>();
  private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();

  public Map<String, Map<Long, RuleStatus>> getAudit() {
    return _auditMap;
  }
  
  /**
   * Registers a rule to be audited.
   * @param rule  and {@link Auditable} rule
   */
  public void registerRule(Auditable rule) {
    _lock.writeLock().lock();
    try {
      _auditMap.put(rule.getName(), new HashMap<>());
    } finally {
      _lock.writeLock().unlock();
    }
  }

  /**
   * Updates the status of the rule & stores the status with the Auditor.
   * @param rule    the rule in question
   * @param status  the status of the rule
   */
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

  /**
   * Gets the status of the rule with the given name.
   * @param name  the name of the rule
   * @return      the status of the rule
   */
  public RuleStatus getRuleStatus(String name) {
    return getRuleStatusMap().getOrDefault(name, RuleStatus.NONE);
  }

  /**
   * Gets a map of each rule name with its associated status.
   * @return  a map of rule names and their associated status
   */
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
