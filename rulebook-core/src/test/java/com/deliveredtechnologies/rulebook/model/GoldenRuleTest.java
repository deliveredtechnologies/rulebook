package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Fact;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Tests for {@link GoldenRule}.
 */
public class GoldenRuleTest {

  @Test
  public void addFactsShouldAddFactsToTheRule() {
    NameValueReferableMap<String> facts = new FactMap<>();
    Rule<String, Object> rule = new GoldenRule<>(String.class);

    facts.setValue("fact1", "Fact One");
    facts.setValue("fact2", "Fact Two");

    rule.addFacts(new Fact<>("hello", "world"));
    rule.addFacts(facts);

    Assert.assertEquals(3, rule.getFacts().size());
    Assert.assertEquals("Fact One", rule.getFacts().getValue("fact1"));
    Assert.assertEquals("Fact Two", rule.getFacts().getValue("fact2"));
    Assert.assertEquals("world", rule.getFacts().getValue("hello"));
  }

  @Test
  public void setFactsShouldOverwriteExistingFacts() {
    NameValueReferableMap<String> facts = new FactMap<>();
    Rule<String, Object> rule = new GoldenRule<>(String.class);

    facts.setValue("fact1", "Fact One");
    facts.setValue("fact2", "Fact Two");

    rule.addFacts(new Fact<>("hello", "world"));
    rule.setFacts(facts);

    Assert.assertEquals(2, rule.getFacts().size());
    Assert.assertEquals("Fact One", rule.getFacts().getValue("fact1"));
    Assert.assertEquals("Fact Two", rule.getFacts().getValue("fact2"));
    Assert.assertTrue(facts == rule.getFacts());
  }

  @Test
  public void setConditionShouldSetTheCondition() {
    Predicate<NameValueReferableTypeConvertibleMap<String>> condition = facts -> true;
    Rule<String, Object> rule = new GoldenRule<>(String.class);
    rule.setCondition(condition);

    Assert.assertTrue(condition == rule.getCondition());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void setRuleStateShouldSetTheRuleState() {
    Rule rule = new GoldenRule(Object.class);
    rule.setRuleState(RuleState.BREAK);

    Assert.assertEquals(RuleState.BREAK, rule.getRuleState());
    rule.setRuleState(RuleState.NEXT);
    Assert.assertEquals(RuleState.NEXT, rule.getRuleState());
  }

  @Test
  public void addingActionsAddsActionsToTheActionList() {
    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer = facts -> facts.setValue("fact1", "Fact1");
    BiConsumer<NameValueReferableTypeConvertibleMap<String>, Result<String>> biConsumer =
        (facts, result) -> result.setValue("result");
    Rule<String, String> rule = new GoldenRule<>(String.class);
    rule.addAction(consumer);
    rule.addAction(biConsumer);

    Assert.assertTrue(rule.getActions().contains(consumer));
    Assert.assertTrue(rule.getActions().contains(biConsumer));
    Assert.assertEquals(2, rule.getActions().size());
  }

  @Test
  public void settingTheResultSetsTheResult() {
    Rule<String, String> rule = new GoldenRule<>(String.class);
    Assert.assertFalse(rule.getResult().isPresent());
    rule.setResult(new Result<>("My Result"));
    Assert.assertEquals("My Result", rule.getResult().get().getValue());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void addingDuplicateActionsFindsOnlyOneActionAdded() {
    Rule<String, String> rule = new GoldenRule<>(String.class);
    Result<String> result = new Result<>("result value");

    rule.setResult(result);
    rule.setCondition(whatever -> true);

    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer = Mockito.mock(Consumer.class);
    rule.addAction(consumer);
    rule.addAction(consumer);

    BiConsumer<NameValueReferableTypeConvertibleMap<String>, Result<String>> biConsumer =
        Mockito.mock(BiConsumer.class);
    rule.addAction(biConsumer);
    rule.addAction(biConsumer);
    rule.addAction(consumer);

    rule.invoke(new FactMap<>());

    Mockito.verify(consumer, Mockito.times(1))
        .accept(Mockito.any(NameValueReferableTypeConvertibleMap.class));
    Mockito.verify(biConsumer, Mockito.times(1))
        .accept(Mockito.any(NameValueReferableTypeConvertibleMap.class), Mockito.any(Result.class));
  }

  @Test(expected = RuleException.class)
  public void rulesSetToErrorOnFailureThrowExceptionsInWhen() {
    Rule<String, String> rule = new GoldenRule<>(String.class, RuleChainActionType.ERROR_ON_FAILURE);
    rule.setCondition(facts -> facts.getValue("some fact").equals("nothing"));
    rule.invoke(new FactMap<String>());
  }

  @Test(expected = RuleException.class)
  public void rulesToErrorOnFailureThrowExceptionsInActions() {
    Rule<String, String> rule = new GoldenRule<>(String.class, RuleChainActionType.ERROR_ON_FAILURE);
    rule.setCondition(facts -> true);
    rule.addAction(facts -> System.out.println(facts.getValue("some fact").toLowerCase()));
    rule.invoke(new FactMap<String>());
  }
}
