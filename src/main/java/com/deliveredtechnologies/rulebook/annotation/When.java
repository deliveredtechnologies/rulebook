package com.deliveredtechnologies.rulebook.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by clong on 2/12/17.
 * When marks a method as a condition.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface When {

}
