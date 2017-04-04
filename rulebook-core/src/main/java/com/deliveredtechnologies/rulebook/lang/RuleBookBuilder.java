package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by clong on 3/29/17.
 */
public class RuleBookBuilder<T> implements TerminatingRuleBookBuilder {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookBuilder.class);

  private RuleBook<T> _ruleBook;
  private Class<T> _resultType;

  public static <T> RuleBookBuilder<T> create(Class<T> resultType) {
    return new RuleBookBuilder<T>(resultType);
  }

  @SuppressWarnings("unchecked")
  public static <T> RuleBookBuilder<T> create(Class<T> resultType, Class<? extends RuleBook> ruleBookClass) {
    try {
      Method method = ruleBookClass.getMethod("create", Class.class);
      return new RuleBookBuilder((RuleBook<T>)method.invoke(ruleBookClass, new Object[] {resultType}), resultType);
    } catch (IllegalAccessException | InvocationTargetException| NoSuchMethodException ex) {
      try {
        return new RuleBookBuilder<T>(ruleBookClass.newInstance(), resultType);
      } catch (InstantiationException | IllegalAccessException e) {
        LOGGER.error("Unable to create RuleBook " + ruleBookClass, e);
        return create(resultType);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static RuleBookBuilder create() {
    return new RuleBookBuilder(Object.class);
  }

  private RuleBookBuilder(Class<T> resultType) {
    this(new CoRRuleBook<T>(), resultType);
  }

  private RuleBookBuilder(RuleBook<T> ruleBook, Class<T> resultType) {
    _resultType = resultType;
    _ruleBook = ruleBook;
  }

  public RuleBookBuilder<T> withDefaultResult(T result) {
    _ruleBook.setDefaultResult(result);
    return this;
  }

  public TerminatingRuleBookBuilder<T> addRule(TerminatingRuleBuilder rule) {
    _ruleBook.addRule(rule.build());
    return () -> _ruleBook;
  }

  public AddRuleBookRuleBuilder<T> addRule() {
    return new AddRuleBookRuleBuilder<T>(_ruleBook);
  }

  @Override
  public RuleBook build() {
    return _ruleBook;
  }
}
