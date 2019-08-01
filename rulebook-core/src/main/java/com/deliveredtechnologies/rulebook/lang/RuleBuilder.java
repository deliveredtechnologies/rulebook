package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.NameValueReferable;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.model.AuditableRule;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleChainActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.CONTINUE_ON_FAILURE;
import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.STOP_ON_FAILURE;

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
  private RuleChainActionType _actionType = CONTINUE_ON_FAILURE;
  private Optional<String> _name = Optional.empty();

  /**
   * Returns a new RuleBuilder for the specified Rule class.
   * @param ruleClass   the class of Rule to build
   * @param actionType  if STOP_ON_FAILURE, stops the rule chain if RuleState is BREAK only if the rule fails
   *                    (for supported rule classes); if ERROR_ON_FAILURE, allows exceptions to be thrown from rule;
   *                    default is CONTINUE_ON_FAILURE
   * @return            a new RuleBuilder
   */
  public static RuleBuilder<Object, Object> create(Class<? extends Rule> ruleClass,
                                                   RuleChainActionType actionType) {
    return new RuleBuilder<>(ruleClass, actionType);
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

  private RuleBuilder(Class<? extends Rule> ruleClass, RuleChainActionType actionType) {
    this(ruleClass);
    _actionType = actionType;
  }

  public RuleBuilder<T, U> withName(String name) {
    _name = Optional.of(name);
    return this;
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
    builder._actionType = _actionType;
    builder._name = _name;
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
    builder._actionType = _actionType;
    builder._name = _name;
    return builder;
  }

  /**
   * Adds a fact to the Rule using a name value pair to specify a new fact.
   * @param name    the name of the fact
   * @param value   the value of the fact
   * @return        a builder for building rules after a 'given' statement
   */
  @SuppressWarnings("unchecked")
  public GivenRuleBuilder<T, U> given(String name, T value) {
    Rule<T, U> rule = _name.map(ruleName -> (Rule<T, U>)new AuditableRule<T, U>(newRule(), ruleName)).orElse(newRule());
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
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public final GivenRuleBuilder<T, U> given(NameValueReferable... facts) {
    Rule<T, U> rule = _name.map(name -> (Rule<T, U>)new AuditableRule<T, U>(newRule(), name)).orElse(newRule());
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
  @SuppressWarnings("unchecked")
  public final GivenRuleBuilder<T, U> given(NameValueReferableMap facts) {
    Rule<T, U> rule = _name.map(name -> (Rule<T, U>)new AuditableRule<T, U>(newRule(), name)).orElse(newRule());
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
  @SuppressWarnings("unchecked")
  public WhenRuleBuilder<T, U> when(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) {
    Rule<T, U> rule = _name.map(name -> (Rule<T, U>)new AuditableRule<T, U>(newRule(), name)).orElse(newRule());
    if (rule == null) {
      throw new IllegalStateException("No Rule is instantiated; An invalid Rule class may have been provided");
    }
    return new WhenRuleBuilder<T, U>(rule, condition);
  }

  /**
   * Adds a using constraint in the Rule that restricts the facts supplied to the subsequent 'then' action.
   * @param factNames the fact names to be supplied to the subsequent 'then' action
   * @return          a builder the allows for the Rule to be built following the 'using' statement
   */
  @SuppressWarnings("unchecked")
  public UsingRuleBuilder<T, U> using(String... factNames) {
    Rule<T, U> rule = _name.map(name -> (Rule<T, U>)new AuditableRule<T, U>(newRule(), name)).orElse(newRule());
    if (rule == null) {
      throw new IllegalStateException("No Rule is instantiated; An invalid Rule class may have been provided");
    }
    return new UsingRuleBuilder<T, U>(rule, factNames);
  }

  /**
   * Adds an action as a Consumer to the Rule.
   * @param action  a Consumer action to be added to the Rule that accepts the facts specified for the Rule
   * @return        a builder for building rules after a 'then' action is specified
   */
  @SuppressWarnings("unchecked")
  public ThenRuleBuilder<T, U> then(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    Rule<T, U> rule = _name.map(name -> (Rule<T, U>)new AuditableRule<T, U>(newRule(), name)).orElse(newRule());
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
  @SuppressWarnings("unchecked")
  public ThenRuleBuilder<T, U> then(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    Rule<T, U> rule = _name.map(name -> (Rule<T, U>)new AuditableRule<T, U>(newRule(), name)).orElse(newRule());
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
    if (_actionType.equals(STOP_ON_FAILURE)) {
      try {
        Constructor<?> constructor = _ruleClass.getConstructor(Class.class, RuleChainActionType.class);
        return (Rule<T, U>)constructor.newInstance(new Object[]{_factType, _actionType});
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
