package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;


public class RuleBookRuleBuilder<U> {
  private RuleBook<U> _ruleBook;
  private Class<? extends Rule> _ruleClass = GoldenRule.class;

  RuleBookRuleBuilder(RuleBook<U> ruleBook) {
    _ruleBook = ruleBook;
  }

  public <T> RuleBookRuleWithFactTypeBuilder<T, U> withRule(Rule<T, U> rule) {
    _ruleBook.addRule(rule);
    return new RuleBookRuleWithFactTypeBuilder<>(rule);
  }

  public RuleBookRuleBuilder<U> withRuleType(Class<? extends Rule> ruleType) {
    _ruleClass = ruleType;
    return this;
  }

  public <T> RuleBookRuleWithFactTypeBuilder<T, U> withFactType(Class<T> factType) {
    Rule<T, U> rule = (Rule<T, U>)RuleBuilder.create(_ruleClass).withFactType(factType).build();
    _ruleBook.addRule(rule);
    return new RuleBookRuleWithFactTypeBuilder<>(rule);
  }

  public RuleBookRuleWithFactTypeBuilder<Object, U> withNoSpecifiedFactType() {
    Rule<Object, U> rule = (Rule<Object, U>)RuleBuilder.create(_ruleClass).build();
    _ruleBook.addRule(rule);
    return new RuleBookRuleWithFactTypeBuilder<>(rule);
  }
}
