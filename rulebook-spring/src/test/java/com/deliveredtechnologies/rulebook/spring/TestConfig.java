package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.StandardDecision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by clong on 2/27/17.
 * Test configuration for rulebook-spring.
 */
@Configuration
@ComponentScan("com.deliveredtechnologies.rulebook.spring")
public class TestConfig {

  @Autowired
  private ApplicationContext _context;

  /**
   * Creates a RuleBookBean; DSL rules and RuleBean POJO rules can be mixed together.
   * @return  RuleBean prototype
   */
  @Bean
  @Scope("prototype")
  public RuleBookBean ruleBookBean() {
    RuleBookBean ruleBookBean = new RuleBookBean();
    ruleBookBean.addRule(_context.getBean(SpringRuleWithResult.class));
    ruleBookBean.addRule(StandardDecision.create(String.class, String.class)
        .when(factMap -> true)
        .then((factMap, result) -> {
            result.setValue("SecondRule");
            return RuleState.BREAK;
          }));
    return ruleBookBean;
  }
}
