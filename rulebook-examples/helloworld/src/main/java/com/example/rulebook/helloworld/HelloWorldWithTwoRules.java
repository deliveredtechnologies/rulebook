package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;

public class HelloWorldWithTwoRules {
  public static void main(String args[]) {
    RuleBook ruleBook = RuleBookBuilder.create()
      .addRule(rule -> rule.withNoSpecifiedFactType().then(f -> System.out.print("Hello ")))
      .addRule(rule -> rule.withNoSpecifiedFactType().then(f -> System.out.println("World")))
      .build();
    ruleBook.run(new FactMap());

    //establish the facts
    NameValueReferableMap factMap = new FactMap();
    factMap.setValue("hello", "Hello");
    factMap.setValue("world", "World");

    //run the RuleBook!
    ruleBook.run(factMap);
  }
}
