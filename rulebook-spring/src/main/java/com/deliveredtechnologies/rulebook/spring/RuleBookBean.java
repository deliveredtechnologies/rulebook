package com.deliveredtechnologies.rulebook.spring;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by clong on 2/23/17.
 */
@Component
@Scope("prototype")
public @interface RuleBookBean {
}
