package com.example.rulebook.megabank;

import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

public class HomeLoanRateRuleBook extends CoRRuleBook<Double> {
  @Override
  public void defineRules() {
    //credit score under 600 gets a 4x rate increase
    addRule(RuleBuilder.create().withFactType(ApplicantBean.class).withResultType(Double.class)
      .when(facts -> facts.getOne().getCreditScore() < 600)
      .then((facts, result) -> result.setValue(result.getValue() * 4))
      .stop()
      .build());

    //credit score between 600 and 700 pays a 1 point increase
    addRule(RuleBuilder.create().withFactType(ApplicantBean.class).withResultType(Double.class)
      .when(facts -> facts.getOne().getCreditScore() < 700)
      .then((facts, result) -> result.setValue(result.getValue() + 1))
      .build());

    //credit score is 700 and they have at least $25,000 cash on hand
    addRule(RuleBuilder.create().withFactType(ApplicantBean.class).withResultType(Double.class)
      .when(facts ->
        facts.getOne().getCreditScore() >= 700 &&
        facts.getOne().getCashOnHand() >= 25000)
      .then((facts, result) -> result.setValue(result.getValue() - 0.25))
      .build());

    //first time homebuyers get 20% off their rate (except if they have a creditScore < 600)
    addRule(RuleBuilder.create().withFactType(ApplicantBean.class).withResultType(Double.class)
      .when(facts -> facts.getOne().isFirstTimeHomeBuyer())
      .then((facts, result) -> result.setValue(result.getValue() * 0.80))
      .build());
  }
}
