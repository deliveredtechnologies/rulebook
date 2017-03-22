package com.deliveredtechnologies.rulebook;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A special type of rule that has a return type, which may be different from the type of input (Facts).
 */
public interface Decision<T, U> extends Rule<T> {

  //Overriding these methods for method chaining to work with this interface

  @Override
  Decision<T, U> given(String name, T value);

  @Override
  Decision<T, U> given(Fact<T>... facts);

  @Override
  Decision<T, U> given(List<Fact<T>> facts);

  @Override
  Decision<T, U> given(FactMap<T> facts);

  @Override
  Decision<T, U> givenUnTyped(FactMap facts);

  @Override
  Decision<T, U> when(Predicate<FactMap<T>> test);

  @Override
  Decision<T, U> then(Consumer<FactMap<T>> action);

  /**
   * The then method specifies the action taken if <code>when()</code> evaluates to true.
   * The execution of this 'then' takes precedence over <code>then(Function)</code> if both exist.
   *
   * @param action the action to be performed
   * @return the current <code>Rule</code> object
   */
  Decision<T, U> then(BiConsumer<FactMap<T>, Result<U>> action);

  @Override
  Decision<T, U> using(String... factName);

  @Override
  Decision<T, U> stop();

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
