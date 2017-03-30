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
import java.util.function.Function;

/**
 * Created by clong on 3/29/17.
 */
public class RuleBookBuilder<T> {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookBuilder.class);

  RuleBook<T> _ruleBook;

  public static <T> RuleBookBuilder<T> create(Class<T> factType) {
    return new RuleBookBuilder<T>(new CoRRuleBook<T>());
  }

  public static <T> RuleBookBuilder<T> create(Class<T> factType, Class<? extends RuleBook> ruleBookClass) {

    try {
      Method method = ruleBookClass.getMethod("create");
      return new RuleBookBuilder((RuleBook<T>)method.invoke(new Object[] {factType}));
    } catch (IllegalAccessException | InvocationTargetException| NoSuchMethodException ex) {
      try {
        return new RuleBookBuilder<T>(ruleBookClass.newInstance());
      } catch (InstantiationException | IllegalAccessException e) {
        LOGGER.error("Unable to create RuleBook " + ruleBookClass, e);
        return create(factType);
      }
    }
  }

  public static RuleBuilder create() {
    return new RuleBuilder();
  }

  public RuleBookBuilder(RuleBook<T> ruleBook) {
    _ruleBook = ruleBook;
  }

  public DefaultResultRuleBookBuilder<T> withDefaultResult(T result) {
    return new DefaultResultRuleBookBuilder<T>(_ruleBook, result);
  }

  @SuppressWarnings("unchecked")
  public AddRuleBookBuilder<T, Object> addRule(Class<T> factType, Function<RuleBuilder<T, Object>, TerminatingRuleBuilder> ruleFunction) {
    return new AddRuleBookBuilder<T, Object>((RuleBook<Object>)_ruleBook, factType, Object.class, ruleFunction);
  }

  @SuppressWarnings("unchecked")
  public <U, V extends T> AddRuleBookBuilder<U, V> addRule(Class<U> factType, Class<V> resultType, Function<RuleBuilder<U, V>, TerminatingRuleBuilder> ruleFunction) {
    return new AddRuleBookBuilder<U, V>((RuleBook<V>)_ruleBook, factType, resultType, ruleFunction);
  }

  public <U, V extends T> AddRuleBookBuilder<U, V> addRule(Rule<U, V> rule) {
    return new AddRuleBookBuilder<U, V>((RuleBook<V>)_ruleBook, rule);
  }
}
