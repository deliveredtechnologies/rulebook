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
  public void decisionBooksRunDecisionsAndRules() {
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
  public void decisionBooksRunDecisionsAndGetResults() {
    Fact<String> hello = new Fact<>("hello", "Hello");
    Fact<String> world = new Fact<>("world", "World");

    DecisionBook<String, StringBuffer> decisionBook = new DecisionBook<String, StringBuffer>() {
      @Override
      protected void defineRules() {
        addRule(StandardDecision.create(String.class, StringBuffer.class)
            .when(facts -> true)
            .then((facts, result) -> {
                result.getValue().append(facts.getValue("hello"));
                return RuleState.NEXT;
              })
        );
        addRule(StandardDecision.create(String.class, StringBuffer.class)
            .when(facts -> true)
            .then((facts, results) -> {
                results.getValue().append(facts.getValue("world"));
                return RuleState.NEXT;
              })
        );
      }
    };
    decisionBook.withDefaultResult(new StringBuffer()).given(hello, world).run();
    Assert.assertEquals(decisionBook.getResult().toString(), "HelloWorld");
  }
}
