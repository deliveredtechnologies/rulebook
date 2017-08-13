package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;

public class HelloWorldExample {
  public static void main(String args[]) {
    RuleBook ruleBook = RuleBookBuilder.create()
      .addRule(rule -> rule.withNoSpecifiedFactType()
        .then(f -> System.out.print("Hello "))
        .then(f -> System.out.println("World")))
      .build();

    ruleBook.run(new FactMap());
  }
}
