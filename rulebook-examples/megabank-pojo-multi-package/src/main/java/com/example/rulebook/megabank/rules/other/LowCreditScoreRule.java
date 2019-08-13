package com.example.rulebook.megabank.rules.other;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;
import com.example.rulebook.megabank.ApplicantBean;

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
