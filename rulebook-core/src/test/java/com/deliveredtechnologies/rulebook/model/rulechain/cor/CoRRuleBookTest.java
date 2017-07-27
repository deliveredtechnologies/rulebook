package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import net.jodah.concurrentunit.Waiter;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

/**
 * Tests for {@link CoRRuleBook}.
 */
public class CoRRuleBookTest {
  @Test
  public void rulesDefinedInSubclassAreInvoked() {
    SubCoRRuleBook ruleBook = new SubCoRRuleBook();
    ruleBook.setDefaultResult("default");
    ruleBook.run(new FactMap());

    Assert.assertEquals("Success!", ruleBook.getResult().get().getValue());
  }

  @Test
  public void ruleBooksRunInDifferentThreadsDoNotConflict() throws TimeoutException {
    final Waiter waiter = new Waiter();

    RuleBook ruleBook = RuleBookBuilder.create().withResultType(String.class).withDefaultResult("Unknown")
        .addRule(rule -> rule.withFactType(String.class)
            .when(facts -> facts.size() > 2)
            .then((facts, result) -> result.setValue("> 2 facts")).stop())
        .addRule(rule -> rule.withFactType(String.class)
            .when(facts -> facts.containsKey("fact1"))
            .then((facts, result) -> facts.get("fact1").setValue("<= 2 facts")))
        .addRule(rule -> rule.withFactType(String.class)
            .when(facts -> facts.containsKey("factoid"))
            .then((facts, result) -> result.setValue("Factoid Found!"))).build();

    new Thread(() -> {
      FactMap<String> facts = new FactMap<>();
      facts.setValue("fact1", "fact1");

      ruleBook.run(facts);

      waiter.assertEquals(ruleBook.getResult().get().toString(), "Unknown");
      waiter.resume();
      waiter.assertEquals(((Result<String>)ruleBook.getResult().get()).getValue(), "Unknown");
      waiter.resume();
      waiter.assertEquals(facts.getValue("fact1"), "<= 2 facts");
      waiter.resume();
    }).start();

    new Thread(() -> {
      FactMap<String> facts = new FactMap<>();
      facts.setValue("fact1", "fact1");
      facts.setValue("factoid", "fact2");

      ruleBook.run(facts);

      waiter.assertEquals(ruleBook.getResult().get().toString(), "Factoid Found!");
      waiter.resume();
      waiter.assertEquals(facts.getValue("fact1"), "<= 2 facts");
      waiter.resume();
      waiter.assertEquals(facts.getValue("factoid"), "fact2");
      waiter.resume();
    }).start();

    new Thread(() -> {
      FactMap<String> facts = new FactMap<>();
      facts.setValue("fact1", "fact1");
      facts.setValue("fact2", "fact2");
      facts.setValue("fact3", "fact3");

      ruleBook.run(facts);

      waiter.assertEquals(ruleBook.getResult().get().toString(), "> 2 facts");
      waiter.resume();
      waiter.assertEquals(facts.getValue("fact1"), "fact1");
      waiter.resume();
      waiter.assertEquals(facts.getValue("fact2"), "fact2");
      waiter.resume();
      waiter.assertEquals(facts.getValue("fact3"), "fact3");
      waiter.resume();
    }).start();

    waiter.await();
  }
}
