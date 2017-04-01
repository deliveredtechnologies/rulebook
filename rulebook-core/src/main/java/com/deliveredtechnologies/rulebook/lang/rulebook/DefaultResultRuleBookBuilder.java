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
  private Class<T> _resultType;

  DefaultResultRuleBookBuilder(RuleBook<T> ruleBook, Class<T> resultType, T result) {
    ruleBook.setDefaultResult(result);
    _ruleBook = ruleBook;
    _resultType = resultType;
  }

  @SuppressWarnings("unchecked")
  public <V, U extends T> AddRuleBookBuilder<U> addRule(
          Class<V> factType, Function<RuleBuilder<V, U>, TerminatingRuleBuilder<V, U>> function) {
    Class<U> resultType = (Class<U>)(_resultType == null ? Object.class : _resultType);
    return new AddRuleBookBuilder<U>(
            (RuleBook<U>)_ruleBook,
            resultType,
            function.apply(RuleBuilder.create(factType, resultType)));
  }

  public AddRuleBookBuilder<T> addRule(TerminatingRuleBuilder rule) {
    return new AddRuleBookBuilder<T>(_ruleBook, rule);
  }
}
