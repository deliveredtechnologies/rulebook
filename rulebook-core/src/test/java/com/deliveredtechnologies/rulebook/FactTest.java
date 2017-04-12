package com.deliveredtechnologies.rulebook;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link Fact}.
 */
public class FactTest {

  @Test
  public void factsCanBeCreatedFromOtherFacts() {
    Fact<String> fact1 = new Fact<String>("fact1", "Fact One");
    Fact<String> fact2 = new Fact<String>(fact1);

    Assert.assertEquals(fact1, fact2);
  }

  @Test
  public void settingFactNameShouldChangeTheName() {
    Fact<String> fact = new Fact<String>("name", "value");
    fact.setName("newName");

    Assert.assertEquals("newName", fact.getName());
  }

  @Test
  public void equivalentFactsShouldHaveTheSametHashCode() {
    Fact<String> fact1 = new Fact<String>("fact1", "Fact One");
    Fact<String> fact2 = new Fact<String>("fact1", "Fact One");
    Fact<Double> fact3 = new Fact<Double>("fact2", 1.25);
    Fact<Double> fact4 = new Fact<Double>("fact2", 1.25);

    Assert.assertEquals(fact3, fact4);
    Assert.assertEquals(fact1, fact2);
    Assert.assertEquals(fact1.hashCode(), fact2.hashCode());
    Assert.assertEquals(fact3.hashCode(), fact4.hashCode());
    Assert.assertNotEquals(fact1, fact3);
  }

  @Test
  public void nonFactObjectsShouldNotEqualFactObjects() {
    Fact fact = new Fact("another", "fact");
    NameValueReferable obj = Mockito.mock(NameValueReferable.class);
    Mockito.when(obj.getName()).thenReturn("another");
    Mockito.when(obj.getValue()).thenReturn("fact");

    Assert.assertNotEquals(fact, "anotherfact");
    Assert.assertNotEquals(fact, obj);
  }
}


