package com.deliveredtechnologies.rulebook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * StandardDecision is the standard implementation of {@link Decision}.
 */
@Deprecated
public class StandardDecision<T, U> implements Decision<T, U> {
  private static Logger LOGGER = LoggerFactory.getLogger(StandardDecision.class);

  private Rule<T> _rule;

  private Result<U> _result = new Result<>();

  public StandardDecision(Class<T> factClazz, Class<U> resultClazz) {
    _rule = new StandardRule<T>(factClazz);
  }

  /**
   * This create() method is a convenience method to avoid using new and generic syntax.
   *
   * @param factType    the type of object stored in facts for this Decision
   * @param resultType  the type of object stored in the Result
   * @param <T>         the class type of the objects in the Facts used
   * @param <U>         the class type of object stored in the Result
   *
   * @return a new instance of StandardDecision
   */
  public static <T, U> StandardDecision<T, U> create(Class<T> factType, Class<U> resultType) {
    return new StandardDecision<T, U>(factType, resultType);
  }

  /**
   * This create() method is another convenience method to create a non-type specific StandardDecision.
   * @return a new instance of StandardDecision
   */
  public static StandardDecision<Object, Object> create() {
    return new StandardDecision<Object, Object>(Object.class, Object.class);
  }

  /**
   * The run() method runs the {@link Predicate} supplied by the when() method.<br/>
   * If it evaluates to true then the {@link BiConsumer}(s) and {@link Consumer}(s) supplied by the then() method(s)
   * are executed.<br/>
   * If the stop() method was invoked then no further rules are evaluated.<br/>
   * Otherwise, the next rule in the chain is evaluated.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void run(Object... args) {
    _rule.run(_result);
  }

  /**
   * The given() method accepts a name/value pair to be used as a Fact.
   * @param name  name of the Fact
   * @param value object provided as the Fact with the given name
   * @return      the current object
   */
  @Override
  public Decision<T, U> given(String name, T value) {
    _rule.given(name, value);
    return this;
  }

  /**
   * The given() method accepts Facts for the StandardDecision.
   * @param facts Facts to be used by the Rule
   * @return      the current object for chaining other methods
   */
  @Override
  public Decision<T, U> given(Fact<T>... facts) {
    _rule.given(facts);
    return this;
  }

  /**
   * The given() method accepts Facts for the StandardDecision.
   * @param facts a List of Facts to be used by the Rule
   * @return      the current object
   */
  @Override
  public Decision<T, U> given(List<Fact<T>> facts) {
    _rule.given(facts);
    return this;
  }

  /**
   * The given() method accepts Facts for the StandardDecision.
   * @param facts a {@link FactMap}
   * @return      the current object
   */
  @Override
  public Decision<T, U> given(FactMap<T> facts) {
    _rule.given(facts);
    return this;
  }

  @Override
  public Decision<T, U> givenUnTyped(FactMap facts) {
    return null;
  }

  @Override
  public FactMap getFactMap() {
    return _rule.getFactMap();
  }

  /**
   * The when() method accepts a {@link Predicate} that returns true or false based on Facts.
   * @param test  the condition(s) to be evaluated against the Facts
   * @return      true, if the then() statement(s) should be evaluated, otherwise false
   */
  @Override
  public Decision<T, U> when(Predicate<FactMap<T>> test) {
    _rule.when(test);
    return this;
  }

  /**
   * The then() method accepts a {@link BiConsumer} that performs an action based on Facts.<br/>
   * The arguments of the BiConsumer are a {@link FactMap} and a {@link Result}, respectively.
   * @param action  the action to be performed
   * @return        the current object
   */
  @Override
  public Decision<T, U> then(BiConsumer<FactMap<T>, Result<U>> action) {
    _rule.getThen().add(action);
    return this;
  }

  /**
   * The then() method accepts a {@link Consumer} that performs an action based on Facts.
   * @param action  the action to be performed
   * @return        the current object
   */
  @Override
  public Decision<T, U> then(Consumer<FactMap<T>> action) {
    _rule.then(action);
    return this;
  }

  /**
   * The stop() method causes the rule chain to stop if the when() condition true and only
   * after the then() actions have been executed.
   * @return  the current object
   */
  @Override
  public Decision<T, U> stop() {
    _rule.stop();
    return this;
  }

  /**
   * The using() method reduces the facts to those specifically named here.
   * The using() method only applies to the then() method immediately following it.
   * @param factNames the names of the facts to be used
   * @return          the current object
   */
  @Override
  @SuppressWarnings("unchecked")
  public Decision<T, U> using(String... factNames) {
    _rule.using(factNames);
    return this;
  }

  /**
   * The setNextRule() method sets the next rule in the chain.
   * @param rule  the next Rule to add to the chain
   */
  @Override
  public void setNextRule(Rule<T> rule) {
    _rule.setNextRule(rule);
  }

  /**
   * The getResult() method gets the stored Result value from the execution of the StandardDecision.
   * @return the stored Result value
   */
  @Override
  public U getResult() {
    return _result.getValue();
  }

  /**
   * The setResult() method sets the stored Result.
   * @param result the instantiated result
   */
  @Override
  public void setResult(Result<U> result) {
    _result = result;
  }

  /**
   * The getWhen() method returns the {@link Predicate} to be used for the condition of the Rule.
   * @return  the Predicate used for the condition
   */
  @Override
  public Predicate<FactMap<T>> getWhen() {
    return _rule.getWhen();
  }

  /**
   * The getThen() method returns a {@link List} of {@link Consumer} objects that combined
   * together in sequence represent the then() action(s).
   * @return  a List of Consumer and/or BiConsumer objects
   */
  @Override
  public List<Object> getThen() {
    return _rule.getThen();
  }
}
