package com.deliveredtechnologies.rulebook.model;

/**
 * Defines an interface for creating a RuleBook object.
 */
public interface RuleBookFactory<T> {

  /**
   * Creates a RuleBook.
   * @return  a new RuleBook instance
   */
  RuleBook<T> createRuleBook();
}
