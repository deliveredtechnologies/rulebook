package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.Rule;

/**
 * An interface for building Rule objects.
 */
public interface TerminatingRuleBuilder<T, U> {
  /**
   * Builds a Rule object.
   * @return  a Rule object
   */
  Rule<T, U> build();
}
