package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleBookAuditor;

/**
 * CoRRuleBook with auditing and rules defined in defineRules().
 * This class is for testing that rules created in defineRules can be audited.
 */
public class SubCoRRuleBookAuditor extends RuleBookAuditor<String> {

  public SubCoRRuleBookAuditor() {
    this(new CoRRuleBook<>());
  }

  public SubCoRRuleBookAuditor(RuleBook<String> ruleBook) {
    super(ruleBook);
  }

  @Override
  public void defineRules() {
    addRule(RuleBuilder.create().withName("First").withResultType(String.class)
        .then((facts, result) -> result.setValue("Success!")).build());
    addRule(RuleBuilder.create().withName("Second").withResultType(String.class)
        .when(facts -> false)
        .then((facts, result) -> result.setValue("Success!")).build());
  }
}
