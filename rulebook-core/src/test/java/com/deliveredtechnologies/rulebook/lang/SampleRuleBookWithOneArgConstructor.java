package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A sample RuleBook with a single argument constructor.
 */
public class SampleRuleBookWithOneArgConstructor<T> implements RuleBook<T> {

  public SampleRuleBookWithOneArgConstructor(Class<T> resultType) {

  }

  @Override
  public void addRule(Rule rule) {

  }

  @Override
  public void run(NameValueReferableMap facts) {

  }

  @Override
  public void setDefaultResult(T result) {

  }

  @Override
  public void setDefaultResult(Supplier<T> result) {

  }

  @Override
  public Optional<Result<T>> getResult() {
    return null;
  }

  @Override
  public boolean hasRules() {
    return false;
  }
}
