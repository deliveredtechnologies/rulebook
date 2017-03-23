package com.deliveredtechnologies.rulebook;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * Tests for {@link FactMap}.
 */

public class FactMapTest {
  @Test
  public void getOneWhenOnlyOneFactExists() {
    FactMap<String> factMap = new FactMap<>();
    factMap.put("one", new Fact<String>("one"));

    Assert.assertEquals("one", factMap.getOne());
  }

  @Test
  public void getOneWhenMoreThanOneFactExists() {
    FactMap<String> factMap = new FactMap<>();
    factMap.put("one", new Fact<String>("one"));
    factMap.put("two", new Fact<String>("two"));

    Assert.assertNull(factMap.getOne());
  }

  @Test
  public void getValueWhenValueIsPresent() {
    FactMap<String> factMap = new FactMap<>();
    factMap.put("one", new Fact<String>("one"));
    factMap.put("two", new Fact<String>("two"));

    Assert.assertEquals(factMap.getValue("one"), "one");
    Assert.assertEquals(factMap.getValue("two"), "two");
  }

  @Test
  public void getValueWhenValueIsNotPresent() {
    FactMap<String> factMap = new FactMap<>();
    factMap.put("one", new Fact<String>("one"));
    factMap.put("two", new Fact<String>("two"));

    Assert.assertNull(factMap.getValue("three"));
  }

  @Test
  public void setValueChangesTheFactWhenTheFactExists() {
    Fact<String> fact = new Fact<String>("fact1");
    FactMap<String> factMap = new FactMap<>();
    factMap.put(fact);
    factMap.setValue("fact1", "First Fact");

    Assert.assertEquals("First Fact", fact.toString());
    Assert.assertEquals("First Fact", factMap.getValue("fact1"));
  }

  @Test
  public void setValueCreatesNewFactWhenTheFactDoesNotExist() {
    FactMap<String> factMap = new FactMap<>();
    factMap.setValue("fact1", "First Fact");

    Fact<String> fact = factMap.get("fact1");

    Assert.assertEquals("First Fact", factMap.getValue("fact1"));
    Assert.assertEquals(fact, factMap.get("fact1"));
  }

  @Test
  public void toStringReturnsToStringOfFactWhenOnlyOneFactExists() {
    Fact<String> fact = new Fact<String>("fact1");
    FactMap<String> factMap = new FactMap<>();
    factMap.put(fact);
    factMap.setValue("fact1", "First Fact");

    Assert.assertEquals(fact.toString(), factMap.toString());
  }

  @Test
  public void toStringReturnsHashMapToStringWhenMultipleFactsExist() {
    Fact<String> fact1 = new Fact<String>("fact1");
    Fact<String> fact2 = new Fact<String>("fact2");
    FactMap<String> factMap = new FactMap<>();
    HashMap<String, Fact<String>> hashMap = new HashMap<>();
    factMap.put(fact1);
    factMap.put(fact2);
    hashMap.put("fact1", fact1);
    hashMap.put("fact2", fact2);

    Assert.assertEquals(hashMap.toString(), factMap.toString());
  }

  @Test
  public void getStrValConvertsFactValueToString() {
    Fact<String> fact1 = new Fact<String>("fact1", "First Fact");
    Fact<Integer> fact2 = new Fact<Integer>("benjamin", 100);
    FactMap factMap = new FactMap();
    factMap.put(fact1);
    factMap.put(fact2);

    Assert.assertEquals("First Fact", factMap.getStrVal("fact1"));
    Assert.assertEquals("100", factMap.getStrVal("benjamin"));
  }

  @Test
  public void getIntValConvertsFactValueToInteger() {
    Fact<String> fact1 = new Fact<String>("fact1", "100");
    Fact<Integer> fact2 = new Fact<Integer>("benjamin", 100);
    FactMap factMap = new FactMap();
    factMap.put(fact1);
    factMap.put(fact2);
    factMap.put(new Fact<String>("null", null));

    int fact1int = factMap.getIntVal("fact1");
    int benjamin = factMap.getIntVal("benjamin");

    Assert.assertEquals(100, fact1int);
    Assert.assertEquals(100, benjamin);
    Assert.assertNull(factMap.getIntVal("null"));
    Assert.assertNull(factMap.getIntVal("doesNotExist"));
  }

  @Test
  public void getDblValConvertsFactValueToDouble() {
    Fact<String> fact1 = new Fact<String>("fact1", "100");
    Fact<Float> fact2 = new Fact<Float>("benjamin", 100.00f);
    Fact<Double> fact3 = new Fact<Double>("lockness", 3.50);
    Fact<Integer> fact4 = new Fact<Integer>("jackson", 20);
    Fact<Long> fact5 = new Fact<Long>("grant", 50L);

    FactMap factMap = new FactMap();
    factMap.put(fact1);
    factMap.put(fact2);
    factMap.put(fact3);
    factMap.put(fact4);
    factMap.put(fact5);

    double fact1dbl = factMap.getDblVal("fact1");
    double fact2dbl = factMap.getDblVal("benjamin");
    double fact3dbl = factMap.getDblVal("lockness");
    double fact4dbl = factMap.getDblVal("jackson");
    double fact5dbl = factMap.getDblVal("grant");

    Assert.assertEquals(100.00, fact1dbl, 0);
    Assert.assertEquals(100.00, fact2dbl, 0);
    Assert.assertEquals(3.50, fact3dbl, 0);
    Assert.assertEquals(20.0, fact4dbl, 0);
    Assert.assertEquals(50.0, fact5dbl, 0);
    Assert.assertNull(factMap.getDblVal("doesNotExist"));
  }
}
