package com.deliveredtechnologies.rulebook;

import com.deliveredtechnologies.rulebook.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.deliveredtechnologies.rulebook.RuleState.BREAK;
import static com.deliveredtechnologies.rulebook.RuleState.NEXT;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * StandardRule is a standard rule implementation that can be used with a {@link RuleBook}.
 */
@Deprecated
public class StandardRule<T> implements Rule<T> {
  private static Logger LOGGER = LoggerFactory.getLogger(StandardRule.class);

  private Class<T> _factType;
  private Optional<Rule<T>> _nextRule = Optional.empty();
  private FactMap _facts = new FactMap();
  private Predicate<FactMap<T>> _test;
  private List<Object> _actionChain = new ArrayList<>();
  private Map<Integer, List<String>> _factNameMap = new HashMap<>();
  private RuleState _ruleState = NEXT;

  /**
   * This create() method is a convenience method to avoid using new and generic syntax.
   *
   * @param factType the type of object stored in facts for this Rule
   * @param <T>      the type of Fact
   * @return a new instance of a StandardRule
   */
  public static <T> StandardRule<T> create(Class<T> factType) {
    return new StandardRule<>(factType);
  }

  /**
   * This create() method is a convenience method to create a non-type specific StandardRule.
   * @return a new instance of a StandardRule
   */
  public static StandardRule<Object> create() {
    return new StandardRule<>(Object.class);
  }

  protected StandardRule() {
  }

  public StandardRule(Class<T> clazz) {
    this();
    _factType = clazz;
  }

