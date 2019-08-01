package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleStatus;

import java.util.Map;
import java.util.Optional;

/**
 * A sample RuleBook with a private constructor.
 */
public class SampleRuleBookWithPrivateConstructor<T> implements RuleBook<T> {

  private SampleRuleBookWithPrivateConstructor() { }

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
  public Optional<Result<T>> getResult() {
    return null;
  }

  @Override
  public boolean hasRules() {
    return false;
  }

  @Override
  public Map<String, Map<Long, RuleStatus>> getAudit() {
    return null;
  }

}
