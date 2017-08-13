package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;

public class Application {
  public static void main(String args[]) {
    RuleBookRunner ruleBook = new RuleBookRunner("com.example.rulebook.helloworld");
    NameValueReferableMap facts = new FactMap();
    facts.setValue("hello", "Hello");
    facts.setValue("world", "World");
    ruleBook.run(facts);
    ruleBook.getResult().ifPresent(System.out::println); //prints "Hello World"
  }
}
