[RuleBook-Spring Maven Central]:http://search.maven.org/#artifactdetails|com.deliveredtechnologies|rulebook-spring|0.3.4|
[RuleBook-Core Maven Central]:http://search.maven.org/#artifactdetails|com.deliveredtechnologies|rulebook-core|0.3.4|
[Apache 2.0 License]:https://opensource.org/licenses/Apache-2.0

# RuleBook <img src="https://github.com/Clayton7510/RuleBook/blob/master/LambdaBook.png" height="100" align="left"/>
**&raquo; A Simple &amp; Intuitive Rules Abstraction for Java** <br/><sub> _100% Java_ &middot; _Lambda Enabled_ &middot; _Simple, Intuitive DSL_ &middot; _Lightweight_ </sub>

---

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)][Apache 2.0 License] [![Maven Central](https://img.shields.io/badge/maven%20central-0.3.4-brightgreen.svg)][RuleBook-Core Maven Central] [![Build Status](https://travis-ci.org/Clayton7510/RuleBook.svg?branch=master&maxAge=600)](https://travis-ci.org/Clayton7510/RuleBook) [![Coverage Status](https://coveralls.io/repos/github/Clayton7510/RuleBook/badge.svg?branch=master)](https://coveralls.io/github/Clayton7510/RuleBook?branch=master)  [![Gitter](https://badges.gitter.im/RuleBook.svg)](https://gitter.im/RuleBook?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

## Why RuleBook?
RuleBook rules are built in the way that Java developers think: Java code. And they are executed in the way that programmers expect: In order. RuleBook also allows you to specify rules using an easy to use Lambda enabled Domain Specific Language or using POJOs that you define!

Tired of classes filled with if/then/else statements? Need a nice abstraction that allows rules to be easily specified in way that decouples them from each other? Want to write rules the same way that you write the rest of your code [in Java]? RuleBook just might be the rules abstraction you've been waiting for!

#### Contents

* [1 Getting RuleBook](#1-getting-rulebook)
  * [1.1 Building RuleBook](##11-building-rulebook)
  * [1.2 Maven Central Releases](#12-maven-central-releases)
  * [1.3 Latest SNAPSHOT Releases](#13-latest-sonatype-snapshot-development-release)
  * [1.4 Adding RuleBook to Your Maven Project](#14-adding-rulebook-to-your-maven-project)
  * [1.5 Adding RuleBook to Your Gradle Project](#15-adding-rulebook-to-your-gradle-project)
* [2 Using RuleBook](#2-using-rulebook)
  * [2.1 Using the Java Domain Specific Language](#21-a-helloworld-example-using-the-java-domain-specific-language)
  * [2.2 Using Facts](#22-the-above-example-using-facts)
  * [2.3 A \[Slightly\] More Complex Scenario](#23-a-slightly-more-complex-scenario)
* [3 POJO Rules](#3-pojo-rules)
  * [3.1 A POJO Rules Example](#31-a-hello-world-example)
  * [3.2 A \[Slightly\] More Complex POJO Rules Example](#32-the-megabank-example-with-pojo-rules)
* [4 Using RuleBook with Spring](#4-using-rulebook-with-spring)
  * [4.1 Creating a Spring Enabled POJO Rule](#41-creating-a-spring-enabled-pojo-rule)
  * [4.2 Configuring a RuleBook in Spring](#42-configuring-a-rulebook-in-spring)
  * [4.3 Using a Spring Enabled RuleBook](#43-using-a-spring-enabled-rulebook)
* [5 How to Contribute](#5-how-to-contribute)
  * [5.1 Developer Guidelines](#51-developer-guidelines)
  
## 1 Getting RuleBook

### 1.1 Building RuleBook

```bash
git clone https://github.com/Clayton7510/RuleBook.git
cd RuleBook
./gradlew build
```

### 1.2 Maven Central Releases

* rulebook-core &nbsp;&nbsp;&nbsp;[![Maven Central](https://img.shields.io/badge/maven%20central-0.3.4-brightgreen.svg)][RuleBook-Core Maven Central]
* rulebook-spring [![Maven Central](https://img.shields.io/badge/maven%20central-0.3.4-brightgreen.svg)][RuleBook-Spring Maven Central]

### 1.3 Latest Sonatype SNAPSHOT (Development) Release

* rulebook-core &nbsp;&nbsp;&nbsp;[![Sonatype Nexus](https://img.shields.io/badge/SNAPSHOT-0.3.5-green.svg)](https://oss.sonatype.org/content/repositories/snapshots/)
* rulebook-spring [![Sonatype Nexus](https://img.shields.io/badge/SNAPSHOT-0.3.5-green.svg)](https://oss.sonatype.org/content/repositories/snapshots/)

### 1.4 Adding RuleBook to Your Maven Project

_Add the code below to your pom.xml_

```xml
<dependency>
    <groupId>com.deliveredtechnologies</groupId>
    <artifactId>rulebook-core</artifactId>
    <version>0.3.4</version>
</dependency>
```

### 1.5 Adding RuleBook to Your Gradle Project

_Add the code below to your build.gradle_

```groovy
compile 'com.deliveredtechnologies:rulebook-core:0.3.4'
```

<sub>[Top](#contents)</sub>

## 2 Using RuleBook
### 2.1 A HelloWorld Example Using the Java Domain Specific Language
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
### 2.2 The Above Example Using Facts
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
### 2.3 A [Slightly] More Complex Scenario

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

<sub>[[Top](#contents)]</sub>

## 3 POJO Rules

As of RuleBook v0.2, POJO rules are supported. Simply define your rules as annotated POJOs in a package and then use _RuleBookRunner_ to scan the package for rules and create a RuleBook out of them. It's that simple!

### 3.1 A Hello World Example

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

### 3.2 The MegaBank Example With POJO Rules

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

<sub>[[Top](#contents)]</sub>

## 4 Using RuleBook with Spring

RuleBooks in Spring can be created using Spring configurations with RuleBookBean classes. RuleBookBean classes should be scoped as prototype and they can add either rules created through the RuleBook DSL or Spring enabled POJO rules. And creating a Spring enabled POJO rule couldn't be easier; just create a POJO rule, but instead of using @Rule, use @RuleBean.

### 4.1 Creating a Spring Enabled POJO Rule

```java
@RuleBean
public class HelloSpringRule {
  @Given("hello")
  private String hello;
  
  @Result
  private String result;
  
  @When
  public boolean when() {
    return hello != null;
  }
  
  @Then
  public RuleState then() {
    result = hello + " ";
    return RuleState.NEXT;
  }
}
```

### 4.2 Configuring a RuleBook in Spring

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
      .when(factMap -> factMap.containsKey("world"))
      .then((factMap, result) -> {
        result += factMap.getValue("world");
        return RuleState.BREAK;
      });
    return ruleBookBean;
  }
}
```

### 4.3 Using a Spring Enabled RuleBook

```java
  @Autowired
  private ApplicationContext context;
  
  public void someMethod() {
    RuleBookBean ruleBook = context.getBean(RuleBookBean.class));
    ruleBook.given(new Fact("hello", "Hello"), new Fact("world", "World")).run(); 
    System.out.println(ruleBook.getResult()); //prints "Hello World"
  }
```

<sub>[[Top](#contents)]</sub>

## 5 How to Contribute

Suggestions and code contributions are welcome! Please see the _Developer Guidelines_ below.

### 5.1 Developer Guidelines

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

<sub>[[Top](#contents)]</sub>
