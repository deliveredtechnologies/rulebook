package com.deliveredtechnologies.rulebook;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


/**
 * Tests for {@link TypeConvertibleFactMap}.
 */
public class TypeConvertibleFactMapTest {

  @Test
  @SuppressWarnings("unchecked")
  public void getStrValConvertsFactValueToString() {
    NameValueReferable<String> fact1 = new Fact<>("fact1", "First Fact");
    NameValueReferable<Integer> fact2 = new Fact<>("benjamin", 100);
    NameValueReferableMap factMap = new FactMap();
    TypeConvertibleFactMap typeConvertibleFactMap = new TypeConvertibleFactMap(factMap);
    factMap.put(fact1);
    factMap.put(fact2);

    Assert.assertEquals("First Fact", typeConvertibleFactMap.getStrVal("fact1"));
    Assert.assertEquals("100", typeConvertibleFactMap.getStrVal("benjamin"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getIntValConvertsFactValueToInteger() {
    NameValueReferable<String> fact1 = new Fact<>("fact1", "100");
    NameValueReferable<Integer> fact2 = new Fact<>("benjamin", 100);
    NameValueReferableMap factMap = new FactMap();
    TypeConvertibleFactMap typeConvertibleFactMap = new TypeConvertibleFactMap(factMap);
    factMap.put(fact1);
    factMap.put(fact2);
    factMap.put(new Fact<String>("null", null));

    int fact1int = typeConvertibleFactMap.getIntVal("fact1");
    int benjamin = typeConvertibleFactMap.getIntVal("benjamin");

    Assert.assertEquals(100, fact1int);
    Assert.assertEquals(100, benjamin);
    Assert.assertNull(typeConvertibleFactMap.getIntVal("null"));
    Assert.assertNull(typeConvertibleFactMap.getIntVal("doesNotExist"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getDblValConvertsFactValueToDouble() {
    Fact<String> fact1 = new Fact<>("fact1", "100");
    Fact<Float> fact2 = new Fact<>("benjamin", 100.00f);
    Fact<Double> fact3 = new Fact<>("lockness", 3.50);
    Fact<Integer> fact4 = new Fact<>("jackson", 20);
    Fact<Long> fact5 = new Fact<>("grant", 50L);

    FactMap factMap = new FactMap();
    TypeConvertibleFactMap typeConvertibleFactMap = new TypeConvertibleFactMap(factMap);
    factMap.put(fact1);
    factMap.put(fact2);
    factMap.put(fact3);
    factMap.put(fact4);
    factMap.put(fact5);

    double fact1dbl = typeConvertibleFactMap.getDblVal("fact1");
    double fact2dbl = typeConvertibleFactMap.getDblVal("benjamin");
    double fact3dbl = typeConvertibleFactMap.getDblVal("lockness");
    double fact4dbl = typeConvertibleFactMap.getDblVal("jackson");
    double fact5dbl = typeConvertibleFactMap.getDblVal("grant");

    Assert.assertEquals(100.00, fact1dbl, 0);
    Assert.assertEquals(100.00, fact2dbl, 0);
    Assert.assertEquals(3.50, fact3dbl, 0);
    Assert.assertEquals(20.0, fact4dbl, 0);
    Assert.assertEquals(50.0, fact5dbl, 0);
    Assert.assertNull(typeConvertibleFactMap.getDblVal("doesNotExist"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getBigDeciValConvertsFactValueToBigDecimal() {
    Fact<String> fact1 = new Fact<>("fact1", "100");
    Fact<Float> fact2 = new Fact<>("benjamin", 100.00f);
    Fact<Double> fact3 = new Fact<>("lockness", 3.50);
    Fact<Integer> fact4 = new Fact<>("jackson", 20);
    Fact<Long> fact5 = new Fact<>("grant", 50L);
    Fact<BigDecimal> fact6 = new Fact<>("lincoln", new BigDecimal(5.0));

    FactMap factMap = new FactMap();
    TypeConvertibleFactMap typeConvertibleFactMap = new TypeConvertibleFactMap(factMap);
    factMap.put(fact1);
    factMap.put(fact2);
    factMap.put(fact3);
    factMap.put(fact4);
    factMap.put(fact5);
    factMap.put(fact6);

    BigDecimal fact1decimal = typeConvertibleFactMap.getBigDeciVal("fact1");
    BigDecimal fact2decimal = typeConvertibleFactMap.getBigDeciVal("benjamin");
    BigDecimal fact3decimal = typeConvertibleFactMap.getBigDeciVal("lockness");
    BigDecimal fact4decimal = typeConvertibleFactMap.getBigDeciVal("jackson");
    BigDecimal fact5decimal = typeConvertibleFactMap.getBigDeciVal("grant");
    BigDecimal fact6bigdecimal = typeConvertibleFactMap.getBigDeciVal("lincoln");

    Assert.assertEquals(BigDecimal.valueOf(100), fact1decimal);
    Assert.assertEquals(BigDecimal.valueOf(100.00f), fact2decimal);
    Assert.assertEquals(BigDecimal.valueOf(3.50), fact3decimal);
    Assert.assertEquals(new BigDecimal(20), fact4decimal);
    Assert.assertEquals(BigDecimal.valueOf(50L), fact5decimal);
    Assert.assertEquals(BigDecimal.valueOf(5), fact6bigdecimal);
    Assert.assertNull(typeConvertibleFactMap.getBigDeciVal("doesNotExist"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getBoolValConvertsFactValueToBoolean() {
    Fact<String> fact1 = new Fact<>("true string", "true");
    Fact<String> fact2 = new Fact<>("false string", "false");
    Fact<Boolean> fact3 = new Fact<>("true boolean", true);
    Fact<Boolean> fact4 = new Fact<>("false boolean", false);

    FactMap factMap = new FactMap();
    factMap.put(fact1);
    factMap.put(fact2);
    factMap.put(fact3);
    factMap.put(fact4);

    TypeConvertibleFactMap typeConvertibleFactMap = new TypeConvertibleFactMap(factMap);

    Assert.assertTrue(typeConvertibleFactMap.getBoolVal("true string"));
    Assert.assertFalse(typeConvertibleFactMap.getBoolVal("false string"));
    Assert.assertTrue(typeConvertibleFactMap.getBoolVal("true boolean"));
    Assert.assertFalse(typeConvertibleFactMap.getBoolVal("false boolean"));
    Assert.assertFalse(typeConvertibleFactMap.getBoolVal("does not exist"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void toStringDelegatesToDecoratedMap() {
    NameValueReferableMap factMap = mock(NameValueReferableMap.class);
    when(factMap.toString()).thenReturn("delegated!");

    NameValueReferableMap facts = new TypeConvertibleFactMap(factMap);
    Assert.assertEquals("delegated!", facts.toString());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void putAllDelegatesToDecoratedMap() {
    NameValueReferableMap factMap = mock(NameValueReferableMap.class);
    FactMap map = new FactMap();

    NameValueReferableMap facts = new TypeConvertibleFactMap(factMap);
    facts.putAll(map);

    verify(factMap, times(1)).putAll(map);
  }
}
