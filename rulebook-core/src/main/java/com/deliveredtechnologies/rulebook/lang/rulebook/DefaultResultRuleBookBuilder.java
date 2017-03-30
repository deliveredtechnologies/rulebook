package com.deliveredtechnologies.rulebook.lang.rulebook;

import com.deliveredtechnologies.rulebook.lang.rule.RuleBuilder;
import com.deliveredtechnologies.rulebook.lang.rule.TerminatingRuleBuilder;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.function.Function;

/**
 * Created by clong on 3/29/17.
 */
public class DefaultResultRuleBookBuilder<T> {
  private RuleBook<T> _ruleBook;

  DefaultResultRuleBookBuilder(RuleBook<T> ruleBook, T result) {
    ruleBook.setDefaultResult(result);
    _ruleBook = ruleBook;
  }

  @SuppressWarnings("unchecked")
  public AddRuleBookBuilder<T, Object> addRule(Class<T> factType, Function<RuleBuilder<T, Object>, TerminatingRuleBuilder> ruleFunction) {
    return new AddRuleBookBuilder<T, Object>((RuleBook<Object>)_ruleBook, factType, Object.class, ruleFunction);
  }

  @SuppressWarnings("unchecked")
  public <U, V extends T> AddRuleBookBuilder<U, V> addRule(Class<U> factType, Class<V> resultType, Function<RuleBuilder<U, V>, TerminatingRuleBuilder> ruleFunction) {
    return new AddRuleBookBuilder<U, V>((RuleBook<V>)_ruleBook, factType, resultType, ruleFunction);
  }

  public <U, V extends T> AddRuleBookBuilder<U, V> addRule(Rule<U, V> rule) {
    return new AddRuleBookBuilder<U, V>((RuleBook<V>)_ruleBook, rule);
  }
}
