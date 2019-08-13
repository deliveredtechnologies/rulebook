package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleStatus;
import com.deliveredtechnologies.rulebook.model.runner.RuleAdapter;

import java.io.InvalidClassException;
import java.util.Map;
import java.util.Optional;

/**
 * RuleBook decorator for Spring that allows Rules to be created from either Spring annotated POJO Rules or
 * by building Rules.
 */
@Deprecated
public class SpringRuleBook<T> implements RuleBook<T> {

  private RuleBook<T> _ruleBook;

  public SpringRuleBook(RuleBook<T> ruleBook) {
    _ruleBook = ruleBook;
  }

  @Override
  public void addRule(Rule rule) {
    _ruleBook.addRule(rule);
  }

  public void addRule(Object rule) throws InvalidClassException {
    _ruleBook.addRule(new RuleAdapter(rule));
  }

  @Override
  public void run(NameValueReferableMap facts) {
    _ruleBook.run(facts);
  }

  @Override
  public void setDefaultResult(T result) {
    _ruleBook.setDefaultResult(result);
  }

  @Override
  public Optional<Result<T>> getResult() {
    return _ruleBook.getResult();
  }

  @Override
  public boolean hasRules() {
    return _ruleBook.hasRules();
  }

}
