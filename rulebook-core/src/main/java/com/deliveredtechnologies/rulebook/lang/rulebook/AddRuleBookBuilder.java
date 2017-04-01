package com.deliveredtechnologies.rulebook.lang.rulebook;

import com.deliveredtechnologies.rulebook.lang.rule.RuleBuilder;
import com.deliveredtechnologies.rulebook.lang.rule.TerminatingRuleBuilder;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.function.Function;

/**
 * Created by clong on 3/29/17.
 */
public class AddRuleBookBuilder<U> implements TerminatingRuleBookBuilder {
  private RuleBook<U> _ruleBook;
  private Class<U> _resultType;

  AddRuleBookBuilder(RuleBook<U> ruleBook, TerminatingRuleBuilder rule) {
    _ruleBook = ruleBook;
    addRule(rule);
  }

  AddRuleBookBuilder(RuleBook<U> ruleBook, Class<U> resultType, TerminatingRuleBuilder rule) {
    _ruleBook = ruleBook;
    _resultType = resultType;
    addRule(rule);
  }

  public AddRuleBookBuilder<U> addRule(TerminatingRuleBuilder rule) {
    _ruleBook.addRule(rule.build());
    return this;
  }

  @SuppressWarnings("unchecked")
  public <V, T extends U> AddRuleBookBuilder<T> addRule(
          Class<V> factType, Function<RuleBuilder<V, T>, TerminatingRuleBuilder<V, T>> function) {
    Class<T> resultType = (Class<T>)(_resultType == null ? Object.class : _resultType);
    _ruleBook.addRule(function.apply(RuleBuilder.create(factType, resultType)).build());
    return (AddRuleBookBuilder<T>)this;
  }

  @Override
  public RuleBook<U> build() {
    return _ruleBook;
  }
}
