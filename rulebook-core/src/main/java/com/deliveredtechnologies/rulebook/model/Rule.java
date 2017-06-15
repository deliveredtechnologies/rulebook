package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.NameValueReferable;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The Rule interface used for specifying Rules that can be grouped together to create RuleBooks.
 */
public interface Rule<T, U> {
  /**
   * Adds facts to a Rule.
   * @param facts the facts to be added to the Rule
   */
  void addFacts(NameValueReferable... facts);

  /**
   * Adds facts to a Rule.
   * @param facts the map of facts to be added to the Rule
   */
  void addFacts(NameValueReferableMap facts);

  /**
   * Sets the facts for a Rule.
   * @param facts the map object to be used for facts in the Rule
   */
  void setFacts(NameValueReferableMap facts);

  /**
   * Sets the condition for the Rule
   * @param condition the true or false condition implementation for the Rule.
   */
  void setCondition(Predicate<NameValueReferableTypeConvertibleMap<T>> condition);

  /**
   * Sets the {@link RuleState} for the Rule; determines whether the rule chain should continue or not.
   * @param ruleState the new RuleState to be set
   */
  void setRuleState(RuleState ruleState);

  /**
   * Adds an action to the Rule to be invoked if the condition evaluates to true.
   * @param action  the specified action using facts
   */
  void addAction(Consumer<NameValueReferableTypeConvertibleMap<T>> action);

  /**
   * Adds an action to the Rule to be invoked if the condition evaluates to true.
   * @param action  the specified action using facts and a Result
   */
  void addAction(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action);

  /**
   * Adds a filter to the facts by name.
   * @param factNames the names of the facts filtered (i.e. to be included)
   */
  void addFactNameFilter(String... factNames);

  /**
   * Gets the facts maintained in the Rule.
   * @return  a map of the facts applied to the Rule
   */
  NameValueReferableMap getFacts();

  /**
   * Gets the condition specified for the Rule.
   * @return  the condition specified for the Rule
   */
  Predicate<NameValueReferableTypeConvertibleMap<T>> getCondition();

  /**
   * Gets the RuleState of the Rule.
   * @return  the RuleState of the Rule
   */
  RuleState getRuleState();

  /**
   * Gets an ordered list of the actions added to the Rule.
   * @return  an ordered list of the actions added to the Rule
   */
  List<Object> getActions();

  /**
   * Invokes the Rule; defaults to calling invoke(facts) using the facts assigned to
   * the Rule.
   * @return  true if the the action(s) were executed, otherwise false
   */
  default boolean invoke() {
    return invoke(getFacts());
  }

  /**
   * Invokes the Rule using the given facts in place of the facts assigned to the Rule.
   * @param facts the facts to be used in the invocation of the Rule
   * @return      true if the action(s) were executed, otherwise false
   */
  boolean invoke(NameValueReferableMap facts);

  /**
   * Sets the Result of the Rule.
   * @param result  the Result of the Rule
   */
  void setResult(Result<U> result);

  /**
   * Gets the Result of the Rule
   * @return  the Optional Result of the Rule, otherwise Optional.empty()
   */
  Optional<Result<U>> getResult();
}
