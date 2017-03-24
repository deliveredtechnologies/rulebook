package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by clong on 3/24/17.
 */
public class WhenRuleBuilder<T, U> {

  private Rule<T, U> _rule;

  WhenRuleBuilder(Rule<T, U> rule, Predicate<FactMap<T>> condition) {
    _rule = rule;
    _rule.setCondition(condition);
  }

  public ThenRuleBuilder<T, U> then(Consumer<FactMap<T>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }

  public ThenRuleBuilder<T, U> then(BiConsumer<FactMap<T>, Result<U>> action) {
    return new ThenRuleBuilder<T, U>(_rule, action);
  }
}
