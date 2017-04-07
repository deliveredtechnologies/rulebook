package com.deliveredtechnologies.rulebook.lang;


import com.deliveredtechnologies.rulebook.*;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by clong on 3/24/17.
 */
public class GivenRuleBuilder<T, U> {
  Rule<T, U> _rule;

  GivenRuleBuilder(Rule<T, U> rule, NameValueReferableMap facts) {
    _rule = rule;
    given(facts);
  }

  @SafeVarargs
  GivenRuleBuilder(Rule<T, U> rule, NameValueReferable... facts) {
    _rule = rule;
    given(facts);
  }

  GivenRuleBuilder<T, U> given(String name, T value) {
    return given(new Fact<T>(name, value));
  }

  public final GivenRuleBuilder<T, U> given(NameValueReferableMap facts) {
    _rule.setFacts(facts);
    return this;
  }

  @SafeVarargs
  public final GivenRuleBuilder<T, U> given(NameValueReferable... facts) {
    _rule.addFacts(facts);
    return this;
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
