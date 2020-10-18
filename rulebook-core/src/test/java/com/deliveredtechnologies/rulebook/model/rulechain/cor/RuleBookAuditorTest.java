package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.Auditor;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RuleBookAuditorTest {
  @Test
  public void ruleBookAuditorAuditsRulesDeclaredInDefineRules() {
    RuleBook ruleBook = RuleBookBuilder.create(SubCoRRuleBookAuditor.class)
        .withResultType(String.class)
        .withDefaultResult("Unknown")
        .build();
    ruleBook.run(new FactMap<>());

    Auditor auditor = (Auditor) ruleBook;
    assertEquals(auditor.getRuleStatus("First"), RuleStatus.EXECUTED);
    assertEquals(auditor.getRuleStatus("Second"), RuleStatus.SKIPPED);
  }

  @Test
  public void ruleBookAuditorAuditsRulesDeclaredInDefineRulesSupplierDefaultResult() {
    RuleBook ruleBook = RuleBookBuilder.create(SubCoRRuleBookAuditor.class)
        .withResultType(String.class)
        .withDefaultResult(() -> "Unknown")
        .build();
    ruleBook.run(new FactMap<>());

    Auditor auditor = (Auditor) ruleBook;
    assertEquals(auditor.getRuleStatus("First"), RuleStatus.EXECUTED);
    assertEquals(auditor.getRuleStatus("Second"), RuleStatus.SKIPPED);
  }
}
