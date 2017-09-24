package com.example.rulebook.megabank.Application;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;

import java.util.List;

import static com.deliveredtechnologies.rulebook.RuleState.BREAK;

@Rule(order = 2)
public class LowCreditScoreRule {
  @Given
  private List<ApplicantBean> applicants;

  @Result
  private double rate;

  @When
  public boolean when() {
    return applicants.stream().allMatch(applicant -> applicant.getCreditScore() < 600);
  }

  @Then
  public RuleState then() {
    rate *= 4;
    return BREAK;
  }
}
