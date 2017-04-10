package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by clong on 4/3/17.
 */
public class RuleHandlerTest {
  @Test
  public void ruleHandlerShouldInvokeSuccessorIfWhenDoesntExist() {
    NameValueReferableMap factMap = new FactMap();
    factMap.setValue("fact1", "First Fact");
    Handler handler1 = new RuleHandler(RuleBuilder.create()
            .given(factMap)
            .then(facts -> facts.setValue("fact2", "Second Fact"))
            .then(facts -> facts.setValue("fact3", "Third Fact")).build());
    Handler handler2 = new RuleHandler(RuleBuilder.create()
            .then(facts -> facts.setValue("fact4", "Fourth Fact"))
            .then(facts -> facts.setValue("fact5", "Fifth Fact")).build());
    handler1.setSuccessor(handler2);
    handler1.handleRequest();

    Assert.assertEquals(5, factMap.size());
  }

  @Test
  public void ruleHandlerShouldInvokeSuccessorIfWhenFails() {
    NameValueReferableMap factMap = new FactMap();
    factMap.setValue("fact1", "First Fact");
    Handler handler1 = new RuleHandler(RuleBuilder.create()
            .given(factMap)
            .when(facts -> facts.get("invalid").equals(null))
            .then(facts -> facts.setValue("fact2", "Second Fact"))
            .then(facts -> facts.setValue("fact3", "Third Fact")).build());
    Handler handler2 = new RuleHandler(RuleBuilder.create()
            .then(facts -> facts.setValue("fact4", "Fourth Fact"))
            .then(facts -> facts.setValue("fact5", "Fifth Fact")).build());
    handler1.setSuccessor(handler2);
    handler1.handleRequest();

    Assert.assertEquals(3, factMap.size());
    Assert.assertNotNull(factMap.get("fact5"));
    Assert.assertNull(factMap.get("fact3"));
  }

  @Test
  public void ruleHandlerShouldINotInvokeSuccessorIfPriorRuleBreaksChain() {
    NameValueReferableMap factMap = new FactMap();
    factMap.setValue("fact1", "First Fact");
    Handler handler1 = new RuleHandler(RuleBuilder.create()
            .given(factMap)
            .then(facts -> facts.setValue("fact2", "Second Fact"))
            .then(facts -> facts.setValue("fact3", "Third Fact"))
            .stop().build());
    Handler handler2 = new RuleHandler(RuleBuilder.create()
            .then(facts -> facts.setValue("fact4", "Fourth Fact"))
            .then(facts -> facts.setValue("fact5", "Fifth Fact")).build());
    handler1.setSuccessor(handler2);
    handler1.handleRequest();

    Assert.assertEquals(3, factMap.size());
    Assert.assertNotNull(factMap.get("fact3"));
    Assert.assertNull(factMap.get("fact5"));
  }

  @Test
  public void ruleHandlerShouldINotErrorIfSuccessorIsNull() {
    NameValueReferableMap factMap = new FactMap();
    factMap.setValue("fact1", "First Fact");
    Handler handler1 = new RuleHandler(RuleBuilder.create()
            .given(factMap)
            .when(facts -> true)
            .then(facts -> facts.setValue("fact2", "Second Fact"))
            .then(facts -> facts.setValue("fact3", "Third Fact"))
            .stop().build());
    handler1.setSuccessor(null);
    handler1.handleRequest();

    Assert.assertEquals(3, factMap.size());
    Assert.assertNotNull(factMap.get("fact1"));
    Assert.assertNotNull(factMap.get("fact2"));
    Assert.assertNotNull(factMap.get("fact3"));
  }
}
