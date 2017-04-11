package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by clong on 2/23/17.
 * RuleBean is an annotation for Spring enabled POJO rules.
 * This should be used to annotate POJOs that can be converted to Rules using
 * {@link com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner} and that are expected to be injected
 * using Spring.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Rule
@Component
@Scope("prototype")
public @interface RuleBean { }
