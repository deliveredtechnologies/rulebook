package com.example.rulebook.megabank;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;

import java.util.List;

import static com.deliveredtechnologies.rulebook.RuleState.BREAK;
import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.ERROR_ON_FAILURE;

@Rule(order = 2, ruleChainAction = ERROR_ON_FAILURE)
public class LowCreditScoreRule {
  @Given
  private List<ApplicantBean> applicants;

  @Result
  private double rate;

  @When
  public boolean when() throws InvalidCreditScoreException {
    if (applicants.stream().anyMatch(applicant -> applicant.getCreditScore() < 300)) {
      throw new InvalidCreditScoreException("An applicant was registered with a credit score less than 300!");
    }
    return applicants.stream().allMatch(applicant -> applicant.getCreditScore() < 600);
  }

  @Then
  public RuleState then() {
    rate *= 4;
    return BREAK;
  }
}
