package com.deliveredtechnologies.rulebook.annotation;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Given defines a field that is hydrated from a {@link Fact}.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Given {

  /**
   * This method gets the name of the Fact in the {@link FactMap}.
   */
  String value() default "";
}
