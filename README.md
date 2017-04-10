[RuleBook-Spring Maven Central]:http://search.maven.org/#artifactdetails|com.deliveredtechnologies|rulebook-spring|0.4|
[RuleBook-Core Maven Central]:http://search.maven.org/#artifactdetails|com.deliveredtechnologies|rulebook-core|0.4|
[Apache 2.0 License]:https://opensource.org/licenses/Apache-2.0

# RuleBook <img src="https://github.com/Clayton7510/RuleBook/blob/master/LambdaBook.png" height="100" align="left"/>
**&raquo; A Simple &amp; Intuitive Rules Abstraction for Java** <br/><sub> _100% Java_ &middot; _Lambda Enabled_ &middot; _Simple, Intuitive DSL_ &middot; _Lightweight_ </sub>

---

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)][Apache 2.0 License] [![Maven Central](https://img.shields.io/badge/maven%20central-0.4-brightgreen.svg)][RuleBook-Core Maven Central] [![Build Status](https://travis-ci.org/rulebook-rules/rulebook.svg?branch=develop&maxAge=600)](https://travis-ci.org/rulebook-rules/rulebook) [![Coverage Status](https://coveralls.io/repos/github/rulebook-rules/rulebook/badge.svg?branch=develop&maxAge=600)](https://coveralls.io/github/rulebook-rules/rulebook?branch=develop)  [![Gitter](https://badges.gitter.im/RuleBook.svg)](https://gitter.im/RuleBook?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

## _Note: The DSL in the RuleBook develop branch has changed significantly; This README will be updated soon to reflect those changes._ 

## Why RuleBook?
RuleBook rules are built in the way that Java developers think: Java code. And they are executed in the way that programmers expect: In order. RuleBook also allows you to specify rules using an easy to use Lambda enabled Domain Specific Language or using POJOs that you define!

Tired of classes filled with if/then/else statements? Need a nice abstraction that allows rules to be easily specified in way that decouples them from each other? Want to write rules the same way that you write the rest of your code [in Java]? RuleBook just might be the rules abstraction you've been waiting for!

#### Contents

* **[1 Getting RuleBook](#1-getting-rulebook)**
  * [1.1 Building RuleBook](#11-building-rulebook)
  * [1.2 Maven Central Releases](#12-maven-central-releases)
  * [1.3 Latest SNAPSHOT Releases](#13-latest-sonatype-snapshot-development-release)
  * [1.4 Adding RuleBook to Your Maven Project](#14-adding-rulebook-to-your-maven-project)
  * [1.5 Adding RuleBook to Your Gradle Project](#15-adding-rulebook-to-your-gradle-project)
* **[2 Using RuleBook](#2-using-rulebook)**
  * [2.1 A Hello World Example](#21-a-helloworld-example)
  * [2.2 An Example Using Facts](#22-the-above-example-using-facts)
  * [2.3 A \[Slightly\] More Complex Scenario](#23-a-slightly-more-complex-scenario)
* **[3 The RuleBook Domain Specific Language](#3-the-java-domain-specific-language-explained)**
  * [3.1 Given-When-Then: The Basis of the RuleBook Language](#31-given-when-then-the-basis-of-the-rulebook-language)
  * [3.2 The Using Method](#32-the-using-method)
  * [3.3 The Stop Method](#33-the-stop-method)
  * [3.4 Working With Facts](#34-working-with-facts)
    * [3.4.1 The Single Fact Convenience Method](#341-the-single-fact-convenience-method)
    * [3.4.2 The FactMap Convenience Methods](#342-the-factmap-convenience-methods)
* **[4 POJO Rules](#4-pojo-rules)**
  * [4.1 A POJO Rules Example](#41-a-hello-world-example)
  * [4.2 A \[Slightly\] More Complex POJO Rules Example](#42-the-megabank-example-with-pojo-rules)
  * [4.3 POJO Rules Explained](#43-pojo-rules-explained)
    * [4.3.1 Ordering POJO Rules](#431-ordering-pojo-rules)
    * [4.3.2 Injecting Collections into POJO Rules](#432-injecting-collections-into-pojo-rules)
    * [4.3.3 POJO Rule Annotation Inheritance](#433-pojo-rule-annotation-inheritance)
* **[5 Using RuleBook with Spring](#5-using-rulebook-with-spring)**
  * [5.1 Adding RuleBook Spring Support to Your Maven Project](#51-adding-rulebook-spring-support-to-your-maven-project)
  * [5.2 Adding RuleBook Spring Support to Your Gradle Project](#52-adding-rulebook-spring-support-to-your-gradle-project)
  * [5.3 Creating a Spring Enabled POJO Rule](#53-creating-a-spring-enabled-pojo-rule)
  * [5.4 Configuring a RuleBook in Spring](#54-configuring-a-rulebook-in-spring)
  * [5.5 Using a Spring Enabled RuleBook](#55-using-a-spring-enabled-rulebook)
  * [5.6 Spring Enabled POJO Rules Explained](#56-spring-enabled-pojo-rules-explained)
* **[6 How to Contribute](#6-how-to-contribute)**
  * [6.1 Developer Guidelines](#61-developer-guidelines)
  
## 1 Getting RuleBook

### 1.1 Building RuleBook

```bash
git clone https://github.com/Clayton7510/RuleBook.git
cd RuleBook
./gradlew build
```

### 1.2 Maven Central Releases

* rulebook-core &nbsp;&nbsp;&nbsp;[![Maven Central](https://img.shields.io/badge/maven%20central-0.4-brightgreen.svg)][RuleBook-Core Maven Central]
* rulebook-spring [![Maven Central](https://img.shields.io/badge/maven%20central-0.4-brightgreen.svg)][RuleBook-Spring Maven Central]

### 1.3 Latest Sonatype SNAPSHOT (Development) Release

* rulebook-core &nbsp;&nbsp;&nbsp;[![Sonatype Nexus](https://img.shields.io/badge/SNAPSHOT-0.5-green.svg)](https://oss.sonatype.org/content/repositories/snapshots/)
* rulebook-spring [![Sonatype Nexus](https://img.shields.io/badge/SNAPSHOT-0.5-green.svg)](https://oss.sonatype.org/content/repositories/snapshots/)

### 1.4 Adding RuleBook to Your Maven Project

_Add the code below to your pom.xml_

```xml
<dependency>
    <groupId>com.deliveredtechnologies</groupId>
    <artifactId>rulebook-core</artifactId>
    <version>0.4</version>
</dependency>
```

### 1.5 Adding RuleBook to Your Gradle Project

_Add the code below to your build.gradle_

```groovy
compile 'com.deliveredtechnologies:rulebook-core:0.4'
```

<sub>[[Top](#contents)]</sub>

## 2 Using RuleBook
### 2.1 A HelloWorld Example
```java
RuleBook ruleBook = RuleBookBuilder.create()
    .addRule(rule -> rule
      .then(f -> System.out.print("Hello "))
      .then(f -> System.out.println("World")))
    .build();
```
**...or use 2 rules**
```java
RuleBook ruleBook = RuleBookBuilder.create()
    .addRule(rule -> rule.then(f -> System.out.print("Hello ")))
    .addRule(rule -> rule.then(f -> System.out.println("World")))
    .build();
```
**now, run it!**
```java
ruleBook.run(new FactMap());
```
### 2.2 The Above Example Using Facts
```java
RuleBook ruleBook = RuleBookBuilder.create()
    .addRule(rule -> rule
      .when(f -> f.containsKey("hello"))
      .using("hello")
      .then(System.out::print))
    .addRule(rule -> rule
      .when(f -> f.containsKey("world"))
      .using("world")
      .then(System.out::print))
    .build();
```
**..or it could be a single rule**
```java
RuleBook ruleBook = RuleBookBuilder.create()
    .addRule(rule -> rule
      .when(f -> f.containsKey("hello") && f.containsKey("world")
      .using("hello").then(System.out::print))
      .using("world").then(System.out::print))
    .build();
```
**now, run it!**
```java
NameValueReferableMap factMap = new FactMap();
factMap.setValue("hello", "Hello");
factMap.setValue("world", " World");
ruleBook.run(factMap);
```
### 2.3 A [Slightly] More Complex Scenario

_MegaBank issues home loans. If an applicant's credit score is less than 600 then they must pay 4x the current rate. If an applicant’s credit score is between 600, but less than 700, then they must pay a an additional point on top of their rate. If an applicant’s credit score is at least 700 and they have at least $25,000 cash on hand, then they get a quarter point reduction on their rate. If an applicant is a first time home buyer then they get a 20% reduction on their calculated rate after adjustments are made based on credit score (note: first time home buyer discount is only available for applicants with a 600 credit score or greater)._

This type of problem lends itself well to Decisions. As stated above, Decsisions accept one type of Fact and return a different type of Result. In this case, the Facts are applicant information for each applicant and the Result is whether the loan is approved or denied. The following code example shows how the rules for this scenario can be implemeted.

```java
public class ApplicantBean {
  private int creditScore;
  private float cashOnHand;
  private boolean firstTimeHomeBuyer;

  public ApplicantBean(int creditScore, float cashOnHand, boolean firstTimeHomeBuyer) {
    this.creditScore = creditScore;
    this.cashOnHand = cashOnHand;
    this.firstTimeHomeBuyer = firstTimeHomeBuyer;
  }

  public int getCreditScore() {
    return creditScore;
  }

  public void setCreditScore(int creditScore) {     
    this.creditScore = creditScore;
  }

  public float getCashOnHand() {
    return cashOnHand;
  }

  public void setCashOnHand(float cashOnHand) {
    this.cashOnHand = cashOnHand;
  }

  public boolean isFirstTimeHomeBuyer() {
    return firstTimeHomeBuyer;
  }

  public void setFirstTimeHomeBuyer(boolean firstTimeHomeBuyer) {
    this.firstTimeHomeBuyer = firstTimeHomeBuyer;
  }
}
```
```java
public class HomeLoanRateDecisionBook extends DecisionBook<ApplicantBean, Float> {
  @Override
  protected void defineRules() {
    //credit score under 600 gets a 4x rate increase
    addRule(StandardDecision.create(ApplicantBean.class, Float.class)
      .when(facts -> facts.getOne().getCreditScore() < 600)
      .then((facts, result) -> result.setValue(result.getValue() * 4f))
      .stop());

    //credit score between 600 and 700 pays a 1 point increase
    addRule(StandardDecision.create(ApplicantBean.class, Float.class)
      .when(facts -> facts.getOne().getCreditScore() < 700)
      .then((facts, result) -> result.setValue(result.getValue() + 1f)));

    //credit score is 700 and they have at least $25,000 cash on hand
    addRule(StandardDecision.create(ApplicantBean.class, Float.class)
      .when(facts -> 
            facts.getOne().getCreditScore() >= 700 &&
            facts.getOne().getCashOnHand() >= 25000f)
      .then((facts, result) -> result.setValue(result.getValue() - 0.25f)));

    //first time homebuyers get 20% off their rate (except if they have a creditScore < 600)
    addRule(StandardDecision.create(ApplicantBean.class, Float.class)
      .when(facts -> facts.getOne().isFirstTimeHomeBuyer())
      .then((facts, result) -> result.setValue(result.getValue() * 0.80f)));
    }
}
```
```java
public class ExampleSolution {
  public static void main(String[] args) {
    HomeLoanRateDecisionBook homeLoanRateDecisionBook = new HomeLoanRateDecisionBook();
    ApplicantBean applicant = new ApplicantBean(650, 20000, true);
    homeLoanRateDecisionBook.withDefaultResult(4.5f).given("applicant", applicant).run();
    
    System.out.println("Applicant qualified for the following rate: " + homeLoanRateDecisionBook.getResult());
  }
}
```
**...or nix the ApplicantBean and just use independent Facts**
```java
public class HomeLoanRateDecisionBook extends DecisionBook {
  @Override
  protected void defineRules() {
    //credit score under 600 gets a 4x rate increase
    addRule(StandardDecision.create(Integer.class, Float.class)
      .when(facts -> facts.getValue("Credit Score" < 600)
      .then((facts, result) -> result.setValue(result.getValue() * 4f))
      .stop());

    //credit score between 600 and 700 pays a 1 point increase
    addRule(StandardDecision.create(Integer.class, Float.class)
      .when(facts -> facts.getValue("Credit Score") < 700)
      .then((facts, result) -> result.setValue(result.getValue() + 1f)));

    //credit score is 700 and they have at least $25,000 cash on hand
    addRule(StandardDecision.create(Object.class, Float.class)
      .when(facts -> 
            facts.getIntValue("Credit Score") >= 700 &&
            facts.getDblVal("Cash on Hand") >= 25000
      .then((facts, result) -> result.setValue(result.getValue() - 0.25f)));

    //first time homebuyers get 20% off their rate (except if they have a creditScore < 600)
    addRule(StandardDecision.create(Boolean.class, Float.class)
      .when(facts -> facts.getOne())
      .then((facts, result) -> result.setValue(result.getValue() * 0.80f)));
    }
}
```
```java
public class ExampleSolution {
  public static void main(String[] args) {
    HomeLoanRateDecisionBook homeLoanRateDecisionBook = new HomeLoanRateDecisionBook();
    homeLoanRateDecisionBook.withDefaultResult(4.5f)
      .given("Credit Score", 650)
      .given("Cash on Hand", 20000)
      .given("First Time Homebuyer", true).run();
    
    System.out.println("Applicant qualified for the following rate: " + homeLoanRateDecisionBook.getResult());
  }
}
```

In the above example, the default Result value was initialized to false. So, unless a Decision set the result to something else, the result of running the DecisionBook would be false. And unfortunately, for these applicants, they just didn't meet the requirements for a loan at MegaBank as determined by the rules.

<sub>[[Top](#contents)]</sub>

## 3 The RuleBook Domain Specific Language Explained

The RuleBook Java Domain Specific Language (DSL) uses the Given-When-Then format, popularized by Behavior Driven Development (BDD) and associated testing frameworks (e.g. Cucumber and Spock). Many of the ideas that went into creating the RuleBook Java DSL are also borrowed from BDD, including: _**Sentences should be used to describe rules**_ and _**Rules should be defined using a ubiquitous language that translates into the codebase**_.

### 3.1 Given-When-Then: The Basis of the RuleBook Language 

Much like the Given-When-Then language for defining tests that was popularized by BDD, RuleBook uses a Given-When-Then language for defining rules. The RuleBook Given-When-Then methods have the following meanings.

* **Given** some Fact(s)
* **When** a condition evaluates to true
* **Then** an action is triggered

**Given** methods can accept one or more Facts in various different forms and are used as a collection of information provided to a single Rule/Decision or a chain of Rules/Decisions called a RuleBook/DecisionBook.

**When** methods accept a Predicate that evaluates a condition based on the Facts provided. Only one when() method can be specified per Rule/Decision.

**Then** methods accept a Consumer (or BiConsumer for Decisions) that describe the action to be invoked if the condition in the when() method evaluates to true. There can be multiple then() methods specified in a Rule or Decision that will all be 
invoked in the order they are specified if the when() condition evaluates to true.

### 3.2 The Using Method
**Using** methods reduce the set of Facts available to a then() method. Mutiple using() methods can also be chained together if so desired. The aggregate of the Facts with the names specified in all using() methods immediately preceeding a then() method will be made available to that then() method. An example of how using() works [is shown above](#22-the-above-example-using-facts).

### 3.3 The Stop Method
**Stop** methods break the rule chain. If a stop() method is specified when defining a rule, it means that if the when() condition evaluates to true, following the completion of the then() action(s), the rule chain should be broken and no more rules in that chain should be evaluated.

### 3.4 Working With Facts
As stated above, Facts are provided to Rules and Decisions using the given() method. The Facts available to Rules/Decisions and RuleBooks/DecisionBooks are contained in a FactMap, which is a special kind of Map that allows for easy access to the underlying objects contained in Facts. The reason why Facts exist is so that there is always a reference to the objects that Rules and Decisions work with - even if say, an immutable object is replaced, the perception is that the Fact still exists and provides a named reference to representative object.

#### 3.4.1 The Single Fact Convenience Method
Facts really only have a single convenience method. Since the FactMap is what is passed into then when() and then() methods, most of the convenience methods around Facts are made available in the FactMap. However, there is one... the constructor. Facts consist of a name value pair. But in some cases, the name of the Fact should just be the string value of the object it contains. In those cases, a constructor with a single argument of the type of the object contained in the Fact can be used.

#### 3.4.2 The FactMap Convenience Methods
Although the reason for FactMaps is important, that doesn't mean anyone wants to chain a bunch of boiler plate calls to get to the value object contained in an underlying Fact. So, some convenience methods are there to make life easier when working with when() and then() methods.

**getOne()** gets the value of the Fact when only one Fact exists in the FactMap

**getValue(String name)** gets the value of the Fact by the name of the Fact

**getStrVal(String name)** gets the value of the Fact by name as a String

**getDblVal(String)** gets the value of the Fact by name as a Double

**getIntVal(String)** gets the value of the Fact by name as an Integer

**setValue(String name, T value)** sets the Fact with the name specified to the new value 

**put(Fact fact)** adds a Fact to the FactMap, using the Fact's name as the key for the Map

**toString()** toString gets the toString() method of the Fact's value when only one Fact exists

<sub>[Top](#contents)</sub>

## 4 POJO Rules

As of RuleBook v0.2, POJO rules are supported. Simply define your rules as annotated POJOs in a package and then use _RuleBookRunner_ to scan the package for rules and create a RuleBook out of them. It's that simple!

### 4.1 A Hello World Example

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

### 4.2 A New MegaBank Example With POJO Rules
_MegaBank changed their rate adjustment policy. They also now accept loan applications that include up to 3 applicants. If all of the applicants' credit scores are below 600, then they must pay 4x the current rate. However, if all of the applicants have a credit score of less than 700, but at least one applicant has a credit score greater than 600, then they must pay an additional point on top the rate. Also, if any of the applicants have a credit score of 700 or more and the sum of the cash on hand available from all applicants is greater than or equal to $50,000, then they get a quarter point reduction in their rate. And if at least one applicant is a first time home buyer and at least one applicant has a credit score of over 600, then they get a 20% reduction in their calculated rate after all other adjustments are made.

**...using the ApplicantBean defined above**
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
public class LowCreditScoreRule {
  @Given
  private List<ApplicantBean> applicants;

  @Result
  private float rate;
    
  @When
  public boolean when() {
    return applicants.stream()
      .allMatch(applicant -> applicant.getCreditScore() < 600);
  }

  @Then
  public RuleState then() {
    rate *= 4f;
    return BREAK;
  }
}
```
```java
@Rule(order = 3)
public class QuarterPointReductionRule {
  @Given
  List<ApplicantBean> applicants; 

  @Result
  private float rate;

  @When
  public boolean when() {
    return 
      applicants.stream().anyMatch(applicant -> applicant.getCreditScore() >= 700) &&
      applicants.stream().map(applicant -> applicant.getCashOnHand()).reduce(0.0, Float::sum) >= 50000;
  }

  @Then
  public void then() {
    approved = true;
    return rate = rate - (rate * 0.25f);
  }
}
```
```java
@Rule(order = 3)
public class ExtraPointRule {
  @Given
  List<ApplicantBean> applicants; 

  @Result
  private float rate;

  @When
  public boolean when() {
    return 
      applicants.stream().anyMatch(applicant -> applicant.getCreditScore() < 700 && applicant.getCreditScore() >= 600);
  }

  @Then
  public void then() {
    rate += 1f;
  }
}
```
```java
@Rule(order = 4)
public class FirstTimeHomeBuyerRule {
  @Given
  List<ApplicantBean> applicants; 

  @Result
  private float rate;

  @When
  public boolean when() {
    return 
      applicants.stream().anyMatch(applicant -> applicant.isFirstTimeHomeBuyer());
  }

  @Then
  public void then() {
    rate = rate - (rate * 0.20f);
  }
}
```
```java
public class ExampleSolution {
  public static void main(String[] args) {
    HomeLoanRateDecisionBook homeLoanRateDecisionBook = new HomeLoanRateDecisionBook();
    ApplicantBean applicant = new ApplicantBean(650, 20000, true);
    ApplicantBean applicant = new ApplicantBean(620, 30000, true);
    homeLoanRateDecisionBook.withDefaultResult(4.5f).given("applicant", applicant).run();
    
    System.out.println("Applicant qualified for the following rate: " + homeLoanRateDecisionBook.getResult());
  }
}
```

### 4.3 POJO Rules Explained

POJO Rules are annotated with @Rule at the class level. This lets the RuleBookRunner know that the class you defined is really a Rule. Facts are injected into POJO Rules using @Given annotations. The value passed into the @Given annotation is the name of the Fact given to the RuleBookRunner. The types annotated by @Given can either be the generic type of the matching Fact or the Fact type as seen above. The big difference between the two is that changes applied to immutable objects are not propigated down the rule chain if the Fact’s generic object is changed (because it would then be a new object). However, if you set the value on a Fact object, those changes will be persisted down the rule chain.

The @When annotation denotes the method that is used as the condition for executing the ‘then’ action. The method annotated with @When should accept no arguments and it should return a boolean result.

The @Then annotation denotes the action(s) of the rule that is executed if the ‘when’ condition evaluates to true. The method(s) annotated with @Then should accept no arugments and it can optionally return a RuleState result. If more than one method in a POJO rule is annotated with @Then, then all rules annotated with @Then are executed if the 'when' condition evaluates to true.

The @Result annotation denotes the result of the Rule. Of course, some Rules may not have a result. In that case, just don’t use the @Result annotation. It’s that simple.

#### 4.3.1 Ordering POJO Rules

The ‘order’ property can \[optionally\] be used with the @Rule annoataion to specify the orner in which POJO Rules will execute [as seen above](#42-the-megabank-example-with-pojo-rules). If the order property is not specified, then Rules may execute in any order. Similarly, more than one Rule may have the same order, which would mean that the Rules with a matching order can fire in any order - order would then denote a group of rules, where the execution of the group is ordered among other rules, but the execution of the rules within that group doesn’t matter.

#### 4.3.2 Injecting Collections into POJO Rules

If the following conditions are met then the objects contained in all Facts of generic type specified are injected into a collection:

* A List, Set, Map or FactMap is annotated with a @Given annotation
* The @Given annotation on the collection has no value specified
* The generic type of the List, Set, Map (the first generic type in a Map is String - representing the name of the Fact injected) or FactMap is the same type of at least one Fact supplied to the RuleBookRunner

#### 4.3.3 POJO Rule Annotation Inheritance

As of v.0.3.2, RuleBook supports annotation inheritance on POJO Rules. That means if you have a subclass, whose parent is annotated with RuleBook annotations (i.e. @Given, @When, @Then, @Result) then the subclass will inherit the parent’s annotations. @Given and @Result attributes injected in the parent, will be available to the subclass. @Then and @When methods defined in the parent will be visible in the subclass.

<sub>[[Top](#contents)]</sub>

## 5 Using RuleBook with Spring

RuleBooks in Spring can be created using Spring configurations with RuleBookBean classes. RuleBookBean classes should be scoped as prototype and they can add either rules created through the RuleBook DSL or Spring enabled POJO rules. And creating a Spring enabled POJO rule couldn't be easier; just create a POJO rule, but instead of using @Rule, use @RuleBean.

### 5.1 Adding RuleBook Spring Support to Your Maven Project

_Add the code below to your pom.xml_

```xml
<dependency>
    <groupId>com.deliveredtechnologies</groupId>
    <artifactId>rulebook-spring</artifactId>
    <version>0.4</version>
</dependency>
```

### 5.2 Adding RuleBook Spring Support to Your Gradle Project

_Add the code below to your build.gradle_

```groovy
compile 'com.deliveredtechnologies:rulebook-spring:0.4.1'
```

### 5.3 Creating a Spring Enabled POJO Rule

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

### 5.4 Configuring a RuleBook in Spring

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

### 5.5 Using a Spring Enabled RuleBook

```java
  @Autowired
  private ApplicationContext context;
  
  public void someMethod() {
    RuleBookBean ruleBook = context.getBean(RuleBookBean.class));
    ruleBook.given(new Fact("hello", "Hello"), new Fact("world", "World")).run(); 
    System.out.println(ruleBook.getResult()); //prints "Hello World"
  }
```

### 5.6 Spring Enabled POJO Rules Explained

In the Spring configuration, a RuleBookBean is used. RuleBookBean is like a RuleBookRunner that's made for Spring. The difference between RuleBookBean and RuleBookRunner is that RuleBookBean easily allows rules to be specified/wired up with Spring by delegating injection to Spring. Notice that RuleBookBean is also scoped as “prototype” in the examples above. This is because RuleBookBean also stores state - in the form of Facts and [possibly] a Result. If it was a Singleton then any time the RuleBookBean object was used, Facts could be changed across threads, and the Result could get overwritten.

_Note: Since Facts hold references to objects, if the same exact Facts are used in two different RuleBooks, DecisionBooks, RuleBookRunners, or RuleBookBeans_

#### 5.6.1 Ordering Spring Enabled POJO Rules

Unlike regular POJO Rules, Spring Enabled POJO Rules don't have an order property in RuleBean. That's because the order of Spring Enabled POJO Rules is determined by the order they are configured. Simple, right?

<sub>[[Top](#contents)]</sub>

## 6 How to Contribute

Suggestions and code contributions are welcome! Please see the _Developer Guidelines_ below.

### 6.1 Developer Guidelines

Contributions must adhere to the following criteria:

1. The forked repository must be publicly visible.
2. The issues addressed in the request must be associated to an accepted issue.
3. The build (i.e. ./gradlew build) must pass with no errors or warnings.
4. All new and existing tests must pass.
5. The code must adhere to the style guidleines icluded in the checkstyle configuration (i.e. no checkstyle errors).
6. Newly introduced code must have at least 85% test coverage.
7. Pull requests must be for the _develop_ branch.
8. The version number in gradle.properties should match the milestone of the issue its associated with appended with _-SNAPSHOT_ (ex. 0.2-SNAPSHOT)

Anyone may submit an issue, which can be either an enhancement/feature request or a bug to be remediated. If a feature request or a bug is approved (most reasonable ones will be), completed and an associated pull request is submitted that adheres to the above criteria, then the pull request will be merged and the contributor will be added to the list of contributors in the following release.

<sub>[[Top](#contents)]</sub>
