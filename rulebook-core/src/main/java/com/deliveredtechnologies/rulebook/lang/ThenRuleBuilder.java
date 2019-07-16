package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Builds the portion of a Rule that is available in the languate on 'then'.
 */
public class ThenRuleBuilder<T, U> implements TerminatingRuleBuilder<T, U> {
  Rule<T, U> _rule;

  ThenRuleBuilder(Rule<T, U> rule, Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    _rule = rule;
    _rule.addAction(action);
  }

  ThenRuleBuilder(Rule<T, U> rule, BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    _rule = rule;
    _rule.addAction(action);
  }

  /**
   * Adds a using constraint in the Rule that restricts the facts supplied to the subsequent 'then' action.
   * @param factNames the fact names to be supplied to the subsequent 'then' action
   * @return          a builder the allows for the Rule to be built following the 'using' statement
   */
  public UsingRuleBuilder<T, U> using(String... factNames) {
    return new UsingRuleBuilder<T, U>(_rule, factNames);
  }

  /**
   * Adds a then action into the Rule.
   * @param action  an action that the rule will execute based on the condition
   * @return        a builder that allows for the Rule to be built following the 'then' statement
   */
  public ThenRuleBuilder<T, U> then(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    _rule.addAction(action);
    return this;
  }

  /**
   * Addds a then action into the Rule.
   * @param action  an action that the rule will execute based on the condition; accepts facts and the result
   * @return        a builder that allows for the Rule to be built following the 'then' statement
   */
  public ThenRuleBuilder<T, U> then(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    _rule.addAction(action);
    return this;
  }

  /**
   * Sends a signal that the rule chain should be broken.
   * @return  a TerminatingRuleBuilder that only allows for the Rule to be built following stop()
   */
  public TerminatingRuleBuilder<T, U> stop() {
    // Because when we use POJO Rules we may want to manually set a BREAK
    // we have to use another RuleState called POJOMANUALBREAK
    // otherwise we'll ignore the BREAK in the Invoke
    _rule.setRuleState(RuleState.BREAK);
    return this;
  }

  /**
   * Builds the Rule.
   * @return  a Rule
   */
  public Rule<T, U> build() {
    return _rule;
  }

}
