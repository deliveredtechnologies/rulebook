package com.deliveredtechnologies.rulebook;

/**
 * Created by clong on 2/6/17.
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
