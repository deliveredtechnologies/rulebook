package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.*;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by clong on 9/3/17.
 */
public class AuditableRule<T, U> implements Rule<T, U>, Auditable {
  private Rule<T, U> _rule;
  private String _name = getClass().getSimpleName();
  private Auditor _auditor;

  public AuditableRule(Rule<T, U> rule) {
    _rule = rule;
  }

  public AuditableRule(Rule<T, U> rule, String name) {
    this(rule);
    _name = name;
  }

  @Override
  public void addFacts(NameValueReferable... facts) {
    _rule.addFacts();
  }

  @Override
  public void addFacts(NameValueReferableMap facts) {
    _rule.addFacts(facts);
  }

  @Override
  public void setFacts(NameValueReferableMap facts) {
    _rule.addFacts(facts);
  }

  @Override
  public void setCondition(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) {
    _rule.setCondition(condition);
  }

  @Override
  public void setRuleState(RuleState ruleState) {
    _rule.setRuleState(ruleState);
  }

  @Override
  public void addAction(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    _rule.addAction(action);
  }

  @Override
  public void addAction(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    _rule.addAction(action);
  }

  @Override
  public void addFactNameFilter(String... factNames) {
    _rule.addFactNameFilter(factNames);
  }

  @Override
  public NameValueReferableMap getFacts() {
    return _rule.getFacts();
  }

  @Override
  public Predicate<NameValueReferableTypeConvertibleMap<T>> getCondition() {
    return _rule.getCondition();
  }

  @Override
  public RuleState getRuleState() {
    return _rule.getRuleState();
  }

  @Override
  public List<Object> getActions() {
    return _rule.getActions();
  }

  @Override
  public boolean invoke(NameValueReferableMap facts) {
    boolean isPassing = _rule.invoke(facts);
    _auditor.updateRuleStatus(this, isPassing ? RuleStatus.EXECUTED : RuleStatus.SKIPPED);
    return isPassing;
  }

  @Override
  public void setResult(Result<U> result) {
    _rule.setResult(result);
  }

  @Override
  public Optional<Result<U>> getResult() {
    return _rule.getResult();
  }

  @Override
  public void setAuditor(Auditor auditor) {
    _auditor = auditor;
    _auditor.registerRule(this);
  }

  @Override
  public String getName() {
    return _name;
  }
}
