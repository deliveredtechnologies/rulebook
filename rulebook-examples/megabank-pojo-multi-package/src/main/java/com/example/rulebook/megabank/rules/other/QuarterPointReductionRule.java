package com.example.rulebook.megabank.rules.other;

import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;
import com.example.rulebook.megabank.ApplicantBean;

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
