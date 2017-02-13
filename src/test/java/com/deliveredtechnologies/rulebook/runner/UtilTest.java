package com.deliveredtechnologies.rulebook.runner;


import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Created by clong on 2/13/17.
 */
public class UtilTest {

  SampleRule sampleRule;
  FactMap factMap;

  @Before
  public void setup() {
    sampleRule = new SampleRule();
    factMap = new FactMap();
    factMap.put("fact1", new Fact("fact1", "FirstFact"));
    factMap.put("fact2", new Fact("fact2", "SecondFact"));
    factMap.put("value1", new Fact("value1", 1));
  }

  @Test
  public void givenAttributesShouldMapToFacts() {

    Util.mapGivenFactsToProperties(sampleRule, factMap);

    Assert.assertEquals("FirstFact", sampleRule.getFact1());
    Assert.assertEquals("SecondFact", sampleRule.getFact2());
    Assert.assertEquals(1, sampleRule.getValue1());
  }

  @Test
  public void whenMethodShouldConvertToPredicate() {

    Util.mapGivenFactsToProperties(sampleRule, factMap);
    Predicate predicate = Util.getWhenMethodAsPredicate(sampleRule);

    Assert.assertFalse(predicate.test(null));

    Fact fact = (Fact)factMap.get("fact2");
    fact.setValue("FirstFact");

    Assert.assertTrue(predicate.test(null));
  }

  @Test
  public void thenMethodShouldConvertToBiFunctionIfResultPresent() {

    Util.mapGivenFactsToProperties(sampleRule, factMap);
    BiFunction biFunction = Util.getThenMethodAsBiFunction(sampleRule);

    Ass
  }
}
