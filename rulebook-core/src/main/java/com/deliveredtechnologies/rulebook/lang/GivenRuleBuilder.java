package com.deliveredtechnologies.rulebook.lang;


import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.NameValueReferable;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Builds the portion of a Rule that is available in the language on 'given.'
 */
public class GivenRuleBuilder<T, U> {
  Rule<T, U> _rule;

  GivenRuleBuilder(Rule<T, U> rule, NameValueReferableMap facts) {
    _rule = rule;
    given(facts);
  }

  @SafeVarargs
  GivenRuleBuilder(Rule<T, U> rule, NameValueReferable... facts) {
    _rule = rule;
    given(facts);
  }

  /**
   * Builds a fact into the Rule.
   * @param name  the name of the fact
   * @param value the value of the fact
   * @return      a GivenRuleBuilder
   */
  GivenRuleBuilder<T, U> given(String name, T value) {
    return given(new Fact<T>(name, value));
  }

  /**
   * Builds one or more facts into the Rule using a {@link NameValueReferableMap}.
   * @param   facts the facts to be added to the Rule
   * @return  the current builder object
   */
  public final GivenRuleBuilder<T, U> given(NameValueReferableMap facts) {
    _rule.setFacts(facts);
    return this;
  }

  /**
   * Builds one or more facts into the Rule.
   * @param   facts one or more facts
   * @return  the current builder object
   */
  @SafeVarargs
  public final GivenRuleBuilder<T, U> given(NameValueReferable... facts) {
    _rule.addFacts(facts);
    return this;
  }

  /**
   * Builds a condition into the Rule.
   * @param condition the condition specified for the Rule
   * @return          a builder that allows for the Rule to be built following the condition
   */
  public WhenRuleBuilder<T, U> when(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) {
    return new WhenRuleBuilder<T, U>(_rule, condition);
  }

  /**
   * Builds a using constraint into the Rule that restricts the facts supplied to the subsequent 'then' action.
   * @param factNames the fact names to be supplied to the subsequent 'then' action
   * @return          a builder the allows for the Rule to be built following the 'using' statement
   */
  public UsingRuleBuilder<T, U> using(String... factNames) {
    return new UsingRuleBuilder<T, U>(_rule, factNames);
  }

  /**
   * Builds a then action into the Rule.
   * @param action  an action that the rule will execute based on the condition
   * @return        a builder that allows for the Rule to be built following the 'then' statement
   */
  public ThenRuleBuilder<T, U> then(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }

  /**
   * Builds a then action into the Rule.
   * @param action  an action that the rule will execute based on the condition; accepts facts and the result
   * @return        a builder that allows for the Rule to be built following the 'then' statement
   */
  public ThenRuleBuilder<T, U> then(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }
}
