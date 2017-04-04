package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by clong on 4/3/17.
 */
public class RuleBookBuilderTest {

  @Test
  public void ruleBookBuilderShouldBuildRuleBooksWithRulesAndaResult() {
    NameValueReferableMap factMap = new FactMap();
    RuleBookBuilder<String> ruleBookBuilder = RuleBookBuilder.create(String.class).withDefaultResult("nothing");
    ruleBookBuilder.addRule().withFactType(String.class)
            .when(facts -> facts.getValue("fact1").equals("First Fact"))
            .then(facts -> facts.setValue("fact2", "Second Fact"));
    ruleBookBuilder.addRule().withFactType(String.class)
            .when(facts -> facts.getValue("fact2").equals("Second Fact"))
            .then((facts, result) -> result.setValue("something"));
    RuleBook<String> ruleBook = ruleBookBuilder.build();

    factMap.setValue("fact1", "First Fact");
    ruleBook.run(factMap);

    Assert.assertEquals("something", ruleBook.getResult().get().getValue());
    Assert.assertEquals(2, factMap.size());
    Assert.assertTrue(factMap.containsKey("fact2"));
  }
}
