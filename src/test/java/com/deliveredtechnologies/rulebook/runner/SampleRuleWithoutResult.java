package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

/**
 * Created by clong on 2/13/17.
 */
@Rule
public class SampleRuleWithoutResult {
  @Given("fact1")
  private Fact<String> fact1;

  @Given("fact2")
  private Fact<String> fact2;

  @When
  public boolean when() {
    return fact1.getValue().equals(fact2.getValue());
  }

  @Then
  public RuleState then() {
    fact2.setValue("So Factual!");
    return RuleState.NEXT;
  }

  public String getFact1() {
    return fact1.getValue();
  }

  public String getFact2() {
    return fact2.getValue();
  }
}
