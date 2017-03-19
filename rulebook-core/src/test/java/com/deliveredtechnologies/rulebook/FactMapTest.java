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
}
