package com.example.pojorules.homeloan;

import com.deliveredtechnologies.rulebook.annotation.*;
import com.deliveredtechnologies.rulebook.RuleState;

import java.util.List;

@Rule(order = 3)
public class QuarterPointReductionRule {
  @Given
  List<ApplicantBean> applicants;

  @Result
  private double rate;

  @When
  public boolean when() {
    return
        applicants.stream().anyMatch(applicant -> applicant.getCreditScore() >= 700) &&
            applicants.stream().map(applicant -> applicant.getCashOnHand()).reduce(0.0, Double::sum) >= 50000;
  }

  @Then
  public void then() {
    rate = rate - (rate * 0.25);
  }
}
