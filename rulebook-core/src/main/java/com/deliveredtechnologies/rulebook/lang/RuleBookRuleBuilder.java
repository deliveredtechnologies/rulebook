package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

/**
 * Chains building of a Rule onto a RuleBookBuilder.
 * @param <U> the type of the Result
 */
public class RuleBookRuleBuilder<U> {
  private RuleBook<U> _ruleBook;
  private Class<? extends Rule> _ruleClass = GoldenRule.class;

  RuleBookRuleBuilder(RuleBook<U> ruleBook) {
    _ruleBook = ruleBook;
  }

  /**
   * Specifies the Rule type.
   * @param ruleType  the type (class) of Rule to build
   * @return          RuleBookRuleBuilder using the Rule type specified
   */
  public RuleBookRuleBuilder<U> withRuleType(Class<? extends Rule> ruleType) {
    _ruleClass = ruleType;
    return this;
  }

  public RuleBookAuditableRuleBuilder<U> withName(String ruleName) {
    return new RuleBookAuditableRuleBuilder<>(_ruleBook, _ruleClass, ruleName);
  }

  /**
   * Specifies the fact type
   * @param factType  the type of object that facts are restricted to in the Rule.
   * @param <T>       the generic fact type
   * @return          RuleBookRuleWithFactTypeBuilder for building RuleBook Rules with a specific fact type
   */
  @SuppressWarnings("unchecked")
  public <T> RuleBookRuleWithFactTypeBuilder<T, U> withFactType(Class<T> factType) {
    Rule<T, U> rule = (Rule<T, U>)RuleBuilder.create(_ruleClass).withFactType(factType).build();
    _ruleBook.addRule(rule);
    return new RuleBookRuleWithFactTypeBuilder<>(rule);
  }

  /**
   * Specifies no fact type.
   * A generic Object type is used for the Rule's fact type.
   * @return RuleBookRuleWithFactTypeBuilder for building RuleBook Rules with a specific fact type
   */
  public RuleBookRuleWithFactTypeBuilder<Object, U> withNoSpecifiedFactType() {
    return withFactType(Object.class);
  }
}
