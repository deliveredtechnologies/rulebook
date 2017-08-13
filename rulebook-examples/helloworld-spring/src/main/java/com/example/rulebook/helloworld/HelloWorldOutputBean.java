package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldOutputBean {
  @Autowired
  private RuleBook ruleBook;

  public void printResult() {
    NameValueReferableMap<String> facts = new FactMap<>();
    facts.setValue("hello", "Hello ");
    facts.setValue("world", "World");
    ruleBook.run(facts);
    ruleBook.getResult().ifPresent(System.out::println); //prints Hello World
  }
}
