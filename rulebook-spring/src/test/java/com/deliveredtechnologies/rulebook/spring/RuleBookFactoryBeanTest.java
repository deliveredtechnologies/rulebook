package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferable;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;
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

/**
 * Tests for {@link RuleBookFactoryBean}.
 */
@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RuleBookFactoryBeanTest {
  @Resource(name = "ruleBookWithResult")
  private RuleBook<String> _ruleBook;

  @Autowired
  ApplicationContext _context;

  private NameValueReferableMap<String> _facts = new FactMap<>();

  @Before
  public void setUp() {
    _facts.setValue("value1", "Value");
    _facts.setValue("value2", "Value");
  }

  @Test
  public void ruleBookFactoryShouldInjectRuleBookObject() {
    Assert.assertNotNull(_ruleBook);
  }

  @Test
  public void ruleBookShouldRunRulesInPackage() {
    _ruleBook.run(_facts);

    Assert.assertEquals("valueTwo", _facts.getValue("value2"));
    Assert.assertEquals("firstRule", _ruleBook.getResult().get().getValue());
  }

  @Test
  public void ruleBookFactoryShouldBeSingletonScopeForRuleBooks() {
    RuleBook ruleBook = (RuleBook)_context.getBean("ruleBookWithResult");
    Assert.assertTrue(ruleBook == _ruleBook);
  }

  @Test
  public void ruleBookFactoryBeanShouldBeCreatedUsingOnlyPackageName() throws Exception {
    RuleBookFactoryBean factoryBean = new RuleBookFactoryBean("com.deliveredtechnologies.rulebook.spring");
    RuleBook<String> ruleBook = (RuleBook<String>)factoryBean.getObject();

    ruleBook.run(_facts);

    Assert.assertEquals("valueTwo", _facts.getValue("value2"));
    Assert.assertEquals("firstRule", ruleBook.getResult().get().getValue());
  }

  @Test
  public void ruleBookFactoryBeanShouldBeCreatedUsingOnlyClassName() throws Exception {
    RuleBookFactoryBean factoryBean = new RuleBookFactoryBean(CoRRuleBook.class);
    RuleBook<String> ruleBook = (RuleBook<String>)factoryBean.getObject();
    ruleBook.addRule(RuleBuilder.create().withFactType(String.class)
            .given(_facts)
            .when(facts -> facts.getValue("value1").equals("Value"))
            .then(facts -> facts.setValue("value1", "Value One"))
            .build());

    ruleBook.run(_facts);

    Assert.assertEquals("Value One", _facts.getValue("value1"));
  }

  @Test
  public void ruleBookFactoryBeanShouldBeCreatedUsingClassAndPackageName() throws Exception {
    RuleBookFactoryBean factoryBean = new RuleBookFactoryBean(
            CoRRuleBook.class,
            "com.deliveredtechnologies.rulebook.spring");
    RuleBook<String> ruleBook = (RuleBook<String>)factoryBean.getObject();

    ruleBook.run(_facts);

    Assert.assertEquals("valueTwo", _facts.getValue("value2"));
    Assert.assertEquals("firstRule", ruleBook.getResult().get().getValue());
  }

  @Test
  public void ruleBookFactoryBeanShouldBeCreatedEvenIfBothClassAndPackageAreNull() throws Exception {
    RuleBookFactoryBean factoryBean = new RuleBookFactoryBean(null, null);
    RuleBook<String> ruleBook = (RuleBook<String>)factoryBean.getObject();
    ruleBook.addRule(RuleBuilder.create().withFactType(String.class)
            .given(_facts)
            .when(facts -> facts.getValue("value1").equals("Value"))
            .then(facts -> facts.setValue("value1", "Value One"))
            .build());

    ruleBook.run(_facts);

    Assert.assertEquals("Value One", _facts.getValue("value1"));
  }
}
