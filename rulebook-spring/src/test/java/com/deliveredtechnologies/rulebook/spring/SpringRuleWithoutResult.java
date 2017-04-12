package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.When;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;

/**
 * Sample POJO Rule with no result for testing Spring support.
 */
@Rule(order = 2)
public class SpringRuleWithoutResult {
  @Given("value1")
  private String _value1;

  @Given("value2")
  private Fact<String> _value2;

  @When
  public boolean when() {
    return _value2.equals("value2");
  }

  /**
   * The then() action.
   * @return  RuleState.NEXT
   */
  @Then
  public void then() {
    _value2.setValue("valueTwo");
  }
}

