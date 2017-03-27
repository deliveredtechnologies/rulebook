package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RuleBuilder<T, U> {
  private Rule<T, U> _rule;

  public static <T, U> RuleBuilder<T, U> create(Class<T> factType, Class<T> resultType) {
    return new RuleBuilder<T, U>(new GoldenRule<T, U>(factType));
  }

  public static RuleBuilder create() {
    return new RuleBuilder();
  }

  public RuleBuilder(Rule<T, U> rule) {
    _rule = rule;
  }

  @SuppressWarnings("unchecked")
  public RuleBuilder() {
    this(new GoldenRule(Object.class));
  }
  
  public GivenRuleBuilder<T, U> withDefaultResult(U resultVal) {
    _rule.setResult(new Result<U>(resultVal));
    return new GivenRuleBuilder<T, U>(_rule);
  }

  public GivenRuleBuilder<T, U> given(String name, T value) {
    return new GivenRuleBuilder<T, U>(_rule, new Fact<T>(name, value));
  }

  @SafeVarargs
  public final GivenRuleBuilder<T, U> given(Fact<T>... facts) {
    return new GivenRuleBuilder<T, U>(_rule, facts);
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
