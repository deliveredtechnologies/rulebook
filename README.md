[RuleBook-Spring Maven Central]:http://search.maven.org/#artifactdetails|com.deliveredtechnologies|rulebook-spring|0.3|
[RuleBook-Core Maven Central]:http://search.maven.org/#artifactdetails|com.deliveredtechnologies|rulebook-core|0.3|
[Apache 2.0 License]:https://opensource.org/licenses/Apache-2.0

# RuleBook <img src="https://github.com/Clayton7510/RuleBook/blob/master/LambdaBook.png" height="100" align="left"/>
**&raquo; A Simple &amp; Intuitive Rules Abstraction for Java** <br/><sub> _100% Java_ &middot; _Lambda Enabled_ &middot; _Simple, Intuitive DSL_ &middot; _Lightweight_ </sub>

---

<sub>&nbsp;[How It Works](#how-does-rulebook-work) &raquo; [Using RuleBook](#using-rulebook) &raquo; [POJO Rules](#pojo-rules) &raquo; [RuleBook with Spring](#using-rulebook-with-spring) &raquo; [Contributing](#want-to-contribute)</sub>

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)][Apache 2.0 License] [![Maven Central](https://img.shields.io/badge/maven%20central-0.3-brightgreen.svg)][RuleBook-Core Maven Central] [![Build Status](https://travis-ci.org/Clayton7510/RuleBook.svg?branch=master&maxAge=600)](https://travis-ci.org/Clayton7510/RuleBook) [![Coverage Status](https://coveralls.io/repos/github/Clayton7510/RuleBook/badge.svg?branch=master)](https://coveralls.io/github/Clayton7510/RuleBook?branch=master)  [![Gitter](https://badges.gitter.im/RuleBook.svg)](https://gitter.im/RuleBook?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


**Current Maven Releases**

|Description | Group                   | Artifact |  Version      |Repository |
|---         |---                      |---       |---            |---        |
|Core RuleBook Functionality |com.deliveredtechnologies|rulebook-core  |0.3 |[![Maven Central](https://img.shields.io/badge/maven%20central-0.3-brightgreen.svg)][RuleBook-Core Maven Central]|
|Spring Support for RuleBook |com.deliveredtechnologies|rulebook-spring  |0.3 |[![Maven Central](https://img.shields.io/badge/maven%20central-0.3-brightgreen.svg)][RuleBook-Spring Maven Central]|

**Current Maven _Development_ Releases** &raquo; For those who like to live dangerously...

| Group                   | Artifact |  Version      |Repository |
|---                      |---       |---            |---        |
|com.deliveredtechnologies|rulebook-core  |0.3.1-SNAPSHOT |[![Sonatype Nexus](https://img.shields.io/badge/sonatype-SNAPSHOT-green.svg)](https://oss.sonatype.org/content/repositories/snapshots/)|
|com.deliveredtechnologies|rulebook-spring  |0.3.1-SNAPSHOT |[![Sonatype Nexus](https://img.shields.io/badge/sonatype-SNAPSHOT-green.svg)](https://oss.sonatype.org/content/repositories/snapshots/)|

### Why Another Rules Abstraction?
Rules engines like Drools are more than many projects need. The format of the rules is also very specialized. And how rules are evaulated is not entirely straightforward. Other rules engines have other specialized requirements that can be foreign to many Java developers. That's why RuleBook is a dead simple, 100% Java rules abstraction without the mountain of specialized knowledge required by other [simple?] rules abstractions. It also executes rules in the order in which they are specified (ALWAYS!). 

RuleBook rules are built in the way that Java developers think: Java code. And they are executed in the way that programmers expect: In order. Tired of classes filled with if/then/else statements? Need a nice abstraction that allows rules to be easily specified in way that decouples them from each other? Want to write rules the same way that you write the rest of your code [in Java]? RuleBook just might be the rules abstraction you've been waiting for.

### How Does RuleBook Work?
RuleBook is a rules abstraction based on the Chain of Responsibility pattern. Each rule specified in a RuleBook is chained together in the order in which it is specified. As one rule completes, the next rule is evaluated. Any rule can break the chain by returning RuleState.BREAK from the 'then' Function in the rule.

State in Rules is handled through Facts. A Fact is literally just data that is named and supplied to a Rule or RuleBook _(note: facts added to a RuleBook are applied to all rules in the RuleBook)_. Facts can be both read and written to. So, in that way, facts can be used to evaluate state at the completion of a RuleBook execution and they can also be used to pass data into a Rule or RuleBook.

A special type of Rule called a Decision accepts Facts of one type and can store a Result of a different type. This works nicely when there are several different inputs all of the same type and there is a need to distill those inputs down to a different return type. Similar to how RuleBooks chain rules together, DecisionBooks chain Decisions together. And since a Decision is really just a special type of rule, DecisionBooks can also chain Rules and Decisions togehter. An example below illustrates how Rules and Decisions can be used together to create a Result based on the input of several Facts.

<sub>[[Top](#rulebook-)]</sub>

### Using RuleBook
**A HelloWorld Example**
```java
public class ExampleRuleBook extends RuleBook {
  @Override
  public void defineRules() {
    //first rule prints "Hello"
    addRule(StandardRule.create().when(f -> true).then(f -> {
      System.out.print("Hello");
      return NEXT; //continue to the next Rule
    }));
    
    //second rule prints "World"
    addRule(StandardRule.create().when(f -> true).then(f -> {
      System.out.println("World");
      return BREAK; //it doesn't matter if NEXT or BREAK is returned here since it's the last Rule
    }));
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
  @Override
  public void defineRules() {
    //first rule prints "Hello" value from helloFact
    addRule(StandardRule.create().when(f -> f.containsKey("hello")).then(f -> {
      System.out.print(f.getValue("hello"));
      return NEXT; //continue to the next Rule
    }));
    
    //second rule prints "World" value from worldFact
    addRule(StandardRule.create().when(f -> f.containsKey("world")).then(f -> {
      System.out.println(f.getValue("world"));
      return BREAK; //it doesn't matter if NEXT or BREAK is returned here since it's the last Rule
    }));
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

    //if everyone has a credit score of 700 or more then the loan is approved
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
    decisionBook.withDefaultResult(false)
      .given(
        new Fact("applicant1", new ApplicantBean(699, BigDecimal.valueOf(199))),
        new Fact("applicant2", new ApplicantBean(701, BigDecimal.valueOf(51000))))
      .run();

    System.out.println(decisionBook.getResult() ? "Loan Approved!" : "Loan Denied!");
  }
}
```
In the above example, the default Result value was initialized to false. So, unless a Decision set the result to something else, the result of running the DecisionBook would be false. And unfortunately, for these applicants, they just didn't meet the requirements for a loan at MegaBank as determined by the rules.

One interesting thing about the HomeLoanDecisionBook is that Rules and Decisions were mixed in together. Why? Well, in this case, the requirement that there be no more than 3 applicants can disqualify an application immediately without having to change the default return value. And since a Rule is really a Decision that doesn't update the return value, using a Rule to specify the 3 applicants or less requirement works well.

<sub>[[Top](#rulebook-)]</sub>

### _POJO Rules_

As of RuleBook v0.2, POJO rules are supported. Simply define your rules as annotated POJOs in a package and then use _RuleBookRunner_ to scan the package for rules and create a RuleBook out of them. It's that simple!

**A Hello World Example**

```java
package com.example.pojorules;

import com.deliveredtechnologies.rulebook.annotations.*;
import com.deliveredtechnologies.rulebook.RuleState;

@Rule
public class HelloWorld {

  @Given("hello")
  private String hello;
  
  @Given("world")
  private String world;
  
  @Result
  private String helloworld;
  
  @When
  public boolean when() {
    return true;
  }
  
  @Then
  public RuleState then() {
    helloworld = hello + " " + world;
    return RuleState.BREAK;
  }
}
```
```java

public static void main(String args[]) {
  RuleBookRunner ruleBook = new RuleBookRunner("com.example.pojorules");
  ruleBook.given(new Fact("hello", "Hello"), new Fact("world", "World")).run();
  System.out.println(ruleBook.getResult()); //prints "Hello World"
}
```

**The MegaBank Example With POJOs**

```java
@Rule(order = 1) //order specifies the order the rule should execute in; if not specified, any order may be used
public class ApplicantNumberRule {
  @Given
  private List<ApplicantBean> applicants; //Annotated Lists get injected with all Facts of the declared generic type

  @When
  public boolean when() {
    return applicants.size() > 3;
  }

  @Then
  public RuleState then() {
    return RuleState.BREAK;
  }
}
```
```java
@Rule(order = 2)
public class CreditScoreRule {
  @Given
  private List<ApplicantBean> applicants;

  @Result
  private boolean approved;
    
  @When
  public boolean when() {
    return applicants.stream()
      .allMatch(applicant -> applicant.getCreditScore() >= 700);
  }

  @Then
  public RuleState then() {
    approved = true;
    return RuleState.NEXT;
  }
}
```
```java
@Rule(order = 3)
public class CashOnHandRule {
  @Given
  List<ApplicantBean> applicants; 

  @Result
  private boolean approved;

  @When
  public boolean when() {
    return applicants.stream()
      .allMatch(applicant -> applicant.getCashOnHand().compareTo(BigDecimal.valueOf(50000)) >= 0);
  }

  @Then
  public RuleState then() {
    approved = true;
    return RuleState.BREAK;
  }
}
```
```java
public static void main(String[] args) {
  RuleBookRunner ruleBook = new RuleBookRunner("com.example.rulebook");
  ruleBook.withDefaultResult(false)
    .given(
      new Fact("applicant1", new ApplicantBean(699, BigDecimal.valueOf(199))),
      new Fact("applicant2", new ApplicantBean(701, BigDecimal.valueOf(51000))))
    .run();
  boolean approval = (boolean)ruleBook.getResult();
  System.out.println("Application is " + (approval ? "approved!" : "not approved!"));
}
```

_Some Important Things to Note About POJOs..._
* The order property on the @Rule annotation groups the order that rules are executed in.
* Only Lists are injected with multiple Facts based on the type of the Facts.
* If the object type of a Fact is declared as a @Given, its state won't be changed outsite the instance of the POJO rule.
* If a Fact is declared as a @Given, any state changes made in the POJO rule instance seen after the rule completes.
* The when() and then() methods don't have to be declared when() and then(), they just have to be annotated.
* The annotated @When method must have no arguments and it must return a boolean result.
* The annotated @Then method must have no arguments and it must return a RuleState result.

<sub>[[Top](#rulebook-)]</sub>

###_Using RuleBook with Spring_

RuleBooks in Spring can be created using Spring configurations with RuleBookBean classes. RuleBookBean classes should be scoped as prototype and they can add either rules created through the RuleBook DSL or Spring enabled POJO rules. And creating a Spring enabled POJO rule couldn't be easier; just create a POJO rule, but instead of using @Rule, use @RuleBean.

**Creating a Spring Enabled POJO Rule**
```java
@RuleBean
public class HelloSpringRule {
  @Given
  private String hello;
  
  @Result
  private String result;
  
  @When
  public boolean when() {
    return hello.equalsIgnoreCase("Hello");
  }
  
  @Then
  public RuleState then() {
    result = "Hello ";
    return RuleState.NEXT;
  }
}
```

**Configuring a RuleBook in Spring**
```java
@Configuration
public class SpringConfig {
  @Autowired
  private ApplicationContext context;
  
  @Bean
  @Scope("prototype")
  public RuleBookBean ruleBookBean() throws InvalidClassException {
    RuleBookBean ruleBookBean = new RuleBookBean();
    ruleBookBean.addRule(context.getBean(HelloSpringRule.class)); //add a Spring enabled POJO rule
    ruleBookBean.addRule(StandardDecision.create()
      .when(factMap -> factMap.getValue("world").equalsIgnoreCase("World"))
      .then((factMap, result) -> {
        result += "World";
        return RuleState.BREAK;
      });
    return ruleBookBean;
  }
}
```

**Using a Spring Enabled RuleBook**
```java
  @Autowired
  private ApplicationContext context;
  
  public void someMethod() {
    RuleBookBean ruleBook = context.getBean(RuleBookBean.class));
    ruleBook.given(new Fact("hello"), new Fact("hello")).run(); 
    System.out.println(ruleBook.getResult()); //prints "Hello World"
  }
```

<hr/>

<sub>[[Top](#rulebook-)]</sub>

### _Want to Contribute?_

Suggestions and code contributions are welcome! Please see the _Developer Guidelines_ below.

**_Developer Guidelines_**

Contributions must adhere to the following criteria:

1. The forked repository must be publicly visible.
2. The issues addressed in the request must be associated to an accepted issue.
3. The build (i.e. ./gradlew build) must pass with no errors or warnings.
4. All new and existing tests must pass.
5. The code must adhere to the style guidleines icluded in the checkstyle configuration (i.e. no checkstyle errors).
6. Newly introduced code must have at least 85% test coverage.
7. Pull requests must be for the _develop_ branch.
8. The version number in gradle.properties should match the milestone of the issue its associated with appended with _-SNAPSHOT_ (ex. 0.2-SNAPSHOT)

Anyone may submit an issue, which can be either an enhancement/feature request or a bug to be remediated. If a feature request or a bug is approved, completed and an associated pull request is submitted that adheres to the above criteria, then the pull request will be merged and the contributor will be added to the list of contributors in the following release.

<sub>[[Top](#rulebook-)]</sub>
