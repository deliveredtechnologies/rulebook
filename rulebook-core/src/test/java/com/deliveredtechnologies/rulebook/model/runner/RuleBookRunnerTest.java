package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link RuleBookRunner}.
 */
public class RuleBookRunnerTest {
  @Test
  public void ruleBookRunnerShouldAddRuleClassesInPackage() {
    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook.runner"));
    ruleBookRunner.run(new FactMap());

    verify(ruleBookRunner, times(4)).addRule(any(RuleAdapter.class));
  }

  @Test
  public void ruleBookRunnerShouldNotLoadClassesIfNotInPackage() {
    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook"));
    ruleBookRunner.run(new FactMap());

    verify(ruleBookRunner, times(0)).addRule(any(RuleAdapter.class));
  }

  @Test
  public void ruleBookRunnerShouldNotLoadClassesForInvalidPackage() {
    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook.invalid"));
    ruleBookRunner.run(new FactMap());

    verify(ruleBookRunner, times(0)).addRule(any(RuleAdapter.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ruleBookRunnerOrdersTheExecutionOfRules() {
    Fact<String> fact1 = new Fact("fact1", "Fact");
    Fact<String> fact2 = new Fact("fact2", "Fact");
    FactMap<String> factMap = new FactMap<>();

    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook.runner"));
    factMap.put(fact1);
    factMap.put(fact2);
    ruleBookRunner.run(factMap);

    Assert.assertEquals("So Factual Too!", fact1.getValue());
    Assert.assertEquals("So Factual!", fact2.getValue());
    Assert.assertEquals("Equivalence, Bitches!", ruleBookRunner.getResult().get().toString());
  }
}
