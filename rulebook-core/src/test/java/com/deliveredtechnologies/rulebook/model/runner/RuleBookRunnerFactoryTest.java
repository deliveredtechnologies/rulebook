package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Tests for {@link RuleBookRunnerFactory}.
 */

public class RuleBookRunnerFactoryTest {
  @Test
  public void ruleBookRunnerFactoryIsCreatedFromPackageNameOnly() {
    RuleBookRunnerFactory factory = new RuleBookRunnerFactory("com.deliveredtechnologies.rulebook.runner");
    RuleBook ruleBook = factory.createRuleBook();

    Assert.assertNotNull(ruleBook);
  }

  @Test
  public void ruleBookRunnerFactoryIsCreatedFromPackageNameAndRuleBookClass() throws Exception {
    String pkg = "com.deliveredtechnologies.rulebook.runner";
    RuleBookRunnerFactory factory =
        new RuleBookRunnerFactory(GoodRuleBook.class, pkg);
    RuleBookRunner ruleBook = (RuleBookRunner)factory.createRuleBook();

    Field ruleBookField = ruleBook.getClass().getDeclaredField("_ruleBook");
    ruleBookField.setAccessible(true);

    Field packageField = ruleBook.getClass().getDeclaredField("_package");
    packageField.setAccessible(true);

    Assert.assertEquals(ruleBookField.get(ruleBook).getClass(), GoodRuleBook.class);
    Assert.assertEquals(packageField.get(ruleBook), pkg);
  }

  @Test
  public void ruleBookRunnerFactoryCreatesCoRRuleBookWhenRuleBookTypeIsNotValid() throws Exception {
    RuleBookRunnerFactory factory =
        new RuleBookRunnerFactory(BadRuleBook.class,"com.deliveredtechnologies.rulebook.runner");
    RuleBookRunner ruleBookRunner = (RuleBookRunner)factory.createRuleBook();
    Field ruleBookField = ruleBookRunner.getClass().getDeclaredField("_ruleBook");
    ruleBookField.setAccessible(true);

    Assert.assertEquals(ruleBookField.get(ruleBookRunner).getClass(), CoRRuleBook.class);
  }
}
