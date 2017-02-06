package com.deliveredtechnologies.rulebook;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by clong on 2/6/17.
 * Tests for {@link FactMap}
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
}
