package com.deliveredtechnologies.rulebook.lang;


import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by clong on 3/24/17.
 */
public class GivenRuleBuilder<T, U> {
  Rule<T, U> _rule;

  GivenRuleBuilder(Rule<T, U> rule) {
    _rule = rule;
  }

  @SafeVarargs
  GivenRuleBuilder(Rule<T, U> rule, Fact<T>... facts) {
    _rule = rule;
    given(facts);
  }

  GivenRuleBuilder<T, U> given(String name, T value) {
    return given(new Fact<T>(name, value));
  }

  @SafeVarargs
  public final GivenRuleBuilder<T, U> given(Fact<T>... facts) {
    _rule.addFacts(facts);
    return this;
  }

  public WhenRuleBuilder<T, U> when(Predicate<FactMap<T>> condition) {
    return new WhenRuleBuilder<T, U>(_rule, condition);
  }

  public UsingRuleBuilder<T, U> using(String... factNames) {
    return new UsingRuleBuilder<T, U>(_rule, factNames);
  }

  public ThenRuleBuilder<T, U> then(Consumer<FactMap<T>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }

  public ThenRuleBuilder<T, U> then(BiConsumer<FactMap<T>, Result<U>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }
}
