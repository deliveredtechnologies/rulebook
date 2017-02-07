# RuleBook
**A Simple Rules Abstraction for Java8+** 

-----------------

### Why Another Rules Abstraction?
Rules engines like Drools are more than many projects need. The format of the rules is also very specialized. And how rules are evaulated is not entirely straightforward. Other rules engines add a bunch of annotations or specialized requirements that can be foreign to many Java developers. That's why RuleBook is a dead simple, 100% Java rules abstraction without the mountain of special annotations or other specialized knowledge required by other [simple?] rules abstractions. It also executes rules in the order in which they are specified (ALWAYS!). 

RuleBook rules are built in the way that Java developers think: Java code. And they are executed in the way that programmers expect: In order. Tired of classes filled with if/then/else statements? Need a nice abstraction that allows rules to be easily specified in way that decouples rules from eachother? Want to write rules the same way that you write the rest of your code [in Java]? RuleBook just might be the rules abstraction you've been waiting for.

### How Does RuleBook Work?
RuleBook is a rules abstraction based on the Chain of Responsibility pattern. Each rule specified in a RuleBook is chained together in the order in which it is specified. As one rule completes, the next rule is evaluated. Any rule can break the chain by returning RuleState.BREAK from the 'then' Function in the rule.

State in Rules is handled through Facts. A Fact is literally just data that is named and supplied to a Rule or RuleBook _(note: facts added to a RuleBook are applied to all rules in the RuleBook)_. Facts can be both read and written to. So, in that way, facts can be used to evaluate state at the completion of a RuleBook execution and they can also be used to pass data into a Rule or RuleBook.

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

_MegaBank issues home loans. Each home loan can have between one and 3 applicants. If any of the applicant's credit scores is less than 700 then all of the applicants' available cash on hand must be at least $50,000.00, otherwise the loan is denied._

The two different approaches to implementing these rules are

1. Use a single fact containing a Java Bean that aggregates all of the input and the results
2. Use a different fact for each input and for the result

There are pros and cons to each approach. 
In the first approach, a single fact contains an uber-bean that is all of the input (and the responses). The good thing about this approach is that there is only one type of fact, so generics work well. The downside is that you have to combine everything into a single object.
In the second approach, each input is a discrete object. However, since the result is also a fact, there are multiple types of facts, which means that a cast is necessary when retrieving a fact from the FactMap. Since we've been using Java before generics were even a thing (in Java 1.5), we've decided to trade syntactic elegance for independent objects representing independent facts. ~~Perhaps in future version, there will be a more elegant way to handle facts of different types and/or results as facts.~~ _A more elegant way of handling facts and return types of different types is coming soon with **Decision** objects_

_An Example Solution_
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

    public BigDecimal getCashOnHand() { return cashOnHand; }

    public void setCashOnHand(BigDecimal cashOnHand) { this.cashOnHand = cashOnHand; }
}
```
```java
public class HomeLoanRuleBook extends RuleBook {
    @Override
    protected void defineRules() {

        //if everyone has cash on hand of greater than or equal to $50,000 then the loan is approved!
        addRule(StandardRule.create()
          .when(factMap -> factMap.values().stream()
            .filter(fact -> fact.getValue() instanceof ApplicantBean)
            .allMatch(applicantFact -> ((ApplicantBean)applicantFact.getValue()).getCashOnHand().compareTo(BigDecimal.valueOf(50000)) >= 0))
          .then(factMap -> {
            factMap.get("result").setValue(true);
            return BREAK; //stop the rules chain; it's approved!
          })
        );

        //if everyone has a credit score over 700 then the loan is approved!
        addRule(StandardRule.create()
          .when(factMap -> factMap.values().stream()
            .filter(fact -> fact.getValue() instanceof ApplicantBean)
              .allMatch(applicantFact -> ((ApplicantBean)applicantFact.getValue()).getCreditScore() >= 700))
          .then(factMap -> {
            factMap.get("result").setValue(true);
            return BREAK; //it doesn't matter if NEXT or BREAK is returned here since it's the last Rule
          })
        );
        
        //the default value of the result should be false
    }
}
```
_Sadly, the loan was not approved :(_
```java
public class ExampleMainClass {
  public static void main(String[] args) {
    Fact resultFact = new Fact("result", false);
    HomeLoanRuleBook ruleBook = new HomeLoanRuleBook();
    ruleBook.given(
      new Fact("applicant1", new ApplicantBean(699, BigDecimal.valueOf(100))),
      new Fact("applicant2", new ApplicantBean(701, BigDecimal.valueOf(51000))),
      resultFact)
      .run();
    Boolean result = (Boolean)resultFact.getValue();
    System.out.println(result ? "Loan Approved!" : "Loan Denied!");
  }
}
```
