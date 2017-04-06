package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by clong on 3/29/17.
 */
public class RuleBookBuilder<T> implements TerminatingRuleBookBuilder<T> {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookBuilder.class);

  private RuleBook<T> _ruleBook;
  private Class<? extends RuleBook> _ruleBookClass;
  private Class<?> _resultType = Object.class;

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
    if (_ruleBook == null || !_ruleBookClass.isInstance(_ruleBook)) {
      try {
        Method method = _ruleBookClass.getMethod("create", Class.class);
        _ruleBook = (RuleBook<T>) method.invoke(_ruleBookClass, new Object[]{_resultType});
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        try {
          Method method = _ruleBookClass.getMethod("create", new Class[] {});
          _ruleBook = (RuleBook<T>) method.invoke(_ruleBookClass, new Object[]{});
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
          try {
            _ruleBook = _ruleBookClass.newInstance();
          } catch (IllegalAccessException | InstantiationException exc) {
            try {
              Constructor<?> constructor = _ruleBookClass.getConstructor(Class.class);
              _ruleBook = (RuleBook<T>) constructor.newInstance(_resultType);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException exce) {
              LOGGER.error("Unable to create RuleBook '" + _ruleBookClass + "'", exce);
            }
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
    newRuleBook();
    _ruleBook.setDefaultResult(result);
    return this;
  }

  public RuleBookBuilder<T> addRule(Consumer<RuleBookRuleBuilder<T>> consumer) {
    newRuleBook();
    consumer.accept(new RuleBookRuleBuilder<>(_ruleBook));
    return this;
  }

  public <U> RuleBookBuilder<T> addRule(TerminatingRuleBuilder<U, T> rule) {
    newRuleBook();
    _ruleBook.addRule(rule.build());
    return this;
  }

  public <U> RuleBookBuilder<T> addRule(Rule<U, T> rule) {
    newRuleBook();
    _ruleBook.addRule(rule);
    return this;
  }

  @Override
  public RuleBook<T> build() {
    newRuleBook();
    return _ruleBook;
  }

}
