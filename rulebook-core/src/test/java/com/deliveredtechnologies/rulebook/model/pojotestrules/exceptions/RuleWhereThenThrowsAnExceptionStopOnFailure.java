package com.deliveredtechnologies.rulebook.model.pojotestrules.exceptions;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.STOP_ON_FAILURE;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;

@Rule(order = 1, ruleChainAction = STOP_ON_FAILURE)
public class RuleWhereThenThrowsAnExceptionStopOnFailure {

  @Then
  public boolean then() throws Exception {
    throw new Exception("Then Exception");
  }
}
