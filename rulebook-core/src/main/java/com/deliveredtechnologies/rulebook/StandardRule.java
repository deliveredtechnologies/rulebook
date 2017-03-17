package com.deliveredtechnologies.rulebook;

import static com.deliveredtechnologies.rulebook.RuleState.BREAK;
import static com.deliveredtechnologies.rulebook.RuleState.NEXT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * StandardRule is a standard rule implementation that can be used with a {@link RuleBook}.
 */
public class StandardRule<T> implements Rule<T> {
  private Optional<Rule<T>> _nextRule = Optional.empty();
  private FactMap<T> _facts = new FactMap<>();
  private Predicate<FactMap<T>> _test;
  private List _actionChain = new ArrayList();
  private Map<Integer, String[]> _factNameMap = new HashMap<>();
  private RuleState _ruleState = NEXT;

  public StandardRule() {
  }

  /**
   * This create() method is a convenience method to avoid using new and generic syntax.
   *
   * @param factType the type of object stored in facts for this Rule
   * @param <T>      the type of Fact
   * @return a new instance of a StandardRule
   */
  public static <T> StandardRule<T> create(Class<T> factType) {
    return new StandardRule<>();
  }

  /**
   * This create() method is a convenience method to create a non-type specific StandardRule.
   * @return a new instance of a StandardRule
   */
  public static StandardRule<Object> create() {
    return new StandardRule<>();
  }

  /**
   * The run() method runs the {@link Predicate} supplied by the when() method. If it evaluates to true then
   * the {@link Consumer} supplied by the then() method is executed. If the break()
   * method was invoked then no further rules are evaluated. Otherwise, the next rule in the
   * chain is evaluated.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void run() {
    if (getWhen().test(_facts)) {
      List<Object> actionList = (List<Object>)getThen();
      for (int i =0; i < ((List<Object>)getThen()).size(); i++) {
        Object action = actionList.get(i);
        String[] factNames = _factNameMap.get(i);
        FactMap<T> usingFacts;
        if (factNames != null) {
          usingFacts = new FactMap<T>();
          for (String factName : factNames) {
            usingFacts.put(factName, _facts.get(factName));
          }
        }
        else {
          usingFacts = _facts;
        if (action instanceof Consumer) {
            ((Consumer) action).accept(usingFacts);
          } else {
            ((Consumer) action).accept(usingFacts);
          }
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
  public Rule<T> given(String name, T value) {
    _facts.put(name, new Fact(name, value));
    return this;
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   * @param facts     Facts to be used by the Rule
   * @return the current object
   */
  @Override
  public Rule<T> given(Fact<T>... facts) {
    for (Fact f : facts) {
      _facts.put(f.getName(), f);
    }

    return this;
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   *
   * @param facts a List of Facts to be used by the Rule
   * @return the current object
   */
  @Override
  public Rule<T> given(List<Fact<T>> facts) {
    for (Fact f : facts) {
      _facts.put(f.getName(), f);
    }

    return this;
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   * @param facts     a {@link FactMap}
   * @return the current object
   */
  @Override
  public Rule<T> given(FactMap<T> facts) {
    _facts = facts;
    return this;
  }

  /**
   * The when() method accepts a {@link Predicate} that returns true or false based on Facts.
   * @param test      the condition(s) to be evaluated against the Facts
   * @return the current object
   */
  @Override
  public Rule<T> when(Predicate<FactMap<T>> test) {
    _test = test;
    return this;
  }

  /**
   * The then() method accepts a {@link Consumer} that performs an action based on Facts
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

  @Override
  public Rule<T> stop() {
    _ruleState = BREAK;
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public StandardRule<T> using(String... factNames) {
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
   * The setNextRule() method sets the next Rule in the chain.
   *
   * @param rule the next Rule to add to the chain
   */
  @Override
  public void setNextRule(Rule<T> rule) {
    _nextRule = Optional.ofNullable(rule);
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
