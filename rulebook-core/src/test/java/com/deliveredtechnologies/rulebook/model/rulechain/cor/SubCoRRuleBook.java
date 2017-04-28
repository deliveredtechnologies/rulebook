package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.lang.RuleBuilder;

/**
 * Subclass of CoRRuleBook that defines rules.
 */
public class SubCoRRuleBook extends CoRRuleBook<String> {
  @Override
  public void defineRules() {
    addRule(RuleBuilder.create().withResultType(String.class)
        .then((facts, result) -> result.setValue("Success!")).build());
  }
}
