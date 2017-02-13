package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.When;

/**
 * Created by clong on 2/13/17.
 */
public class SampleRule {
  @Given("fact1")
  private Fact<String> fact1;

  @Given("fact2")
  private Fact<String> fact2;

  @Given("value1")
  private int value1;

  @When
  public boolean when() {
    return fact1.getValue().equals(fact2.getValue());
  }

  public String getFact1() {
    return fact1.getValue();
  }

  public String getFact2() {
    return fact2.getValue();
  }

  public int getValue1() { return value1; }
}
