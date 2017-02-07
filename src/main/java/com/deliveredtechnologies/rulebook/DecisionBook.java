package com.deliveredtechnologies.rulebook;

import java.util.Optional;

/**
 * Created by clong on 2/6/17.
 */
public abstract class DecisionBook<T, U> extends RuleBook<T> {
    private Result<U> _result;

    public final DecisionBook<T, U> withResult(Result<U> result) {
        _result = result;
        return this;
    }

    protected final void addRule(Decision<T, U> rule) {
        super.addRule(rule);
    }

    public Result<U> getResult() {
        return _result;
    }
}
