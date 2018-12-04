package com.example.rulebook.megabank;

import com.deliveredtechnologies.rulebook.annotation.*;

import java.util.List;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.ERROR_ON_FAILURE;

@Rule(order = 3, ruleChainAction = ERROR_ON_FAILURE)
public class QuarterPointReductionRule {
  @Given
  List<ApplicantBean> applicants;

  @Result
  private double rate;

  @When
  public boolean when() throws InvalidCreditScoreException {
    if (applicants.stream().anyMatch(applicant -> applicant.getCreditScore() > 850)) {
      throw new InvalidCreditScoreException("An applicant was registered with a credit score greater than 850!");
    }
    return
      applicants.stream().anyMatch(applicant -> applicant.getCreditScore() >= 700) &&
      applicants.stream().map(applicant -> applicant.getCashOnHand()).reduce(0.0, Double::sum) >= 50000;
  }

  @Then
  public void then() {
      rate = rate - (rate * 0.25);
  }
}
