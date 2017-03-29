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
  void addFact(Fact fact);
  void addFacts(List<Fact> facts);
  void addFacts(FactMap facts);
  void setFacts(FactMap facts);
  void setCondition(Predicate<FactMap<T>> condition) throws IllegalStateException;
  void setRuleState(RuleState ruleState);
  void addAction(Consumer<FactMap<T>> action);
  void addAction(BiConsumer<FactMap<T>, Result<U>> action);
  void addFactNameFilter(String... factNames);
  FactMap getFacts();
  Predicate<FactMap<T>> getCondition();
  RuleState getRuleState();
  List<Object> getActions();
  boolean invokeAction();
  void setResult(Result<U> result);
  Optional<Result<U>> getResult();
}
