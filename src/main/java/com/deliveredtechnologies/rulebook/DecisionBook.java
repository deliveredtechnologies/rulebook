package com.deliveredtechnologies.rulebook;

/**
 * Created by clong on 2/6/17.
 *
 */
public abstract class DecisionBook<T, U> extends RuleBook<T> {
    private Result<U> _result = new Result<U>();

    public final DecisionBook<T, U> withDeafultResult(U result) {
        _result.setValue(result);
        return this;
    }


    protected final void addRule(Decision<T, U> rule) {
        super.addRule(rule);
    }

    public U getResult() {
        return _result.getValue();
    }

}
