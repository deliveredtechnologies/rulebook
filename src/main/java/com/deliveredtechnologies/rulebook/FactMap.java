package com.deliveredtechnologies.rulebook;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by clong on 2/6/17.
 * A FactMap is an extension of {@link HashMap}; it stores facts by their name and provides convenience methods for
 * accessing {@link Fact} objects
 */
public class FactMap<T> extends HashMap<String, Fact<T>> {
    /**
     * @return the value of a fact stored if only one fact is stored, otherwise null
     */
    public T getOne() {
        if (this.size() == 1) {
            return this.values().iterator().next().getValue();
        }
        return null;
    }

    /**
     *
     * @param name  the name of the Fact
     * @return      the value of the Fact
     */
    public T getValue(String name) {
        Fact<T> fact = this.get(name);
        return Optional.ofNullable(fact).isPresent() ? fact.getValue() : null;
    }
}

