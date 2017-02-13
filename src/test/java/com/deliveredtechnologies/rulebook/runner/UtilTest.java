package com.deliveredtechnologies.rulebook.runner;


import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by clong on 2/13/17.
 */
public class UtilTest {

  FactMap factMap;

  @Before
  public void setup() {
    factMap = new FactMap();
    factMap.put("fact1", new Fact("fact1", "FirstFact"));
    factMap.put("fact2", new Fact("fact2", "SecondFact"));
    factMap.put("value1", new Fact("value1", 1));
  }

  @Test
  public void givenAttributesShouldMapToFacts() {

    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();

    Util.mapGivenFactsToProperties(sampleRuleWithResult, factMap);

    Assert.assertEquals("FirstFact", sampleRuleWithResult.getFact1());
    Assert.assertEquals("SecondFact", sampleRuleWithResult.getFact2());
    Assert.assertEquals(1, sampleRuleWithResult.getValue1());
  }

  @Test
  public void whenMethodShouldConvertToPredicate() {
    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();

    Util.mapGivenFactsToProperties(sampleRuleWithResult, factMap);
    Predicate predicate = Util.getWhenMethodAsPredicate(sampleRuleWithResult);

    Assert.assertFalse(predicate.test(null));

    Fact fact = (Fact)factMap.get("fact2");
    fact.setValue("FirstFact");

    Assert.assertTrue(predicate.test(null));
  }

  @Test
  public void thenMethodShouldConvertToBiFunctionIfResultPresent() {
    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();

    Result<String> result = new Result<>();

    Util.mapGivenFactsToProperties(sampleRuleWithResult, factMap);
    Optional<BiFunction> biFunction = Util.getThenMethodAsBiFunction(sampleRuleWithResult);

    Assert.assertTrue(biFunction.isPresent());
    Assert.assertEquals(RuleState.NEXT, biFunction.get().apply(factMap, result));
    Assert.assertEquals(result.getValue(), sampleRuleWithResult.getResult());
  }

  @Test
  public void thenMethodShouldConvertToFunctionIfResultNotPresent() {

    SampleRuleWithoutResult sampleRuleWithoutResult = new SampleRuleWithoutResult();

    Util.mapGivenFactsToProperties(sampleRuleWithoutResult, factMap);
    Optional<Function> function = Util.getThenMethodAsFunction(sampleRuleWithoutResult);

    Assert.assertTrue(function.isPresent());
    Assert.assertEquals(RuleState.NEXT, function.get().apply(factMap));
    Assert.assertEquals(factMap.getValue("fact2"), "So Factual!");
  }
}
