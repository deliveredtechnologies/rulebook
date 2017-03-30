package com.deliveredtechnologies.rulebook.lang.rule;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by clong on 3/24/17.
 */
public class UsingRuleBuilder<T, U> {
  private Rule<T, U> _rule;

  UsingRuleBuilder(Rule<T, U> rule, String... factNames) {
    rule.addFactNameFilter(factNames);
    _rule = rule;
  }

  public ThenRuleBuilder<T, U> then(Consumer<FactMap<T>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }

  public ThenRuleBuilder<T, U> then(BiConsumer<FactMap<T>, Result<U>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }

  public UsingRuleBuilder<T, U> using(String... factNames) {
    _rule.addFactNameFilter(factNames);
    return this;
  }
}
