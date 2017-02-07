package com.deliveredtechnologies.rulebook;

/**
 * Created by clong on 2/7/17.
 * This is a wrapper class for the actual result.
 * This is needed to ensure that an instance is available and that a reference is maintained to the actual that
 * can be manipulated in the then {@link java.util.function.BiFunction}.
 * The <code>Result</code> object can then contain a pointer to any instance, which can be shared with lamda calls
 * and across other objects (i.e. DecisionBook and Decision objects)
 */
public class Result<T> {
    private T _value;

    public T getValue() {
        return _value;
    }

    public void setValue(T _value) {
        this._value = _value;
    }
}
