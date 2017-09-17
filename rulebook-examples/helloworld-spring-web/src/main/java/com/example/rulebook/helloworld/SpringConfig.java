package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
  @Bean
  public RuleBook ruleBook() {
    RuleBook ruleBook = new RuleBookRunner("com.example.rulebook.helloworld");
    return ruleBook;
  }
}
