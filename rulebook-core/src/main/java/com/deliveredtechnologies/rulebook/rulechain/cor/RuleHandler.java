package com.deliveredtechnologies.rulebook.rulechain.cor;

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
  public void handleRequest() {
    boolean actionResult = _rule.invokeAction();
    if (!actionResult || _rule.getRuleState() == RuleState.NEXT) {
      getSuccessor().ifPresent(handler -> handler.getDelegate().addFacts(_rule.getFacts()));
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
  public Rule setSuccessor(Rule successor) {
    if (successor != null) {
      _successor = new RuleHandler(successor);
    }
    return successor;
  }
}
