package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.Optional;

/**
 * A Handler for Rule objects.
 */
public class RuleHandler implements Handler<Rule> {

  private Rule _rule;
  private Handler<Rule> _successor;

  public RuleHandler(Rule rule) {
    _rule = rule;
  }

  /**
   * Invokes the current Rule's action and then moves down the chain to the successor
   * if the RuleState of the current Rule is next or the action(s) was not executed.
   * @param obj facts to be applied to the request (rule invocation)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void handleRequest(Object obj) {
    boolean actionResult = _rule.invoke((NameValueReferableMap)obj);
    if (!actionResult || _rule.getRuleState() == RuleState.NEXT) {
      getSuccessor().ifPresent(handler -> {
          _rule.getResult().ifPresent(result -> handler.getDelegate().setResult((Result)result));
          handler.handleRequest(obj);
        });
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
