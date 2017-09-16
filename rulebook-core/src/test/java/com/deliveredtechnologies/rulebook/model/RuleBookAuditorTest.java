package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link RuleBookAuditor}.
 */
public class RuleBookAuditorTest {
  @Test
  public void ruleBookAuditorCanAuditRules() {
    RuleBook rulebook = RuleBookBuilder.create().asAuditor()
        .addRule(rule -> rule.withName("Rule1").withNoSpecifiedFactType()
          .when(facts -> true)
          .then(facts -> { } ))
        .addRule(rule -> rule.withName("Rule2").withNoSpecifiedFactType()
            .when(facts -> false)
            .then(facts -> { } ))
        .addRule(RuleBuilder.create().withName("Rule3")
            .when(facts -> true)
            .then(facts -> { } ).build())
        .build();

    rulebook.run(new FactMap());
    Auditor auditor = (Auditor)rulebook;

    Assert.assertEquals(auditor.getRuleStatus("Rule1"), RuleStatus.EXECUTED);
    Assert.assertEquals(auditor.getRuleStatus("Rule2"), RuleStatus.SKIPPED);
    Assert.assertEquals(auditor.getRuleStatus("Rule3"), RuleStatus.EXECUTED);
  }
}
