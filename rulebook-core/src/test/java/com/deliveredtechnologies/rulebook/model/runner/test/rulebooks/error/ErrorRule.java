package com.deliveredtechnologies.rulebook.model.runner.test.rulebooks.error;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.ERROR_ON_FAILURE;

/**
 * Created by clayton.long on 5/8/18.
 */
@Rule(order = 2, ruleChainAction = ERROR_ON_FAILURE)
public class ErrorRule {
  @When
  public boolean when() throws Exception {
    throw new Exception("Sumthin' Broke!");
  }

  @Then
  public void then() {}
}
