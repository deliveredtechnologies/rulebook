package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;


public class AddRuleBookRuleBuilder<U> {
  private RuleBook<U> _ruleBook;

  AddRuleBookRuleBuilder(RuleBook<U> ruleBook) {
    _ruleBook = ruleBook;
  }

  public <T> AddRuleBookRuleWithFactTypeBuilder<T, U> withRule(Rule<T, U> rule) {
    _ruleBook.addRule(rule);
    return new AddRuleBookRuleWithFactTypeBuilder<>(rule);
  }

  public <T> AddRuleBookRuleWithFactTypeBuilder<T, U> withFactType(Class<T> factType) {
    Rule<T, U> rule = new GoldenRule<>(factType);
    _ruleBook.addRule(rule);
    return new AddRuleBookRuleWithFactTypeBuilder<>(rule);
  }

  public AddRuleBookRuleWithFactTypeBuilder<Object, U> withNoSpecifiedFactType() {
    Rule<Object, U> rule = new GoldenRule<>(Object.class);
    _ruleBook.addRule(rule);
    return new AddRuleBookRuleWithFactTypeBuilder<>(rule);
  }
}
