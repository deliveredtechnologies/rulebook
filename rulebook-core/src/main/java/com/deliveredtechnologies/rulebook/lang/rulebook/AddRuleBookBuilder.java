package com.deliveredtechnologies.rulebook.lang.rulebook;

import com.deliveredtechnologies.rulebook.lang.rule.RuleBuilder;
import com.deliveredtechnologies.rulebook.lang.rule.TerminatingRuleBuilder;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.function.Function;

/**
 * Created by clong on 3/29/17.
 */
public class AddRuleBookBuilder<T, U> {
  private RuleBook<U> _ruleBook;

  AddRuleBookBuilder(RuleBook<U> ruleBook, Class<T> factType, Class<U> resultType,
                     Function<RuleBuilder<T, U>, TerminatingRuleBuilder> ruleFunction) {
    _ruleBook = ruleBook;
    addRule(factType, resultType, ruleFunction);
  }

  AddRuleBookBuilder(RuleBook<U> rulebook, Rule<T, U> rule) {
    _ruleBook = rulebook;
    addRule(rule);
  }

  @SuppressWarnings("unchecked")
  public AddRuleBookBuilder<T, U> addRule(Class<T> factType, Function<RuleBuilder<T, Object>, TerminatingRuleBuilder> ruleFunction) {
    RuleBuilder<T, Object> ruleBuilder = RuleBuilder.create(factType);
    TerminatingRuleBuilder terminatingRuleBuilder = ruleFunction.apply(ruleBuilder);
    return addRule(terminatingRuleBuilder.build());
  }

  @SuppressWarnings("unchecked")
  public AddRuleBookBuilder<T, U> addRule(Class<T> factType, Class<U> resultType, Function<RuleBuilder<T, U>, TerminatingRuleBuilder> ruleFunction) {
    RuleBuilder ruleBuilder = RuleBuilder.create(factType, resultType);
    TerminatingRuleBuilder terminatingRuleBuilder = ruleFunction.apply(ruleBuilder);
    return addRule(terminatingRuleBuilder.build());
  }

  public AddRuleBookBuilder<T, U> addRule(Rule<T, U> rule) {
    _ruleBook.addRule(rule);
    return this;
  }
}
