package com.example.rulebook.megabank.rules.base;

import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;
import com.example.rulebook.megabank.ApplicantBean;

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
