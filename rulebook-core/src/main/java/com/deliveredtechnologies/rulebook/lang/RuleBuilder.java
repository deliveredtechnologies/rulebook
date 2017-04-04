package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.*;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RuleBuilder<T, U> {
  private Rule<T, U> _rule;

  public static <T, U> RuleBuilder<T, U> create(Rule<T, U> rule) {
    return new RuleBuilder<T, U>(rule);
  }

  public static <T, U> RuleBuilder<T, U> create(Class<T> factType, Class<U> resultType) {
    return new RuleBuilder<T, U>(new GoldenRule<>(factType));
  }

  public static <T> RuleBuilder<T, Object> create(Class<T> factType) {
    return new RuleBuilder<T, Object>(new GoldenRule<>(factType));
  }

  public static RuleBuilder<Object, Object> create() {
    return new RuleBuilder<Object, Object>();
  }

  private RuleBuilder(Rule<T, U> rule) {
    _rule = rule;
  }

  @SuppressWarnings("unchecked")
  private RuleBuilder() {
    this(new GoldenRule(Object.class));
  }

  public GivenRuleBuilder<T, U> given(String name, T value) {
    return new GivenRuleBuilder<T, U>(_rule, new Fact<T>(name, value));
  }

  @SafeVarargs
  public final GivenRuleBuilder<T, U> given(NameValueReferable... facts) {
    return new GivenRuleBuilder<T, U>(_rule, facts);
  }

  public final GivenRuleBuilder<T, U> given(NameValueReferableMap facts) {
    return new GivenRuleBuilder<T, U>(_rule, facts);
  }

  public WhenRuleBuilder<T, U> when(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) {
    return new WhenRuleBuilder<T, U>(_rule, condition);
  }

  public UsingRuleBuilder<T, U> using(String... factNames) {
    return new UsingRuleBuilder<T, U>(_rule, factNames);
  }

  public ThenRuleBuilder<T, U> then(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }

  public ThenRuleBuilder<T, U> then(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }
}
