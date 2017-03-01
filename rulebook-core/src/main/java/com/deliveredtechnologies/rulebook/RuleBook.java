package com.deliveredtechnologies.rulebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by clong on 2/6/17.
 * RuleBook is an abstract class that follows the Template Method design pattern, leaving the defineRules() method
 * implementation for subclasses. This class facilitates the aggregation and chaining together of Rules.
 */
public abstract class RuleBook<T> {
  protected Optional<Rule<T>> _headRule = Optional.empty();
  protected Rule<T> _tailRule;
  protected List<Fact<T>> _facts = new ArrayList<>();

  public RuleBook() {
  }

  /**
   * The run() method adds the rules [via defineRules()] and runs the rules as long as at least one rule was
   * added.
   */
  public final void run() {
    defineRules();
    _headRule.ifPresent(rule -> rule.given(_facts));
    _headRule.ifPresent(Rule::run);
  }

  /**
   * The given() method accepts the Facts for this RuleBook.
   * The facts passed in will also be applied to all rules added to this RuleBook.
   *
   * @param facts Facts to be added to the RuleBook and applied to all the Rules in the RuleBook
   * @return the current RuleBook object
   */
  @SafeVarargs
  public final RuleBook<T> given(Fact<T>... facts) {
    Arrays.stream(facts).forEach(_facts::add);
    return this;
  }

  /**
   * The addRule() method adds a rule to the end of the Rules chain.
   *
   * @param rule the Rule to be added
   */
  public void addRule(Rule<T> rule) {
    if (rule == null) {
      return;
    }

    if (!_headRule.isPresent()) {
      _headRule = Optional.of(rule); // this rule is the head if there was no head
      _tailRule = rule;
    } else {
      _tailRule.setNextRule(rule);
      _tailRule = rule;
    }
  }

  /**
   * this is where the rules can be specified in the subclass; it will be executed by run().
   */
  protected abstract void defineRules();
}