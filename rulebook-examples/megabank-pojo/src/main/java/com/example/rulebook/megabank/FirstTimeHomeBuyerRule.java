package com.example.rulebook.megabank;

import com.deliveredtechnologies.rulebook.annotation.*;

import java.util.List;

@Rule(order = 4)
public class FirstTimeHomeBuyerRule {
  @Given
  List<ApplicantBean> applicants;

  @Result
  private double rate;

  @When
  public boolean when() {
    return
      applicants.stream().anyMatch(applicant -> applicant.isFirstTimeHomeBuyer());
  }

  @Then
  public void then() {
    rate = rate - (rate * 0.20);
  }
}
