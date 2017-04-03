package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link RuleBuilder}
 */
public class RuleBookBuilderTest {

  @Test
  public void ruleBookBuilderShouldCreateItself() {
    RuleBookBuilder builder1 = RuleBookBuilder.create();
    RuleBookBuilder<Boolean> builder2 =  RuleBookBuilder.create(Boolean.class);
    RuleBookBuilder<String> builder3 = RuleBookBuilder.create(String.class, CoRRuleBook.class);
    RuleBookBuilder<String> builder4 = RuleBookBuilder.create(String.class, SampleRuleBook.class);
    RuleBookBuilder<String> builder5 = RuleBookBuilder.create(String.class, RuleBook.class);

    Assert.assertNotNull(builder1);
    Assert.assertNotNull(builder2);
    Assert.assertNotNull(builder3);
    Assert.assertNotNull(builder4);
    Assert.assertNotNull(builder5);
  }

  @Test
  public void ruleBookBuilderShouldBuildRuleBookWithNoRules() {
    RuleBookBuilder builder = RuleBookBuilder.create(String.class, CoRRuleBook.class);
    RuleBookBuilder sampleBuilder = RuleBookBuilder.create(Boolean.class, SampleRuleBook.class);

    Assert.assertNotNull(builder.build());
    Assert.assertTrue(builder.build() instanceof CoRRuleBook);
    Assert.assertNotNull(sampleBuilder.build());
    Assert.assertTrue(sampleBuilder.build() instanceof SampleRuleBook);
  }

  @Test
  public void ruleBookBuilderShouldBuildRuleBookWithRulesAndResult() {
    RuleBook<String> ruleBook = RuleBookBuilder.create(String.class)
            .withDefaultResult("")
            .addRule(RuleBuilder.create(String.class, String.class).then((facts, result) -> result.setValue("result")))
            .build();
    ruleBook.run(new FactMap());

    Assert.assertEquals("result", ruleBook.getResult().get().getValue());
  }

  @Test
  public void ruleBookBuilderShouldBuildRuleBookWithLambdaRulesAndResult() {
    RuleBook<String> ruleBook = RuleBookBuilder.create(String.class)
            .withDefaultResult("")
            .addRule(String.class, rule -> rule.then((facts, result) -> result.setValue("result")))
            .build();
    ruleBook.run(new FactMap());

    Assert.assertEquals("result", ruleBook.getResult().get().getValue());
  }

  @Test
  public void ruleBookBuilderShouldBuildRuleBookWithRulesAndNoResult() {
    RuleBook ruleBook = RuleBookBuilder.create(String.class)
            .addRule(RuleBuilder.create(String.class, String.class).then((facts, result) -> result.setValue("result")))
            .build();
    ruleBook.run(new FactMap());

    Assert.assertFalse(ruleBook.getResult().isPresent());
  }

  @Test
  public void ruleBookBuilderShouldAddRuleAsFunction() {
    FactMap factMap = new FactMap();
    RuleBook<String> ruleBook = RuleBookBuilder.create(String.class)
            .addRule(String.class, rule -> rule.then(facts -> facts.setValue("test", "Test")))
            .build();
    ruleBook.run(factMap);

    Assert.assertFalse(ruleBook.getResult().isPresent());
    Assert.assertTrue(factMap.containsKey("test"));
    Assert.assertEquals(factMap.toString(), "Test");
  }

  @Test
  public void ruleBookBuilderShouldAddRuleWithNoResult() {
    FactMap factMap = new FactMap();
    RuleBook ruleBook = RuleBookBuilder.create()
            .addRule(RuleBuilder.create(String.class).then(facts -> facts.setValue("test", "Test")))
            .build();
    ruleBook.run(factMap);

    Assert.assertFalse(ruleBook.getResult().isPresent());
    Assert.assertTrue(factMap.containsKey("test"));
    Assert.assertEquals(factMap.toString(), "Test");
  }

  @Test
  public void ruleBookBuilderShouldBuildRuleBookWithMultipleRulesAndResult() {
    RuleBook<String> ruleBook = RuleBookBuilder.create(String.class)
            .withDefaultResult("result")
            .addRule(String.class, rule -> rule.then((facts, result) -> facts.setValue("poo", "poo")))
            .addRule(Integer.class, rule -> rule.then(facts -> facts.setValue("pee", 5)))
            .addRule(Double.class, rule -> rule.then((facts, result) -> {
              facts.setValue("double", 0.12);
              result.setValue("something");
            })).build();

    ruleBook.run(new FactMap());

    Assert.assertTrue(ruleBook.getResult().isPresent());
    Assert.assertEquals("something", ruleBook.getResult().get().getValue());
  }
}
