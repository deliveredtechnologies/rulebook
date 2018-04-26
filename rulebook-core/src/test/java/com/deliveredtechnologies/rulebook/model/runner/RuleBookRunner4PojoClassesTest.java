package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithResult;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithoutAnnotations;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithoutResult;
import com.deliveredtechnologies.rulebook.runner.SubRuleWithResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tests for RuleBookRunner4PojoClasses.
 */
public class RuleBookRunner4PojoClassesTest {
  @Test
  @SuppressWarnings("unchecked")
  public void ruleBookRunnerOrdersTheExecutionOfRulesForOnePojoClass() {
    Fact<String> fact1 = new Fact("fact1", "Fact");
    Fact<String> fact2 = new Fact("fact2", "Fact");
    FactMap<String> factMap = new FactMap<>();

    List<Class<?>> pojoClasses = new ArrayList<>();
    pojoClasses.add(SampleRuleWithResult.class);
    AbstractRuleBookRunner ruleBookRunner = new RuleBookRunner4PojoClasses(pojoClasses);
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
    AbstractRuleBookRunner ruleBookRunner = new RuleBookRunner4PojoClasses(pojoClasses);

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
    AbstractRuleBookRunner ruleBookRunner = new RuleBookRunner4PojoClasses(pojoClasses);
    factMap.put(fact1);
    factMap.put(fact2);
    ruleBookRunner.run(factMap);

    Assert.assertEquals("Fact", fact1.getValue());
    Assert.assertEquals("Fact", fact2.getValue());
    Optional<Result> result = ruleBookRunner.getResult();
    Assert.assertFalse("Expected Result annotation to be not defined", result.isPresent());
  }
}
