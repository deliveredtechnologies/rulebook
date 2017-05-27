package com.example.pojorules.homeloan;

import com.deliveredtechnologies.rulebook.annotation.*;
import com.deliveredtechnologies.rulebook.RuleState;

import java.util.List;

@Rule(order = 3)
public class ExtraPointRule {
  @Given
  List<ApplicantBean> applicants;

  @Result
  private double rate;

  @When
  public boolean when() {
    return
        applicants.stream().anyMatch(applicant -> applicant.getCreditScore() < 700 && applicant.getCreditScore() >= 600);
  }

  @Then
  public void then() {
    rate += 1;
  }
}
