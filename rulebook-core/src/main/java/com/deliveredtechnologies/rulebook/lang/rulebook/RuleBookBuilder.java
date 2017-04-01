package com.deliveredtechnologies.rulebook.lang.rulebook;

import com.deliveredtechnologies.rulebook.lang.rule.RuleBuilder;
import com.deliveredtechnologies.rulebook.lang.rule.TerminatingRuleBuilder;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.rulechain.cor.CoRRuleBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by clong on 3/29/17.
 */
public class RuleBookBuilder<T> implements TerminatingRuleBookBuilder {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookBuilder.class);

  private RuleBook<T> _ruleBook;
  private Class<T> _resultType;

  public static <T> RuleBookBuilder<T> create(Class<T> resultType) {
    return new RuleBookBuilder<T>(new CoRRuleBook<T>(), resultType);
  }

  @SuppressWarnings("unchecked")
  public static <T> RuleBookBuilder<T> create(Class<T> resultType, Class<? extends RuleBook> ruleBookClass) {
    try {
      Method method = ruleBookClass.getMethod("create");
      return new RuleBookBuilder((RuleBook<T>)method.invoke(new Object[] {resultType}), resultType);
    } catch (IllegalAccessException | InvocationTargetException| NoSuchMethodException ex) {
      try {
        return new RuleBookBuilder<T>(ruleBookClass.newInstance(), resultType);
      } catch (InstantiationException | IllegalAccessException e) {
        LOGGER.error("Unable to create RuleBook " + ruleBookClass, e);
        return create(resultType);
      }
    }
  }

  public static RuleBookBuilder create() {
    return new RuleBookBuilder();
  }

  private RuleBookBuilder() { }

  private RuleBookBuilder(RuleBook<T> ruleBook, Class<T> resultType) {
    _resultType = resultType;
    _ruleBook = ruleBook;
  }

  public DefaultResultRuleBookBuilder<T> withDefaultResult(T result) {
    return new DefaultResultRuleBookBuilder<T>(_ruleBook, _resultType, result);
  }

  public AddRuleBookBuilder<T> addRule(TerminatingRuleBuilder rule) {
    return new AddRuleBookBuilder<T>(_ruleBook, rule);
  }

  @SuppressWarnings("unchecked")
  public <U, V extends T> AddRuleBookBuilder<V> addRule(
          Class<U> factType, Function<RuleBuilder<U, V>, TerminatingRuleBuilder<U, V>> function) {
    Class<V> resultType = (Class<V>)(_resultType == null ? Object.class : _resultType);
    return new AddRuleBookBuilder<V>(
            (RuleBook<V>)_ruleBook,
            resultType,
            function.apply(RuleBuilder.create(factType, resultType)));
  }

  @Override
  public RuleBook build() {
    return _ruleBook;
  }
}
