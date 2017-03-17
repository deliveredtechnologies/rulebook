package com.deliveredtechnologies.rulebook;

import static com.deliveredtechnologies.rulebook.RuleState.BREAK;
import static com.deliveredtechnologies.rulebook.RuleState.NEXT;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tests for {@link StandardRule}.
 */
public class StandardRuleTest {
  @Test
  public void standardRuleIsCreated() {
    Rule<String> rule1 = new StandardRule<>();
    Rule<String> rule2 = StandardRule.create(String.class);
    Rule rule3 = StandardRule.create();

    Assert.assertNotNull(rule1);
    Assert.assertNotNull(rule2);
    Assert.assertNotNull(rule3);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenIsRunIfWhenIsTrue() {
    Rule<String> rule = spy(
        StandardRule.create(String.class).given("hello", "world"));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule.when(f -> true).then(action).run();

    verify(action, times(1)).accept(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenIsNotRunIfWhenIsFalse() {
    Rule<String> rule = spy(
        StandardRule.create(String.class).given(new Fact<>("hello", "world")));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule.when(f -> false).then(action).run();

    verify(action, times(0)).accept(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nextRuleInChainIsRunIfWhenIsFalse() {
    Rule<String> rule1 = spy(
        StandardRule.create(String.class).given(new Fact<>("hello", "world")));
    Rule<String> rule2 = spy(
        StandardRule.create(String.class).given(new Fact<>("hello", "world")));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule1 = rule1.when(f -> false).then(action);
    rule1.setNextRule(rule2.when(f -> true).then(action));
    rule1.run();

    verify(rule2, times(1)).run();
    verify(action, times(1)).accept(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nextRuleInChainIsRunIfWhenIsTrueAndThenReturnsNext() {
    FactMap<String> factMap = new FactMap<>();
    factMap.put("hello", new Fact<>("hello", "world"));
    Rule<String> rule1 = spy(
        StandardRule.create(String.class).given(factMap));
    Rule<String> rule2 = spy(
        StandardRule.create(String.class).given(factMap));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);
    rule1 = rule1.when(f -> true).then(action);
    rule1.setNextRule(rule2.when(f -> true).then(action));
    rule1.run();

    verify(rule2, times(1)).run();
    verify(action, times(2)).accept(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nextRuleInChainIsNotRunIfWhenIsTrueAndStopMethodIsUsed() {
    List<Fact<String>> factList = new ArrayList<>();
    factList.add(new Fact<>("hello", "world"));
    Rule<String> rule1 = spy(
        StandardRule.create(String.class).given(factList));
    Rule<String> rule2 = spy(
        StandardRule.create(String.class).given(factList));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule1 = rule1.when(f -> true).then(action).stop();
    rule1.setNextRule(rule2.when(f -> true).then(action));
    rule1.run();

    verify(rule2, times(0)).run();
    verify(action, times(1)).accept(any(FactMap.class));
  }
}
