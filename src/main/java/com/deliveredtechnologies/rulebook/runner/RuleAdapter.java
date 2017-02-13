package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.StandardDecision;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.Rule;
import com.deliveredtechnologies.rulebook.RuleState;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by clong on 2/12/17.
 */
public class RuleAdapter extends StandardDecision {
  private Object _ruleObj;

  public RuleAdapter(Object ruleObj) {
    _ruleObj = ruleObj;
  }

  private RuleAdapter buildWhen() {
    Predicate predicate = Util.getWhenMethodAsPredicate(_ruleObj);
    if (Optional.ofNullable(predicate).isPresent()) {
      when(predicate);
    }
    return this;
  }

  @Override
  public void run() {
    super.run();
  }

  @Override
  public StandardDecision then(BiFunction action) {
    return null;
  }

  @Override
  public StandardDecision given(Fact[] facts) {
    return null;
  }

  @Override
  public Object getResult() {
    return null;
  }

  @Override
  public StandardDecision given(List list) {
    return null;
  }

  @Override
  public void setResult(Result result) {

  }

  @Override
  public StandardDecision given(FactMap facts) {
    return null;
  }


  @Override
  public StandardDecision then(Function action) {
    return null;
  }

  @Override
  public void setNextRule(Rule rule) {
    super.setNextRule(rule);
  }
}
