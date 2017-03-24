package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by clong on 3/24/17.
 */
public interface Rule<T, U> {
  void addFact(Fact<T> fact);
  void addFacts(List<Fact<T>> facts);
  void addFacts(FactMap<T> facts);
  void setCondition(Predicate<FactMap<T>> condition) throws IllegalStateException;
  void setRuleState(RuleState ruleState);
  void addAction(Consumer<FactMap<T>> condition);
  void addAction(BiConsumer<FactMap<T>, Result<U>> condition);
  void addFactNameFilter(String... factNames);
  FactMap<T> getFacts();
  Predicate<T> getCondition();
  RuleState getRuleState();
  List<Object> getActions();
  U invokeAction();
  U invokeAction(Object... args);
  void setResult(U obj);
  Optional<U> getResult();
}
