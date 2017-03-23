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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Tests for {@link StandardRule}.
 */
public class StandardRuleTest {
  @Test
  public void standardRuleIsCreated() {
    Rule<String> rule1 = new StandardRule<>(String.class);
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
  public void thenIsRunIfWhenDoesNotExist() {
    Rule<String> rule = spy(
        StandardRule.create(String.class).given("hello", "world"));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule.then(action).run();

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
  public void multipleThenMethodsCanBeChainedTogether() {
    Rule<String> rule = spy(
        StandardRule.create(String.class).given(new Fact<>("hello", "world")));
    Consumer<FactMap<String>> action1 = (Consumer<FactMap<String>>) mock(Consumer.class);
    Consumer<FactMap<String>> action2 = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule.when(f -> true).then(action1).then(action2).run();

    verify(action1, times(1)).accept(any(FactMap.class));
    verify(action2, times(1)).accept(any(FactMap.class));
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
  public void nextRuleInChainIsRunIfWhenErrors() {
    Rule<String> rule1 = spy(
        StandardRule.create(String.class).given(new Fact<>("hello", "world")));
    Rule<String> rule2 = spy(
        StandardRule.create(String.class).given(new Fact<>("hello", "world")));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule1 = rule1.when(facts -> facts.getValue("nothing").equals("something")).then(action);
    rule1.setNextRule(rule2.when(facts -> true).then(action));
    rule1.run();

    verify(rule2, times(1)).run();
    verify(action, times(1)).accept(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nextRuleInChainIsRunIfWhenIsTrueAndStopIsNotCalled() {
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

  @Test
  @SuppressWarnings("unchecked")
  public void usingMethodReducesTheNumberOfFactsForThen() {
    Consumer<FactMap<Object>> action = mock(Consumer.class);
    ArgumentCaptor<FactMap> captor = ArgumentCaptor.forClass(FactMap.class);
    Rule rule1 = StandardRule.create()
        .given("fact1", "First Fact")
        .given("fact2", "Second Fact")
        .given("fact3", "Third Fact")
        .using("fact3", "fact1")
        .then(action);
    rule1.run();

    verify(action, times(1)).accept(captor.capture());
    Assert.assertEquals(2, captor.getValue().size());
    Assert.assertEquals("First Fact", captor.getValue().getValue("fact1"));
    Assert.assertEquals("Third Fact", captor.getValue().getValue("fact3"));
    Assert.assertNull(captor.getValue().getValue("fact2"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void usingInSequenceCombinesFactsForThen() {
    Consumer<FactMap<Object>> action = mock(Consumer.class);
    ArgumentCaptor<FactMap> captor = ArgumentCaptor.forClass(FactMap.class);
    Rule rule1 = StandardRule.create()
        .given("fact1", "First Fact")
        .given("fact2", "Second Fact")
        .given("fact3", "Third Fact")
        .given("fact4", "Fourth Fact")
        .given("fact5", "Fifth Fact")
        .using("fact3", "fact1")
        .using("fact5")
        .then(action);
    rule1.run();

    verify(action, times(1)).accept(captor.capture());
    Assert.assertEquals(3, captor.getValue().size());
    Assert.assertEquals("First Fact", captor.getValue().getValue("fact1"));
    Assert.assertEquals("Third Fact", captor.getValue().getValue("fact3"));
    Assert.assertEquals("Fifth Fact", captor.getValue().getValue("fact5"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void usingResizesFactsForEachThen() {
    Consumer<FactMap<Object>> action1 = mock(Consumer.class);
    Consumer<FactMap<Object>> action2 = mock(Consumer.class);
    Rule rule1 = StandardRule.create()
        .given("fact1", "First Fact")
        .given("fact2", "Second Fact")
        .given("fact3", "Third Fact")
        .given("fact4", "Fourth Fact")
        .given("fact5", "Fifth Fact")
        .using("fact3", "fact1")
        .using("fact5")
        .then(action1)
        .using("fact1", "fact2")
        .then(action2);
    rule1.run();

    ArgumentCaptor<FactMap> captor1 = ArgumentCaptor.forClass(FactMap.class);

    verify(action1, times(1)).accept(captor1.capture());
    Assert.assertEquals(3, captor1.getValue().size());
    Assert.assertEquals("First Fact", captor1.getValue().getValue("fact1"));
    Assert.assertEquals("Third Fact", captor1.getValue().getValue("fact3"));
    Assert.assertEquals("Fifth Fact", captor1.getValue().getValue("fact5"));

    ArgumentCaptor<FactMap> captor2 = ArgumentCaptor.forClass(FactMap.class);

    verify(action2, times(1)).accept(captor2.capture());
    Assert.assertEquals(2, captor2.getValue().size());
    Assert.assertEquals("First Fact", captor2.getValue().getValue("fact1"));
    Assert.assertEquals("Second Fact", captor2.getValue().getValue("fact2"));
  }
}
