package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.Optional;

/**
 * A very basic RuleBook shell implementation.
 */
public class GoodRuleBook implements RuleBook {
  @Override
  public void addRule(Rule rule) {

  }

  @Override
  public void run(NameValueReferableMap facts) {

  }

  @Override
  public void setDefaultResult(Object result) {

  }

  @Override
  public Optional<Result> getResult() {
    return null;
  }

  @Override
  public boolean hasRules() {
    return false;
  }
}
