package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

import java.util.Map;
import java.util.Optional;

/**
 * Created by clong on 9/3/17.
 */
public class RuleBookAuditor<T> extends Auditor implements RuleBook<T> {
  private RuleBook<T> _ruleBook;

  public RuleBookAuditor(RuleBook<T> ruleBook) {
    _ruleBook = ruleBook;
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
    if ( getRegisteredRules().size() == 0 ) {
      if ( _ruleBook instanceof RuleBookAuditor ) {
        RuleBookAuditor auditableRuleBook = (RuleBookAuditor) _ruleBook;
        if ( auditableRuleBook.getRegisteredRules().size() > 0 ) {
          this._auditMap = auditableRuleBook.getRegisteredRules();
        }
      }
    }
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
