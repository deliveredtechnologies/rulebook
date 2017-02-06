package com.deliveredtechnologies.rulebook;

/**
 * Created by clong on 2/6/17.
 * A Fact is a single piece of data that can be supplied to a {@link Rule}
 * Facts are not immutable; they may be changed by rules and used to derive a result state
 */
public class Fact<T> {
    private String _name;
    private T _value;

    public Fact(String name, T value) {
        _name = name;
        _value = value;
    }

    public Fact (T obj) {
        _name = obj.toString();
        _value = obj;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public T getValue() {
        return _value;
    }

    public void setValue(T _value) {
        this._value = _value;
    }
}
