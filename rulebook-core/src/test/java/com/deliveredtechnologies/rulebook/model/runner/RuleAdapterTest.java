package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.NameValueReferable;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithoutAnnotations;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithResult;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithoutRuleAnnotation;
import com.deliveredtechnologies.rulebook.runner.SubRuleWithResult;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithoutResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InvalidClassException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

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
  public void ruleAdapterShouldGetCreatedUsingPojoRuleAndOptionallyRuleImplementation() throws InvalidClassException {
    RuleAdapter ruleAdapter1 = new RuleAdapter(new SampleRuleWithResult());
    RuleAdapter ruleAdapter2 = new RuleAdapter(new SampleRuleWithResult(), new GoldenRule(String.class));

    Assert.assertNotNull(ruleAdapter1);
    Assert.assertNotNull(ruleAdapter2);
  }

  @Test(expected = InvalidClassException.class)
  public void pojoRulesMissingRuleAnnotationThrowAnErrorOnInitialization() throws InvalidClassException {
    new RuleAdapter(new SampleRuleWithoutRuleAnnotation());
  }

  @Test
  public void addingFactsShouldAddThemToRuleDelegateAndMapToPropertiesInPojoRule() throws InvalidClassException {
    NameValueReferableMap<Integer> factMap = new FactMap<>();
    Rule<String, Object> rule = new GoldenRule<>(String.class);
    SampleRuleWithResult pojo = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(pojo, rule);

    factMap.setValue("value1", 500);
    ruleAdapter.addFacts(new Fact<String>("fact1", "Fact One"), new Fact<String>("fact2", "Fact Two"));
    ruleAdapter.addFacts(factMap);

    Assert.assertEquals(3, ruleAdapter.getFacts().size());
    Assert.assertEquals("Fact One", pojo.getFact1());
    Assert.assertEquals("Fact Two", pojo.getFact2());
    Assert.assertEquals(500, pojo.getValue1());
  }

  @Test
  public void settingFactsShouldOverwriteFactsInRuleDelegateAndInPojoRule() throws InvalidClassException {
    NameValueReferableMap factMap = new FactMap();
    Rule<String, Object> rule = new GoldenRule<>(String.class);
    SampleRuleWithResult pojo = new SampleRuleWithResult();

    factMap.setValue("value1", 5100);
    factMap.setValue("fact1", "Fact1");
    factMap.setValue("fact2", "Fact2");

    RuleAdapter ruleAdapter = new RuleAdapter(pojo, rule);
    ruleAdapter.addFacts(new Fact<String>("fact1", "Fact One"),
            new Fact<String>("fact2", "Fact Two"),
            new Fact<Integer>("value1", 500));
    ruleAdapter.setFacts(factMap);

    Assert.assertEquals(3, ruleAdapter.getFacts().size());
    Assert.assertEquals("Fact1", pojo.getFact1());
    Assert.assertEquals("Fact2", pojo.getFact2());
    Assert.assertEquals(5100, pojo.getValue1());
  }

  @Test
  public void givenAttributesShouldMapToInheritedFactsParams() throws InvalidClassException {
    SubRuleWithResult subRuleWithResult = new SubRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(subRuleWithResult);
    ruleAdapter.addFacts(_factMap);

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
  public void explicitlySettingConditionTakesPrecedenceOverPojoCondition() throws InvalidClassException {
    RuleAdapter ruleAdapter = new RuleAdapter(new SampleRuleWithResult());
    Predicate<NameValueReferableMap> condition = facts -> true;
    ruleAdapter.setCondition(condition);

    Assert.assertEquals(condition, ruleAdapter.getCondition());
  }

  @Test
  public void whenAnnotatedMethodShouldConvertToPredicate() throws InvalidClassException {

    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    Fact fact = (Fact)_factMap.get("fact1");
    fact.setValue("SecondFact");
    ruleAdapter.addFacts(_factMap);

    //when is true when fact1 eq fact2; so, here it should be true
    Predicate predicate = ruleAdapter.getCondition();
    Assert.assertTrue(predicate.test(null));

    fact.setValue("FirstFact");
    ruleAdapter.addFacts(_factMap);

    //after changing fact1, it should be false
    Assert.assertFalse(predicate.test(null));
  }

  @Test
  public void whenAnnotatedMethodInParentShouldConvertToPredicate() throws InvalidClassException {

    SubRuleWithResult subRuleWithResult = new SubRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(subRuleWithResult);
    Fact fact = (Fact)_factMap.get("fact1");
    fact.setValue("SecondFact");
    ruleAdapter.addFacts(_factMap);

    //when is true when fact1 eq fact2; so, here it should be true
    Predicate predicate = ruleAdapter.getCondition();
    Assert.assertTrue(predicate.test(null));

    fact.setValue("FirstFact");
    ruleAdapter.addFacts(_factMap);

    //after changing fact1, it should be false
    Assert.assertFalse(predicate.test(null));
  }

  @Test
  public void suppliedWhenPredicateShouldTakePrecendence() throws InvalidClassException {
    Predicate predicate = mock(Predicate.class);
    when(predicate.test(any())).thenReturn(false);

    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    ruleAdapter.addFacts(_factMap);
    ruleAdapter.setCondition(predicate);
    ruleAdapter.invoke();

    verify(predicate, times(1)).test(any());
    Assert.assertTrue(predicate == ruleAdapter.getCondition());
  }

  @Test
  public void pojoWithNoWhenAnnotationDefaultsToFalse() throws InvalidClassException {
    SampleRuleWithoutAnnotations sampleRule = new SampleRuleWithoutAnnotations();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRule);
    Predicate predicate = ruleAdapter.getCondition();

    Assert.assertFalse(predicate.test(null));
  }

  @Test
  public void settingRuleStateShouldSetRuleState() throws InvalidClassException {
    RuleAdapter ruleAdapter = new RuleAdapter(new SampleRuleWithResult());
    ruleAdapter.setRuleState(RuleState.BREAK);
    Assert.assertEquals(RuleState.BREAK, ruleAdapter.getRuleState());
    ruleAdapter.setRuleState(RuleState.NEXT);
    Assert.assertEquals(RuleState.NEXT, ruleAdapter.getRuleState());
  }

  @Test
  public void addingActionsAddsToActionList() throws InvalidClassException {
    RuleAdapter ruleAdapter = new RuleAdapter(new SampleRuleWithResult());
    BiConsumer<NameValueReferableTypeConvertibleMap<Object>, Result<Object>> biConsumer =
            (facts, result) -> facts.setValue("fact", "Fact");
    Consumer<NameValueReferableTypeConvertibleMap<Object>> consumer =
            facts -> facts.setValue("fact2", "Fact2");
    ruleAdapter.addAction(biConsumer);
    ruleAdapter.addAction(consumer);

    List<Object> actionList = ruleAdapter.getActions();
    Assert.assertTrue(actionList.contains(consumer));
    Assert.assertTrue(actionList.contains(biConsumer));
    Assert.assertEquals(2, actionList.size());
  }

  @Test
  public void thenAnnotatedMethodWithResultShouldConvertToBiConsumer() throws InvalidClassException {
    Result<String> result = new Result<>();
    SampleRuleWithResult sampleRuleWithResult = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithResult);
    ruleAdapter.addFacts(_factMap);

    BiConsumer<FactMap, Result> biConsumer =
            (BiConsumer<FactMap, Result>)((List<Object>)ruleAdapter.getActions()).get(0);
    biConsumer.accept(_factMap, result);

    Assert.assertEquals("So Factual Too!", ((Fact)_factMap.get("fact2")).getValue());
    Assert.assertEquals(sampleRuleWithResult.getResult(), result.getValue());
  }

  @Test
  public void thenAnnotatedMethodInParentWithResultShouldConvertToBiConsumer() throws InvalidClassException {
    Result<String> result = new Result<>();
    SubRuleWithResult subRuleWithResult = new SubRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(subRuleWithResult);
    ruleAdapter.addFacts(_factMap);

    BiConsumer<FactMap, Result> biConsumer =
            (BiConsumer<FactMap, Result>)((List<Object>)ruleAdapter.getActions()).get(0);
    biConsumer.accept(_factMap, result);
    Assert.assertEquals("So Factual Too!", ((Fact)_factMap.get("fact2")).getValue());
    Assert.assertEquals(subRuleWithResult.getResult(), result.getValue());
  }

  @Test
  public void inokingRuleAdapterShouldRunConvertedPojoRuleAndUpdateReferencedFacts() throws InvalidClassException {
    Result<String> result = new Result<>();
    SubRuleWithResult subRuleWithResult = new SubRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(subRuleWithResult);
    ruleAdapter.addFacts(_factMap);
    ruleAdapter.setResult(result);
    ruleAdapter.invoke();

    Assert.assertEquals("So Factual Too!", ((Fact)_factMap.get("fact2")).getValue());
    Assert.assertEquals(subRuleWithResult.getResult(), result.getValue());
  }

  @Test
  public void thenAnnotatedMethodWithoutResultShouldConvertToConsumer() throws InvalidClassException {
    SampleRuleWithoutResult sampleRuleWithoutResult = new SampleRuleWithoutResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithoutResult);
    ruleAdapter.addFacts(_factMap);

    Consumer<FactMap> consumer = (Consumer<FactMap>)((List<Object>)ruleAdapter.getActions()).get(0);
    consumer.accept(_factMap);

    Assert.assertEquals("So Factual!", ((Fact)_factMap.get("fact2")).getValue());
  }

  @Test
  public void suppliedThenConsumerShouldTakePrecendenceOverPojo() throws InvalidClassException {
    Consumer<FactMap> consumer = (Consumer<FactMap>)mock(Consumer.class);
    SampleRuleWithoutResult sampleRuleWithoutResult = new SampleRuleWithoutResult();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRuleWithoutResult);
    ruleAdapter.addFacts(_factMap);
    ruleAdapter.setCondition(facts -> true);
    ruleAdapter.addAction(consumer);
    ruleAdapter.invoke();

    verify(consumer, times(1)).accept(any(FactMap.class));
    Assert.assertTrue(consumer == ((List<Object>)ruleAdapter.getActions()).get(0));
  }

  @Test
  public void pojoWithNoThenAnnotationDefaultsToNext() throws InvalidClassException {
    SampleRuleWithoutAnnotations sampleRule = new SampleRuleWithoutAnnotations();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRule);

    Assert.assertEquals(0, ((List<Object>)ruleAdapter.getActions()).size());
  }

  @Test(expected = InvalidClassException.class)
  public void pojoWithNoRuleAnnotationThrowsException() throws InvalidClassException {
    SampleRuleWithoutRuleAnnotation sampleRule = new SampleRuleWithoutRuleAnnotation();
    RuleAdapter ruleAdapter = new RuleAdapter(sampleRule);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void usingMethodShouldThrowNotImplemented() throws InvalidClassException {
    RuleAdapter ruleAdapter = new RuleAdapter(new SampleRuleWithResult());
    ruleAdapter.addFactNameFilter();
  }

  @Test
  public void thenMethodShouldDelegateToRuleThenMethod() throws InvalidClassException {
    Rule rule = mock(Rule.class);
    BiConsumer<NameValueReferable, Result> biConsumer = (result, facts)  -> { };
    RuleAdapter ruleAdapter = new RuleAdapter(new SampleRuleWithResult(), rule);
    ruleAdapter.addAction(biConsumer);

    verify(rule, times(1)).addAction(biConsumer);
  }
}
