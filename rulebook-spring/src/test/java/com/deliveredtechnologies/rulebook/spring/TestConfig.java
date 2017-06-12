package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.StandardDecision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.InvalidClassException;

/**
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
  public RuleBookBean ruleBookBean()  {
    RuleBookBean ruleBookBean = new RuleBookBean();
    try {
      ruleBookBean.addRule(_context.getBean(SpringRuleWithResult.class));
    } catch (InvalidClassException e) {
      e.printStackTrace();
    }
    ruleBookBean.addRule(StandardDecision.create(String.class, String.class)
        .when(factMap -> true)
        .then((factMap, result) -> {
          result.setValue("SecondRule");
        }));
    return ruleBookBean;
  }

  @Bean
  public RuleBookFactoryBean ruleBookWithResult() {
    return new RuleBookFactoryBean("com.deliveredtechnologies.rulebook.spring");
  }

  @Bean
  public RuleBookRunnerFactoryBean ruleBookFactoryWithResult() {
    return new RuleBookRunnerFactoryBean("com.deliveredtechnologies.rulebook.spring");
  }
}
