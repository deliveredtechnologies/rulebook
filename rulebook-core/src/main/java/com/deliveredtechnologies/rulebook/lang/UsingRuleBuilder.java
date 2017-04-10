package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Builds the portion of a Rule that is available in the languate on 'using'.
 */
public class UsingRuleBuilder<T, U> {
  private Rule<T, U> _rule;

  UsingRuleBuilder(Rule<T, U> rule, String... factNames) {
    rule.addFactNameFilter(factNames);
    _rule = rule;
  }

  /**
   * Adds a then action into the Rule.
   * @param action  an action that the rule will execute based on the condition
   * @return        a builder that allows for the Rule to be built following the 'then' statement
   */
  public ThenRuleBuilder<T, U> then(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }

  /**
   * Addds a then action into the Rule.
   * @param action  an action that the rule will execute based on the condition; accepts facts and the result
   * @return        a builder that allows for the Rule to be built following the 'then' statement
   */
  public ThenRuleBuilder<T, U> then(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }

  /**
   * Adds a using constraint in the Rule that restricts the facts supplied to the subsequent 'then' action.
   * @param factNames the fact names to be supplied to the subsequent 'then' action
   * @return          a builder the allows for the Rule to be built following the 'using' statement
   */
  public UsingRuleBuilder<T, U> using(String... factNames) {
    _rule.addFactNameFilter(factNames);
    return this;
  }
}
