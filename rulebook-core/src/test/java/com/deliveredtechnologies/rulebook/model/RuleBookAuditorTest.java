package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
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

  /**
   * Test to ensure that rules invoked using null facts don't error just because that facts are null.
   */
  @Test
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

  @Test
  @SuppressWarnings("unchecked")
  public void namedRulesInRuleBookAuditorBuiltByRuleBookBuilderAreAudited() {
    RuleBookAuditor ruleBookAuditor = (RuleBookAuditor) RuleBookBuilder.create()
        .withResultType(Integer.class).withDefaultResult(0)
        .asAuditor()
        .addRule(rule -> rule.withName("jaRule")
            .withFactType(String.class)
            .when(facts -> facts.getOne().equals("Fact Value"))
            .then((facts, result) -> result.setValue(100)).build()).build();

    // run facts
    FactMap facts = new FactMap();
    facts.setValue("fact", "Fact Value");

    ruleBookAuditor.run(facts);
    Assert.assertTrue(ruleBookAuditor.getResult().isPresent());
    Assert.assertEquals(100, ruleBookAuditor.getResult().map(result -> ((Result)result).getValue()).orElse(null));
    Assert.assertEquals(1, ruleBookAuditor.getRuleStatusMap().size());
    Assert.assertEquals(RuleStatus.EXECUTED, ruleBookAuditor.getRuleStatus("jaRule"));
  }
}
