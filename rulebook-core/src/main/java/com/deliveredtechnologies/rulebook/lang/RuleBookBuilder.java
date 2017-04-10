package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * The initial builder used to build a RuleBook.
 */
public class RuleBookBuilder<T> implements TerminatingRuleBookBuilder<T> {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookBuilder.class);

  private RuleBook<T> _ruleBook;
  private Class<? extends RuleBook> _ruleBookClass;
  private Class<?> _resultType = Object.class;

  /**
   * Returns a new RuleBookBuilder for the default RuleBook type.
   * @return  a new RuleBookBuilder
   */
  public static RuleBookBuilder<Object> create() {
    return new RuleBookBuilder<Object>(CoRRuleBook.class);
  }

  /**
   * Returns a new RuleBookBuilder using the supplied RuleBook class.
   * @param ruleBookClass a RuleBook class to be used in the builder
   * @return              a new RuleBookBuilder
   */
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
    if (_ruleBook == null) {
      try {
        _ruleBook = _ruleBookClass.newInstance();
      } catch (IllegalAccessException | InstantiationException e) {
        try {
          Constructor<?> constructor = _ruleBookClass.getConstructor(Class.class);
          _ruleBook = (RuleBook<T>) constructor.newInstance(_resultType);
        } catch (InvocationTargetException
                | NoSuchMethodException
                | InstantiationException
                | IllegalAccessException ex) {
          throw new IllegalStateException("RuleBook of class " + _ruleBookClass + " can not be instantiated", ex);
        }
      }
    }
  }

  /**
   * Specifies the Result type for the RuleBook.
   * @param resultType  result class
   * @param <U>         type of the result class
   * @return            a builder with the new Result type
   */
  public <U> RuleBookBuilder<U> withResultType(Class<U> resultType) {
    _resultType = resultType;
    return new RuleBookBuilder<>(this);
  }

  /**
   * Specifies the default Result value.
   * Note: RuleBooks that return a single Result must have a default Result value set.
   * @param result  the default value of the Result
   * @return        a builder with the default Result value
   */
  public RuleBookBuilder<T> withDefaultResult(T result) {
    newRuleBook();
    _ruleBook.setDefaultResult(result);
    return this;
  }

  /**
   * Adds a rule to the RuleBook.
   * @param consumer  functional interface that supplies a RuleBookRuleBuilder for building a Rule
   * @return          a builder with the added Rule
   */
  public RuleBookBuilder<T> addRule(Consumer<RuleBookRuleBuilder<T>> consumer) {
    newRuleBook();
    consumer.accept(new RuleBookRuleBuilder<>(_ruleBook));
    return this;
  }

  /**
   * Adds a rule to the RuleBook.
   * @param rule  Rule to be added into the RuleBook
   * @param <U>   the fact type of the Rule
   * @return      RuleBookBuilder with the added Rule
   */
  public <U> RuleBookBuilder<T> addRule(Rule<U, T> rule) {
    newRuleBook();
    _ruleBook.addRule(rule);
    return this;
  }

  /**
   * Builds the RuleBook.
   * @return  a RuleBook
   */
  @Override
  public RuleBook<T> build() {
    newRuleBook();
    return _ruleBook;
  }
}
