package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InvalidClassException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by clong on 2/13/17.
 * Tests for {@link RuleAdapter}
 */
public class RuleAdapterTest {
  private FactMap<Object> _factMap;

  /**
   * Setup creates a default FactMap.
   */
  @Before
  public void setup() {
    _factMap = new FactMap<>();
    _factMap.put("fact1", new Fact("fact1", "FirstFact"));
    _factMap.put("fact2", new Fact("fact2", "SecondFact"));
    _factMap.put("value1", new Fact("value1", 1));
  }

  @Test
  public void givenAttributesShouldMapToFacts() throws InvalidClassException {

    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    ruleAdapter.given(_factMap);

    Assert.assertEquals("FirstFact", sampleRuleWithResult.getFact1());
    Assert.assertEquals("SecondFact", sampleRuleWithResult.getFact2());
    Assert.assertEquals(1, sampleRuleWithResult.getValue1());
  }

  @Test
  public void givenAttributesShouldMapToFactsParams() throws InvalidClassException {
    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    ruleAdapter.given(_factMap.get("fact1"), _factMap.get("fact2"), _factMap.get("value1"));

    Assert.assertEquals("FirstFact", sampleRuleWithResult.getFact1());
    Assert.assertEquals("SecondFact", sampleRuleWithResult.getFact2());
    Assert.assertEquals(2, sampleRuleWithResult.getStrList().size());
    Assert.assertEquals(1, sampleRuleWithResult.getValue1());
    Assert.assertNull(sampleRuleWithResult.getValueSet());
  }

  @Test
  public void whenAnnotatedMethodShouldConvertToPredicate() throws InvalidClassException {

    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    Fact fact = (Fact)_factMap.get("fact1");
    fact.setValue("SecondFact");
    ruleAdapter.given(_factMap);

    //when is true when fact1 eq fact2; so, here it should be true
    Predicate predicate = ruleAdapter.getWhen();
    Assert.assertTrue(predicate.test(null));

    fact.setValue("FirstFact");
    ruleAdapter.given(_factMap);

    //after changing fact1, it should be false
    Assert.assertFalse(predicate.test(null));
  }

  @Test
  public void suppliedWhenPredicateShouldTakePrecendence() throws InvalidClassException {
    Predicate predicate = mock(Predicate.class);
    when(predicate.test(any())).thenReturn(false);

    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    ruleAdapter.given(_factMap).when(predicate).run();

    verify(predicate, times(1)).test(any());
    Assert.assertTrue(predicate == ruleAdapter.getWhen());
  }

  @Test
  public void pojoWithNoWhenAnnotationDefaultsToFalse() throws InvalidClassException {
    SampleRuleWithoutAnnotations sampleRule = new SampleRuleWithoutAnnotations();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRule);
    Predicate predicate = ruleAdapter.getWhen();

    Assert.assertFalse(predicate.test(null));
  }

  @Test
  public void thenAnnotatedMethodWithResultShouldConvertToBiFunction() throws InvalidClassException {
    Result<String> result = new Result<>();
    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    ruleAdapter.given(_factMap);

    BiFunction<FactMap, Result, RuleState> biFunction = (BiFunction<FactMap, Result, RuleState>)ruleAdapter.getThen();
    Assert.assertEquals(RuleState.NEXT, biFunction.apply(null, result));
    Assert.assertEquals("So Factual Too!", ((Fact)_factMap.get("fact2")).getValue());
    Assert.assertEquals(sampleRuleWithResult.getResult(), result.getValue());
  }

  @Test
  public void thenAnnotatedMethodWithResultShouldConvertToFunction() throws InvalidClassException {
    SampleRuleWithoutResult sampleRuleWithoutResult = new SampleRuleWithoutResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithoutResult);
    ruleAdapter.given(_factMap);

    Function<FactMap, RuleState> function = (Function<FactMap, RuleState>)ruleAdapter.getThen();

    Assert.assertEquals(RuleState.NEXT, function.apply(null));
    Assert.assertEquals("So Factual!", ((Fact)_factMap.get("fact2")).getValue());
  }

  @Test
  public void suppliedThenFunctionShouldTakePrecendenceOverPojo() throws InvalidClassException {
    Function<FactMap, RuleState> function = (Function<FactMap, RuleState>)mock(Function.class);
    when(function.apply(any(FactMap.class))).thenReturn(RuleState.NEXT);
    SampleRuleWithoutResult sampleRuleWithoutResult = new SampleRuleWithoutResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithoutResult);
    ruleAdapter.given(_factMap).when(facts -> true).then(function).run();

    verify(function, times(1)).apply(any(FactMap.class));
    Assert.assertTrue(function == ruleAdapter.getThen());
  }

  @Test
  public void pojoWithNoThenAnnotationDefaultsToNext() throws InvalidClassException {
    SampleRuleWithoutAnnotations sampleRule = new SampleRuleWithoutAnnotations();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRule);
    Function function = (Function)ruleAdapter.getThen();

    Assert.assertEquals(RuleState.NEXT, function.apply(null));
  }

  @Test(expected = InvalidClassException.class)
  public void pojoWithNoRuleAnnotationThrowsException() throws InvalidClassException {
    SampleRuleWithoutRuleAnnotation sampleRule = new SampleRuleWithoutRuleAnnotation();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRule);
  }
}
