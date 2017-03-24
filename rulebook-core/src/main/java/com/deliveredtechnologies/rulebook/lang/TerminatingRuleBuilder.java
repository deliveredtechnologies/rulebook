package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.Rule;

/**
 * Created by clong on 3/24/17.
 */
public interface TerminatingRuleBuilder<T, U> {
  Rule<T, U> build();
}
