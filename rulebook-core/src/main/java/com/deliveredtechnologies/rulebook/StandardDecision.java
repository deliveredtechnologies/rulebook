package com.deliveredtechnologies.rulebook;

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

/**
 * StandardDecision is the standard implementation of {@link Decision}.
 */
public class StandardDecision<T, U> implements Decision<T, U> {
  private Optional<Rule<T>> _nextRule = Optional.empty();
  private FactMap<T> _facts = new FactMap<>();
  private Result<U> _result = new Result<>();
  private Predicate<FactMap<T>> _test;
  private List<Object> _actionChain = new ArrayList<>();
  private Map<Integer, String[]> _factNameMap = new HashMap<>();
  private RuleState _ruleState = NEXT;

  public StandardDecision() {
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
    return new StandardDecision<>();
  }

  /**
   * This create() method is another convenience method to create a non-type specific StandardDecision.
   * @return a new instance of StandardDecision
   */
  public static StandardDecision<Object, Object> create() {
    return new StandardDecision<>();
  }

  /**
   * The run() method runs the {@link Predicate} supplied by the <code>when()</code> method.
   * If it evaluates to true then the {@link BiFunction} supplied by the then() method is executed.
   * If the BiFuction is not available then the {@link Function} is called instead. If the then() method returns a
   * BREAK {@link RuleState} then no further rules are evaluated, otherwise the next rule in the chain is evaluated.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void run() {
    if (getWhen().test(_facts)) {
      List<Object> actionList = (List<Object>)getThen();
      for (int i = 0; i < actionList.size(); i++) {
        Object action = actionList.get(i);
        String[] factNames = _factNameMap.get(i);
        FactMap<T> usingFacts;
        if (factNames != null) {
          usingFacts = new FactMap<T>();
          for (String factName : factNames) {
            usingFacts.put(factName, _facts.get(factName));
          }
        } else {
          usingFacts = _facts;
        }
        if (action instanceof BiConsumer) {
          ((BiConsumer<FactMap<T>, Result<U>>)action).accept(usingFacts, _result);
        } else if (action instanceof Consumer) {
            ((Consumer<FactMap<T>>)action).accept(usingFacts);
        }
      }
      if (_ruleState == BREAK) {
        return;
      }
    }
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
  public StandardDecision<T, U> given(String name, T value) {
    _facts.put(name, new Fact<T>(name, value));
    return this;
  }

  /**
   * The given() method accepts Facts for the StandardDecision.
   * @param facts     Facts to be used by the Rule
   * @return the current object for chaining other methods
   */
  @Override
  public StandardDecision<T, U> given(Fact<T>... facts) {
    Arrays.stream(facts).forEach(fact -> _facts.put(fact.getName(), fact));
    return this;
  }

  /**
   * The given() method accepts Facts for the StandardDecision.
   * @param facts     a List of Facts to be used by the Rule
   * @return the current object
   */
  @Override
  public StandardDecision<T, U> given(List<Fact<T>> facts) {
    facts.forEach(fact -> _facts.put(fact.getName(), fact));

    return this;
  }

  /**
   * The given() method accepts Facts for the StandardDecision.
   * @param facts     a {@link FactMap}
   * @return the current object
   */
  @Override
  public StandardDecision<T, U> given(FactMap<T> facts) {
    _facts = facts;
    return this;
  }

  /**
   * The when() method accepts a {@link Predicate} that returns true or false based on Facts.
   * @param test      the condition(s) to be evaluated against the Facts
   * @return true, if the then() statement should be evaluated, otherwise false
   */
  @Override
  public StandardDecision<T, U> when(Predicate<FactMap<T>> test) {
    _test = test;
    return this;
  }

  @Override
  public StandardDecision<T, U> then(BiConsumer<FactMap<T>, Result<U>> action) {
    _actionChain.add(action);
    return this;
  }

  /**
   * The then() method accepts a {@link Function} that performs an action based on Facts and then returns a
   * {@link RuleState} of either NEXT or BREAK.
   * @param action    the action to be performed
   * @return NEXT if the next Rule should be run, otherwise BREAK
   */
  @Override
  public StandardDecision<T, U> then(Consumer<FactMap<T>> action) {
    _actionChain.add(action);
    return this;
  }

  @Override
  public StandardDecision stop() {
    _ruleState = BREAK;
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public StandardDecision<T, U> using(String... factNames) {
    if (_factNameMap.containsKey(((List<Object>)getThen()).size())) {
      String[] existingFactNames = _factNameMap.get(((List<Object>)getThen()).size());
      String[] allFactNames = new String[factNames.length + existingFactNames.length];
      System.arraycopy(existingFactNames, 0, allFactNames, 0, existingFactNames.length);
      System.arraycopy(factNames, 9, allFactNames, existingFactNames.length, factNames.length);
      _factNameMap.put(((List<Object>)getThen()).size(), allFactNames);
      return this;
    }

    _factNameMap.put(((List<Object>)getThen()).size(), factNames);
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
   *
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

  @Override
  public Predicate<FactMap<T>> getWhen() {
    return _test;
  }

  @Override
  public Object getThen() {
    return _actionChain;
  }
}
