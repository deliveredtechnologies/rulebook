package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.Optional;


public class RuleHandler implements Handler<Rule> {

  private Rule _rule;
  private Handler<Rule> _successor;

  public RuleHandler(Rule rule) {
    _rule = rule;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void handleRequest() {
    boolean actionResult = _rule.invoke();
    if (!actionResult || _rule.getRuleState() == RuleState.NEXT) {
      getSuccessor().ifPresent(handler -> {
        handler.getDelegate().setFacts(_rule.getFacts());
        _rule.getResult().ifPresent(result -> handler.getDelegate().setResult((Result)result));
      });
      getSuccessor().ifPresent(Handler::handleRequest);
    }
  }

  @Override
  public Rule getDelegate() {
    return _rule;
  }

  @Override
  public Optional<Handler<Rule>> getSuccessor() {
    return Optional.ofNullable(_successor);
  }

  @Override
  public Handler<Rule> setSuccessor(Handler<Rule> successor) {
    if (successor != null) {
      _successor = successor;
    }
    return successor;
  }
}
