package com.example.rulebook.megabank;

import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

public class HomeLoanRateRuleBook extends CoRRuleBook<Double> {
  @Override
  public void defineRules() {
    //credit score under 600 gets a 4x rate increase
    addRule(RuleBuilder.create().withResultType(Double.class)
      .when(facts -> facts.getIntVal("Credit Score") < 600)
      .then((facts, result) -> result.setValue(result.getValue() * 4))
      .stop()
      .build());

    //credit score between 600 and 700 pays a 1 point increase
    addRule(RuleBuilder.create().withResultType(Double.class)
      .when(facts -> facts.getIntVal("Credit Score") < 700)
      .then((facts, result) -> result.setValue(result.getValue() + 1))
      .build());

    //credit score is 700 and they have at least $25,000 cash on hand
    addRule(RuleBuilder.create().withResultType(Double.class)
      .when(facts ->
        facts.getIntVal("Credit Score") >= 700 &&
        facts.getDblVal("Cash on Hand") >= 25000)
      .then((facts, result) -> result.setValue(result.getValue() - 0.25))
      .build());

    //first time homebuyers get 20% off their rate (except if they have a creditScore < 600)
    addRule(RuleBuilder.create().withFactType(Boolean.class).withResultType(Double.class)
      .when(facts -> facts.getOne())
      .then((facts, result) -> result.setValue(result.getValue() * 0.80))
      .build());
  }
}
