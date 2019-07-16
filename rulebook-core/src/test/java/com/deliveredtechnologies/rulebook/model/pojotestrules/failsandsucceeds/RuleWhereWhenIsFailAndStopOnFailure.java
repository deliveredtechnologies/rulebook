package com.deliveredtechnologies.rulebook.model.pojotestrules.failsandsucceeds;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.STOP_ON_FAILURE;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.When;

@Rule(order = 1, ruleChainAction = STOP_ON_FAILURE)
public class RuleWhereWhenIsFailAndStopOnFailure {

  @When
  public boolean when() throws Exception {
    return false;
  }
}
