package com.deliveredtechnologies.rulebook;

import com.deliveredtechnologies.rulebook.annotation.Then;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.deliveredtechnologies.rulebook.RuleState.BREAK;
import static com.deliveredtechnologies.rulebook.RuleState.NEXT;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link StandardDecision}.
 */
public class StandardDecisionTest {

  @Test
  @SuppressWarnings("checked")
  public void standardDecisionIsCreated() {
    Decision<String, Boolean> decision1 = new StandardDecision<>();
    Decision<String, Boolean> decision2 = StandardDecision.create(String.class, Boolean.class);
    Decision decision3 = StandardDecision.create();

    Assert.assertNotNull(decision1);
    Assert.assertNotNull(decision2);
    Assert.assertNotNull(decision3);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenIsRunAndResultIsSetIfWhenIsTrue() {
    Decision<String, Boolean> decision = StandardDecision.create(String.class, Boolean.class)
        .given("helo", "world")
        .when(facts -> true)
        .then((facts, result) -> {
            result.setValue(true);
          });
    decision.run();

    Assert.assertTrue(decision.getResult());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenIsRunIfWhenIsTrue() {
    Decision<String, Boolean> rule = spy(
        StandardDecision.create(String.class, Boolean.class).given(new Fact<String>("hello", "world")));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule.when(f -> true).then(action).run();

    verify(action, times(1)).accept(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenIsNotRunIfWhenIsFalse() {
    Decision<String, Boolean> rule = spy(
        StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule.when(f -> false).then(action).run();

    verify(action, times(0)).accept(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenIsRunIfWhenDoesNotExist() {
    Decision<String, Boolean> rule = spy(
      StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    Consumer<FactMap<String>> action = (Consumer<FactMap<String>>) mock(Consumer.class);

    rule.then(action).run();

    verify(action, times(1)).accept(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenMethodsCanBeChained() {
    StandardDecision<String, String> rule =
      StandardDecision.create(String.class, String.class).given(new Fact<>("hello", "world"));
    Consumer<FactMap<String>> action1 = (Consumer<FactMap<String>>) mock(Consumer.class);
    BiConsumer<FactMap<String>, Result<String>> action2 = (facts, result) -> result.setValue("New");
    Consumer<FactMap<String>> action3 = (Consumer<FactMap<String>>) mock(Consumer.class);
    Result result = new Result("Old");
    rule.setResult(result);
    rule.then(action1).then(action2).then(action3).run();

    verify(action1, times(1)).accept(any(FactMap.class));
    verify(action3, times(1)).accept((any(FactMap.class)));
    Assert.assertEquals("New", result.getValue());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nextRuleInChainIsRunAndResultIsSetIfWhenIsFalse() {
    Decision<String, Boolean> decision1 = spy(
        StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    Decision<String, Boolean> decision2 = spy(
        StandardDecision.create(String.class, Boolean.class).given(new Fact<>("goodbye", "world")));

    decision1 = decision1.when(facts -> false);
    decision2 = decision2.when(facts -> true).then((facts, result) -> {
        result.setValue(true);
      });
    decision1.setNextRule(decision2);
    decision1.run();

    verify(decision1, times(1)).run();
    verify(decision2, times(1)).run();
    Assert.assertTrue(decision2.getResult());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nextRuleInChainIsRunAndResultIsSetIfWhenIsTrueAndThenReturnsNext() {
    Decision<String, Boolean> decision1 = spy(
        StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    Decision<String, Boolean> decision2 = spy(
        StandardDecision.create(String.class, Boolean.class).given(new Fact<>("goodbye", "world")));

    decision1 = decision1.when(facts -> true).then(f -> {});
    decision2 = decision2.when(facts -> true).then((facts, result) -> {
        result.setValue(true);
      });
    decision1.setNextRule(decision2);
    decision1.run();

    verify(decision1, times(1)).run();
    verify(decision2, times(1)).run();
    Assert.assertTrue(decision2.getResult());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nextRuleInChainIsNotRunIfWhenIsTrueAndThenReturnsBreak() {
    Decision<String, Boolean> decision1 = spy(
        StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    Decision<String, Boolean> decision2 = spy(
        StandardDecision.create(String.class, Boolean.class).given(new Fact<>("goodbye", "world")));

    decision1 = decision1.when(f -> true).then(facts -> {}).stop();
    decision2 = decision2.when(f -> true).then((facts, result) -> result.setValue(true)).stop();
    decision1.setNextRule(decision2);
    decision1.run();

    verify(decision1, times(1)).run();
    verify(decision2, times(0)).run();
    Assert.assertNull(decision2.getResult());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void usingMethodReducesTheNumberOfFactsForThen() {
    Consumer<FactMap<Object>> action = mock(Consumer.class);
    ArgumentCaptor<FactMap> captor = ArgumentCaptor.forClass(FactMap.class);
    Decision decision = StandardDecision.create()
      .given("fact1", "First Fact")
      .given("fact2", "Second Fact")
      .given("fact3", "Third Fact")
      .using("fact3", "fact1")
      .then(action);
    decision.run();

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
    Decision decision = StandardDecision.create()
      .given("fact1", "First Fact")
      .given("fact2", "Second Fact")
      .given("fact3", "Third Fact")
      .given("fact4", "Fourth Fact")
      .given("fact5", "Fifth Fact")
      .using("fact3", "fact1")
      .using("fact5")
      .then(action);
    decision.run();

    verify(action, times(1)).accept(captor.capture());
    Assert.assertEquals(3, captor.getValue().size());
    Assert.assertEquals("First Fact", captor.getValue().getValue("fact1"));
    Assert.assertEquals("Third Fact", captor.getValue().getValue("fact3"));
    Assert.assertEquals("Fifth Fact", captor.getValue().getValue("fact5"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void usingResizesFactsForEachThen() {
    BiConsumer<FactMap<Object>, Result<String>> action1 = mock(BiConsumer.class);
    Consumer<FactMap<Object>> action2 = mock(Consumer.class);
    ArgumentCaptor<FactMap> captor1 = ArgumentCaptor.forClass(FactMap.class);
    ArgumentCaptor<FactMap> captor2 = ArgumentCaptor.forClass(FactMap.class);
    Decision rule1 = StandardDecision.create(Object.class, String.class)
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

    verify(action1, times(1)).accept(captor1.capture(), any());
    Assert.assertEquals(3, captor1.getValue().size());
    Assert.assertEquals("First Fact", captor1.getValue().getValue("fact1"));
    Assert.assertEquals("Third Fact", captor1.getValue().getValue("fact3"));
    Assert.assertEquals("Fifth Fact", captor1.getValue().getValue("fact5"));
    verify(action2, times(1)).accept(captor2.capture());
    Assert.assertEquals(2, captor2.getValue().size());
    Assert.assertEquals("First Fact", captor2.getValue().getValue("fact1"));
    Assert.assertEquals("Second Fact", captor2.getValue().getValue("fact2"));
  }
}
