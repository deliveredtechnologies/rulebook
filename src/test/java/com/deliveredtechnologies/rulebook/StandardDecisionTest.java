package com.deliveredtechnologies.rulebook;

import org.junit.Assert;
import org.junit.Test;

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
 * Created by clong on 2/7/17.
 * Tests for {@link StandardDecision}
 */
public class StandardDecisionTest {

  @Test
  @SuppressWarnings("checked")
  public void standardDecisionIsCreated() {
    StandardDecision<String, Boolean> decision1 = new StandardDecision<>();
    StandardDecision<String, Boolean> decision2 = StandardDecision.create(String.class, Boolean.class);
    StandardDecision decision3 = StandardDecision.create();

    Assert.assertNotNull(decision1);
    Assert.assertNotNull(decision2);
    Assert.assertNotNull(decision3);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenIsRunAndResultIsSetIfWhenIsTrue() {
    StandardDecision<String, Boolean> decision = StandardDecision.create(String.class, Boolean.class)
      .given(new Fact<>("hello", "world"))
      .when(facts -> true)
      .then((facts, result) -> {
        result.setValue(true);
        return NEXT;
      });
    decision.run();

    Assert.assertTrue(decision.getResult());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenIsRunIfWhenIsTrue() {
    StandardDecision<String, Boolean> rule = spy(
      StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    Function<FactMap<String>, RuleState> action = (Function<FactMap<String>, RuleState>) mock(Function.class);
    when(action.apply(any(FactMap.class))).thenReturn(NEXT);

    rule.when(f -> true).then(action).run();

    verify(action, times(1)).apply(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void thenIsNotRunIfWhenIsFalse() {
    StandardDecision<String, Boolean> rule = spy(
      StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    Function<FactMap<String>, RuleState> action = (Function<FactMap<String>, RuleState>) mock(Function.class);
    when(action.apply(any(FactMap.class))).thenReturn(NEXT);

    rule.when(f -> false).then(action).run();

    verify(action, times(0)).apply(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void nextRuleInChainIsRunAndResultIsSetIfWhenIsFalse() {
    StandardDecision<String, Boolean> decision1 = spy(
      StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    StandardDecision<String, Boolean> decision2 = spy(
      StandardDecision.create(String.class, Boolean.class).given(new Fact<>("goodbye", "world")));

    decision1 = decision1.when(facts -> false);
    decision2 = decision2.when(facts -> true).then((facts, result) -> {
      result.setValue(true);
      return BREAK;
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
    StandardDecision<String, Boolean> decision1 = spy(
      StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    StandardDecision<String, Boolean> decision2 = spy(
      StandardDecision.create(String.class, Boolean.class).given(new Fact<>("goodbye", "world")));

    decision1 = decision1.when(facts -> true).then(f -> NEXT);
    decision2 = decision2.when(facts -> true).then((facts, result) -> {
      result.setValue(true);
      return BREAK;
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
    StandardDecision<String, Boolean> decision1 = spy(
      StandardDecision.create(String.class, Boolean.class).given(new Fact<>("hello", "world")));
    StandardDecision<String, Boolean> decision2 = spy(
      StandardDecision.create(String.class, Boolean.class).given(new Fact<>("goodbye", "world")));

    decision1 = decision1.when(f -> true).then(facts -> BREAK);
    decision2 = decision2.when(f -> true).then((facts, result) -> {
      result.setValue(true);
      return BREAK;
    });
    decision1.setNextRule(decision2);
    decision1.run();

    verify(decision1, times(1)).run();
    verify(decision2, times(0)).run();
    Assert.assertNull(decision2.getResult());
  }
}
