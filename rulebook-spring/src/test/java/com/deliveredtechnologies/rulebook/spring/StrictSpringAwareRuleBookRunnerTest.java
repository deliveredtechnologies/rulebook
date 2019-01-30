package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StrictSpringAwareRuleBookRunnerTest {
  @Autowired
  @Qualifier("strictSpringRuleBookRunner")
  private SpringAwareRuleBookRunner _ruleBook;

  private NameValueReferableMap<String> _facts = new FactMap<>();

  @Before
  public void setUp() {
    _facts.setValue("value1", "Value");
    _facts.setValue("value2", "Value");
  }


  @Test(expected = IllegalStateException.class)
  public void strictRuleBookShouldThrowInvalidStateForPojoRules() {
    _ruleBook.getRuleInstance(SpringRuleWithoutResult.class);
  }

  @Test()
  public void strictRuleBookShouldReturnBeanRuleSuccessfully() {
    final Object ruleInstance = _ruleBook.getRuleInstance(SpringRuleWithResult.class);

    assertThat(ruleInstance, is(SpringRuleWithResult.class));
  }


}
