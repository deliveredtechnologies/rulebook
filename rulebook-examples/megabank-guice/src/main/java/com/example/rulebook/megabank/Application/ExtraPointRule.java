package com.example.rulebook.megabank.Application;

import com.deliveredtechnologies.rulebook.annotation.*;

import java.util.List;

@Rule(order = 3)
public class ExtraPointRule {
  @Given
  List<ApplicantBean> applicants;

  @Result
  private double rate;

  @When
  public boolean when() {
    return applicants.stream()
        .anyMatch(applicant -> applicant.getCreditScore() < 700 && applicant.getCreditScore() >= 600);
  }

  @Then
  public void then() {
    rate += 1;
  }
}
