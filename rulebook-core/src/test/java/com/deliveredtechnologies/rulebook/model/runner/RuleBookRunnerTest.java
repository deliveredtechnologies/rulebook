package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link RuleBookRunner}.
 */
public class RuleBookRunnerTest {
  @Test
  public void ruleBookRunnerShouldAddRuleClassesInPackage() {
    RuleBookRunner ruleBookRunner = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");
    ruleBookRunner.run(new FactMap());

    Assert.assertTrue(ruleBookRunner.hasRules());
  }

  @Test
  public void ruleBookRunnerShouldNotLoadClassesIfNotInPackage() {
    RuleBookRunner ruleBookRunner = new RuleBookRunner("com.deliveredtechnologies.rulebook");
    ruleBookRunner.run(new FactMap());

    Assert.assertFalse(ruleBookRunner.hasRules());
  }

  @Test
  public void ruleBookRunnerShouldNotLoadClassesForInvalidPackage() {
    RuleBookRunner ruleBookRunner = new RuleBookRunner("com.deliveredtechnologies.rulebook.invalid");
    ruleBookRunner.run(new FactMap());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ruleBookRunnerOrdersTheExecutionOfRules() {
    Fact<String> fact1 = new Fact("fact1", "Fact");
    Fact<String> fact2 = new Fact("fact2", "Fact");
    FactMap<String> factMap = new FactMap<>();

    RuleBookRunner ruleBookRunner = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");
    factMap.put(fact1);
    factMap.put(fact2);
    ruleBookRunner.run(factMap);

    Assert.assertEquals("So Factual Too!", fact1.getValue());
    Assert.assertEquals("So Factual!", fact2.getValue());
    Assert.assertEquals("Equivalence, Bitches!", ruleBookRunner.getResult().get().toString());
  }

  @Test(expected = IllegalStateException.class)
  public void rulesCanNotBeAddedByCallingAddRule() {
    Rule rule = mock(Rule.class);
    RuleBookRunner ruleBookRunner = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");
    ruleBookRunner.addRule(rule);
  }

  @Test
  public void ruleBookRunnerIsThreadSafe() throws TimeoutException {
    final Waiter waiter = new Waiter();

    RuleBook ruleBook = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");

    new Thread(() -> {
      FactMap<String> equalFacts = new FactMap<>();
      equalFacts.setValue("fact1", "Fact");
      equalFacts.setValue("fact2", "Fact");

      ruleBook.run(equalFacts);

      waiter.assertEquals("So Factual Too!", equalFacts.getValue("fact1"));
      waiter.resume();
      waiter.assertEquals("So Factual!", equalFacts.getValue("fact2"));
      waiter.resume();
      waiter.assertEquals("Equivalence, Bitches!", ruleBook.getResult().get().toString());
      waiter.resume();
    }).start();

    new Thread(() -> {
      FactMap<String> unequalFacts = new FactMap<>();
      unequalFacts.setValue("fact1", "Some");
      unequalFacts.setValue("fact2", "Value");

      ruleBook.run(unequalFacts);

      waiter.assertEquals("Some", unequalFacts.getValue("fact1"));
      waiter.resume();
      waiter.assertEquals("Value", unequalFacts.getValue("fact2"));
      waiter.resume();
    }).start();

    new Thread(() -> {
      FactMap<String> equalFacts = new FactMap<>();
      equalFacts.setValue("fact1", "Factoid");
      equalFacts.setValue("fact2", "Factoid");

      ruleBook.run(equalFacts);

      waiter.assertEquals("So Factual Too!", equalFacts.getValue("fact1"));
      waiter.resume();
      waiter.assertEquals("So Factual!", equalFacts.getValue("fact2"));
      waiter.resume();
      waiter.assertEquals("Equivalence, Bitches!", ruleBook.getResult().get().toString());
      waiter.resume();
    }).start();

    new Thread(() -> {
      FactMap<String> unequalFacts = new FactMap<>();
      unequalFacts.setValue("fact1", "Fact");
      unequalFacts.setValue("fact2", "Factoid");

      ruleBook.run(unequalFacts);

      waiter.assertEquals("Fact", unequalFacts.getValue("fact1"));
      waiter.resume();
      waiter.assertEquals("Factoid", unequalFacts.getValue("fact2"));
      waiter.resume();
    }).start();

    waiter.await();
  }
}
