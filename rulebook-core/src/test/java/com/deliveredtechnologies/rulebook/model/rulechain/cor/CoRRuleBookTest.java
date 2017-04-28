package com.deliveredtechnologies.rulebook.model.rulechain.cor;

import com.deliveredtechnologies.rulebook.FactMap;
import org.junit.Assert;
import org.junit.Test;

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
}
