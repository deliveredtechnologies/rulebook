package com.deliveredtechnologies.rulebook;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A <code>Rule</code> is an interface that uses the following format:
 * rule.given(facts).when(some condition given facts).then(do something)
 */
public interface Rule<T> {
  /**
   * The run() method evaluates the Rule.
   */
  void run();

  /**
   * The given() method sets the Facts to be used by the Rule.
   *
   * @param facts Facts to be used by the Rule
   * @return the current Rule object
   */
  @SuppressWarnings("all")
  Rule<T> given(Fact<T>... facts);

  /**
   * The given() method sets the Facts to be used by the Rule.
   * @param facts     a List of Facts to be used by the Rule
   * @return the current Rule object
   */
  Rule<T> given(List<Fact<T>> facts);

  /**
   * The given() method sets the Facts to be used by the Rule.
   * @param facts     a {@link FactMap}
   * @return the current Rule object
   */
  Rule<T> given(FactMap<T> facts);

  /**
   * Method getWhen() gets the {@link Predicate} that evaluates the condition of the Rule.
   * @return  a Predicate object
   */
  Predicate<FactMap<T>> getWhen();

  /**
   * The when() method takes in a {@link Predicate} that evaluates the facts against a condition.
   * @param test      the condition(s) to be evaluated against the Facts
   * @return the current Rule object
   */
  Rule<T> when(Predicate<FactMap<T>> test);

  /**
   * Method getThen() gets an instance of a functional interface responsbile for the action to be performed by
   * the Rule.
   * @return  a functional interface Object
   */
  Object getThen();

  /**
   * The then() method performs some action based on facts and returns a {@link RuleState} of
   * either NEXT or BREAK. If NEXT is returned then the next rule in the chain is executed.
   * If BREAK is returned then the chain is broken and no more rules are executed in the chain.
   * @param action    the action to be performed
   * @return the current Rule object
   */
  Rule<T> then(Function<FactMap<T>, RuleState> action);

  /**
   * The setNextRule method adds the next Rule to the chain.
   * @param rule  the next Rule to add to the chain
   */
  void setNextRule(Rule<T> rule);
}
