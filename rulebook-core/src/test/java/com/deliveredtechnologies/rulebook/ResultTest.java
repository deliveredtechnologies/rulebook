package com.deliveredtechnologies.rulebook;

import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Tests for {@link Result}.
 */
public class ResultTest {
  @Test
  public void rulebooksRunInDifferentThreadsHaveDifferentResults() throws Exception {
    //define the RuleBook
    com.deliveredtechnologies.rulebook.model.RuleBook ruleBook = RuleBookBuilder.create()
        .withResultType(String.class).withDefaultResult("Nada")
        .addRule(rule -> rule.withFactType(String.class)
            .when(facts -> facts.getOne().equals("Hello"))
            .then((facts, result) -> result.setValue(facts.getOne() + " World")))
        .addRule(rule -> rule.withFactType(String.class)
            .when(facts -> facts.getOne().equals("Bye"))
            .then((facts, result) -> result.setValue(facts.getOne() + " Felicia"))).build();

    //establish facts for thread A
    NameValueReferableMap<String> exhibitA = new FactMap<>();
    exhibitA.put(new Fact<String>("Hello"));

    //establish facts for thread B
    NameValueReferableMap<String> exhibitB = new FactMap<>();
    exhibitB.put(new Fact<String>("Bye"));

    //return result from thread A
    Callable<String> threadA = () -> {
      ruleBook.run(exhibitA);
      return ((Result<String>)ruleBook.getResult().get()).getValue();
    };

    //return result from thread B
    Callable<String> threadB = () -> {
      ruleBook.run(exhibitB);
      return ((Result<String>)ruleBook.getResult().get()).getValue();
    };

    //create thread pool with 2 threads
    ExecutorService service = Executors.newFixedThreadPool(2);

    //invoke RuleBook defined above in different threads using different facts
    Future<String> resultB = service.submit(threadB);
    Future<String> resultA = service.submit(threadA);

    //wait for both threads to complete
    service.shutdown();
    service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

    //check the results of the execution of the same RuleBook run in different threads with different results
    Assert.assertEquals(resultB.get(), "Bye Felicia");
    Assert.assertEquals(resultA.get(), "Hello World");
  }
}
