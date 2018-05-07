package com.deliveredtechnologies.rulebook.model;

/**
 * Runtime exception to wrap Rule exceptions.
 * This exception exists for two reasons:
 * <ol>
 *     <li>It denotes that the exception happened in a Rule.</li>
 *     <li>It allows exceptions to be thrown from a Rule without changing the interface.</li>
 * </ol>
 */
public class RuleException extends RuntimeException {

  /**
   * Create a RuleException with an exception message.
   * @param message exception message
   */
  public RuleException(String message) {
    super(message);
  }

  /**
   * Create a RuleException with an exception message and a cause (the originating Throwable exception).
   * @param message exception message
   * @param cause   the originating Throwable exception
   */
  public RuleException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Create a RuleException using the originating Throwable exception.
   * @param cause the originating Throwable exception
   */
  public RuleException(Throwable cause) {
    super(cause);
  }
}
