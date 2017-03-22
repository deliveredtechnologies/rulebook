package com.deliveredtechnologies.rulebook;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Tests for {@link DecisionBook}.
 */
public class DecisionBookTest {
  @Test
  @SuppressWarnings("unchecked")
  public void decisionBooksRunDecisions() {
    Decision<String, Boolean> decision1 = (Decision<String, Boolean>) mock(Decision.class);
    Decision<String, Boolean> decision2 = (Decision<String, Boolean>) mock(Decision.class);

    Fact<String> fact = new Fact<String>("hello", "Hello");

    DecisionBook<String, Boolean> decisionBook = spy(new DecisionBook<String, Boolean>() {
      @Override
      protected void defineRules() {

      }
    });
    decisionBook.given(fact).addRule(decision1);
    decisionBook.addRule(decision2);
    decisionBook.run();

    verify(decision1, times(1)).setNextRule(decision2);
    verify(decision1, times(1)).run();
    verify(decisionBook, times(0)).defineRules();
    verify(decision1, times(1)).given(anyList());

  }

  @Test
  @SuppressWarnings("unchecked")
  public void decisionBooksShouldRunDecisionsAndRules() {
    Decision<String, Boolean> decision1 = (Decision<String, Boolean>) mock(Decision.class);
    Rule<String> rule = (Rule<String>) mock(Rule.class);
    Decision<String, Boolean> decision2 = (Decision<String, Boolean>) spy(Decision.class);

    DecisionBook<String, Boolean> decisionBook = spy(new DecisionBook<String, Boolean>() {
      @Override
      protected void defineRules() {

      }
    });
    decisionBook.given("hello", "world").addRule(decision1);
    decisionBook.addRule(rule);
    decisionBook.addRule(decision2);
    decisionBook.run();

    verify(decision1, times(1)).setNextRule(rule);
    verify(rule, times(1)).setNextRule(decision2);
    verify(decision1, times(1)).run();
    verify(decision1, times(1)).given(anyList());
    verify(decisionBook, times(0)).defineRules();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void decisionBooksShouldRunDecisionsAndGetResults() {
    Fact<String> hello = new Fact<>("hello", "Hello");
    Fact<String> world = new Fact<>("world", "World");

    DecisionBook<String, StringBuffer> decisionBook = new DecisionBook<String, StringBuffer>() {
      @Override
      protected void defineRules() {
        addRule(StandardDecision.create(String.class, StringBuffer.class)
            .when(facts -> true)
            .then((facts, result) -> {
                result.getValue().append(facts.getValue("hello"));
              })
        );
        addRule(StandardDecision.create(String.class, StringBuffer.class)
            .when(facts -> true)
            .then((facts, results) -> {
                results.getValue().append(facts.getValue("world"));
              })
        );
      }
    };
    decisionBook.withDefaultResult(new StringBuffer()).given(hello, world).run();
    Assert.assertEquals(decisionBook.getResult().toString(), "HelloWorld");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void genericDecisionBooksShouldWorkWithDecisionsAndRulesOfDifferentTypes() {
    DecisionBook decisionBook = new DecisionBook() {
      @Override
      protected void defineRules() {
        addRule(StandardDecision.create(String.class, Integer.class)
            .when(facts -> facts.getValue("str").equals("String"))
            .then((facts, result) -> facts.get("str").setValue("NewString")));

        addRule(StandardDecision.create(Integer.class, Integer.class)
            .when(facts -> facts.getOne() == 1)
            .then((facts, result) -> result.setValue(result.getValue() + 3)));

        addRule(StandardRule.create(String.class)
            .when(facts -> facts.getOne().equals("NewString"))
            .then(facts -> facts.get("str").setValue("OtherString")));
      }
    };
    Fact<String> strFact = new Fact<String>("str", "String");
    decisionBook.withDefaultResult(0).given(strFact).given("one", 1).run();
    Assert.assertEquals(decisionBook.getResult(), 3);
    Assert.assertEquals(strFact.getValue(), "OtherString");
  }
}
