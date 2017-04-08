package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.*;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by clong on 3/24/17.
 */
public interface Rule<T, U> {
  void addFacts(NameValueReferable... fact);
  void addFacts(NameValueReferableMap facts);
  void setFacts(NameValueReferableMap facts);
  void setCondition(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) throws IllegalStateException;
  void setRuleState(RuleState ruleState);
  void addAction(Consumer<NameValueReferableTypeConvertibleMap<T>> action);
  void addAction(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action);
  void addFactNameFilter(String... factNames);
  NameValueReferableMap getFacts();
  Predicate<NameValueReferableTypeConvertibleMap<T>> getCondition();
  RuleState getRuleState();
  List<Object> getActions();
  boolean invoke();
  void setResult(Result<U> result);
  Optional<Result<U>> getResult();
}
