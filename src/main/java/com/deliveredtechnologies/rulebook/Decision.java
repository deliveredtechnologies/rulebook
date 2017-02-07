package com.deliveredtechnologies.rulebook;

import java.util.function.BiFunction;

/**
 * Created by clong on 2/6/17.
 * A special type of rule that has a return type, which may be different from the type of input (Facts).
 */
public interface Decision <T, U> extends Rule<T> {
    /**
     * @param action the action to be performed
     * @return the current <code>Rule</code> object
     */
    Decision<T, U> then(BiFunction<FactMap<T>, Result<U>, RuleState> action);

    /**
     *
     * @return the result object
     */
    U getResult();

    /**
     * @param result the instantiated result; useful for aggregation/chaining of a result
     */
    void setResult(Result<U> result);
}
