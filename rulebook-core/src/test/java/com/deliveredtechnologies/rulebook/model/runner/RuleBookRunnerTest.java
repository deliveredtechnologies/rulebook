package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

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

    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook.runner"));
    factMap.put(fact1);
    factMap.put(fact2);
    ruleBookRunner.run(factMap);

    Assert.assertEquals("So Factual Too!", fact1.getValue());
    Assert.assertEquals("So Factual!", fact2.getValue());
    Assert.assertEquals("Equivalence, Bitches!", ruleBookRunner.getResult().get().toString());
  }

  @Test
  public void ruleBookRunnerIsThreadSafe() throws TimeoutException {
    final Waiter waiter = new Waiter();

    FactMap<String> equalFacts1 = new FactMap<>();
    FactMap<String> equalFacts2 = new FactMap<>();
    FactMap<String> unequalFacts1 = new FactMap<>();
    FactMap<String> unequalFacts2 = new FactMap<>();

    RuleBook ruleBook1 = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");
    RuleBook ruleBook2 = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");
    RuleBook ruleBook3 = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");
    RuleBook ruleBook4 = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");

    equalFacts1.setValue("fact1", "Fact");
    equalFacts1.setValue("fact2", "Fact");
    equalFacts2.setValue("fact1", "Factoid");
    equalFacts2.setValue("fact2", "Factoid");
    unequalFacts1.setValue("fact1", "Fact");
    unequalFacts1.setValue("fact2", "Factoid");
    unequalFacts2.setValue("fact1", "Some");
    unequalFacts2.setValue("fact2", "Value");

    new Thread(() -> {
      ruleBook1.run(equalFacts1);
      waiter.assertEquals("So Factual Too!", equalFacts1.getValue("fact1"));
      waiter.resume();
      waiter.assertEquals("So Factual!", equalFacts1.getValue("fact2"));
      waiter.resume();
      waiter.assertEquals("Equivalence, Bitches!", ruleBook1.getResult().get().toString());
      waiter.resume();
    }).start();

    new Thread(() -> {
      ruleBook2.run(unequalFacts2);
      waiter.assertEquals("Some", unequalFacts2.getValue("fact1"));
      waiter.resume();
      waiter.assertEquals("Value", unequalFacts2.getValue("fact2"));
      waiter.resume();
    }).start();

    new Thread(() -> {
      ruleBook3.run(equalFacts2);
      waiter.assertEquals("So Factual Too!", equalFacts2.getValue("fact1"));
      waiter.resume();
      waiter.assertEquals("So Factual!", equalFacts2.getValue("fact2"));
      waiter.resume();
      waiter.assertEquals("Equivalence, Bitches!", ruleBook3.getResult().get().toString());
      waiter.resume();
    }).start();

    new Thread(() -> {
      ruleBook4.run(unequalFacts1);
      waiter.assertEquals("Fact", unequalFacts1.getValue("fact1"));
      waiter.resume();
      waiter.assertEquals("Factoid", unequalFacts1.getValue("fact2"));
      waiter.resume();
    }).start();

    waiter.await();
  }
}
