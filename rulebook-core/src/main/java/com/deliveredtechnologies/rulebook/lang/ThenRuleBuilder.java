package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by clong on 3/24/17.
 */
public class ThenRuleBuilder<T, U> implements TerminatingRuleBuilder<T, U> {
  Rule<T, U> _rule;

  ThenRuleBuilder(Rule<T, U> rule, Consumer<FactMap<T>> action) {
    _rule = rule;
    _rule.addAction(action);
  }

  ThenRuleBuilder(Rule<T, U> rule, BiConsumer<FactMap<T>, Result<U>> action) {
    _rule = rule;
    _rule.addAction(action);
  }

  public UsingRuleBuilder<T, U> using(String... factNames) {
    return new UsingRuleBuilder<T, U>(_rule, factNames);
  }

  public ThenRuleBuilder<T, U> then(Consumer<FactMap<T>> action) {
    _rule.addAction(action);
    return this;
  }

  public ThenRuleBuilder<T, U> then(BiConsumer<FactMap<T>, Result<U>> action) {
    _rule.addAction(action);
    return this;
  }

  public TerminatingRuleBuilder<T, U> stop() {
    _rule.setRuleState(RuleState.BREAK);
    return this;
  }

  public Rule<T, U> build() {
    return _rule;
  }

}
