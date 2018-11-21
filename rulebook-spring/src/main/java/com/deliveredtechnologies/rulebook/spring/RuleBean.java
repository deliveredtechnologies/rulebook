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
 * RuleBean is an annotation that allows POJO rules to be Spring aware.
 * This should be used to annotate POJOs that can be converted to Rules using
 * {@link com.deliveredtechnologies.rulebook.spring.SpringAwareRuleBookRunner} and that are expected to be injected
 * using Spring.
 *
 * Alternatively, the annotations below could be added to a POJO Rule.
 * The annotations below are required to properly scope Spring POJO Rules.
 * This annotation exists as a simplification and because there is no way to extend Java annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Scope("prototype")
public @interface RuleBean { }