  /**
   * The run(Object[]) method runs the {@link Predicate} supplied by the when() method. <br/>
   * If it evaluates to true then the action(s) supplied by the then() method(s) are executed. <br/>
   * If the stop() method was invoked then no further rules are evaluated. <br/>
   * Otherwise, the next rule in the chain is evaluated.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void run(Object... otherArgs) {
    try {
      //only use facts of the specified type
      FactMap<T> typeFilteredFacts = new FactMap<T>((Map<String, NameValueReferable<T>>)_facts.values().stream()
          .filter((Object fact) -> _factType.isAssignableFrom(((Fact)fact).getValue().getClass()))
          .collect(Collectors.toMap(fact -> ((Fact)fact).getName(), fact -> (Fact<T>)fact)));
      //invoke then() action(s) if when() is true or if when() was never specified
      if (getWhen() == null || getWhen().test(typeFilteredFacts)) {

        //iterate through the then() actions specified
        List<Object> actionList = getThen();
        for (int i = 0; i < (getThen()).size(); i++) {
          Object action = actionList.get(i);
          List<String> factNames = _factNameMap.get(i);

          //if using() fact names were specified for the specific then(), use only those facts specified
          FactMap<T> usingFacts;
          if (factNames != null) {
            usingFacts = new FactMap<T>(factNames.stream()
                .filter(typeFilteredFacts::containsKey)
                .collect(Collectors.toMap(name -> name, name -> _facts.get(name))));
          } else {
            usingFacts = typeFilteredFacts;
          }

          //invoke the action
          Stream.of(action.getClass().getMethods())
              .filter(method -> method.getName().equals("accept"))
              .findFirst()
              .ifPresent(method -> {
                  try {
                    method.setAccessible(true);
                    method.invoke(action,
                        ArrayUtils.combine(new Object[]{usingFacts}, otherArgs, method.getParameterCount()));
                  } catch (IllegalAccessException | InvocationTargetException err) {
                    LOGGER.error("Error invoking action on " + action.getClass(), err);
                  }
                });
        }

        //if stop() was invoked, stop the rule chain after then is finished executing
        if (_ruleState == BREAK) {
          return;
        }
      }
    } catch (Exception ex) {

      //catch errors in case something like one rule was chained expecting a Fact that doesn't exist
      //eventually, we'll have to resolve that kind of issue ahead of time
      LOGGER.error("Error occurred when trying to evaluate rule!", ex);
    }

    //continue down the rule chain
    _nextRule.ifPresent(rule -> rule.given(_facts));
    _nextRule.ifPresent(Rule::run);
  }

  /**
   * The given() method accepts a name/value pair to be used as a Fact.
   * @param name  name of the Fact
   * @param value object provided as the Fact with the given name
   * @return      the current object
   */
  @Override
  @SuppressWarnings("unchecked")
  public Rule<T> given(String name, T value) {
    _facts.put(name, new Fact<T>(name, value));
    return this;
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   * @param facts Facts to be used by the Rule
   * @return      the current object
   */
  @Override
  @SuppressWarnings("unchecked")
  public Rule<T> given(Fact<T>... facts) {
    for (Fact f : facts) {
      _facts.put(f.getName(), f);
    }

    return this;
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   * @param facts a List of Facts to be used by the Rule
   * @return      the current object
   */
  @SuppressWarnings("unchecked")
  @Override
  public Rule<T> given(List<Fact<T>> facts) {
    for (Fact f : facts) {
      _facts.put(f.getName(), f);
    }

    return this;
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   * @param facts a {@link FactMap}
   * @return      the current object
   */
  @Override
  public Rule<T> given(FactMap<T> facts) {
    _facts = facts;
    return this;
  }

  @Override
  public Rule<T> givenUnTyped(FactMap facts) {
    _facts = facts;
    return this;
  }

  @Override
  public FactMap getFactMap() {
    return _facts;
  }

  /**
   * The when() method accepts a {@link Predicate} that returns true or false based on Facts.
   * @param test  the condition(s) to be evaluated against the Facts
   * @return      the current object
   */
  @Override
  public Rule<T> when(Predicate<FactMap<T>> test) {
    _test = test;
    return this;
  }

  /**
   * The then() method accepts a {@link Consumer} that performs an action based on Facts.
   *
   * @param action  the action to be performed
   * @return        the current object
   */
  @Override
  @SuppressWarnings("unchecked")
  public Rule<T> then(Consumer<FactMap<T>> action) {
    _actionChain.add(action);
    return this;
  }

  /**
   * The stop() method causes the rule chain to stop if the when() condition true and only
   * after the then() actions have been executed.
   * @return  the current object
   */
  @Override
  public Rule<T> stop() {
    _ruleState = BREAK;
    return this;
  }

  /**
   * The using() method reduces the facts to those specifically named here.
   * The using() method only applies to the then() method immediately following it.
   * @param factNames the names of the facts to be used
   * @return          the current object
   */
  @Override
  @SuppressWarnings("unchecked")
  public StandardRule<T> using(String... factNames) {
    List<String> factNameList = Stream.of(factNames)
        .filter(name -> _factType.isInstance(_facts.getValue(name)))
        .collect(Collectors.toList());
    if (_factNameMap.containsKey((getThen()).size())) {
      List<String> existingFactNames = _factNameMap.get((getThen()).size());
      existingFactNames.addAll(factNameList);
      return this;
    }

    _factNameMap.put((getThen()).size(), factNameList);
    return this;
  }

  /**
   * The setNextRule() method sets the next Rule in the chain.
   * @param rule the next Rule to add to the chain
   */
  @Override
  public void setNextRule(Rule<T> rule) {
    _nextRule = Optional.ofNullable(rule);
  }

  /**
   * The getWhen() method returns the {@link Predicate} to be used for the condition of the Rule.
   * @return  the Predicate condition
   */
  @Override
  public Predicate<FactMap<T>> getWhen() {
    return _test;
  }

  /**
   * The getThen() method returns a {@link List} of {@link Consumer} objects that combined
   * together in sequence represent the then() action(s).
   * @return  a List of Consumer objects
   */
  @Override
  public List<Object> getThen() {
    return _actionChain;
  }
}
