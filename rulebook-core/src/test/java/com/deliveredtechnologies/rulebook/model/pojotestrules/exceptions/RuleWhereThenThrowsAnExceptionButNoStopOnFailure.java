package com.deliveredtechnologies.rulebook.model.pojotestrules.exceptions;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;

@Rule
public class RuleWhereThenThrowsAnExceptionButNoStopOnFailure {

  @Then
  public boolean then() throws Exception {
    throw new Exception("Then Exception");
  }
}
