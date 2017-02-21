package com.deliveredtechnologies.rulebook.runner;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by clong on 2/18/17.
 * Tests for {@link RuleBookRunner}
 */
public class RuleBookRunnerTest {
  @Test
  public void ruleBookRunnerAddsRuleClassesInPackage() {
    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook.runner"));
    ruleBookRunner.run();

    verify(ruleBookRunner, times(3)).addRule(any(RuleAdapter.class));
  }

  @Test
  public void ruleBookRunnerDoesntLoadClassesIfNotInPackage() {
    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook"));
    ruleBookRunner.run();

    verify(ruleBookRunner, times(0)).addRule(any(RuleAdapter.class));
  }

  @Test
  public void ruleBookRunnerDoesntLoadClassesForInvalidPackage() {
    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook.invalid"));
    ruleBookRunner.run();

    verify(ruleBookRunner, times(0)).addRule(any(RuleAdapter.class));
  }
}
