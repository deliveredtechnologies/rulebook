package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Created by clong on 3/29/17.
 */
public class CoRRuleBookTest {

  @Test
  @SuppressWarnings("unchecked")
  public void CoRRuleBookRunsRuleChain() {
    Fact<String> fact = new Fact<String>("hello", "world");
    FactMap<String> facts = new FactMap<>();
    Rule<String, Boolean> rule1 = (Rule<String, Boolean>) mock(Rule.class);
    Rule<String, Boolean> rule2 = (Rule<String, Boolean>) mock(Rule.class);
    Rule<String, Boolean> rule3 = (Rule<String, Boolean>) mock(Rule.class);
    when(rule1.getRuleState()).thenReturn(RuleState.NEXT);
    when(rule1.invokeAction()).thenReturn(true);
    when(rule1.getResult()).thenReturn(Optional.empty());
    when(rule2.invokeAction()).thenReturn(false);
    when(rule2.getResult()).thenReturn(Optional.empty());
    when(rule3.invokeAction()).thenReturn(false);
    when(rule3.getResult()).thenReturn(Optional.empty());
    facts.put(fact);

    RuleBook<Boolean> ruleBook = new CoRRuleBook<>();
    ruleBook.addRule(rule1);
    ruleBook.addRule(rule2);
    ruleBook.addRule(rule3);
    ruleBook.run(facts);

    verify(rule1, times(1)).invokeAction();
    verify(rule2, times(1)).invokeAction();
    verify(rule3, times(1)).invokeAction();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void CoRRuleBookRunsRuleChainWithResults() {
    Fact<String> fact = new Fact<String>("hello", "world");
    FactMap<String> factMap = new FactMap<>();
    factMap.put(fact);

    RuleBook<String> ruleBook = new CoRRuleBook<>();
    ruleBook.addRule(RuleBuilder.create(String.class, String.class)
        .then((facts, result) -> result.setValue(facts.getOne()))
        .build());
    ruleBook.addRule(RuleBuilder.create(String.class)
        .when(facts -> facts.containsKey("hello"))
        .then(facts -> facts.put(new Fact("goodbye", "city")))
        .build());
    ruleBook.setDefaultResult("");
    ruleBook.run(factMap);

    Assert.assertEquals("world", ruleBook.getResult().get().getValue());
    Assert.assertTrue(factMap.containsKey("goodbye"));
  }
}
