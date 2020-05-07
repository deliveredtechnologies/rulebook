package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * The RuleBook interface for defining objects that handle the behavior of rules chained together.
 * @param <T> the Result type
 */
public interface RuleBook<T> {
  /**
   * Add a Rule.
   * @param rule  the Rule to add to the RuleBook
   */
  void addRule(Rule rule);

  /**
   * Run the RuleBook given the facts supplied.
   * @param facts the facts to be applied to the execution of the RuleBook
   */
  void run(NameValueReferableMap facts);

  /**
   * Set the default Result value; this should be set for RuleBooks that are expected to produce a Result.
   * @param result  the default Result value
   */
  void setDefaultResult(T result);


  /**
   * Set the default Result supplier function; this should be set for RuleBooks that are expected to produce a Result.
   * @param supplier the default Result value.
   */
  void setDefaultResult(Supplier<T> supplier);

  /**
   * Get the Result of the RuleBook.
   * @return  the Result of the RuleBook
   */
  Optional<Result<T>> getResult();

  /**
   * The defineRules method can be optionally implemented to define rules in a either a completely
   * custom RuleBook or in a RuleBook that extends (subclasses) an existing RuleBook implementation.
   */
  default void defineRules() { }

  /**
   * The hasRules() method returns true if any Rules have been defined in the RuleBook.
   * @return  true if any Rules have been defined in the RuleBook, otherwise false.
   */
  boolean hasRules();
}
