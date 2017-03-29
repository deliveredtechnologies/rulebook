package com.deliveredtechnologies.rulebook.rulechain;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.model.Rule;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by clong on 3/28/17.
 */
public class RuleAdapter implements Rule {

  private Rule _rule;
  private Object _pojo;

  @Override
  public void addFact(Fact fact) {

  }

  @Override
  public void addFacts(List list) {

  }

  @Override
  public void addFacts(FactMap facts) {

  }

  @Override
  public void setFacts(FactMap facts) {

  }

  @Override
  public void setCondition(Predicate condition) throws IllegalStateException {

  }

  @Override
  public void setRuleState(RuleState ruleState) {

  }

  @Override
  public void addAction(Consumer action) {

  }

  @Override
  public void addAction(BiConsumer action) {

  }

  @Override
  public void addFactNameFilter(String... factNames) {

  }

  @Override
  public FactMap getFacts() {
    return null;
  }

  @Override
  public Predicate<FactMap> getCondition() {
    return null;
  }

  @Override
  public RuleState getRuleState() {
    return null;
  }

  @Override
  public List<Object> getActions() {
    return null;
  }

  @Override
  public boolean invokeAction() {
    return false;
  }

  @Override
  public void setResult(Result result) {

  }

  @Override
  public Optional<Result> getResult() {
    return null;
  }
}
