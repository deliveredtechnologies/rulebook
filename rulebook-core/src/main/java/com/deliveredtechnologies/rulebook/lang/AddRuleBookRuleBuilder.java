package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;


public class AddRuleBookRuleBuilder<T, U> {
  private RuleBook<U> _ruleBook;

  AddRuleBookRuleBuilder(RuleBook<U> ruleBook) {
    _ruleBook = ruleBook;
  }

  public <T> AddRuleBookRuleWithFactTypeBuilder<T, U> withRule(Rule<T, U> rule) {
    return new AddRuleBookRuleWithFactTypeBuilder<T, U>(_ruleBook, rule);
  }

  public <T> AddRuleBookRuleWithFactTypeBuilder<T, U> withFactType(Class<T> factType) {
    Rule<T, U> rule = new GoldenRule<T, U>(factType);
    _ruleBook.addRule(rule);
    return new AddRuleBookRuleWithFactTypeBuilder<T, U>(rule);
  }

  public AddRuleBookRuleWithFactTypeBuilder<Object, U> withNoSpecifiedFactType() {
    Rule<Object, U> rule = new GoldenRule<Object, U>(Object.class);
    _ruleBook.addRule(rule);
    return new AddRuleBookRuleWithFactTypeBuilder<Object, U>(rule);
  }
}
