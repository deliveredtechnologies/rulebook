package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by clong on 9/3/17.
 */
public class RuleBookAuditor<T> implements RuleBook<T>, Auditor {
  private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
  private RuleBook<T> _ruleBook;
  private Map<String, Map<Long, RuleStatus>> _auditMap = new HashMap<>();

  public RuleBookAuditor(RuleBook<T> ruleBook) {
    _ruleBook = ruleBook;
  }

  @Override
  public void registerRule(Auditable rule) {
    _lock.writeLock().lock();
    try {
      _auditMap.put(rule.getName(), new HashMap<>());
    } finally {
      _lock.writeLock().unlock();
    }
  }

  @Override
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

  @Override
  public RuleStatus getRuleStatus(String name) {
    return getRuleStatusMap().getOrDefault(name, RuleStatus.UNKNOWN);
  }

  @Override
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

  @Override
  public void addRule(Rule rule) {
    if (rule instanceof Auditable) {
      Auditable auditableRule = (Auditable)rule;
      registerRule(auditableRule);
      auditableRule.setAuditor(this);
    }
    _ruleBook.addRule(rule);
  }

  @Override
  public void run(NameValueReferableMap facts) {
    _ruleBook.run(facts);
  }

  @Override
  public void setDefaultResult(T result) {
    _ruleBook.setDefaultResult(result);
  }

  @Override
  public Optional<Result<T>> getResult() {
    return _ruleBook.getResult();
  }

  @Override
  public boolean hasRules() {
    return _ruleBook.hasRules();
  }
}
