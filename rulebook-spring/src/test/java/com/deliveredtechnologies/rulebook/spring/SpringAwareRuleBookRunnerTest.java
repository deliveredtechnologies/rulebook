package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.deliveredtechnologies.rulebook.spring.SpringTestService.EXPECTED_RESULT;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringAwareRuleBookRunnerTest {
  @Autowired
  private SpringAwareRuleBookRunner _ruleBook;

  private NameValueReferableMap<String> _facts = new FactMap<>();

  @Before
  public void setUp() {
    _facts.setValue("value1", "Value");
    _facts.setValue("value2", "Value");
  }

  @Test
  public void ruleBookShouldRunRulesInPackage() {
    _ruleBook.run(_facts);
    Assert.assertEquals(EXPECTED_RESULT, _facts.getValue("value2"));
  }
}
