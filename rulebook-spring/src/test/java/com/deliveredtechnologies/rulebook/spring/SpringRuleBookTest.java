package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferable;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleStatus;
import com.deliveredtechnologies.rulebook.model.Auditor;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.InvalidClassException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 * Tests for {@link SpringRuleBook}.
 */
@ContextConfiguration(classes = TestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringRuleBookTest {
  RuleBook<String> _ruleBook;
  NameValueReferableMap<String> _facts = new FactMap<>();

  @Autowired
  SpringRuleWithResult _ruleWithResult;

  @Resource(name = "text3")
  RuleBook _auditableRuleBook;

  /**
   * Setup the RuleBook and the facts to be used in testing.
   */
  @Before
  public void setUp() {
    _ruleBook = new CoRRuleBook<>();
    _facts.setValue("value1", "Value");
    _facts.setValue("value2", "Value");
  }

  @Test
  public void addingPojoRulesToSpringRuleBookShouldConvertThemToRules() throws InvalidClassException {
    SpringRuleBook<String> ruleBook = new SpringRuleBook<>(_ruleBook);
    ruleBook.addRule(_ruleWithResult);
    ruleBook.setDefaultResult("");
    ruleBook.run(_facts);

    Assert.assertEquals("value2", _facts.getValue("value2"));
    Assert.assertEquals("firstRule", ruleBook.getResult().get().getValue());
    Assert.assertTrue(ruleBook.hasRules());
  }

  @Test
  public void addingRulesToSpringRuleBookShouldAddTheRuleToTheDelegate() throws InvalidClassException {
    RuleBook<String> mockRuleBook = (RuleBook<String>)mock(CoRRuleBook.class);
    SpringRuleBook<String> ruleBook = new SpringRuleBook<>(mockRuleBook);
    Rule rule = RuleBuilder.create().build();
    ruleBook.addRule(rule);

    Mockito.verify(mockRuleBook, times(1)).addRule(rule);
  }

  @Test
  public void springRuleBooksWithNoRulesShouldReturnFalseForHasRules() {
    SpringRuleBook<String> ruleBook = new SpringRuleBook<>(_ruleBook);

    Assert.assertFalse(ruleBook.hasRules());
  }

  @Test
  public void springRuleBookRunnersShouldBeAuditable() {
    Auditor auditor = (Auditor)_auditableRuleBook;
    NameValueReferableMap<String> facts = new FactMap<String>();
    facts.setValue("value1", "value2");
    facts.setValue("value2", "value2");
    _auditableRuleBook.run(facts);
    Assert.assertEquals(2, auditor.getRuleStatusMap().size());
    for (String key : auditor.getRuleStatusMap().keySet()) {
      Assert.assertEquals(RuleStatus.EXECUTED, auditor.getRuleStatusMap().get(key));
    }
    Assert.assertTrue(_auditableRuleBook.getResult().isPresent());
    _auditableRuleBook.getResult().ifPresent(result -> Assert.assertEquals("firstRule", result.toString()));
  }
}
