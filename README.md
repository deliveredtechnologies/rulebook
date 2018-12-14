[RuleBook-Spring Maven Central]:http://search.maven.org/#artifactdetails|com.deliveredtechnologies|rulebook-spring|0.11|
[RuleBook-Core Maven Central]:http://search.maven.org/#artifactdetails|com.deliveredtechnologies|rulebook-core|0.11|
[Apache 2.0 License]:https://opensource.org/licenses/Apache-2.0

# RuleBook <img src="https://github.com/Clayton7510/RuleBook/blob/master/LambdaBook.png" height="100" align="left"/>
**&raquo; A Simple &amp; Intuitive Rules Abstraction for Java** <br/><sub> _100% Java_ &middot; _Lambda Enabled_ &middot; _Simple, Intuitive DSL_ &middot; _Lightweight_ </sub>

---

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)][Apache 2.0 License] [![Maven Central](https://img.shields.io/badge/maven%20central-0.11-brightgreen.svg)][RuleBook-Core Maven Central] [![Build Status](https://travis-ci.org/rulebook-rules/rulebook.svg?branch=develop&maxAge=600)](https://travis-ci.org/rulebook-rules/rulebook) [![Coverage Status](https://coveralls.io/repos/github/rulebook-rules/rulebook/badge.svg?branch=develop&maxAge=600)](https://coveralls.io/github/rulebook-rules/rulebook?branch=develop)  [![Gitter](https://badges.gitter.im/RuleBook.svg)](https://gitter.im/RuleBook?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge) [![Paypal](https://img.shields.io/badge/donate-PayPal-blue.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=C6EM6HRN45L72)

## Why RuleBook?
RuleBook rules are built in the way that Java developers think: Java code. And they are executed in the way that programmers expect: In order. RuleBook also allows you to specify rules using an easy to use Lambda enabled Domain Specific Language or using POJOs that you define!

Tired of classes filled with if/then/else statements? Need a nice abstraction that allows rules to be easily specified in a way that decouples them from each other? Want to write rules the same way that you write the rest of your code [in Java]? RuleBook just might be the rules abstraction you've been waiting for!

_**<sub>Got questions? Here are answers to [Frequently Asked Questions](https://github.com/rulebook-rules/rulebook/wiki/Frequently-Asked-Questions)!<sub>**_

_**<sub>Still not finding what you are looking for? Try the [Wiki](https://github.com/rulebook-rules/rulebook/wiki)!</sub>**_

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
  * [2.4 Thread Safety](#24-thread-safety)
* **[3 The RuleBook Domain Specific Language](#3-the-rulebook-domain-specific-language-explained)**
  * [3.1 Given-When-Then: The Basis of the RuleBook Language](#31-given-when-then-the-basis-of-the-rulebook-language)
  * [3.2 The Using Method](#32-the-using-method)
  * [3.3 The Stop Method](#33-the-stop-method)
  * [3.4 Working With Facts](#34-working-with-facts)
    * [3.4.1 The Single Fact Convenience Method](#341-the-single-fact-convenience-method)
    * [3.4.2 The FactMap Convenience Methods](#342-the-factmap-convenience-methods)
  * [3.5 Auditing Rules](#35-auditing-rules)
  * [3.6 Rule Chain Behavior](#36-rule-chain-behavior)
* **[4 POJO Rules](#4-pojo-rules)**
  * [4.1 A POJO Rules Example](#41-a-hello-world-example)
  * [4.2 A \[Slightly\] More Complex POJO Rules Example](#42-a-new-megabank-example-with-pojo-rules)
  * [4.3 POJO Rules Explained](#43-pojo-rules-explained)
    * [4.3.1 Ordering POJO Rules](#431-ordering-pojo-rules)
    * [4.3.2 Injecting Collections into POJO Rules](#432-injecting-collections-into-pojo-rules)
    * [4.3.3 POJO Rule Annotation Inheritance](#433-pojo-rule-annotation-inheritance)
    * [4.3.4 Auditing POJO Rules](#434-auditing-pojo-rules)
    * [4.3.5 POJO Rule Chain Behavior](#435-pojo-rule-chain-behavior)
* **[5 Using RuleBook with Spring](#5-using-rulebook-with-spring)**
  * [5.1 Adding RuleBook Spring Support to Your Project](#51-adding-rulebook-spring-support-to-your-project)
  * [5.2 Creating Spring Enabled POJO Rules](#52-creating-spring-enabled-pojo-rules)
  * [5.3 Configuring a RuleBook in Spring](#53-configuring-a-rulebook-in-spring)
  * [5.4 Using a Spring Enabled RuleBook](#54-using-a-spring-enabled-rulebook)
  * [5.5 Ordering Rules With Spring](#55-ordering-rules-with-spring)
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

* rulebook-core &nbsp;&nbsp;&nbsp;[![Maven Central](https://img.shields.io/badge/maven%20central-0.11-brightgreen.svg)][RuleBook-Core Maven Central]
* rulebook-spring [![Maven Central](https://img.shields.io/badge/maven%20central-0.11-brightgreen.svg)][RuleBook-Spring Maven Central]

### 1.3 Latest Sonatype SNAPSHOT (Development) Release

* rulebook-core &nbsp;&nbsp;&nbsp;[![Sonatype Nexus](https://img.shields.io/badge/SNAPSHOT-0.11-green.svg)](https://oss.sonatype.org/content/repositories/snapshots/)
* rulebook-spring [![Sonatype Nexus](https://img.shields.io/badge/SNAPSHOT-0.11-green.svg)](https://oss.sonatype.org/content/repositories/snapshots/)

### 1.4 Adding RuleBook to Your Maven Project

_Add the code below to your pom.xml_

```xml
<dependency>
    <groupId>com.deliveredtechnologies</groupId>
    <artifactId>rulebook-core</artifactId>
    <version>0.11</version>
</dependency>
```

### 1.5 Adding RuleBook to Your Gradle Project

_Add the code below to your build.gradle_

```groovy
compile 'com.deliveredtechnologies:rulebook-core:0.11'
```

<sub>[[Top](#contents)]</sub>

## 2 Using RuleBook
### 2.1 A HelloWorld Example
```java
RuleBook ruleBook = RuleBookBuilder.create()
    .addRule(rule -> rule.withNoSpecifiedFactType()
      .then(f -> System.out.print("Hello "))
      .then(f -> System.out.println("World")))
    .build();
```
**...or use 2 rules**
```java
RuleBook ruleBook = RuleBookBuilder.create()
    .addRule(rule -> rule.withNoSpecifiedFactType().then(f -> System.out.print("Hello ")))
    .addRule(rule -> rule.withNoSpecifiedFactType().then(f -> System.out.println("World")))
    .build();
```
**now, run it!**
```java
ruleBook.run(new FactMap());
```
### 2.2 The Above Example Using Facts
```java
RuleBook ruleBook = RuleBookBuilder.create()
    .addRule(rule -> rule.withFactType(String.class)
      .when(f -> f.containsKey("hello"))
      .using("hello")
      .then(System.out::print))
    .addRule(rule -> rule.withFactType(String.class)
      .when(f -> f.containsKey("world"))
      .using("world")
      .then(System.out::println))
    .build();
```
**..or it could be a single rule**
```java
RuleBook ruleBook = RuleBookBuilder.create()
  .addRule(rule -> rule.withFactType(String.class)
    .when(f -> f.containsKey("hello") && f.containsKey("world"))
    .using("hello").then(System.out::print)
    .using("world").then(System.out::println))
  .build();
```
**now, run it!**
```java
NameValueReferableMap factMap = new FactMap();
factMap.setValue("hello", "Hello ");
factMap.setValue("world", " World");
ruleBook.run(factMap);
```
### 2.3 A [Slightly] More Complex Scenario

_MegaBank issues home loans. If an applicant's credit score is less than 600 then they must pay 4x the current rate. If an applicant’s credit score is between 600, but less than 700, then they must pay a an additional point on top of their rate. If an applicant’s credit score is at least 700 and they have at least $25,000 cash on hand, then they get a quarter point reduction on their rate. If an applicant is a first time home buyer then they get a 20% reduction on their calculated rate after adjustments are made based on credit score (note: first time home buyer discount is only available for applicants with a 600 credit score or greater)._

```java
public class ApplicantBean {
  private int creditScore;
  private double cashOnHand;
  private boolean firstTimeHomeBuyer;

  public ApplicantBean(int creditScore, double cashOnHand, boolean firstTimeHomeBuyer) {
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

  public double getCashOnHand() {
    return cashOnHand;
  }

  public void setCashOnHand(double cashOnHand) {
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
public class HomeLoanRateRuleBook extends CoRRuleBook<Double> {
  @Override
  public void defineRules() {
    //credit score under 600 gets a 4x rate increase
    addRule(RuleBuilder.create().withFactType(ApplicantBean.class).withResultType(Double.class)
      .when(facts -> facts.getOne().getCreditScore() < 600)
      .then((facts, result) -> result.setValue(result.getValue() * 4))
      .stop()
      .build());

    //credit score between 600 and 700 pays a 1 point increase
    addRule(RuleBuilder.create().withFactType(ApplicantBean.class).withResultType(Double.class)
      .when(facts -> facts.getOne().getCreditScore() < 700)
      .then((facts, result) -> result.setValue(result.getValue() + 1))
      .build());

    //credit score is 700 and they have at least $25,000 cash on hand
    addRule(RuleBuilder.create().withFactType(ApplicantBean.class).withResultType(Double.class)
      .when(facts ->
            facts.getOne().getCreditScore() >= 700 &&
            facts.getOne().getCashOnHand() >= 25000)
      .then((facts, result) -> result.setValue(result.getValue() - 0.25))
      .build());

    //first time homebuyers get 20% off their rate (except if they have a creditScore < 600)
    addRule(RuleBuilder.create().withFactType(ApplicantBean.class).withResultType(Double.class)
      .when(facts -> facts.getOne().isFirstTimeHomeBuyer())
      .then((facts, result) -> result.setValue(result.getValue() * 0.80))
      .build());
    }
}
```
```java
public class ExampleSolution {
  public static void main(String[] args) {
    RuleBook homeLoanRateRuleBook = RuleBookBuilder.create(HomeLoanRateRuleBook.class).withResultType(Double.class)
      .withDefaultResult(4.5)
      .build();
    NameValueReferableMap facts = new FactMap();
    facts.setValue("applicant", new ApplicantBean(650, 20000.0, true));
    homeLoanRateRuleBook.run(facts);

    homeLoanRateRuleBook.getResult().ifPresent(result -> System.out.println("Applicant qualified for the following rate: " + result));
  }
}
```
**...or nix the ApplicantBean and just use independent Facts**
```java
public class HomeLoanRateRuleBook extends CoRRuleBook<Double> {
  @Override
  public void defineRules() {
    //credit score under 600 gets a 4x rate increase
    addRule(RuleBuilder.create().withResultType(Double.class)
      .when(facts -> facts.getIntVal("Credit Score") < 600)
      .then((facts, result) -> result.setValue(result.getValue() * 4))
      .stop()
      .build());

    //credit score between 600 and 700 pays a 1 point increase
    addRule(RuleBuilder.create().withResultType(Double.class)
      .when(facts -> facts.getIntVal("Credit Score") < 700)
      .then((facts, result) -> result.setValue(result.getValue() + 1))
      .build());

    //credit score is 700 and they have at least $25,000 cash on hand
    addRule(RuleBuilder.create().withResultType(Double.class)
      .when(facts ->
        facts.getIntVal("Credit Score") >= 700 &&
        facts.getDblVal("Cash on Hand") >= 25000)
      .then((facts, result) -> result.setValue(result.getValue() - 0.25))
      .build());

    //first time homebuyers get 20% off their rate (except if they have a creditScore < 600)
    addRule(RuleBuilder.create().withFactType(Boolean.class).withResultType(Double.class)
      .when(facts -> facts.getOne())
      .then((facts, result) -> result.setValue(result.getValue() * 0.80))
      .build());
  }
}
```
```java
public class ExampleSolution {
  public static void main(String[] args) {
    RuleBook homeLoanRateRuleBook = RuleBookBuilder.create(HomeLoanRateRuleBook.class).withResultType(Double.class)
     .withDefaultResult(4.5)
     .build();

    NameValueReferableMap facts = new FactMap();
    facts.setValue("Credit Score", 650);
    facts.setValue("Cash on Hand", 20000);
    facts.setValue("First Time Homebuyer", true);

    homeLoanRateRuleBook.run(facts);

    homeLoanRateRuleBook.getResult().ifPresent(result -> System.out.println("Applicant qualified for the following rate: " + result));
    }
}
```

## 2.4 Thread Safety

RuleBooks are threadsafe. However, FactMaps and other implementations of NameValueReferableMap are not. This means that a single instance of a RuleBook can be run in different threads with different Facts without unexpected results. However, using the same exact FactMap across different threads may cause unexpected results. Facts represent data for individual invocations of a RuleBook, whereas RuleBooks represent reusable sets of Rules.

<sub>[[Top](#contents)]</sub>

## 3 The RuleBook Domain Specific Language Explained

The RuleBook Java Domain Specific Language (DSL) uses the Given-When-Then format, popularized by Behavior Driven Development (BDD) and associated testing frameworks (e.g. Cucumber and Spock). Many of the ideas that went into creating the RuleBook Java DSL are also borrowed from BDD, including: _**Sentences should be used to describe rules**_ and _**Rules should be defined using a ubiquitous language that translates into the codebase**_.

### 3.1 Given-When-Then: The Basis of the RuleBook Language

Much like the Given-When-Then language for defining tests that was popularized by BDD, RuleBook uses a Given-When-Then language for defining rules. The RuleBook Given-When-Then methods have the following meanings.

* **Given** some Fact(s)
* **When** a condition evaluates to true
* **Then** an action is triggered

**Given** methods can accept one or more facts in various different forms and are used as a collection of information provided to a single Rule. When grouping Rules into a RuleBook, facts are supplied to the Rules when the RuleBook is run, so the 'Given' can be inferred.

**When** methods accept a Predicate that evaluates a condition based on the Facts provided. Only one when() method can be specified per Rule.

**Then** methods accept a Consumer (or BiConsumer for Rules that have a Result) that describe the action to be invoked if the condition in the when() method evaluates to true. There can be multiple then() methods specified in a Rule that will all be
invoked in the order they are specified if the when() condition evaluates to true.

### 3.2 The Using Method
**Using** methods reduce the set of facts available to a then() method. Multiple using() methods can also be chained together if so desired. The aggregate of the facts with the names specified in all using() methods immediately preceeding a then() method will be made available to that then() method. An example of how using() works [is shown above](#22-the-above-example-using-facts).

### 3.3 The Stop Method
**Stop** methods break the rule chain. If a stop() method is specified when defining a rule, it means that if the when() condition evaluates to true, following the completion of the then() action(s), the rule chain should be broken and no more rules in that chain should be evaluated.

### 3.4 Working With Facts
Facts can be provided to Rules using the given() method. In RuleBooks, facts are provided to Rules when the RuleBook is run. The facts available to Rules and RuleBooks are contained in a NameValueReferableMap (the base implementation being FactMap), which is a special kind of Map that allows for easy access to the underlying objects contained in facts. The reason why facts exist is so that there is always a reference to the objects that Rules work with - even if say, an immutable object is replaced, the perception is that the Fact still exists and provides a named reference to a representative object.

#### 3.4.1 The Single Fact Convenience Method
Facts really only have a single convenience method. Since the NameValueReferableMap (e.g. FactMap) is what is passed into when() and then() methods, most of the convenience methods around facts are made available in the Map. However, there is one convenience method included in the Fact class... the constructor. Facts consist of a name value pair. But in some cases, the name of the Fact should just be the string value of the object it contains. In those cases, a constructor with a single argument of the type of the object contained in the fact can be used.

#### 3.4.2 The FactMap Convenience Methods
Although the reason for NameValueReferableMaps (commonly referred to as FactMaps) is important, that doesn't mean anyone wants to chain a bunch of boiler plate calls to get to the value object contained in an underlying Fact. So, some convenience methods are there to make life easier when working with when() and then() methods.

**getOne()** gets the value of the Fact when only one Fact exists in the FactMap

**getValue(String name)** gets the value of the Fact by the name of the Fact

**setValue(String name, T value)** sets the Fact with the name specified to the new value

**put(Fact fact)** adds a Fact to the FactMap, using the Fact's name as the key for the Map

**toString()** toString gets the toString() method of the Fact's value when only one Fact exists

The following methods are part of the NameValueReferrableTypeConvertible interface, which is implemented by the TypeConvertibleFactMap class as a NameValueReferrable decorator. You can think of it as a decorator for FactMaps (because it's also that too!) and it's what's used to inject facts into when() and then() methods.

**getStrVal(String name)** gets the value of the Fact by name as a String

**getDblVal(String)** gets the value of the Fact by name as a Double

**getIntVal(String)** gets the value of the Fact by name as an Integer

**getBigDeciVal(String)** gets the value of the Fact by name as a BigDecimal

**getBoolVal(String)** gets the value of the Fact by name as a Boolean

### 3.5 Auditing Rules
Rules auditing can be enabled when constructing a RuleBook by specifying _asAuditor()_ as follows.

```java
 RuleBook rulebook = RuleBookBuilder.create().asAuditor()
   .addRule(rule -> rule.withName("Rule1").withNoSpecifiedFactType()
     .when(facts -> true)
     .then(facts -> { } ))
   .addRule(rule -> rule.withName("Rule2").withNoSpecifiedFactType()
     .when(facts -> false)
     .then(facts -> { } )).build();
     
 rulebook.run(new FactMap());
```

By using _asAuditor()_ each rule in the RuleBook can register itself as an _Auditable Rule_ if its name is specified. Each _Auditable Rule_ added to a RuleBook _Auditor_ has its state recorded in the RuleBook. At the time when rules are registered as auditable in the RuleBook, their RuleStatus is _NONE_. After the RuleBook is run, their RuleStatus is changed to _SKIPPED_ for all rules that fail or whose conditions do not evaluate to true. For rules whose conditions do evaluate to true and whose then() action completes successfully, their RuleStatus is changed to _EXECUTED_.

Retrieving the status of a rule can be done as follows.

```java
 Auditor auditor = (Auditor)rulebook;
 System.out.println(auditor.getRuleStatus("Rule1")); //prints EXECUTED
 System.out.println(auditor.getRuleStatus("Rule2")); //prints SKIPPED
```

A map of all rule names and their corresponding status can be retrieved as follows.

```java
 Map<String, RuleStatus> auditMap = auditor.getRuleStatusMap();
```

### 3.6 Rule Chain Behavior
By default, errors found when loading rules or exceptions thrown when running rules, remove
those rules from the rule chain. In other words, rules that error are just skipped. Additionally,
by default, a rule can only stop the rule chain if its condition evaluates to
true and if its actions successfully complete.

However, this behavior can be changed on a per-rule basis.

```java
RuleBook ruleBook = RuleBookBuilder.create()
    .addRule(
        RuleBuilder.create(GoldenRule.class, RuleChainActionType.STOP_ON_FAILURE)
            .withFactType(String.class)
            .when(facts -> true)
            .then(consumer)
            .stop()
            .build())
    .addRule(
        RuleBuilder.create()
            .withFactType(String.class)
            .when(facts -> true)
            .then(consumer)
            .build())
    .build();
```

In the above example, the default RuleChainActionType.CONTINUE_ON_FAILURE is changed
to RuleChainActionType.STOP_ON_FAILURE in the first rule. This will ensure
that if there is an error in the first rule, the 2nd rule will never be invoked. However,
no error will be thrown. 

If the desired behavior was to throw any exception that occurred in the first rule and stop the rule chain,
the following code could be used.

```java
RuleBook ruleBook = RuleBookBuilder.create()
    .addRule(
        RuleBuilder.create(GoldenRule.class, RuleChainActionType.ERROR_ON_FAILURE)
            .withFactType(String.class)
            .when(facts -> true)
            .then(consumer)
            .build())
    .addRule(
        RuleBuilder.create()
            .withFactType(String.class)
            .when(facts -> true)
            .then(consumer)
            .build())
    .build();
```

#### 3.6.1 Rule Chain Action Types Defined

| RuleChainActionType | Description                     |
| ------------------- | ------------------------------- |
| CONTINUE_ON_FAILURE | the default RuleChainActionType; false rule conditions and errors effectively 'skip' the rule| 
| ERROR_ON_FAILURE    | exceptions thrown by rules stop the rule chain and bubble up the exception as a RuleException |
| STOP_ON_FAILURE     | rules that have their RuleState set to BREAK will stop the RuleChain if the rule's condition is false or if an exception is thrown |

<sub>[Top](#contents)</sub>

## 4 POJO Rules

As of RuleBook v0.2, POJO rules are supported. Simply define your rules as annotated POJOs in a package and then use _RuleBookRunner_ to scan the package for rules and create a RuleBook out of them. It's that simple!

### 4.1 A Hello World Example

```java
package com.example.rulebook.helloworld;

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
  RuleBookRunner ruleBook = new RuleBookRunner("com.example.rulebook.helloworld");
  NameValueReferableMap facts = new FactMap();
  facts.setValue("hello", "Hello");
  facts.setValue("world", "World");
  ruleBook.run(facts);
  ruleBook.getResult().ifPresent(System.out::println); //prints "Hello World"
}
```

### 4.2 A New MegaBank Example With POJO Rules
_MegaBank changed their rate adjustment policy. They also now accept loan applications that include up to 3 applicants. If all of the applicants credit scores are below 600, then they must pay 4x the current rate. However, if all of the applicants have a credit score of less than 700, but at least one applicant has a credit score greater than 600, then they must pay an additional point on top the rate. Also, if any of the applicants have a credit score of 700 or more and the sum of the cash on hand available from all applicants is greater than or equal to $50,000, then they get a quarter point reduction in their rate. And if at least one applicant is a first time home buyer and at least one applicant has a credit score of over 600, then they get a 20% reduction in their calculated rate after all other adjustments are made._

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
  private double rate;

  @When
  public boolean when() {
    return applicants.stream()
      .allMatch(applicant -> applicant.getCreditScore() < 600);
  }

  @Then
  public RuleState then() {
    rate *= 4;
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
  private double rate;

  @When
  public boolean when() {
    return
      applicants.stream().anyMatch(applicant -> applicant.getCreditScore() >= 700) &&
      applicants.stream().map(applicant -> applicant.getCashOnHand()).reduce(0.0, Double::sum) >= 50000;
  }

  @Then
  public void then() {
    rate = rate - (rate * 0.25);
  }
}
```
```java
@Rule(order = 3)
public class ExtraPointRule {
  @Given
  List<ApplicantBean> applicants;

  @Result
  private double rate;

  @When
  public boolean when() {
    return
      applicants.stream().anyMatch(applicant -> applicant.getCreditScore() < 700 && applicant.getCreditScore() >= 600);
  }

  @Then
  public void then() {
    rate += 1;
  }
}
```
```java
@Rule(order = 4)
public class FirstTimeHomeBuyerRule {
  @Given
  List<ApplicantBean> applicants;

  @Result
  private double rate;

  @When
  public boolean when() {
    return
      applicants.stream().anyMatch(applicant -> applicant.isFirstTimeHomeBuyer());
  }

  @Then
  public void then() {
    rate = rate - (rate * 0.20);
  }
}
```
```java
public class ExampleSolution {
  public static void main(String[] args) {
    RuleBookRunner ruleBook = new RuleBookRunner("com.example.rulebook.megabank");
    NameValueReferableMap<ApplicantBean> facts = new FactMap<>();
    ApplicantBean applicant1 = new ApplicantBean(650, 20000, true);
    ApplicantBean applicant2 = new ApplicantBean(620, 30000, true);
    facts.put(new Fact<>(applicant1));
    facts.put(new Fact<>(applicant2));

    ruleBook.setDefaultResult(4.5);
    ruleBook.run(facts);
    ruleBook.getResult().ifPresent(result -> System.out.println("Applicant qualified for the following rate: " + result));
  }
}
```

### 4.3 POJO Rules Explained

POJO Rules are annotated with @Rule at the class level. This lets the RuleBookRunner know that the class you defined is really a Rule. Facts are injected into POJO Rules using @Given annotations. The value passed into the @Given annotation is the name of the Fact given to the RuleBookRunner. The types annotated by @Given can either be the generic type of the matching Fact or the Fact type as seen above. The big difference between the two is that changes applied to immutable objects are not propagated down the rule chain if the Fact’s generic object is changed (because it would then be a new object). However, if you set the value on a Fact object, those changes will be persisted down the rule chain.

The @When annotation denotes the method that is used as the condition for executing the ‘then’ action. The method annotated with @When should accept no arguments and it should return a boolean result.

The @Then annotation denotes the action(s) of the rule that is executed if the ‘when’ condition evaluates to true. The method(s) annotated with @Then should accept no arugments and it can optionally return a RuleState result. If more than one method in a POJO rule is annotated with @Then, then all rules annotated with @Then are executed if the 'when' condition evaluates to true.

The @Result annotation denotes the result of the Rule. Of course, some Rules may not have a result. In that case, just don’t use the @Result annotation. It’s that simple.

#### 4.3.1 Ordering POJO Rules

The ‘order’ property can \[optionally\] be used with the @Rule annoataion to specify the order in which POJO Rules will execute [as seen above](#42-a-new-megabank-example-with-pojo-rules). If the order property is not specified, then Rules may execute in any order. Similarly, more than one Rule may have the same order, which would mean that the Rules with a matching order can fire in any order - order would then denote a group of rules, where the execution of the group is ordered among other rules, but the execution of the rules within that group doesn’t matter.

#### 4.3.2 Injecting Collections into POJO Rules

If the following conditions are met then the objects contained in all Facts of generic type specified are injected into a collection:

* A List, Set, Map or FactMap is annotated with a @Given annotation
* The @Given annotation on the collection has no value specified
* The generic type of the List, Set, Map (the first generic type in a Map is String - representing the name of the Fact injected) or FactMap is the same type of at least one Fact supplied to the RuleBookRunner

#### 4.3.3 POJO Rule Annotation Inheritance

As of v.0.3.2, RuleBook supports annotation inheritance on POJO Rules. That means if you have a subclass, whose parent is annotated with RuleBook annotations (i.e. @Given, @When, @Then, @Result) then the subclass will inherit the parent’s annotations. @Given and @Result attributes injected in the parent, will be available to the subclass. @Then and @When methods defined in the parent will be visible in the subclass.

#### 4.3.4 Auditing POJO Rules

Auditing is built into POJO Rules via the RuleBookRunner and each POJO Rule is automatically audited. If a name is specified in the @Rule attribute, then that name is used for auditing. Otherwise, the class name of the POJO rule is used. For example, assuming that there is a POJO rule named "My Rule" that was run by the RuleBookRunner, the status of that rule execution can be retrieved as follows.

```java
 Auditor auditor = (Auditor)rulebookRunner;
 RuleStatus myRuleStatus = auditor.getRuleStatus("My Rule");
```

#### 4.3.5 POJO Rule Chain Behavior

```java
@Rule(ruleChainAction = ERROR_ON_FAILURE)
public class ErrorRule {
  @When
  public boolean when() {
    return true;
  }

  @Then
  public void then() throws Exception {
    throw new CustomException("Sumthin' Broke!");
  }
}
```

As seen in the example directly above, the ruleChainAction Rule parameter
can be use to change the rule chain behavior for specific rules as detailed
in [3.6 Rule Chain Behavior](#36-rule-chain-behavior).

<sub>[[Top](#contents)]</sub>

## 5 Using RuleBook with Spring

RuleBook can be integrated with Spring to inject instances of RuleBooks that are created from POJOs in a package. 
RuleBooks can be specified using either the Java DSL or POJO Rules. And since RuleBooks are threadsafe, they can be 
used as Singeltons, Spring's default for injecting beans. Additionally, POJO Rules can now be made Spring Aware, so
you can inject Spring components using @Autowire.

### 5.1 Adding RuleBook Spring Support to Your Project

The preferred way to use RuleBook with Spring is to configure a SpringAwareRuleBookRunner. Then,
simply add the @RuleBean annotation to any POJO Rules that you would like to work with Spring. If you omit
the @RuleBean annotation then the @POJO Rule(s) without @RuleBean can still be loaded and run, they
just will not be managed by or scoped properly for Spring and @Autowired will not work within the Rule.

### 5.2 Creating Spring Enabled POJO Rules

POJO Rules can be created just like they were created above without Spring, but with some extra Spring goodness!
To create Spring enabled POJO Rules, first add rulebook-spring as a dependency.

Maven:

```xml
<dependency>
    <groupId>com.deliveredtechnologies</groupId>
    <artifactId>rulebook-spring</artifactId>
    <version>0.11</version>
</dependency>
```

Gradle:

```groovy
compile 'com.deliveredtechnologies:rulebook-spring:0.11'
```

_Note: 0.11 is currently the only version of rulebook-spring that provides
SpringAwareRuleBookRunner, which is what allows Rules to have injected @Autowired Spring components._

The trivial example below demonstates the basic functionality.

```java

package com.exampl.rulebook.helloworld.component;

@Component
public class HelloWorldComponent {
  public String getHelloWorld(String hello, String world) {
    return hello + " " + world + "!";
  }
}

```

```java

package com.example.rulebook.helloworld;

@RuleBean
@Rule(order = 1)
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
  public void then() {
    result = hello;
  }
}
```

```java

package com.example.rulebook.helloworld;

@RuleBean
@Rule(order = 2)
public class WorldSpringRule {
  @Autowired
  HelloWorldComponent helloWorldComponent;
  
  @Given("world")
  private String world;

  @Result
  private String result;

  @When
  public boolean when() {
    return world != null;
  }

  @Then
  public void then() {
    result = helloWorldComponent.getHelloWorld(result, world);
  }
}
```

### 5.3 Configuring a RuleBook in Spring

```java
@Configuration
@ComponentScan("com.example.rulebook.helloworld")
public class SpringConfig {
  @Bean
  public RuleBook ruleBook() {
    RuleBook ruleBook = new SpringAwareRuleBookRunner("com.example.rulebook.helloworld");
    return ruleBook;
  }
}
```

### 5.4 Using a Spring Enabled RuleBook

```java
  @Autowired
  private RuleBook ruleBook;

  public void printResult() {
    NameValueReferableMap<String> facts = new FactMap<>();
    facts.setValue("hello", "Hello ");
    facts.setValue("world", "World");
    ruleBook.run(facts);
    ruleBook.getResult().ifPresent(System.out::println); //prints Hello World!
  }
```

### 5.5 Ordering Rules With Spring

If you were using the RuleBean annotation to create Spring enabled Rules, all of that stuff still works. And there Spring
enabled POJO Rules can still be configured in RuleBooks in Spring \[using SpringRuleBook\]. But RuleBean doesn't have an
order property. So, if you need to order beans scanned using a RuleBookFactoryBean, just use the @Rule annotation like
you would with regular non-Spring enabled POJO Rules. It works exactly the same way!

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
