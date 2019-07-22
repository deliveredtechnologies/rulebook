package com.deliveredtechnologies.rulebook.model.pojotestrules.exceptions;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.When;

@Rule
public class RuleWhereWhenThrowsAnExceptionButNoStopOnFailure {

  @When
  public boolean when() throws Exception {
    throw new Exception("When Exception");
  }
}
