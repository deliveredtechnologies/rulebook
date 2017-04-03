package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.RuleBook;

public class DefaultResultRuleBookBuilder<T> {
  private RuleBook<T> _ruleBook;

  DefaultResultRuleBookBuilder(RuleBook<T> ruleBook, Class<T> resultType, T result) {
    ruleBook.setDefaultResult(result);
    _ruleBook = ruleBook;
  }

  public TerminatingRuleBookBuilder<T> addRule(TerminatingRuleBuilder rule) {
    _ruleBook.addRule(rule.build());
    return () -> _ruleBook;
  }
}
