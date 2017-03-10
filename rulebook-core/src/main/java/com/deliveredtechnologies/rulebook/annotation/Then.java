package com.deliveredtechnologies.rulebook.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Then marks a method as an action.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Then {
}
