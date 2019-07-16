package com.deliveredtechnologies.rulebook.model.pojotestrules.exceptions;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.ERROR_ON_FAILURE;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;

@Rule(order = 1, ruleChainAction = ERROR_ON_FAILURE)
public class RuleWhereThenThrowsAnExceptionErrorOnFailure {

  @Then
  public boolean then() throws Exception {
    throw new Exception("Then Exception");
  }
}
