package com.deliveredtechnologies.rulebook.model.runner.test.rulebooks.error.condition;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.ERROR_ON_FAILURE;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

/**
 * Created by harshavs on 20/6/19.
 */
@Rule(order = 3, ruleChainAction = ERROR_ON_FAILURE)
public class AfterErrorRule {
  @When
  public boolean when() {
    return true;
  }

  @Then
  public void then() {}
}
