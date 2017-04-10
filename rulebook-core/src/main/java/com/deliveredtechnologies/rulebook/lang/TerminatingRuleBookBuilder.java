package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.RuleBook;

/**
 * An interface for building RuleBook objects.
 */
public interface TerminatingRuleBookBuilder<T> {
  /**
   * Builds a RuleBook object.
   * @return  a RuleBook object
   */
  RuleBook<T> build();
}
