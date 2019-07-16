package com.deliveredtechnologies.rulebook.model.pojotestrules.exceptions;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.ERROR_ON_FAILURE;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.When;

@Rule(order = 1, ruleChainAction = ERROR_ON_FAILURE)
public class RuleWhereWhenThrowsAnExceptionErrorOnFailure {

  @When
  public boolean when() throws Exception {
    throw new Exception("When Exception");
  }
}
