package com.deliveredtechnologies.rulebook.rulechain;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.Optional;

public class RuleBookRunner implements RuleBook {

  RuleBook _ruleBook;

  public RuleBookRunner(RuleBook ruleBook) {
    _ruleBook = ruleBook;
  }

  @Override
  public void addRule(Rule rule) {

  }

  @Override
  public void run(FactMap facts) {

  }

  @Override
  public void setDefaultResult(Object result) {

  }

  @Override
  public Optional<Result> getResult() {
    return null;
  }
}
