package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InvalidClassException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link RuleAdapter}.
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
    ruleAdapter.given(_factMap);

    Assert.assertEquals("FirstFact", sampleRuleWithResult.getFact1());
    Assert.assertEquals("SecondFact", sampleRuleWithResult.getFact2());
    Assert.assertEquals(2, sampleRuleWithResult.getStrList().size());
    Assert.assertEquals(2, sampleRuleWithResult.getStrSet().size());
    Assert.assertEquals(2, sampleRuleWithResult.getStrMap().size());
    Assert.assertEquals(_factMap, sampleRuleWithResult.getFactMap());
    Assert.assertEquals(1, sampleRuleWithResult.getValue1());
    Assert.assertEquals(1, sampleRuleWithResult.getValueSet().size());
    Assert.assertEquals(1, sampleRuleWithResult.getValueList().size());
    Assert.assertEquals(1, sampleRuleWithResult.getValueMap().size());
  }

  @Test
  public void givenAttributesShouldMapToInheritedFactsParams() throws InvalidClassException {
    SubRuleWithResult subRuleWithResult = new SubRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(subRuleWithResult);
    ruleAdapter.given(_factMap.get("fact1"), _factMap.get("fact2"), _factMap.get("value1"));

    Assert.assertEquals("FirstFact", subRuleWithResult.getFact1());
    Assert.assertEquals("SecondFact", subRuleWithResult.getFact2());
    Assert.assertEquals(2, subRuleWithResult.getStrList().size());
    Assert.assertEquals(2, subRuleWithResult.getStrSet().size());
    Assert.assertEquals(2, subRuleWithResult.getStrMap().size());
    Assert.assertEquals(_factMap.size(), subRuleWithResult.getFactMap().size());
    for (Map.Entry kvpair : _factMap.entrySet()) {
      Assert.assertEquals(((Fact)kvpair.getValue()).getValue(),
          subRuleWithResult.getFactMap().getValue((String)kvpair.getKey()));
    }
    Assert.assertEquals(1, subRuleWithResult.getValue1());
    Assert.assertEquals(1, subRuleWithResult.getValueSet().size());
    Assert.assertEquals(1, subRuleWithResult.getValueList().size());
    Assert.assertEquals(1, subRuleWithResult.getValueMap().size());
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
  public void whenAnnotatedMethodInParentShouldConvertToPredicate() throws InvalidClassException {

    SubRuleWithResult subRuleWithResult = new SubRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(subRuleWithResult);
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
  public void thenAnnotatedMethodWithResultShouldConvertToBiConsumer() throws InvalidClassException {
    Result<String> result = new Result<>();
    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    ruleAdapter.given(_factMap);

    BiConsumer<FactMap, Result> biConsumer =
        (BiConsumer<FactMap, Result>)((List<Object>)ruleAdapter.getThen()).get(0);
    biConsumer.accept(_factMap, result);

    Assert.assertEquals("So Factual Too!", ((Fact)_factMap.get("fact2")).getValue());
    Assert.assertEquals(sampleRuleWithResult.getResult(), result.getValue());
  }

  @Test
  public void thenAnnotatedMethodInParentWithResultShouldConvertToBiConsumer() throws InvalidClassException {
    Result<String> result = new Result<>();
    SubRuleWithResult subRuleWithResult = new SubRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(subRuleWithResult);
    ruleAdapter.given(_factMap);

    BiConsumer<FactMap, Result> biConsumer =
        (BiConsumer<FactMap, Result>)((List<Object>)ruleAdapter.getThen()).get(0);
    biConsumer.accept(_factMap, result);
    Assert.assertEquals("So Factual Too!", ((Fact)_factMap.get("fact2")).getValue());
    Assert.assertEquals(subRuleWithResult.getResult(), result.getValue());
  }

  @Test
  public void thenAnnotatedMethodWithoutResultShouldConvertToConsumer() throws InvalidClassException {
    SampleRuleWithoutResult sampleRuleWithoutResult = new SampleRuleWithoutResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithoutResult);
    ruleAdapter.given(_factMap);

    Consumer<FactMap> consumer = (Consumer<FactMap>)((List<Object>)ruleAdapter.getThen()).get(0);
    consumer.accept(_factMap);

    Assert.assertEquals("So Factual!", ((Fact)_factMap.get("fact2")).getValue());
  }

  @Test
  public void suppliedThenConsumerShouldTakePrecendenceOverPojo() throws InvalidClassException {
    Consumer<FactMap> consumer = (Consumer<FactMap>)mock(Consumer.class);
    SampleRuleWithoutResult sampleRuleWithoutResult = new SampleRuleWithoutResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithoutResult);
    ruleAdapter.given(_factMap).when(facts -> true).then(consumer).run();

    verify(consumer, times(1)).accept(any(FactMap.class));
    Assert.assertTrue(consumer == ((List<Object>)ruleAdapter.getThen()).get(0));
  }

  @Test
  public void pojoWithNoThenAnnotationDefaultsToNext() throws InvalidClassException {
    SampleRuleWithoutAnnotations sampleRule = new SampleRuleWithoutAnnotations();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRule);

    Assert.assertEquals(0, ((List<Object>)ruleAdapter.getThen()).size());
  }

  @Test(expected = InvalidClassException.class)
  public void pojoWithNoRuleAnnotationThrowsException() throws InvalidClassException {
    SampleRuleWithoutRuleAnnotation sampleRule = new SampleRuleWithoutRuleAnnotation();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRule);
  }
}
