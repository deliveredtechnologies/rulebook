package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Decorates RuleBook with auditing functionality.
 */
public class RuleBookAuditor<T> extends Auditor implements RuleBook<T> {
  private RuleBook<T> _ruleBook;

  /**
   * Costructor - Decorates the supplied RuleBook; if a RuleBookAuditor is supplied, it assumes its audited rules.
   * @param ruleBook  RuleBook to decorate with auditing functionality
   */
  public RuleBookAuditor(RuleBook<T> ruleBook) {
    _ruleBook = ruleBook;
    if ( _ruleBook instanceof RuleBookAuditor ) {
      RuleBookAuditor auditableRuleBook = (RuleBookAuditor) _ruleBook;
      _auditMap = auditableRuleBook._auditMap;
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
    if (!hasRules()) {
      defineRules();
    }
    _ruleBook.run(facts);
  }

  @Override
  public void setDefaultResult(T result) {
    _ruleBook.setDefaultResult(result);
  }

  @Override
  public void setDefaultResult(Supplier<T> result) {
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
