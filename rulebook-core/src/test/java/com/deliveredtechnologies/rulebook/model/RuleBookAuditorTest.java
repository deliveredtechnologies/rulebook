package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link RuleBookAuditor}.
 */
public class RuleBookAuditorTest {

  @Test
  public void ruleBookAuditorCanAuditRulesWhenCreatedThroughAClassDef() {  
    RuleBook rulebook = RuleBookBuilder.create(SimpleRuleAdds.class).asAuditor()  
        .build();
        
    rulebook.run(new FactMap());
    Auditor auditor = (Auditor)rulebook;

    Assert.assertEquals(auditor.getRuleStatus("Rule1"), RuleStatus.EXECUTED);
    Assert.assertEquals(auditor.getRuleStatus("Rule2"), RuleStatus.SKIPPED);
    Assert.assertEquals(auditor.getRuleStatus("Rule3"), RuleStatus.EXECUTED);
  }
  
    
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
    Rule<Object, Object> rule = new GoldenRule(Object.class);
    rule.addAction((facts, result)
        -> result.setValue("Rule was triggered with status=" + facts.get("status")
        + " and object=" + facts.get("object")));

    AuditableRule auditableRule = new AuditableRule(rule, "SimpleRule");

    FactMap facts = new FactMap();
    facts.setValue("status", 1);
    facts.setValue("object", null);

    RuleBook ruleBook = new CoRRuleBook();

    RuleBookAuditor auditor = new RuleBookAuditor(ruleBook);
    auditor.addRule(auditableRule);
    auditor.run(new FactMap());

    Assert.assertEquals(RuleStatus.EXECUTED, auditor.getRuleStatus("SimpleRule"));
  }

  /**
   * Test to ensure that rules that error on a failure are shown in a PENDING status.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void errorOnFailureRulesResultInErrorStatus() {
    Rule<Object, Object> rule1 = new GoldenRule(Object.class);
    rule1.addAction((facts, result)
        -> result.setValue("Rule was triggered with status=" + facts.get("status")
        + " and object=" + facts.get("object")));

    Rule<Object, Object> rule2 = new GoldenRule(Object.class, RuleChainActionType.ERROR_ON_FAILURE);
    rule2.setCondition(stuff -> stuff.getValue("invalid").equals("something"));

    Rule<Object, Object> rule3 = new GoldenRule(Object.class, RuleChainActionType.ERROR_ON_FAILURE);

    AuditableRule auditableRule1 = new AuditableRule(rule1, "SimpleRule");
    AuditableRule auditableRule2 = new AuditableRule(rule2, "ErrorRule");
    AuditableRule auditableRule3 = new AuditableRule(rule3, "AfterErrorRule");

    FactMap facts = new FactMap();
    facts.setValue("status", 1);
    facts.setValue("object", null);

    RuleBook ruleBook = new CoRRuleBook();

    RuleBookAuditor auditor = new RuleBookAuditor(ruleBook);
    auditor.addRule(auditableRule1);
    auditor.addRule(auditableRule2);
    auditor.addRule(auditableRule3);

    try {
      ruleBook.run(facts);
    } catch (Exception e) {
      Assert.assertEquals(RuleStatus.EXECUTED, auditor.getRuleStatus("SimpleRule"));
      Assert.assertEquals(RuleStatus.ERROR, auditor.getRuleStatus("ErrorRule"));
      Assert.assertEquals(RuleStatus.PENDING, auditor.getRuleStatus("AfterErrorRule"));
      Assert.assertTrue(e instanceof RuleException);
      return;
    }
    Assert.fail();
  }

  /**
   * Test to confirm audited named rules built by RuleBook's builder.
   */
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
