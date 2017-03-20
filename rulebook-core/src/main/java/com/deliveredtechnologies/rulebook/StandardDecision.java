package com.deliveredtechnologies.rulebook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.deliveredtechnologies.rulebook.RuleState.BREAK;
import static com.deliveredtechnologies.rulebook.RuleState.NEXT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * StandardDecision is the standard implementation of {@link Decision}.
 */
public class StandardDecision<T, U> implements Decision<T, U> {
  private static Logger LOGGER = LoggerFactory.getLogger(StandardDecision.class);

  private Class<T> _factType;
  private Class<U> _resultType;
  private Optional<Rule<T>> _nextRule = Optional.empty();
  private FactMap _facts = new FactMap();
  private Result<U> _result = new Result<>();
  private Predicate<FactMap<T>> _test;
  private List<Object> _actionChain = new ArrayList<>();
  private Map<Integer, List<String>> _factNameMap = new HashMap<>();
  private RuleState _ruleState = NEXT;

  protected StandardDecision() {
  }

  public StandardDecision(Class<T> factClazz, Class<U> resultClazz) {
    _factType = factClazz;
    _resultType = resultClazz;
  }

  /**
   * This create() method is a convenience method to avoid using new and generic syntax.
   *
   * @param factType    the type of object stored in facts for this Decision
   * @param returnType  the type of the stored Result
   * @param <T>         the class type of the objects in the Facts used
   * @param <U>         the class type of object stored in the Result
   *
   * @return a new instance of StandardDecision
   */
  public static <T, U> StandardDecision<T, U> create(Class<T> factType, Class<U> returnType) {
    return new StandardDecision<>(factType, returnType);
  }

  /**
   * This create() method is another convenience method to create a non-type specific StandardDecision.
   * @return a new instance of StandardDecision
   */
  public static StandardDecision<Object, Object> create() {
    return new StandardDecision<Object, Object>(Object.class, Object.class);
  }

  /**
   * The run() method runs the {@link Predicate} supplied by the when() method.<br/>
   * If it evaluates to true then the {@link BiConsumer}(s) and {@link Consumer}(s) supplied by the then() method(s)
   * are executed.<br/>
   * If the stop() method was invoked then no further rules are evaluated.<br/>
   * Otherwise, the next rule in the chain is evaluated.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void run() {
    try {
      //invoke then() action(s) if when() is true or if when() was never specified
      if (getWhen() == null || getWhen().test(_facts)) {
        //iterate through the then() actions specified
        List<Object> actionList = getThen();
        for (int i = 0; i < actionList.size(); i++) {
          Object action = actionList.get(i);
          List<String> factNames = _factNameMap.get(i);
          FactMap<T> usingFacts;
          //if using() was specified for the specific then(), use only those facts specified
          if (factNames != null) {
            usingFacts = new FactMap<T>();
            for (String factName : factNames) {
              usingFacts.put(factName, (Fact<T>)_facts.get(factName));
            }
          } else {
            //if no using() was specified, provide the then() with all available facts
            usingFacts = new FactMap<T>((Map<String, Fact<T>>)_facts.values().stream()
              .filter((Object fact) -> _factType.isInstance(((Fact)fact).getValue()))
              .collect(Collectors
                .toMap(fact -> ((Fact)fact).getName(), fact -> (Fact<T>)fact)));
          }
          if (action instanceof BiConsumer) {
            //invoke the then() BiConsumer action
            ((BiConsumer<FactMap<T>, Result<U>>) action).accept(usingFacts, _result);
          } else {
            //invoke the then() BiConsumer action
            ((Consumer<FactMap<T>>) action).accept(usingFacts);
          }
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
  public Decision<T, U> given(String name, T value) {
    _facts.put(name, new Fact<T>(name, value));
    return this;
  }

  /**
   * The given() method accepts Facts for the StandardDecision.
   * @param facts Facts to be used by the Rule
   * @return      the current object for chaining other methods
   */
  @Override
  public Decision<T, U> given(Fact<T>... facts) {
    Arrays.stream(facts).forEach(fact -> _facts.put(fact.getName(), fact));
    return this;
  }

  /**
   * The given() method accepts Facts for the StandardDecision.
   * @param facts a List of Facts to be used by the Rule
   * @return      the current object
   */
  @Override
  public Decision<T, U> given(List<Fact<T>> facts) {
    facts.forEach(fact -> _facts.put(fact.getName(), fact));
    return this;
  }

  /**
   * The given() method accepts Facts for the StandardDecision.
   * @param facts a {@link FactMap}
   * @return      the current object
   */
  @Override
  public Decision<T, U> given(FactMap<T> facts) {
    _facts = facts;
    return this;
  }

  @Override
  public Decision<T, U> givenUnTyped(FactMap facts) {
    return null;
  }

  /**
   * The when() method accepts a {@link Predicate} that returns true or false based on Facts.
   * @param test  the condition(s) to be evaluated against the Facts
   * @return      true, if the then() statement(s) should be evaluated, otherwise false
   */
  @Override
  public Decision<T, U> when(Predicate<FactMap<T>> test) {
    _test = test;
    return this;
  }

  /**
   * The then() method accepts a {@link BiConsumer} that performs an action based on Facts.<br/>
   * The arguments of the BiConsumer are a {@link FactMap} and a {@link Result}, respectively.
   * @param action  the action to be performed
   * @return        the current object
   */
  @Override
  public Decision<T, U> then(BiConsumer<FactMap<T>, Result<U>> action) {
    _actionChain.add(action);
    return this;
  }

  /**
   * The then() method accepts a {@link Consumer} that performs an action based on Facts.
   * @param action  the action to be performed
   * @return        the current object
   */
  @Override
  public Decision<T, U> then(Consumer<FactMap<T>> action) {
    _actionChain.add(action);
    return this;
  }

  /**
   * The stop() method causes the rule chain to stop if the when() condition true and only
   * after the then() actions have been executed.
   * @return  the current object
   */
  @Override
  public Decision<T, U> stop() {
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
  public Decision<T, U> using(String... factNames) {
    List<String>factNameList = Stream.of(factNames)
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
   * The setNextRule() method sets the next rule in the chain.
   * @param rule  the next Rule to add to the chain
   */
  @Override
  public void setNextRule(Rule<T> rule) {
    _nextRule = Optional.ofNullable(rule);
  }

  /**
   * The getResult() method gets the stored Result value from the execution of the StandardDecision.
   * @return the stored Result value
   */
  @Override
  public U getResult() {
    return _result.getValue();
  }

  /**
   * The setResult() method sets the stored Result.
   * @param result the instantiated result
   */
  @Override
  public void setResult(Result<U> result) {
    _result = result;
  }

  /**
   * The getFactMap() method gets the factMap of stored facts.
   * @return  the factMap of stored facts
   */
  public FactMap<T> getFactMap() {
    return _facts;
  }

  /**
   * The getWhen() method returns the {@link Predicate} to be used for the condition of the Rule.
   * @return  the Predicate used for the condition
   */
  @Override
  public Predicate<FactMap<T>> getWhen() {
    return _test;
  }

  /**
   * The getThen() method returns a {@link List} of {@link Consumer} objects that combined
   * together in sequence represent the then() action(s).
   * @return  a List of Consumer and/or BiConsumer objects
   */
  @Override
  public List<Object> getThen() {
    return _actionChain;
  }
}
