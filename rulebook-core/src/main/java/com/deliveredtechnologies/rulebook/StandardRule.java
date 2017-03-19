package com.deliveredtechnologies.rulebook;

import com.deliveredtechnologies.rulebook.util.ArrayUtils;

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
   * The run() method runs the {@link Predicate} supplied by the when() method. <br/>
   * If it evaluates to true then the {@link Consumer}(s) supplied by the then() method(s) are executed. <br/>
   * If the stop() method was invoked then no further rules are evaluated. <br/>
   * Otherwise, the next rule in the chain is evaluated.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void run() {
    //invoke then() action(s) if when() is true or if when() was never specified
    if (getWhen() == null || getWhen().test(_facts)) {
      //iterate through the then() actions specified
      List<Object> actionList = (List<Object>)getThen();
      for (int i = 0; i < ((List<Object>)getThen()).size(); i++) {
        Object action = actionList.get(i);
        String[] factNames = _factNameMap.get(i);
        FactMap<T> usingFacts;
        //if using() was specified for the specific then(), use only those facts specified
        if (factNames != null) {
          usingFacts = new FactMap<T>();
          for (String factName : factNames) {
            usingFacts.put(factName, _facts.get(factName));
          }
        } else {
          //if no using() was specified, provide the then() with all available facts
          usingFacts = _facts;
        }
        if (action instanceof Consumer) {
          //invoke the then() Consumer action
          ((Consumer) action).accept(usingFacts);
        }
      }
      //if stop() was invoked, stop the rule chain after then is finished executing
      if (_ruleState == BREAK) {
        return;
      }
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
  public Rule<T> given(String name, T value) {
    _facts.put(name, new Fact(name, value));
    return this;
  }

  /**
   * The given() method accepts Facts to be evaluated in the Rule.
   * @param facts Facts to be used by the Rule
   * @return      the current object
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
   * @param facts a List of Facts to be used by the Rule
   * @return      the current object
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
   * @param facts a {@link FactMap}
   * @return      the current object
   */
  @Override
  public Rule<T> given(FactMap<T> facts) {
    _facts = facts;
    return this;
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
    if (_factNameMap.containsKey(((List<Object>)getThen()).size())) {
      String[] existingFactNames = _factNameMap.get(((List<Object>)getThen()).size());
      String[] allFactNames = ArrayUtils.combine(existingFactNames, factNames);
      _factNameMap.put(((List<Object>)getThen()).size(), allFactNames);
      return this;
    }

    _factNameMap.put(((List<Object>)getThen()).size(), factNames);
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
   * together in sequence represent the then() action(s). The Object return type is retained
   * for backward compatibility.
   * @return  a List of Consumer objects
   */
  @Override
  public Object getThen() {
    return _actionChain;
  }
}
