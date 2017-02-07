# RuleBook
**A Simple Rules Abstraction for Java8+** 

-----------------

### Why Another Rules Abstraction?
Rules engines like Drools are more than many projects need. The format of the rules is also very specialized. And how rules are evaulated is not entirely straightforward. Other rules engines add a bunch of annotations or specialized requirements that can be foreign to many Java developers. That's why RuleBook is a dead simple, 100% Java rules abstraction without the mountain of special annotations or other specialized knowledge required by other [simple?] rules abstractions. It also executes rules in the order in which they are specified (ALWAYS!). 

RuleBook rules are built in the way that Java developers think: Java code. And they are executed in the way that programmers expect: In order. Tired of classes filled with if/then/else statements? Need a nice abstraction that allows rules to be easily specified in way that decouples rules from eachother? Want to write rules the same way that you write the rest of your code [in Java]? RuleBook just might be the rules abstraction you've been waiting for.

### How Does RuleBook Work?
RuleBook is a rules abstraction based on the Chain of Responsibility pattern. Each rule specified in a RuleBook is chained together in the order in which it is specified. As one rule completes, the next rule is evaluated. Any rule can break the chain by returning RuleState.BREAK from the 'then' Function in the rule.

State in Rules is handled through Facts. A Fact is literally just data that is named and supplied to a Rule or RuleBook _(note: facts added to a RuleBook are applied to all rules in the RuleBook)_. Facts can be both read and written to. So, in that way, facts can be used to evaluate state at the completion of a RuleBook execution and they can also be used to pass data into a Rule or RuleBook.

A special type of Rule called a Decision accepts Facts of one type and can store a Result of a different type. This works nicely when there are several different inputs all of the same type and there is a need to distill those inputs down to a different type. Similar to how RuleBooks chain rules together, DecisionBooks chain Decisions together. And since a Decision is really just a special type of rule, DecisionBooks can also chain Rules and Decisions togehter. An example below illustrates the use of Facts and Results and Decisions can be used to create a Result based on the input of several Facts.

### Using RuleBook
**A HelloWorld Example**
```java
public class ExampleRuleBook extends RuleBook {
  public void defineRules() {
    //first rule prints "Hello"
    addRule(StandardRule.create().when(f -> true).then(f -> {
      System.out.print("Hello");
      return NEXT; //continue to the next Rule
    });
    
    //second rule prints "World"
    addRule(StandardRule.create().when(f -> true).then(f -> {
      System.out.println("World");
      return BREAK; //it doesn't matter if NEXT or BREAK is returned here since it's the last Rule
    });
  }
}
```
```java
public class ExampleMainClass {
  public static void main(String[] args) {
    RuleBook exampleRuleBook = new ExampleRuleBook();
    exampleRuleBook.run();
  }
}
```
**A HelloWorld Example Using Facts**
```java
public class ExampleRuleBook extends RuleBook<String> {
  public void defineRules() {
    //first rule prints "Hello" value from helloFact
    addRule(StandardRule.create().when(f -> f.containsKey("hello")).then(f -> {
      System.out.print(f.getValue("hello"));
      return NEXT; //continue to the next Rule
    });
    
    //second rule prints "World" value from worldFact
    addRule(StandardRule.create().when(f -> f.containsKey("world")).then(f -> {
      System.out.println(f.getValue("world"));
      return BREAK; //it doesn't matter if NEXT or BREAK is returned here since it's the last Rule
    });
  }
}
```
```java
public class ExampleMainClass {
  public static void main(String[] args) {
    Fact<String> helloFact = new Fact<>("hello", "Hello");
    Fact<String> worldFact = new Fact<>("world", "World");
    RuleBook exampleRuleBook = new ExampleRuleBook();
    exampleRuleBook.given(helloFact, worldFact).run();
  }
}
```
**A [Slightly] More Complex Scenario**

_MegaBank issues home loans. Each home loan can have up to 3 applicants. If any of the applicant's credit scores is less than 700 then all of the applicants' available cash on hand must be at least $50,000.00, otherwise the loan is denied._

The two different approaches to implementing these rules are

1. Use a single fact containing a Java Bean that aggregates all of the input and the results
2. Use a different fact for each input and for the result

There are pros and cons to each approach. 
In the first approach, a single fact contains an uber-bean that is all of the input (and the responses). The good thing about this approach is that there is only one type of fact, so generics work well. The downside is that you have to combine everything into a single object.
In the second approach, each input is a discrete object. However, since the result is also a fact, there are multiple types of facts, which means that a cast is necessary when retrieving a fact from the FactMap. Since we've been using Java before generics were even a thing (in Java 1.5), we've decided to trade syntactic elegance for independent objects representing independent facts. ~~Perhaps in future version, there will be a more elegant way to handle facts of different types and/or results as facts.~~ _A more elegant way of handling facts and return types of different types is coming soon with **Decision** objects_
