package com.deliveredtechnologies.rulebook.model.runner.test.rulebooks.subpkg;

import com.deliveredtechnologies.rulebook.annotation.Rule;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by clong on 3/14/17.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Rule
public @interface SubRuleAnnotation {
}
