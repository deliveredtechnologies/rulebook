package com.deliveredtechnologies.rulebook.annotation;

import com.deliveredtechnologies.rulebook.Decision;
import com.deliveredtechnologies.rulebook.model.RuleChainActionType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.CONTINUE_ON_FAILURE;

/**
 * Rule defines a class to be injected into a {@link Decision}.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Rule {

  /**
   * This is the name of the Rule.
   */
  String name() default "None";

  /**
   * This specifies the order in which the rule will execute.
   * The ordering is 1, 2, 3, 4, etc.
   * Two rules in the same package with the same order will execute in a non-specified order.
   */
  int order() default 1;

  /**
   * This specifies the impact that a Rule failure should have on the rule chain.
   */
  RuleChainActionType ruleChainAction() default CONTINUE_ON_FAILURE;
}

