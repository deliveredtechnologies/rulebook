package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.*;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RuleBuilder<T, U> implements TerminatingRuleBuilder<T, U> {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBuilder.class);

  private Class<? extends Rule> _ruleClass;
  private Class<T> _factType;
  private Class<U> _resultType;

  public static RuleBuilder<Object, Object> create(Class<? extends Rule> ruleClass) {
    return new RuleBuilder<Object, Object>(ruleClass);
  }

  public static RuleBuilder<Object, Object> create() {
    RuleBuilder<Object, Object> rule = new RuleBuilder<>(GoldenRule.class);
    rule.setFactType(Object.class);
    return rule;
  }

  private RuleBuilder(Class<? extends Rule> ruleClass) {
    _ruleClass = ruleClass;
  }

  public <S> RuleBuilder<S, U> withFactType(Class<S> factType) {
    RuleBuilder<S, U> builder = new RuleBuilder<>(_ruleClass);
    builder.setFactType(factType);
    builder.setResultType(_resultType);
    return builder;
  }

  public <S> RuleBuilder<T, S> withResultType(Class<S> resultType) {
    RuleBuilder<T, S> builder = new RuleBuilder<>(_ruleClass);
    builder.setFactType(_factType);
    builder.setResultType(resultType);
    return builder;
  }

  public GivenRuleBuilder<T, U> given(String name, T value) {
    Rule<T, U> rule = newRule();
    return rule != null ? new GivenRuleBuilder<T, U>(rule, new Fact<T>(name, value)) : null;
  }

  @SafeVarargs
  public final GivenRuleBuilder<T, U> given(NameValueReferable... facts) {
    Rule<T, U> rule = newRule();
    return rule != null ? new GivenRuleBuilder<T, U>(rule, facts) : null;
  }

  public final GivenRuleBuilder<T, U> given(NameValueReferableMap facts) {
    Rule<T, U> rule = newRule();
    return rule != null ? new GivenRuleBuilder<T, U>(rule, facts) : null;
  }

  public WhenRuleBuilder<T, U> when(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) {
    Rule<T, U> rule = newRule();
    return rule != null ? new WhenRuleBuilder<T, U>(rule, condition) : null;
  }

  public ThenRuleBuilder<T, U> then(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    Rule<T, U> rule = newRule();
    return rule != null ? new ThenRuleBuilder<T, U>(rule, action) : null;
  }

  public ThenRuleBuilder<T, U> then(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    Rule<T, U> rule = newRule();
    return rule != null ? new ThenRuleBuilder<T, U>(rule, action) : null;
  }

  @Override
  public Rule<T, U> build() {
    return newRule();
  }

  private Rule<T, U> newRule() {
    try {
      Method method = _ruleClass.getMethod("create", Class.class);
      switch (method.getParameterCount()) {
        case 0:
          return (Rule<T, U>) method.invoke(_ruleClass, new Object[] {});
        case 1:
          return (Rule<T, U>) method.invoke(_ruleClass, new Object[] {_factType});
        case 2:
          return (Rule<T, U>) method.invoke(_ruleClass, new Object[] {_factType, _resultType});
      }
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      try {
        Constructor<?> constructor = _ruleClass.getConstructor(new Class[] {});
        return (Rule<T, U>)constructor.newInstance(new Object[]{});
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
        //intentionally left blank
      }
      try {
        Constructor<?> constructor = _ruleClass.getConstructor(Class.class);
        return (Rule<T, U>)constructor.newInstance(new Object[] {_factType});
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
        //intentionally left blank
      }
      try {
        Constructor<?> constructor = _ruleClass.getConstructor(Class.class, Class.class);
        return (Rule<T, U>)constructor.newInstance(new Object[]{_factType, _resultType});
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
        LOGGER.error("Unable to create an instance of the specified Rule class '" + _ruleClass + "'");
      }
    }
    return null;
  }

  private void setFactType(Class<T> _factType) {
    this._factType = _factType;
  }

  private void setResultType(Class<U> _resultType) {
    this._resultType = _resultType;
  }
}
