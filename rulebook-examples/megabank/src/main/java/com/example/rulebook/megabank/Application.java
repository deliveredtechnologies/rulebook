package com.example.rulebook.megabank;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;

public class Application {
  public static void main(String[] args) {
    RuleBook homeLoanRateRuleBook = RuleBookBuilder.create(HomeLoanRateRuleBook.class).withResultType(Double.class)
      .withDefaultResult(4.5)
      .build();
    NameValueReferableMap facts = new FactMap();
    facts.setValue("applicant", new ApplicantBean(650, 20000.0, true));
    homeLoanRateRuleBook.run(facts);

    homeLoanRateRuleBook.getResult().ifPresent(result -> System.out.println("Applicant qualified for the following rate: " + result));
  }
}
