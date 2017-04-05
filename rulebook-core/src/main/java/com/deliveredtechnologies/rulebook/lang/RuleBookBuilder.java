package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by clong on 3/29/17.
 */
public class RuleBookBuilder<T> implements TerminatingRuleBookBuilder<T> {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookBuilder.class);

  RuleBook<T> _ruleBook;
  Class<? extends RuleBook> _ruleBookClass;
  Class<?> _resultType = Object.class;

  public static RuleBookBuilder<Object> create() {
    return new RuleBookBuilder<Object>(CoRRuleBook.class);
  }

  public static RuleBookBuilder<Object> create(Class<? extends RuleBook> ruleBookClass) {
    return new RuleBookBuilder<Object>(ruleBookClass);
  }

  private RuleBookBuilder(Class<? extends RuleBook> ruleBookClass) {
    _ruleBookClass = ruleBookClass;
  }

  private RuleBookBuilder(RuleBookBuilder ruleBookBuilder) {
    _resultType = ruleBookBuilder._resultType;
    _ruleBookClass = ruleBookBuilder._ruleBookClass;
    newRuleBook();
  }

  private void newRuleBook() {
    if (_ruleBookClass != null && _ruleBook == null) {
      try {
        Method method = _ruleBookClass.getMethod("create", Class.class);
        if (method.getParameterCount() == 0) {
          _ruleBook = (RuleBook<T>) method.invoke(_ruleBookClass, new Object[]{});
        } else {
          _ruleBook = (RuleBook<T>) method.invoke(_ruleBookClass, new Object[]{_resultType});
        }
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
        try {
          _ruleBook = _ruleBookClass.newInstance();
        } catch (IllegalAccessException | InstantiationException ex) {
          try {
            Constructor<?> constructor = _ruleBookClass.getConstructor(Class.class);
            _ruleBook = (RuleBook<T>) constructor.newInstance(_resultType);

          } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            LOGGER.error("Unable to create RuleBook '" + _ruleBookClass + "'", e);
          }
        }
      }
    }
  }

  public <U> RuleBookBuilder<U> withResultType(Class<U> resultType) {
    _resultType = resultType;
    return new RuleBookBuilder<>(this);
  }

  public RuleBookBuilder<T> withDefaultResult(T result) {
    if (_resultType == null) {
      throw new IllegalStateException("No result type has been specified!");
    }
    _ruleBook.setDefaultResult(result);
    return this;
  }

  public RuleBookRuleBuilder<T> addRule() {
    newRuleBook();
    return new RuleBookRuleBuilder<T>(_ruleBook);
  }

  public <U> void addRule(TerminatingRuleBuilder<U, T> rule) {
    newRuleBook();
    _ruleBook.addRule(rule.build());
  }

  @Override
  public RuleBook<T> build() {
    if (_ruleBook == null) {
      throw new IllegalStateException("RuleBookBuilder has completed building a RuleBook!");
    }
    return _ruleBook;
  }
}
