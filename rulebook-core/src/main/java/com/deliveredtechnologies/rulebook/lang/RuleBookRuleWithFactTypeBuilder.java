package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A chained builder for building Rules in RuleBooks after a fact type has bee specified.
 */
public class RuleBookRuleWithFactTypeBuilder<T, U> {
  private  Rule<T, U> _rule;

  RuleBookRuleWithFactTypeBuilder(Rule<T, U> rule) {
    _rule = rule;
  }

  /**
   * Specifies the condition for the Rule.
   * @param condition the condition for the Rule
   * @return          a builder for building Rules with a specified condition
   */
  public WhenRuleBuilder<T, U> when(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) {
    return new WhenRuleBuilder<>(_rule, condition);
  }

  /**
   * Restricts the facts supplied to the subsequent 'then' based on the fact names supplied.
   * @param factNames the names of the facts to restrict to the subsequent 'then' action
   * @return          a builder for building Rules following a 'using' statement
   */
  public UsingRuleBuilder<T, U> using(String... factNames) {
    return new UsingRuleBuilder<>(_rule, factNames);
  }

  /**
   * Adds a 'then' action Consumer that provides facts to the Rule.
   * @param action  the 'then' action to be added
   * @return        a builder for building Rules following a 'then' action
   */
  public ThenRuleBuilder<T, U> then(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    return new ThenRuleBuilder<>(_rule, action);
  }

  /**
   * Adds a 'then' action Consumer that provides both facts and the result to the Rule.
   * @param action  the 'then' action to be added
   * @return        a builder for building Rules following a 'then' action
   */
  public ThenRuleBuilder<T, U> then(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    return new ThenRuleBuilder<>(_rule, action);
  }
}