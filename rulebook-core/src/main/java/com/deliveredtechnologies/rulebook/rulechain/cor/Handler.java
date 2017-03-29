package com.deliveredtechnologies.rulebook.rulechain.cor;

import java.util.Optional;

/**
 * Created by clong on 3/26/17.
 */
public interface Handler<T> {
  void handleRequest();
  T getDelegate();
  Optional<Handler<T>> getSuccessor();
  Handler<T> setSuccessor(Handler<T> successor);
}
