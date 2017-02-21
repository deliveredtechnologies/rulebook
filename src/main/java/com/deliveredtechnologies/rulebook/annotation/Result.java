package com.deliveredtechnologies.rulebook.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by clong on 2/12/17.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Result {

}
