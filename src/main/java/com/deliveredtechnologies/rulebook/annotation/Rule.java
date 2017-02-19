package com.deliveredtechnologies.rulebook.annotation;

import com.deliveredtechnologies.rulebook.Decision;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by clong on 2/12/17.
 * Rule defines a class to be injected into a {@link Decision}.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Rule {

  /**
   * This is the name of the Rule.
   * @return  the name of the rule
   */
  String name() default "None";

  /**
   * This specifies the order in which the rule will execute.
   * The ordering is 1, 2, 3, 4, etc.
   * Two rules in the same package with the same order will execute in a non-specified order.
   * @return the order of execution
   */
  int order() default 1;
}
