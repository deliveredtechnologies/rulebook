package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.NameValueReferable;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The initial builder used to build a Rule.
 * @param <T> the type of facts used in the Rule
 * @param <U> the Result type used in the Rule
 */
public class RuleBuilder<T, U> implements TerminatingRuleBuilder<T, U> {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBuilder.class);

  private Class<? extends Rule> _ruleClass;
  private Class<T> _factType;
  private Class<U> _resultType;
  private boolean _stopOnRuleFailure = false;

  /**
   * Returns a new RuleBuilder for the specified Rule class.
   * @param ruleClass         the class of Rule to build
   * @param stopOnRuleFailure if true, stops the rule chain if RuleState is BREAK only if the rule fails
   *                          (for supported rule classes)
   * @return                  a new RuleBuilder
   */
  public static RuleBuilder<Object, Object> create(Class<? extends Rule> ruleClass, boolean stopOnRuleFailure) {
    return new RuleBuilder<>(ruleClass, stopOnRuleFailure);
  }

  /**
   * Returns a new RuleBuilder for the specified Rule class.
   * @param ruleClass the class of Rule to build
   * @return          a new RuleBuilder
   */
  public static RuleBuilder<Object, Object> create(Class<? extends Rule> ruleClass) {
    return new RuleBuilder<>(ruleClass);
  }

  /**
   * Returns a new RuleBuilder for the default Rule type.
   * @return  a new RuleBuilder
   */
  public static RuleBuilder<Object, Object> create() {
    RuleBuilder<Object, Object> rule = new RuleBuilder<>(GoldenRule.class);
    rule._factType = Object.class;
    return rule;
  }

  private RuleBuilder(Class<? extends Rule> ruleClass) {
    _ruleClass = ruleClass;
  }

  private RuleBuilder(Class<? extends Rule> ruleClass, boolean stopOnRuleFailure) {
    this(ruleClass);
    _stopOnRuleFailure = stopOnRuleFailure;
  }

  /**
   * Specifies the fact type for the Rule being built.
   * @param factType  the type of facts to be used in the Rule
   * @param <S>       the type of facts
   * @return          a builder using the new fact type for building a Rule
   */
  public <S> RuleBuilder<S, U> withFactType(Class<S> factType) {
    RuleBuilder<S, U> builder = new RuleBuilder<>(_ruleClass);
    builder._factType = factType;
    builder._resultType = _resultType;
    builder._stopOnRuleFailure = _stopOnRuleFailure;
    return builder;
  }

  /**
   * Specifies the Result type for the Rule being built.
   * @param resultType  the type of the Result to be used in the Rule
   * @param <S>         the type of Result
   * @return            a builder using a new result type for building a Rule
   */
  public <S> RuleBuilder<T, S> withResultType(Class<S> resultType) {
    RuleBuilder<T, S> builder = new RuleBuilder<>(_ruleClass);
    builder._factType = _factType;
    builder._resultType = resultType;
    builder._stopOnRuleFailure = _stopOnRuleFailure;
    return builder;
  }

  /**
   * Adds a fact to the Rule using a name value pair to specify a new fact.
   * @param name    the name of the fact
   * @param value   the value of the fact
   * @return        a builder for building rules after a 'given' statement
   */
  public GivenRuleBuilder<T, U> given(String name, T value) {
    Rule<T, U> rule = newRule();
    if (rule == null) {
      throw new IllegalStateException("No Rule is instantiated; An invalid Rule class may have been provided");
    }
    return new GivenRuleBuilder<T, U>(rule, new Fact<T>(name, value));
  }

  /**
   * Adds one or more facts to the Rule.
   * @param facts the facts to be added to the Rule
   * @return  a builder for building rules after a 'given' statement
   */
  @SafeVarargs
  public final GivenRuleBuilder<T, U> given(NameValueReferable... facts) {
    Rule<T, U> rule = newRule();
    if (rule == null) {
      throw new IllegalStateException("No Rule is instantiated; An invalid Rule class may have been provided");
    }
    return new GivenRuleBuilder<T, U>(rule, facts);
  }

  /**
   * Adds facts to a Rule.
   * @param   facts the facts to be added to the Rule
   * @return  a builder for building rules after a 'given' statement
   */
  public final GivenRuleBuilder<T, U> given(NameValueReferableMap facts) {
    Rule<T, U> rule = newRule();
    if (rule == null) {
      throw new IllegalStateException("No Rule is instantiated; An invalid Rule class may have been provided");
    }
    return new GivenRuleBuilder<T, U>(rule, facts);
  }

  /**
   * Specifies the condition for the Rule.
   * @param condition the condition for the Rule
   * @return          a builder for building rules after a 'when' statement
   */
  public WhenRuleBuilder<T, U> when(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) {
    Rule<T, U> rule = newRule();
    if (rule == null) {
      throw new IllegalStateException("No Rule is instantiated; An invalid Rule class may have been provided");
    }
    return new WhenRuleBuilder<T, U>(rule, condition);
  }

  /**
   * Adds an action as a Consumer to the Rule.
   * @param action  a Consumer action to be added to the Rule that accepts the facts specified for the Rule
   * @return        a builder for building rules after a 'then' action is specified
   */
  public ThenRuleBuilder<T, U> then(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    Rule<T, U> rule = newRule();
    if (rule == null) {
      throw new IllegalStateException("No Rule is instantiated; An invalid Rule class may have been provided");
    }
    return new ThenRuleBuilder<T, U>(rule, action);
  }

  /**
   * Adds an action as a Consumer to the Rule.
   * @param action  a Consumer action to be added to the Rule that accepts the facts and the Result specified
   *                for the Rule
   * @return        a builder for building rules after a 'then' action is specified
   */
  public ThenRuleBuilder<T, U> then(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    Rule<T, U> rule = newRule();
    if (rule == null) {
      throw new IllegalStateException("No Rule is instantiated; An invalid Rule class may have been provided");
    }
    return new ThenRuleBuilder<T, U>(rule, action);
  }

  /**
   * Builds the Rule.
   * @return  a Rule
   */
  @Override
  public Rule<T, U> build() {
    return newRule();
  }

  private Rule<T, U> newRule() {
    if (_stopOnRuleFailure) {
      try {
        Constructor<?> constructor = _ruleClass.getConstructor(Class.class, boolean.class);
        return (Rule<T, U>)constructor.newInstance(new Object[]{_factType, _stopOnRuleFailure});
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
        LOGGER.error("Unable to create an instance of the specified Rule class '"
            + _ruleClass
            + "' with the specified fact type and 'stopOnRuleFailire' boolean parameter");
        return null;
      }
    }
    try {
      Constructor<?> constructor = _ruleClass.getConstructor(Class.class, Class.class);
      return (Rule<T, U>)constructor.newInstance(new Object[]{_factType, _resultType});
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
      LOGGER.debug("Unable to create an instance of the specified Rule class '"
          + _ruleClass
          + "' with fact and result types specified");
    }
    try {
      Constructor<?> constructor = _ruleClass.getConstructor(Class.class);
      return (Rule<T, U>)constructor.newInstance(new Object[] {_factType});
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
      LOGGER.debug("Attempt to use a single argument constructor for specifying the Fact type failed", ex);
    }
    try {
      Constructor<?> constructor = _ruleClass.getConstructor(new Class[] {});
      return (Rule<T, U>)constructor.newInstance(new Object[]{});
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
      LOGGER.debug("Attempt to use the default constructor failed for " + _ruleClass, ex);
      LOGGER.error("Unable to create a Rule of type '" + _ruleClass.getName() + "'");
    }

    return null;
  }
}
