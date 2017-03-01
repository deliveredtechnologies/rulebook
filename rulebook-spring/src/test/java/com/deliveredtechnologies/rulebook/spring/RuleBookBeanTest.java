package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.runner.RuleAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InvalidClassException;

/**
 * Created by clong on 2/27/17.
 * Tests for {@link RuleBookBean}
 */
@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RuleBookBeanTest {
  @Autowired
  private SpringRuleWithResult _springRuleWithResult;

  @Autowired
  private ApplicationContext _context;

  @Test
  public void springRuleShouldBeAutowiredAsRule() throws InvalidClassException {
    RuleAdapter ruleAdapter = new RuleAdapter(_springRuleWithResult);
    ruleAdapter.given(new Fact("value1", "value"), new Fact("value2", "value")).run();

    Assert.assertEquals((String) ruleAdapter.getResult(), "firstRule");
  }

  @Test
  public void ruleBookBeanShouldLoadAndRunSpringPojoRules() {
    RuleBookBean ruleBookBean = _context.getBean(RuleBookBean.class);
    Fact<String> fact1 = new Fact<>("value1", "value");
    Fact<String> fact2 = new Fact<>("value2", "value");
    ruleBookBean.withDeafultResult(false).given(fact1, fact2).run();

    Assert.assertEquals((String) ruleBookBean.getResult(), "SecondRule");
    Assert.assertEquals(fact2.getValue(), "value2");
  }
}
