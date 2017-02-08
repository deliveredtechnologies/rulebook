# RuleBook
**A Simple Rules Abstraction for Java8+**  

-----------------

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Build Status](https://travis-ci.org/Clayton7510/RuleBook.svg?branch=master)](https://travis-ci.org/Clayton7510/RuleBook) [![Coverage Status](https://coveralls.io/repos/github/Clayton7510/RuleBook/badge.svg?branch=master)](https://coveralls.io/github/Clayton7510/RuleBook?branch=master)

### Why Another Rules Abstraction?
Rules engines like Drools are more than many projects need. The format of the rules is also very specialized. And how rules are evaulated is not entirely straightforward. Other rules engines add a bunch of annotations or specialized requirements that can be foreign to many Java developers. That's why RuleBook is a dead simple, 100% Java rules abstraction without the mountain of special annotations or other specialized knowledge required by other [simple?] rules abstractions. It also executes rules in the order in which they are specified (ALWAYS!). 

RuleBook rules are built in the way that Java developers think: Java code. And they are executed in the way that programmers expect: In order. Tired of classes filled with if/then/else statements? Need a nice abstraction that allows rules to be easily specified in way that decouples them from eachother? Want to write rules the same way that you write the rest of your code [in Java]? RuleBook just might be the rules abstraction you've been waiting for.

### How Does RuleBook Work?
RuleBook is a rules abstraction based on the Chain of Responsibility pattern. Each rule specified in a RuleBook is chained together in the order in which it is specified. As one rule completes, the next rule is evaluated. Any rule can break the chain by returning RuleState.BREAK from the 'then' Function in the rule.

State in Rules is handled through Facts. A Fact is literally just data that is named and supplied to a Rule or RuleBook _(note: facts added to a RuleBook are applied to all rules in the RuleBook)_. Facts can be both read and written to. So, in that way, facts can be used to evaluate state at the completion of a RuleBook execution and they can also be used to pass data into a Rule or RuleBook.

A special type of Rule called a Decision accepts Facts of one type and can store a Result of a different type. This works nicely when there are several different inputs all of the same type and there is a need to distill those inputs down to a different return type. Similar to how RuleBooks chain rules together, DecisionBooks chain Decisions together. And since a Decision is really just a special type of rule, DecisionBooks can also chain Rules and Decisions togehter. An example below illustrates how Rules and Decisions can be used together to create a Result based on the input of several Facts.

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

This type of problem lends itself well to Decisions. As stated above, Decsisions accept one type of Fact and return a different type of Result. In this case, the Facts are applicant information for each applicant and the Result is whether the loan is approved or denied. The following code example shows how the rules for this scenario can be implemeted.

```java
public class ApplicantBean {
  private int creditScore;
  private BigDecimal cashOnHand;

  public ApplicantBean(int creditScore, BigDecimal cashOnHand) {
    this.creditScore = creditScore;
    this.cashOnHand = cashOnHand;
  }

  public int getCreditScore() {
    return creditScore;
  }

  public void setCreditScore(int creditScore) {
    this.creditScore = creditScore;
  }

  public BigDecimal getCashOnHand() {
    return cashOnHand;
  }

  public void setCashOnHand(BigDecimal cashOnHand) {
    this.cashOnHand = cashOnHand;
  }
}
```
```java
public class HomeLoanDecisionBook extends DecisionBook<ApplicantBean, Boolean> {


  @Override
  protected void defineRules() {
  
    //if there are more than 3 applicants then the loan is denied
    addRule(StandardRule.create(ApplicantBean.class)
      .when(factMap -> factMap.size() > 3)
      .then(f -> BREAK)
    );

    //if everyone has a credit score of over 700 then the loan is approved
    addRule(StandardDecision.create(ApplicantBean.class, Boolean.class)
      .when(factMap -> factMap.values().stream()
        .allMatch(applicantFact -> applicantFact.getValue().getCreditScore() >= 700))
      .then((f, result) -> {
        result.setValue(true);
        return NEXT;
      })
    );

    //if everyone has cash on hand of greater than or equal to $50,000 then the loan is approved
    addRule(StandardDecision.create(ApplicantBean.class, Boolean.class)
      .when(factMap -> factMap.values().stream()
        .allMatch(applicantFact -> applicantFact.getValue().getCashOnHand().compareTo(BigDecimal.valueOf(50000)) >= 0))
      .then((f, result) -> {
        result.setValue(true);
        return BREAK;
      })
    );
  }
}
```
```java
public class ExampleSolution {
  public static void main(String[] args) {
    HomeLoanDecisionBook decisionBook = new HomeLoanDecisionBook();
    decisionBook.withDeafultResult(false)
      .given(
        new Fact("applicant1", new ApplicantBean(699, BigDecimal.valueOf(199))),
        new Fact("applicant2", new ApplicantBean(701, BigDecimal.valueOf(51000))))
      .run();

    System.out.println(decisionBook.getResult() ? "Loan Approved!" : "Loan Denied!");
  }
```
In the above example, the default Result value was initialized to false. So, unless a Decision set the result to something else, the result of running the DecisionBook would be false. And unfortunately, for these applicants, they just didn't meet the requirements for a loan at MegaBank as determined by the rules.

One interesting thing about the HomeLoanDecisionBook is that Rules and Decisions were mixed in together. Why? Well, in this case, the requirement that there be no more than 3 applicants can disqualify an application immediately without having to change the default return value. And since a Rule is really a Decision that doesn't update the return value, using a Rule to specify the 3 applicants or less requirement works well.
