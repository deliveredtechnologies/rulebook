package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Auditor;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleStatus;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithResult;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithoutAnnotations;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithoutResult;
import com.deliveredtechnologies.rulebook.runner.SubRuleWithResult;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.mock;

/**
 * Tests for {@link RuleBookRunner}.
 */
public class RuleBookRunnerTest {
  @Test
  public void ruleBookRunnerDoesAddRuleClassesInPackage() {
    RuleBookRunner ruleBookRunner = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");
    ruleBookRunner.run(new FactMap());

    Assert.assertTrue(ruleBookRunner.hasRules());
  }

  @Test
  public void ruleBookRunnerDoesNotLoadClassesIfNotInPackage() {
    RuleBookRunner ruleBookRunner = new RuleBookRunner("com.deliveredtechnologies.rulebook");
    ruleBookRunner.run(new FactMap());

    Assert.assertFalse(ruleBookRunner.hasRules());
  }

  @Test
  public void ruleBookRunnerDoesNotLoadClassesForInvalidPackage() {
    RuleBookRunner ruleBookRunner = new RuleBookRunner("com.deliveredtechnologies.rulebook.invalid");
    ruleBookRunner.run(new FactMap());
  }

  @Test
  public void ruleBookRunnerResultIsNotPresentIfNull() {
    RuleBookRunner ruleBookRunner =
        new RuleBookRunner("com.deliveredtechnologies.rulebook.model.runner.noresult");
    FactMap<String> facts = new FactMap<>();
    facts.setValue("hello", "Hello");
    facts.setValue("world", "World");

    ruleBookRunner.run(facts);

    Assert.assertFalse(ruleBookRunner.getResult().isPresent());
  }

  @Test
  public void ruleBookRunnerResultIsPresentIfSet() {
    RuleBookRunner ruleBookRunner =
        new RuleBookRunner("com.deliveredtechnologies.rulebook.model.runner.result");
    ruleBookRunner.setDefaultResult(2.02);
    FactMap facts = new FactMap();

    ruleBookRunner.run(facts);

    Assert.assertTrue(ruleBookRunner.getResult().isPresent());
  }

