package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.*;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A sample Rule with a default no-arg constructor.
 */
public class SampleRuleDefaultConstructor<T, U> implements Rule<T, U> {
  @Override
  public void addFacts(NameValueReferable... fact) {

  }

  @Override
  public void addFacts(NameValueReferableMap facts) {

  }

  @Override
  public void setFacts(NameValueReferableMap facts) {

  }

  @Override
  public void setCondition(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) throws IllegalStateException {

  }

  @Override
  public void setRuleState(RuleState ruleState) {

  }

  @Override
  public void addAction(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {

  }

  @Override
  public void addAction(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {

  }

  @Override
  public void addFactNameFilter(String... factNames) {

  }

  @Override
  public NameValueReferableMap getFacts() {
    return null;
  }

  @Override
  public Predicate<NameValueReferableTypeConvertibleMap<T>> getCondition() {
    return null;
  }

  @Override
  public RuleState getRuleState() {
    return null;
  }

  @Override
  public List<Object> getActions() {
    return null;
  }

  @Override
  public boolean invoke() {
    return false;
  }

  @Override
  public void setResult(Result<U> result) {

  }

  @Override
  public Optional<Result<U>> getResult() {
    return null;
  }
}
