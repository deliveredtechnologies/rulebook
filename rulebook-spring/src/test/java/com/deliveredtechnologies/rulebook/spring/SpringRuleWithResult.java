package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

/**
 * Sample POJO Rule with result for testing Spring support.
 */
@RuleBean
public class SpringRuleWithResult {
  @Given("value1")
  private String _value1;

  @Given("value2")
  private Fact<String> _value2;

  @Result
  private String _result = "";

  @When
  public boolean when() {
    return _value1.equals(_value2.getValue());
  }

  /**
   * The then() action.
   * @return  RuleState.NEXT
   */
  @Then
  public RuleState then() {
    _value2.setValue("value2");
    _result = "firstRule";
    return RuleState.NEXT;
  }
}
