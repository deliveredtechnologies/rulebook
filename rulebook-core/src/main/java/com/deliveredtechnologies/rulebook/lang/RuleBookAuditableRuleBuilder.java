package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.AuditableRule;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

/**
 * A chained builder for building a Rule in the context of a RuleBook that can be audited.
 */
public class RuleBookAuditableRuleBuilder<U> {
  private RuleBook<U> _ruleBook;
  private Class<? extends Rule> _ruleClass;
  private String _ruleName;

  RuleBookAuditableRuleBuilder(RuleBook<U> ruleBook, Class<? extends Rule> ruleClass, String ruleName) {
    _ruleBook = ruleBook;
    _ruleClass = ruleClass;
    _ruleName = ruleName;
  }

  /**
   * Specifies the fact type
   * @param factType  the type of object that facts are restricted to in the Rule.
   * @param <T>       the generic fact type
   * @return          RuleBookRuleWithFactTypeBuilder for building RuleBook Rules with a specific fact type
   */
  @SuppressWarnings("unchecked")
  public <T> RuleBookRuleWithFactTypeBuilder<T, U> withFactType(Class<T> factType) {
    Rule<T, U> rule =
        new AuditableRule<T, U>((Rule<T, U>)RuleBuilder.create(_ruleClass).withFactType(factType).build(), _ruleName);
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
