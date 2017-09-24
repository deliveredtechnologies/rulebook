package com.example.rulebook.megabank.Application;


import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.google.inject.Inject;

public class RuleBookService {
  private RuleBook _rulebook;

  @Inject
  RuleBookService(RuleBook ruleBook) {
    _rulebook = ruleBook;
    _rulebook.setDefaultResult(4.5);
  }

  @SuppressWarnings("unchecked")
  public Result run(NameValueReferableMap facts) {
    _rulebook.run(facts);
    return (Result)_rulebook.getResult().orElse(new Result());
  }
}
