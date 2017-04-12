package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import java.util.Optional;

/**
 * Chain of Responsibility handler.
 * @param <T> the type of the delegate object
 */
public interface Handler<T> {

  /**
   * Handle the request.
   */
  void handleRequest();

  /**
   * Get the delegate: the object contained within the handler implementation.
   * @return  the delegate obejct
   */
  T getDelegate();

  /**
   * Gets the successor in the chain.
   * @return  the next Optioanl Handler in the chain or Optional.empty
   */
  Optional<Handler<T>> getSuccessor();

  /**
   * Sets the next Handler in the chain.
   * @param successor the Handler to set as the next in the chain
   * @return  the Handler just set or null if the Handler was not set
   */
  Handler<T> setSuccessor(Handler<T> successor);
}
