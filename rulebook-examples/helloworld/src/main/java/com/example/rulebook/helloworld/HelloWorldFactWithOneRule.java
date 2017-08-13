package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;

public class HelloWorldFactWithOneRule {
  public static void main(String args[]) {
    RuleBook ruleBook = RuleBookBuilder.create()
      .addRule(rule -> rule.withFactType(String.class)
        .when(f -> f.containsKey("hello") && f.containsKey("world"))
        .using("hello").then(System.out::print)
        .using("world").then(System.out::println))
      .build();

    //establish the facts
    NameValueReferableMap factMap = new FactMap();
    factMap.setValue("hello", "Hello ");
    factMap.setValue("world", "World");

    //run the RuleBook!
    ruleBook.run(factMap);
  }
}
