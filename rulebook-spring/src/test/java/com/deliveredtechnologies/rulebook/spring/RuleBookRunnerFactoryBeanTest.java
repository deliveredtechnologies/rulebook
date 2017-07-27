package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleBookFactory;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Tests for {RuleBookRunnerFactoryBean}.
 */
@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RuleBookRunnerFactoryBeanTest {
  @Resource(name = "ruleBookFactoryWithResult")
  private RuleBookFactory _ruleBookFactory;

  @Autowired
  ApplicationContext _context;

  private NameValueReferableMap<String> _facts = new FactMap<>();

  @Before
  public void setUp() {
    _facts.setValue("value1", "Value");
    _facts.setValue("value2", "Value");
  }

  @Test
  public void ruleBookFactoryShouldInjectRuleBookFactoryObject() {
    Assert.assertNotNull(_ruleBookFactory);
  }

  @Test
  public void ruleBookFactoryShouldBeSingletonScopeForRuleBooks() {
    RuleBookFactory ruleBook = (RuleBookFactory)_context.getBean("ruleBookFactoryWithResult");
    Assert.assertTrue(ruleBook == _ruleBookFactory);
  }

  @Test
  public void ruleBookRunnerFactoryBeanShouldBeCreatedUsingClassAndPackageName() throws Exception {
    String pkg = "com.deliveredtechnologies.rulebook.spring";
    RuleBookRunnerFactoryBean factoryBean = new RuleBookRunnerFactoryBean(SomeRuleBook.class, pkg);
    RuleBookFactory<String> ruleBookFactory = (RuleBookFactory<String>)factoryBean.getObject();
    Field ruleBookField = ruleBookFactory.getClass().getDeclaredField("_ruleBookType");
    ruleBookField.setAccessible(true);

    Field packageField = ruleBookFactory.getClass().getDeclaredField("_pkg");
    packageField.setAccessible(true);

    Assert.assertEquals(ruleBookField.get(ruleBookFactory), SomeRuleBook.class);
    Assert.assertEquals(packageField.get(ruleBookFactory), pkg);
  }

  @Test
  public void ruleBookRunnerFactoryBeanShouldBeCreatedUsingOnlyPackageName() throws Exception {
    RuleBookRunnerFactoryBean factoryBean =
        new RuleBookRunnerFactoryBean("com.deliveredtechnologies.rulebook.spring");
    RuleBookFactory<String> ruleBookFactory = (RuleBookFactory<String>)factoryBean.getObject();
    RuleBook<String> ruleBook = ruleBookFactory.createRuleBook();
    Field ruleBookField = ruleBookFactory.getClass().getDeclaredField("_ruleBookType");
    ruleBookField.setAccessible(true);

    Assert.assertEquals(ruleBookField.get(ruleBookFactory), CoRRuleBook.class);
  }

  private static class SomeRuleBook implements RuleBook {
    @Override
    public void addRule(Rule rule) {

    }

    @Override
    public void run(NameValueReferableMap facts) {

    }

    @Override
    public void setDefaultResult(Object result) {

    }

    @Override
    public Optional<Result> getResult() {
      return null;
    }

    @Override
    public boolean hasRules() {
      return false;
    }
  }
}
