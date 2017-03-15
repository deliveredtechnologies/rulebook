package com.deliveredtechnologies.rulebook.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Result is used to map a single field to a Result in a {@link com.deliveredtechnologies.rulebook.Decision}.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Result {

}
