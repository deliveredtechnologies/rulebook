package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;

import java.util.Optional;

/**
 * A RuleBook implementation that chains Rules together using the Chain of Responsibility (CoR) pattern.
 * @param <T> the Result type
 */
public class CoRRuleBook<T> implements RuleBook<T> {
  private Handler<Rule> _headRule = null;
  private Handler<Rule> _tailRule = null;
  private Result<T> _result = null;

  @Override
  public void addRule(Rule rule) {
    if (rule == null) {
      return;
    }

    Handler<Rule> ruleHandler = new RuleHandler(rule);
    if (_headRule == null) {
      getResult().ifPresent(result -> rule.setResult(result));
      _headRule = ruleHandler; // this rule is the head if there was no head
      _tailRule = ruleHandler;
    } else {
      _tailRule = _tailRule.setSuccessor(ruleHandler);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void run(NameValueReferableMap facts) {
    getResult().ifPresent(Result::reset);
    if (!hasRules()) {
      defineRules();
    }

    Optional<Handler<Rule>> headRule = Optional.ofNullable(_headRule);
    headRule.ifPresent(ruleHandler -> {
      getResult().ifPresent(result -> ruleHandler.getDelegate().setResult(result));
      ruleHandler.handleRequest(facts);
    });
  }

  @Override
  public void setDefaultResult(T result) {
    _result = new Result<T>(result);
  }

  @Override
  public Optional<Result<T>> getResult() {
    return Optional.ofNullable(_result);
  }

  @Override
  public boolean hasRules() {
    return _headRule != null;
  }
}
