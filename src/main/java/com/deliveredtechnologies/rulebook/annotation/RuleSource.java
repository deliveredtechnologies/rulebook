package com.deliveredtechnologies.rulebook.annotation;

import com.deliveredtechnologies.rulebook.Decision;
import com.deliveredtechnologies.rulebook.Rule;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by clong on 2/12/17.
 * RuleSource defines a class to be injected into a {@link Rule} or {@link Decision}.
 */
@Target(TYPE)
@Retention(RUNTIME)
@interface RuleSource {

  /**
   * This is the class type of the Rule (e.g. StandardRule, StandardDecision)
   * @return  the type of the rule to be used
   */
  String type();

  /**
   * This is the name of the Rule.
   * @return  the name of the rule
   */
  String name();
}
