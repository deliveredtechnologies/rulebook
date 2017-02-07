package com.deliveredtechnologies.rulebook;

import java.util.function.BiFunction;

/**
 * Created by clong on 2/6/17.
 */
public interface Decision <T, U> extends Rule<T> {
    /**
     * @param action the action to be performed
     * @return the current <code>Rule</code> object
     */
    Decision<T, U> then(BiFunction<FactMap<T>, Result<U>, RuleState> action);

    /**
     *
     * @param result
     * @return  the current <code>Rule</code> object
     */
    Decision<T, U> withResult(Result<U> result);
}
