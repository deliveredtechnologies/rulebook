package com.deliveredtechnologies.rulebook;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import static com.deliveredtechnologies.rulebook.RuleState.*;

/**
 * Created by clong on 2/6/17.
 * A standard rule implementation that can be used with a {@link RuleBook}
 */
public class StandardRule<T> implements Rule<T> {
    private Rule<T> _nextRule;
    private FactMap<T> _facts = new FactMap<>();
    private Predicate<FactMap<T>> _test;
    private Function<FactMap<T>, RuleState> _action;

    public StandardRule() { }

    /**
     * convenience method to avoid using new and generic syntax
     * @param factType  the type of object stored in facts for this <code>Rule</code>
     * @param <T>
     * @return          a new instance of <code>StandardRule</code> of type
     */
    public static <T> StandardRule<T> create(Class<T> factType) {
        return new StandardRule<T>();
    }

    /**
     * convenience method to create a non-type specific <code>StandardRule</code>
     * @return
     */
    public static StandardRule<Object> create() {
        return new StandardRule<Object>();
    }

    /**
     * runs the {@link Predicate} supplied by the <code>when()</code> method; if it evaluates to true then
     * the {@link Function} supplied by the <code>then()</code> method is executed; if the <code>then()</code>
     * method returns a BREAK {@link RuleState} then no further rules are evaluated, otherwise the next rule in the
     * chain is evaluated
     */
    @Override
    public void run() {
        if (_test.test(_facts)) {
            if (_action.apply(_facts) == BREAK) {
                return;
            }
        }
        if (Optional.ofNullable(this._nextRule).isPresent()) {
            this._nextRule.run();
        }
    }

    @Override
    public Rule<T> given(Fact<T>... facts) {
        for (Fact f : facts) {
            _facts.put(f.getName(), f);
        }

        return this;
    }

    @Override
    public Rule<T> given(List<Fact<T>> facts) {
        for (Fact f : facts) {
            _facts.put(f.getName(), f);
        }

        return this;
    }

    @Override
    public Rule<T> given(FactMap<T> facts) {
        _facts = facts;
        return this;
    }

    @Override
    public Rule<T> when(Predicate<FactMap<T>> test) {
        _test = test;
        return this;
    }

    @Override
    public Rule<T> then(Function<FactMap<T>, RuleState> action) {
        _action = action;
        return this;
    }

    @Override
    public void setNextRule(Rule<T> rule) {
        _nextRule = rule;
    }
}
