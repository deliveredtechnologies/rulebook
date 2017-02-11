package com.deliveredtechnologies.rulebook;

import static com.deliveredtechnologies.rulebook.RuleState.BREAK;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by clong on 2/6/17.
 * StandardRule is a standard rule implementation that can be used with a {@link RuleBook}.
 */
public class StandardRule<T> implements Rule<T> {
  private Rule<T> _nextRule;
  private FactMap<T> _facts = new FactMap<>();
  private Predicate<FactMap<T>> _test;
  private Function<FactMap<T>, RuleState> _action;

  public StandardRule() {
  }

  /**
   * This create() method is a convenience method to avoid using new and generic syntax.
   *
   * @param factType the type of object stored in facts for this Rule
   * @param <T>      the type of Fact
   * @return a new instance of a StandardRule
   */
  public static <T> StandardRule<T> create(Class<T> factType) {
    return new StandardRule<>();
  }

  /**
   * This create() method is a convenience method to create a non-type specific StandardRule.
   * @return a new instance of a StandardRule
   */
  public static StandardRule<Object> create() {
    return new StandardRule<>();
  }

  /**
   * The run() method runs the {@link Predicate} supplied by the when() method. If it evaluates to true then
   * the {@link Function} supplied by the then() method is executed. If the then()
   * method returns a BREAK {@link RuleState} then no further rules are evaluated. Otherwise, the next rule in the
   * chain is evaluated.
   */
  @Override
  public void run() {
    if (_test.test(_facts)) {
      if (_action.apply(_facts) == BREAK) {
        return;
      }
    }
    if (Optional.ofNullable(this._nextRule).isPresent()) {
      this._nextRule.run();
    }
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   * @param facts     Facts to be used by the Rule
   * @return the current object
   */
  @Override
  public Rule<T> given(Fact<T>... facts) {
    for (Fact f : facts) {
      _facts.put(f.getName(), f);
    }

    return this;
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   *
   * @param facts a List of Facts to be used by the Rule
   * @return the current object
   */
  @Override
  public Rule<T> given(List<Fact<T>> facts) {
    for (Fact f : facts) {
      _facts.put(f.getName(), f);
    }

    return this;
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   * @param facts     a {@link FactMap}
   * @return the current object
   */
  @Override
  public Rule<T> given(FactMap<T> facts) {
    _facts = facts;
    return this;
  }

  /**
   * The when() method accepts a {@link Predicate} that returns true or false based on Facts.
   * @param test      the condition(s) to be evaluated against the Facts
   * @return the current object
   */
  @Override
  public Rule<T> when(Predicate<FactMap<T>> test) {
    _test = test;
    return this;
  }

  /**
   * The then() method accepts a {@link Function} that performs an action based on Facts and then returns a
   * {@link RuleState} of either NEXT or BREAK.
   *
   * @param action the action to be performed
   * @return the current object
   */
  @Override
  public Rule<T> then(Function<FactMap<T>, RuleState> action) {
    _action = action;
    return this;
  }

  /**
   * The setNextRule() method sets the next Rule in the chain.
   *
   * @param rule the next Rule to add to the chain
   */
  @Override
  public void setNextRule(Rule<T> rule) {
    _nextRule = rule;
  }
}
