package com.deliveredtechnologies.rulebook;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by clong on 2/6/17.
 * A <code>Rule</code> is an interface that uses the following format:
 * rule.given(facts).when(some condition given facts).then(do something)
 */
public interface Rule<T> {
    /**
     * evaluates the <code>Rule</code>
     */
    void run();

    /**
     *
     * @param facts     Facts to be used by the <code>Rule</code>
     * @return          the current <code>Rule</code> object
     */
    Rule<T> given(Fact<T>... facts);

    /**
     *
     * @param facts     a <code>List</code> of Facts to be used by the <code>Rule</code>
     * @return          the current <code>Rule</code> object
     */
    Rule<T> given(List<Fact<T>> facts);

    /**
     *
     * @param facts     a {@link FactMap}
     * @return          the current <code>Rule</code> object
     */
    Rule<T> given(FactMap<T> facts);

    /**
     *
     * @param test      the condition(s) to be evaluated against the Facts
     * @return          the current <code>Rule</code> object
     */
    Rule<T> when(Predicate<FactMap<T>> test);

    /**
     *
     * @param action    the action to be performed
     * @return          the current <code>Rule</code> object
     */
    Rule<T> then(Function<FactMap<T>, RuleState> action);

    /**
     * adds the next <code>Rule</code> to the chain
     * @param rule
     */
    void setNextRule(Rule<T> rule);
}
