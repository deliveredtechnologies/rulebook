package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.*;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.Rule;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by clong on 3/28/17.
 */
public class RuleBuilderTest {
  @Test
  public void ruleBuilderShouldCreateGWTRules() {
    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer = (Consumer<NameValueReferableTypeConvertibleMap<String>>)Mockito.mock(Consumer.class);
    Rule rule = RuleBuilder.create()
            .withFactType(String.class)
            .given("fact1", "First Fact")
            .when(facts -> facts.getValue("fact1").equals("First Fact"))
            .then(consumer)
            .build();
    rule.invokeAction();

    verify(consumer, times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
  }

  @Test
  public void ruleBuilderUsingMethodShouldRestrictThenFacts() {
    FactMap<String> factMap = new FactMap<>();
    Rule rule = RuleBuilder.create()
            .withFactType(String.class)
            .given("fact1", "First Fact")
            .given("fact2", "Second Fact")
            .when(facts -> facts.getValue("fact1").equals("First Fact"))
            .using("fact1")
            .then(factMap::putAll)
            .build();
    rule.invokeAction();

    Assert.assertEquals(1, factMap.size());
    Assert.assertEquals("First Fact", factMap.getOne());
  }

  @Test
  public void ruleBuilderShouldAllowThenMethodAfterCreate() {
    Consumer<NameValueReferableTypeConvertibleMap<Object>> consumer = mock(Consumer.class);
    BiConsumer<NameValueReferableTypeConvertibleMap<Object>, Result<Object>> biConsumer = mock(BiConsumer.class);

    Rule rule1 = RuleBuilder.create().then(consumer).build();
    Rule rule2 = RuleBuilder.create().then(biConsumer).build();
    rule1.invokeAction();
    rule2.invokeAction();

    verify(consumer, times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
    verify(biConsumer, times(1)).accept(any(NameValueReferableTypeConvertibleMap.class), any(Result.class));
  }

  @Test
  public void ruleBuilderShouldAllowWhenMethodAfterCreate() {
    Consumer<NameValueReferableTypeConvertibleMap<Object>> consumer = mock(Consumer.class);
    Predicate<NameValueReferableTypeConvertibleMap<Object>> condition = mock(Predicate.class);
    when(condition.test(any(NameValueReferableTypeConvertibleMap.class))).thenReturn(true);

    Rule rule = RuleBuilder.create().when(condition).then(consumer).build();
    rule.invokeAction();

    verify(condition, times(1)).test(any(NameValueReferableTypeConvertibleMap.class));
    verify(consumer, times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
  }

  @Test
  public void ruleBuilderShouldAllowGivenMethodAfterCreate() {
    NameValueReferableMap<Object> factMap = new FactMap<>();
    factMap.setValue("fact1", "Fact1");
    Consumer<NameValueReferableTypeConvertibleMap<Object>> consumer = mock(Consumer.class);
    Rule rule1 = RuleBuilder.create()
            .given("fact1", "Fact1")
            .when(facts -> facts.containsKey("fact1"))
            .then(consumer).build();
    Rule rule2 = RuleBuilder.create()
            .given(new Fact("fact1", "Fact1"))
            .when(facts -> facts.containsKey("fact1"))
            .then(consumer).build();
    Rule rule3 = RuleBuilder.create()
            .given(factMap)
            .when(facts -> facts.containsKey("fact1"))
            .then(consumer).build();

    rule1.invokeAction();
    rule2.invokeAction();
    rule3.invokeAction();

    verify(consumer, times(3)).accept(any(NameValueReferableTypeConvertibleMap.class));
  }
}
