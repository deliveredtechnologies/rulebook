package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleChainActionType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link RuleBuilder}.
 */
public class RuleBuilderTest {
  @Test
  @SuppressWarnings("unchecked")
  public void ruleBuilderCreatesGwtRules() {
    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer =
            (Consumer<NameValueReferableTypeConvertibleMap<String>>)Mockito.mock(Consumer.class);
    Rule rule = RuleBuilder.create()
            .withFactType(String.class)
            .given("fact1", "First Fact")
            .when(facts -> facts.getValue("fact1").equals("First Fact"))
            .then(consumer)
            .build();
    rule.invoke();

    verify(consumer, times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
  }

  @Test
  public void ruleBuilderCreatesRuleWithClassAndBooleanConstructor() {
    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer =
        (Consumer<NameValueReferableTypeConvertibleMap<String>>)Mockito.mock(Consumer.class);
    Rule rule = RuleBuilder.create(GoldenRule.class, RuleChainActionType.STOP_ON_FAILURE)
        .withFactType(String.class)
        .when(facts -> false)
        .then(consumer)
        .stop()
        .build();
    boolean result = rule.invoke();

    verify(consumer, times(0)).accept(any(NameValueReferableTypeConvertibleMap.class));

    Assert.assertEquals(true, result);
    Assert.assertEquals(RuleState.BREAK, rule.getRuleState());
  }

  @Test
  public void ruleBuilderUsingMethodRestrictsThenFacts() {
    FactMap<String> factMap = new FactMap<>();
    Rule rule = RuleBuilder.create()
            .withFactType(String.class)
            .given("fact1", "First Fact")
            .given("fact2", "Second Fact")
            .given("fact3", "Third Fact")
            .when(facts -> facts.getValue("fact1").equals("First Fact"))
            .using("fact1")
            .using("fact3")
            .then(factMap::putAll)
            .build();
    rule.invoke();

    Assert.assertEquals(2, factMap.size());
    Assert.assertEquals("First Fact", factMap.getValue("fact1"));
    Assert.assertEquals("Third Fact", factMap.getValue("fact3"));
  }

  @Test
  public void ruleBuilderThenWithResultAfterUsingMethodRestrictsThenFacts() {
    Rule<String, String> rule = RuleBuilder.create()
            .withFactType(String.class)
            .withResultType(String.class)
            .given("fact1", "First Fact")
            .given("fact2", "Second Fact")
            .given("fact3", "Third Fact")
            .when(facts -> facts.getValue("fact1").equals("First Fact"))
            .using("fact1")
            .then((facts, result) -> result.setValue(facts.getOne()))
            .build();
    rule.invoke();

    Assert.assertEquals("First Fact", rule.getResult().get().getValue());
  }

  @Test
  public void ruleBuilderAllowsThenMethodAfterCreate() {
    Consumer<NameValueReferableTypeConvertibleMap<Object>> consumer = mock(Consumer.class);
    BiConsumer<NameValueReferableTypeConvertibleMap<Object>, Result<Object>> biConsumer = mock(BiConsumer.class);

    Rule rule1 = RuleBuilder.create().then(consumer).build();
    Rule rule2 = RuleBuilder.create().then(biConsumer).build();
    rule1.invoke();
    rule2.invoke();

    verify(consumer, times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
    verify(biConsumer, times(1)).accept(any(NameValueReferableTypeConvertibleMap.class), any(Result.class));
  }

  @Test
  public void ruleBuilderAllowsWhenMethodAfterCreate() {
    Consumer<NameValueReferableTypeConvertibleMap<Object>> consumer = mock(Consumer.class);
    Predicate<NameValueReferableTypeConvertibleMap<Object>> condition = mock(Predicate.class);
    when(condition.test(any(NameValueReferableTypeConvertibleMap.class))).thenReturn(true);

    Rule rule = RuleBuilder.create().when(condition).then(consumer).build();
    rule.invoke();

    verify(condition, times(1)).test(any(NameValueReferableTypeConvertibleMap.class));
    verify(consumer, times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
  }

  @Test
  public void ruleBuilderAllowsGivenMethodAfterCreate() {
    NameValueReferableMap<Object> factMap = new FactMap<>();
    factMap.setValue("fact1", "Fact1");
    Consumer<NameValueReferableTypeConvertibleMap<Object>> consumer = mock(Consumer.class);
    Rule rule1 = RuleBuilder.create()
            .given("fact1", "Fact1")
            .when(facts -> facts.containsKey("fact1"))
            .then(consumer).build();
    Rule rule2 = RuleBuilder.create()
            .given(new Fact("fact1", "Fact1"))
            .when(facts -> facts.containsKey("fact1"))
            .then(consumer).build();
    Rule rule3 = RuleBuilder.create()
            .given(factMap)
            .when(facts -> facts.containsKey("fact1"))
            .then(consumer).build();

    rule1.invoke();
    rule2.invoke();
    rule3.invoke();

    verify(consumer, times(3)).accept(any(NameValueReferableTypeConvertibleMap.class));
  }

  @Test
  public void ruleBuilderAllowsForChainedThenMethods() {
    Rule<String, String> rule = RuleBuilder.create()
            .withFactType(String.class)
            .withResultType(String.class)
            .given("fact1", "Fact1")
            .then(facts -> facts.setValue("fact2", "Fact2"))
            .then(facts -> facts.setValue("fact3", "Fact3"))
            .then((facts, result) -> result.setValue(facts.getValue("fact2") + facts.getValue("fact3")))
            .using("fact1")
            .then((facts, result) -> result.setValue(result.getValue() + facts.getOne()))
            .build();
    rule.invoke();

    Assert.assertEquals(rule.getResult().get().getValue(), "Fact2Fact3Fact1");
  }

  @Test
  public void ruleBuilderAllowsThenWithResultsToFollowGiven() {
    Rule<String, String> rule = RuleBuilder.create()
            .withFactType(String.class)
            .withResultType(String.class)
            .given("fact1", "Fact1")
            .then((facts, result) -> result.setValue(facts.getOne()))
            .build();
    rule.invoke();

    Assert.assertEquals("Fact1", rule.getResult().get().getValue());
  }

  @Test
  public void ruleBuilderAllowsUsingToFollowGiven() {
    FactMap<String> factMap = new FactMap<>();
    Rule rule = RuleBuilder.create()
            .withFactType(String.class)
            .given("fact1", "Fact1")
            .given("fact2", "Fact2")
            .using("fact1")
            .then(factMap::putAll)
            .build();
    rule.invoke();

    Assert.assertEquals(factMap.getOne(), "Fact1");
  }

  @Test
  public void ruleBuilderBuildsRulesWithConstructorsHavingTwoOrFewerArgs() {
    Rule rule1 = RuleBuilder.create(SampleRuleDefaultConstructor.class).build();
    Rule rule2 = RuleBuilder.create(SampleRuleWithFactAndResultTypes.class).build();
    Rule rule3 = RuleBuilder.create(SampleRuleWithThreeArgConstructor.class).build();
    Rule rule4 = RuleBuilder.create(GoldenRule.class).build();

    Assert.assertNotNull(rule1);
    Assert.assertNotNull(rule2);
    Assert.assertNull(rule3);
    Assert.assertNotNull(rule4);
  }

  @Test(expected = IllegalStateException.class)
  public void ruleBuilderThrowsAnExceptionWhenGivenNameValueIsCalledOnInvalidRule() {
    RuleBuilder.create(SampleRuleWithThreeArgConstructor.class)
            .withFactType(String.class)
            .given("fact", "Fact");
  }

  @Test(expected = IllegalStateException.class)
  public void ruleBuilderThrowsAnExceptionWhenGivenFactIsCalledOnInvalidRule() {
    RuleBuilder.create(SampleRuleWithThreeArgConstructor.class)
            .withFactType(String.class)
            .given(new Fact<String>("fact", "Fact"));
  }

  @Test(expected = IllegalStateException.class)
  public void ruleBuilderThrowsAnExceptionWhenGivenFactMapIsCalledOnInvalidRule() {
    RuleBuilder.create(SampleRuleWithThreeArgConstructor.class)
            .withFactType(String.class)
            .given(new FactMap());
  }

  @Test(expected = IllegalStateException.class)
  public void ruleBuilderThrowsAnExceptionOnWhenMethodCallWithInvalidRule() {
    RuleBuilder.create(SampleRuleWithThreeArgConstructor.class)
            .when(facts -> true);
  }

  @Test(expected = IllegalStateException.class)
  public void ruleBuilderThrowsAnExceptionOnThenMethodCallWithInvalidRule() {
    RuleBuilder.create(SampleRuleWithThreeArgConstructor.class)
            .then(facts -> facts.setValue("fact", "Fact"));
  }

  @Test(expected = IllegalStateException.class)
  public void ruleBuilderThrowsAnExceptionOnThenMethodCallWithResultWithInvalidRule() {
    RuleBuilder.create(SampleRuleWithThreeArgConstructor.class)
            .then((facts, result) -> facts.setValue("fact", "Fact"));
  }
}
