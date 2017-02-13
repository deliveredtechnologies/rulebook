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

  public RuleAdapter(Object ruleObj, Fact... facts) {
    _ruleObj = ruleObj;
    this.given(facts);
    buildGiven().buildWhen().buildThen();
  }

  private RuleAdapter buildWhen() {
    Predicate predicate = Util.getWhenMethodAsPredicate(_ruleObj);
    if (Optional.ofNullable(predicate).isPresent()) {
      when(predicate);
    }
    return this;
  }

  private RuleAdapter buildGiven() {
    Util.mapGivenFactsToProperties(_ruleObj, getFactMap());
    return this;
  }

  private RuleAdapter buildThen() {
    return this;
  }
}
