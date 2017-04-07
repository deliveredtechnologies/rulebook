package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithResult;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithoutAnnotations;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithoutRuleAnnotation;
import org.junit.Assert;
import org.junit.Test;

import java.io.InvalidClassException;
import java.util.function.Predicate;

/**
 * Tests for {@link RuleAdapter}
 */
public class RuleAdapterTest {
  @Test
  public void ruleAdapterShouldGetCreatedUsingPojoRuleAndOptionallyRuleImplementation() throws InvalidClassException {
    RuleAdapter ruleAdapter1 = new RuleAdapter(new SampleRuleWithResult());
    RuleAdapter ruleAdapter2 = new RuleAdapter(new SampleRuleWithResult(), new GoldenRule(String.class));

    Assert.assertNotNull(ruleAdapter1);
    Assert.assertNotNull(ruleAdapter2);
  }

  @Test(expected = InvalidClassException.class)
  public void pojoRulesMissingRuleAnnotationThrowAnErrorOnInitialization() throws InvalidClassException {
    new RuleAdapter(new SampleRuleWithoutRuleAnnotation());
  }

  @Test
  public void addingFactsShouldAddThemToRuleDelegateAndMapToPropertiesInPojoRule() throws InvalidClassException {
    NameValueReferableMap<Integer> factMap = new FactMap<>();
    Rule<String, Object> rule = new GoldenRule<>(String.class);
    SampleRuleWithResult pojo = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(pojo, rule);

    factMap.setValue("value1", 500);
    ruleAdapter.addFacts(new Fact<String>("fact1", "Fact One"), new Fact<String>("fact2", "Fact Two"));
    ruleAdapter.addFacts(factMap);

    Assert.assertEquals(3, ruleAdapter.getFacts().size());
    Assert.assertEquals("Fact One", pojo.getFact1());
    Assert.assertEquals("Fact Two", pojo.getFact2());
    Assert.assertEquals(500, pojo.getValue1());
  }

  @Test
  public void settingFactsShouldOverwriteFactsInRuleDelegateAndInPojoRule() throws InvalidClassException {
    NameValueReferableMap factMap = new FactMap();
    Rule<String, Object> rule = new GoldenRule<>(String.class);
    SampleRuleWithResult pojo = new SampleRuleWithResult();
    RuleAdapter ruleAdapter = new RuleAdapter(pojo, rule);

    factMap.setValue("value1", 5100);
    factMap.setValue("fact1", "Fact1");
    factMap.setValue("fact2", "Fact2");
    ruleAdapter.addFacts(new Fact<String>("fact1", "Fact One"),
            new Fact<String>("fact2", "Fact Two"),
            new Fact<Integer>("value1", 500));
    ruleAdapter.setFacts(factMap);

    Assert.assertEquals(3, ruleAdapter.getFacts().size());
    Assert.assertEquals("Fact1", pojo.getFact1());
    Assert.assertEquals("Fact2", pojo.getFact2());
    Assert.assertEquals(5100, pojo.getValue1());
  }

  @Test
  public void explicitlySettingConditionTakesPrecedenceOverPojoCondition() throws InvalidClassException {
    RuleAdapter ruleAdapter = new RuleAdapter(new SampleRuleWithResult());
    Predicate<NameValueReferableMap> condition = facts -> true;
    ruleAdapter.setCondition(condition);

    Assert.assertEquals(condition, ruleAdapter.getCondition());
  }
}
