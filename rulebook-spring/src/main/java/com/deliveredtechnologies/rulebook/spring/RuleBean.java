package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by clong on 2/23/17.
 */
@Rule
@Component
@Scope("prototype")
public @interface RuleBean {
}
