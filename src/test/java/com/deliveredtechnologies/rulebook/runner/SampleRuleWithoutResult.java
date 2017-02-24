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
 * Sample POJO rule that only contains facts, no result.
 */
@Rule(order = 3)
public class SampleRuleWithoutResult {
  @Given("fact1")
  private Fact<String> _fact1;

  @Given("fact2")
  private Fact<String> _fact2;

  @When
  public boolean when() {
    return _fact1.getValue().equals(_fact2.getValue());
  }

  /**
   * The then() method is the action of the rule
   * @return  RuleState.NEXT to continue to the next rule
   */
  @Then
  public RuleState then() {
    _fact2.setValue("So Factual!");
    return RuleState.NEXT;
  }

  public String getFact1() {
    return _fact1.getValue();
  }

  public String getFact2() {
    return _fact2.getValue();
  }
}
