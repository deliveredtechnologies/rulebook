package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.Mockito.*;

/**
 * Created by clong on 2/13/17.
 */
public class RuleAdapterTest {
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
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    ruleAdapter.given(factMap);

    Assert.assertEquals("FirstFact", sampleRuleWithResult.getFact1());
    Assert.assertEquals("SecondFact", sampleRuleWithResult.getFact2());
    Assert.assertEquals(1, sampleRuleWithResult.getValue1());
  }

  @Test
  public void whenAttributesShouldConvertToPredicate() {

    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    Fact fact = (Fact)factMap.get("fact1");
    fact.setValue("SecondFact");
    ruleAdapter.given(factMap);

    //when is true when fact1 eq fact2; so, here it should be true
    Predicate predicate = ruleAdapter.getWhen();
    Assert.assertTrue(predicate.test(null));

    fact.setValue("FirstFact");
    ruleAdapter.given(factMap);

    //after changing fact1, it should be false
    Assert.assertFalse(predicate.test(null));
  }

  @Test
  public void thenAttributesWithResultShouldConvertToBiFunction() {

    Result<String> result = new Result<>();
    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    ruleAdapter.given(factMap);

    BiFunction<FactMap, Result, RuleState> biFunction = (BiFunction<FactMap, Result, RuleState>)ruleAdapter.getThen();
    Assert.assertEquals(RuleState.NEXT, biFunction.apply(null, result));
    Assert.assertEquals("So Factual!", ((Fact)factMap.get("fact2")).getValue());
    Assert.assertEquals(sampleRuleWithResult.getResult(), result.getValue());
  }

  @Test
  public void thenAttributesWithResultShouldConvertToFunction() {

    SampleRuleWithoutResult sampleRuleWithoutResult = new SampleRuleWithoutResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithoutResult);
    ruleAdapter.given(factMap);

    Function<FactMap, RuleState> function = (Function<FactMap, RuleState>)ruleAdapter.getThen();

    Assert.assertEquals(RuleState.NEXT, function.apply(null));
    Assert.assertEquals("So Factual!", ((Fact)factMap.get("fact2")).getValue());
  }
}
