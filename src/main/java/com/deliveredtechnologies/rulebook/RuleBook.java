package com.deliveredtechnologies.rulebook;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by clong on 2/6/17.
 */
public abstract class RuleBook<T> {
    protected Rule<T> _headRule;
    protected Rule<T> _tailRule;
    protected List<Fact<T>> _facts = new ArrayList<>();

    public RuleBook() { }

    public final void run() {
        defineRules();
        _headRule.run();
    }

    @SafeVarargs
    public final RuleBook<T> given(Fact<T>... facts) {
        for (Fact<T> f : facts) {
            _facts.add(f);
        }
        return this;
    }

    protected final void addRule(Rule<T> rule) {
        rule.given(_facts);
        if (!Optional.ofNullable(_headRule).isPresent()) {
            _headRule = rule; //this rule is the head if there was no head
            _tailRule = rule;
        }
        else {
            _tailRule.setNextRule(rule);
            _tailRule = rule;
        }
    }

    /**
     * this is where the rules can be specified in the subclass; it will be executed by <code>run()</code>
     */
    protected abstract void defineRules();
}