package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;
import com.deliveredtechnologies.rulebook.spring.SpringAwareRuleBookRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackages = "com.example.rulebook.helloworld")
@Configuration
public class SpringConfig {
  @Bean
  public RuleBook ruleBook() {
    RuleBook ruleBook = new SpringAwareRuleBookRunner("com.example.rulebook.helloworld");
    return ruleBook;
  }
}
