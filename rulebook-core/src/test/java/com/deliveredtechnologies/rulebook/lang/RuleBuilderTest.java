package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.model.Rule;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by clong on 3/28/17.
 */
public class RuleBuilderTest {
  @Test
  @SuppressWarnings("unchecked")
  public void ruleBuilderCreatesGWTRules() {
    Consumer<FactMap<String>> consumer = (Consumer<FactMap<String>>)Mockito.mock(Consumer.class);
    Rule rule = RuleBuilder.create(String.class)
            .given("fact1", "First Fact")
            .when(facts -> facts.getValue("fact1").equals("First Fact"))
            .then(consumer)
            .build();
    rule.invokeAction();

    verify(consumer, times(1)).accept(any(FactMap.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ruleBuilderUsingMethod() {
    FactMap<String> factMap = new FactMap<>();
    Rule rule = RuleBuilder.create(String.class)
            .given("fact1", "First Fact")
            .given("fact2", "Second Fact")
            .when(facts -> facts.getValue("fact1").equals("First Fact"))
            .using("fact1")
            .then(facts -> factMap.putAll(facts))
            .build();
    rule.invokeAction();

    Assert.assertEquals(1, factMap.size());
    Assert.assertEquals("First Fact", factMap.getOne());
  }
}