  @Test
  public void ruleBookRunnerResultIsNotPresentIfNotReferenced() {
    RuleBookRunner ruleBookRunner =
        new RuleBookRunner("com.deliveredtechnologies.rulebook.model.runner.noresultref");
    FactMap facts = new FactMap();
    facts.setValue("aFact", "aFact");

    ruleBookRunner.run(facts);

    Assert.assertFalse(ruleBookRunner.getResult().isPresent());
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

  @Test
  @SuppressWarnings("unchecked")
  public void ruleBookRunnerOrdersTheExecutionOfRulesForOnePojoClass() {
    Fact<String> fact1 = new Fact("fact1", "Fact");
    Fact<String> fact2 = new Fact("fact2", "Fact");
    FactMap<String> factMap = new FactMap<>();

    List<Class<?>> pojoClasses = new ArrayList<>();
    pojoClasses.add(SampleRuleWithResult.class);
    RuleBookRunner ruleBookRunner = new RuleBookRunner(pojoClasses);
    factMap.put(fact1);
    factMap.put(fact2);
    ruleBookRunner.run(factMap);

    Assert.assertEquals("So Factual Too!", fact1.getValue());
    Assert.assertEquals("So Factual Too!", fact2.getValue());
    Assert.assertEquals("Equivalence, Bitches!", ruleBookRunner.getResult().get().toString());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ruleBookRunnerOrdersTheExecutionOfRulesForMultiplePojoClass() {
    Fact<String> fact1 = new Fact("fact1", "Fact");
    Fact<String> fact2 = new Fact("fact2", "Fact");
    FactMap<String> factMap = new FactMap<>();
    factMap.put(fact1);
    factMap.put(fact2);

    List<Class<?>> pojoClasses = new ArrayList<>();
    pojoClasses.add(SampleRuleWithResult.class);
    pojoClasses.add(SampleRuleWithoutAnnotations.class);
    pojoClasses.add(SampleRuleWithoutResult.class);
    pojoClasses.add(SubRuleWithResult.class);
    RuleBookRunner ruleBookRunner = new RuleBookRunner(pojoClasses);

    ruleBookRunner.run(factMap);

    Assert.assertEquals("So Factual Too!", fact1.getValue());
    Assert.assertEquals("So Factual!", fact2.getValue());
    Assert.assertEquals("Equivalence, Bitches!", ruleBookRunner.getResult().get().toString());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ruleBookRunnerOrdersTheExecutionOfRulesNoAnnotation() {
    Fact<String> fact1 = new Fact("fact1", "Fact");
    Fact<String> fact2 = new Fact("fact2", "Fact");
    FactMap<String> factMap = new FactMap<>();

    List<Class<?>> pojoClasses = new ArrayList<>();
    pojoClasses.add(SampleRuleWithoutAnnotations.class);
    RuleBookRunner ruleBookRunner = new RuleBookRunner(pojoClasses);
    factMap.put(fact1);
    factMap.put(fact2);
    ruleBookRunner.run(factMap);

    Assert.assertEquals("Fact", fact1.getValue());
    Assert.assertEquals("Fact", fact2.getValue());
    Optional<Result> result = ruleBookRunner.getResult();
    Assert.assertFalse("Expected Result annotation to be not defined", result.isPresent());
  }


  @Test(expected = UnsupportedOperationException.class)
  public void rulesCanNotBeAddedByCallingAddRule() {
    Rule rule = mock(Rule.class);
    RuleBookRunner ruleBookRunner = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");
    ruleBookRunner.addRule(rule);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ruleBookRunnerResetsToDefaultResult() {
    RuleBook<String> ruleBook = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");
    ruleBook.setDefaultResult("default");

    FactMap<String> facts = new FactMap<>();
    facts.setValue("fact1", "Fact");
    facts.setValue("fact2", "Fact");

    ruleBook.run(facts);
    Assert.assertEquals("Equivalence Default", ruleBook.getResult().get().getValue());
    ruleBook.getResult().ifPresent(Result::reset);
    Assert.assertEquals("default", ruleBook.getResult().get().getValue());
    facts.setValue("fact1", "Fact");
    facts.setValue("fact2", "Fact");
    ruleBook.run(facts);
    Assert.assertEquals("Equivalence Default", ruleBook.getResult().get().getValue());
    ruleBook.getResult().ifPresent(Result::reset);
    Assert.assertEquals("default", ruleBook.getResult().get().getValue());
  }

  @Test
  public void ruleBookRunnerIsThreadSafe() throws TimeoutException {
    final Waiter waiter = new Waiter();

    RuleBook ruleBook = new RuleBookRunner("com.deliveredtechnologies.rulebook.runner");

    FactMap<String> equalFacts1 = new FactMap<>();
    equalFacts1.setValue("fact1", "Fact");
    equalFacts1.setValue("fact2", "Fact");

    FactMap<String> equalFacts2 = new FactMap<>();
    equalFacts2.setValue("fact1", "Factoid");
    equalFacts2.setValue("fact2", "Factoid");

    FactMap<String> unequalFacts1 = new FactMap<>();
    unequalFacts1.setValue("fact1", "Fact");
    unequalFacts1.setValue("fact2", "Factoid");

    FactMap<String> unequalFacts2 = new FactMap<>();
    unequalFacts2.setValue("fact1", "Some");
    unequalFacts2.setValue("fact2", "Value");

    ExecutorService service = null;

    try {

      service = Executors.newCachedThreadPool();

      service.execute(() -> {
        ruleBook.run(equalFacts1);
        waiter.assertEquals("So Factual Too!", equalFacts1.getValue("fact1"));
        waiter.resume();
        waiter.assertEquals("So Factual!", equalFacts1.getValue("fact2"));
        waiter.resume();
        waiter.assertEquals("Equivalence, Bitches!", ruleBook.getResult().get().toString());
        waiter.resume();
        waiter.assertEquals(4, ((Auditor) ruleBook).getRuleStatusMap().size());
        waiter.resume();
        waiter.assertEquals(RuleStatus.EXECUTED, ((Auditor) ruleBook).getRuleStatus("Result Rule"));
        waiter.resume();
      });

      service.execute(() -> {
        ruleBook.run(unequalFacts2);
        waiter.assertEquals("Some", unequalFacts2.getValue("fact1"));
        waiter.resume();
        waiter.assertEquals("Value", unequalFacts2.getValue("fact2"));
        waiter.resume();
        waiter.assertEquals(RuleStatus.SKIPPED, ((Auditor) ruleBook).getRuleStatus("Result Rule"));
        waiter.resume();
      });

      service.execute(() -> {
        ruleBook.run(equalFacts2);
        waiter.assertEquals("So Factual Too!", equalFacts2.getValue("fact1"));
        waiter.resume();
        waiter.assertEquals("So Factual!", equalFacts2.getValue("fact2"));
        waiter.resume();
        waiter.assertEquals("Equivalence, Bitches!", ruleBook.getResult().get().toString());
        waiter.resume();
      });

      service.execute(() -> {
        ruleBook.run(unequalFacts1);
        waiter.assertEquals("Fact", unequalFacts1.getValue("fact1"));
        waiter.resume();
        waiter.assertEquals("Factoid", unequalFacts1.getValue("fact2"));
        waiter.resume();
      });

      waiter.await();
    } finally {
      if (service != null) {
        service.shutdown();
      }
    }
  }
}
