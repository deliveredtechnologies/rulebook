package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.BiConsumer;

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

  @SuppressWarnings("unchecked")
  public void rulesAreStillExecutedWithNullFacts() {
    Rule rule = new GoldenRule(Object.class);
    BiConsumer<NameValueReferableMap, Result> action = (facts, result)
        -> result.setValue("Rule was triggered with status=" + facts.get("status")
        + " and object=" + facts.get("object"));
    rule.addAction(action);

    AuditableRule auditableRule = new AuditableRule(rule, "SimpleRule");

    FactMap facts = new FactMap();
    facts.setValue("status", 1);
    facts.setValue("object", null);

    RuleBook ruleBook = new CoRRuleBook();

    RuleBookAuditor auditor = new RuleBookAuditor(ruleBook);
    auditor.addRule(auditableRule);
    ruleBook.run(facts);

    Assert.assertEquals(RuleStatus.EXECUTED, auditor.getRuleStatus("SimpleRule"));
  }
}
