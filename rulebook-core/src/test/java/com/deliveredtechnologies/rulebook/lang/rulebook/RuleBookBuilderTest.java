package com.deliveredtechnologies.rulebook.lang.rulebook;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.lang.rule.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * Created by clong on 3/29/17.
 */
public class RuleBookBuilderTest {
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
  public void ruleBookBuilderShouldBuildRuleBookWithMultipleRulesAndResult() {
    RuleBook<String> ruleBook = RuleBookBuilder.create(String.class)
            .withDefaultResult("result")
            .addRule(String.class, rule -> rule.then((facts, result) -> facts.setValue("poo", "poo")))
            .addRule(Integer.class, rule -> rule.then(facts -> facts.setValue("pee", 5)))
            .addRule(Double.class, rule -> rule.then((facts, result) -> {
              facts.put("double", 0.12);
              result.setValue("something");
            })).build();

    ruleBook.run(new FactMap());

    Assert.assertTrue(ruleBook.getResult().isPresent());
    Assert.assertEquals("something", ruleBook.getResult().get().getValue());
  }
}
