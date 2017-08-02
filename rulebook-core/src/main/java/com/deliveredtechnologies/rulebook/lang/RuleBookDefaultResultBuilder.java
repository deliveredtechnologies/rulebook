package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.function.Consumer;

/**
 * Builds the portion of the RuleBook that specifies a default result value.
 */
public class RuleBookDefaultResultBuilder<T> implements TerminatingRuleBookBuilder<T> {

  private RuleBook<T> _ruleBook;

  public RuleBookDefaultResultBuilder(RuleBook<T> ruleBook) {
    this._ruleBook = ruleBook;
  }

  /**
   * Adds a rule to the RuleBook.
   * @param consumer  functional interface that supplies a RuleBookRuleBuilder for building a Rule
   * @return          a builder with the added Rule
   */
  public RuleBookAddRuleBuilder<T> addRule(Consumer<RuleBookRuleBuilder<T>> consumer) {
    return new RuleBookAddRuleBuilder<>(_ruleBook, consumer);
  }

  /**
   * Adds a rule to the RuleBook.
   * @param rule  Rule to be added into the RuleBook
   * @param <U>   the fact type of the Rule
   * @return      RuleBookBuilder with the added Rule
   */
  public <U> RuleBookAddRuleBuilder<T> addRule(Rule<U, T> rule) {
    return new RuleBookAddRuleBuilder<>(_ruleBook, rule);
  }

  @Override
  public RuleBook<T> build() {
    return _ruleBook;
  }
}
