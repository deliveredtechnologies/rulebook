package com.deliveredtechnologies.rulebook;

import java.util.function.BiFunction;

/**
 * Created by clong on 2/6/17.
 * A special type of rule that has a return type, which may be different from the type of input (Facts).
 */
public interface Decision<T, U> extends Rule<T> {

  /**
   * The then method specifies the action taken if <code>when()</code> evaluates to true.
   * The execution of this 'then' takes precedence over <code>then(Function)</code> if both exist.
   *
   * @param action the action to be performed
   * @return the current <code>Rule</code> object
   */
  Decision<T, U> then(BiFunction<FactMap<T>, Result<U>, RuleState> action);

  /**
   * The <code>getResult()</code> method returns the stored result from the Decision
   * The value may be null.
   *
   * @return the result object
   */
  U getResult();

  /**
   * The <code>setResult()</code> method initializes the stored result; useful for aggregation/chaining of a result.
   *
   * @param result the instantiated result
   */
  void setResult(Result<U> result);
}
