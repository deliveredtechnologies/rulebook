package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Decision;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.any;

/**
 * Created by clong on 2/18/17.
 * Tests for {@link RuleBookRunner}
 */
public class RuleBookRunnerTest {
  @Test
  public void ruleBookRunnerAddsRuleClassesInPackage() {
    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook.runner"));
    ruleBookRunner.run();

    verify(ruleBookRunner, times(2)).addRule(any(RuleAdapter.class));
  }

  @Test
  public void ruleBookRunnerDoesntLoadClassesIfNotInPackage() {
    RuleBookRunner ruleBookRunner = spy(new RuleBookRunner("com.deliveredtechnologies.rulebook"));
    ruleBookRunner.run();

    verify(ruleBookRunner, times(0)).addRule(any(RuleAdapter.class));
  }
}
