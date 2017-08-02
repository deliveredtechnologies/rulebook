package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.mockito.Matchers.any;

/**
 * Tests for {@link RuleBookBuilder}.
 */
public class RuleBookBuilderTest {
  @Test
  public void ruleBookBuilderShouldBuildRulesWithoutResult() {
    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer = Mockito.mock(Consumer.class);
    RuleBook ruleBook = RuleBookBuilder.create()
        .addRule(rule -> rule
            .withFactType(String.class)
            .then(consumer))
        .build();
    ruleBook.run(new FactMap());

    Mockito.verify(consumer, Mockito.times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
  }

  @Test
  public void ruleBookBuilderShouldBuildRulesWithResult() {
    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer = Mockito.mock(Consumer.class);
    RuleBook<Boolean> ruleBook = RuleBookBuilder.create().withResultType(Boolean.class).withDefaultResult(false)
            .addRule(rule -> rule
                    .withFactType(String.class)
                    .then((facts, result) -> result.setValue(true))
                    .then(consumer))
            .build();
    ruleBook.run(new FactMap());

    Mockito.verify(consumer, Mockito.times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
    Assert.assertTrue(ruleBook.getResult().get().getValue());
  }

  @Test
  public void ruleBookBuilderShouldChainMultipleRulesWithResult() {
    NameValueReferableMap factMap = new FactMap();
    RuleBook<String> ruleBook = RuleBookBuilder.create().withResultType(String.class).withDefaultResult("initial value")
            .addRule(RuleBuilder.create(GoldenRule.class)
                    .withFactType(String.class)
                    .withResultType(String.class)
                    .when(facts -> true)
                    .then((facts, result) -> result.setValue("RESULT"))
                    .build())
            .addRule(rule -> rule
                    .withFactType(String.class)
                    .then(facts -> facts.setValue("fact", "FACT")))
            .addRule(rule -> rule
                    .withFactType(String.class)
                    .using("fact2")
                    .then(facts -> facts.setValue("fact22", "Second " + facts.getOne())))
            .build();

    factMap.setValue("fact2", "Second Fact!");
    ruleBook.run(factMap);
    Assert.assertTrue(ruleBook.getResult().isPresent());
    Assert.assertEquals("RESULT", ruleBook.getResult().get().getValue());
    Assert.assertEquals("FACT", factMap.getValue("fact"));
    Assert.assertEquals("Second Second Fact!", factMap.getValue("fact22"));
  }

  @Test
  public void ruleBookBuilderShouldCreateSpecifiedType() {
    Assert.assertNotNull(RuleBookBuilder.create(CoRRuleBook.class).build());
    Assert.assertNotNull(RuleBookBuilder.create(SampleRuleBookWithOneArgConstructor.class).build());
  }

  @Test(expected = IllegalStateException.class)
  public void ruleBookBuilderShouldThrowExceptionOnRuleBookThatCantBeCreated() {
    RuleBookBuilder.create(SampleRuleBookWithPrivateConstructor.class).build();
  }

  @Test
  public void ruleBookBuilderShouldAddRules() {
    NameValueReferableMap<String> factMap = new FactMap<>();
    RuleBook ruleBook = RuleBookBuilder.create().addRule(
        RuleBuilder.create().withFactType(String.class)
            .then(facts -> facts.setValue("fact1", "Fact One"))
            .build())
        .build();

    ruleBook.run(factMap);

    Assert.assertEquals(1, factMap.size());
    Assert.assertTrue(factMap.containsKey("fact1"));
  }
}
