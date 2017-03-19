package com.deliveredtechnologies.rulebook;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Rule is an interface that uses the following format:
 * rule.given(facts).when(some condition given facts).then(do something)
 */
public interface Rule<T> {
  /**
   * The run() method evaluates the Rule.
   */
  void run();

  /**
   * The given() method sets the Facts to be used by the Rule.
   * @param name  name of the Fact
   * @param value object provided as the Fact with the given name
   * @return      the current Rule object
   */
  Rule<T> given(String name, T value);

  /**
   * The given() method sets the Facts to be used by the Rule.
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
   * @param test  the condition(s) to be evaluated against the Facts
   * @return      the current Rule object
   */
  Rule<T> when(Predicate<FactMap<T>> test);

  /**
   * The then() method performs some action based on facts.<br/>
   * This then() method does not imply a return value, only that the next chained then() is executed.
   * @param action  the action to be performed
   * @return        the current Rule object
   */
  Rule<T> then(Consumer<FactMap<T>> action);

  /**
   * Stops the rule chain after the then() method executes.<br/>
   * Note: this will only happen if the when() condition evaluates to true.
   */
  Rule<T> stop();

  /**
   * The using() method reduces the Facts used by the then() method to only the value of the Fact specified
   * by the factName.
   * @param factName  the name of the Fact value to be used by the then() method.
   * @return          thencurrent Rule object
   */
  Rule<T> using(String... factName);

  /**
   * Method getThen() gets the instance(s) of the functional interface(s) responsible for the action to be performed by
   * the Rule.
   * @return  a functional interface Object
   */
  Object getThen();

  /**
   * The setNextRule method adds the next Rule to the chain.
   * @param rule  the next Rule to add to the chain
   */
  void setNextRule(Rule<T> rule);
}
