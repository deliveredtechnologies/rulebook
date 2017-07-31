package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.function.Consumer;

/**
 * Created by clayton.long on 7/31/17.
 */
public class RuleBookAddRuleBuilder<T> implements TerminatingRuleBookBuilder<T> {

  private RuleBook<T> _ruleBook;

  RuleBookAddRuleBuilder(RuleBook<T> ruleBook) {
    _ruleBook = ruleBook;
  }

  RuleBookAddRuleBuilder(RuleBook<T> ruleBook, Consumer<RuleBookRuleBuilder<T>> consumer) {
    this(ruleBook);
    addRule(consumer);
  }

  <U> RuleBookAddRuleBuilder(RuleBook<T> ruleBook, Rule<U, T> rule) {
    this(ruleBook);
    addRule(rule);
  }

  /**
   * Adds a rule to the RuleBook.
   * @param consumer  functional interface that supplies a RuleBookRuleBuilder for building a Rule
   * @return          a builder with the added Rule
   */
  public RuleBookAddRuleBuilder<T> addRule(Consumer<RuleBookRuleBuilder<T>> consumer) {
    consumer.accept(new RuleBookRuleBuilder<>(_ruleBook));
    return this;
  }

  /**
   * Adds a rule to the RuleBook.
   * @param rule  Rule to be added into the RuleBook
   * @param <U>   the fact type of the Rule
   * @return      RuleBookBuilder with the added Rule
   */
  public <U> RuleBookAddRuleBuilder<T> addRule(Rule<U, T> rule) {
    _ruleBook.addRule(rule);
    return this;
  }

  @Override
  public RuleBook<T> build() {
    return _ruleBook;
  }
}
