package com.example.rulebook.megabank;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.Auditor;
import com.deliveredtechnologies.rulebook.model.RuleBook;

public class Application {
  public static void main(String[] args) {
    RuleBook homeLoanRateRuleBook = RuleBookBuilder.create(HomeLoanRateRuleBook.class).withResultType(Double.class)
      .withDefaultResult(4.5)
      .build();

    NameValueReferableMap facts = new FactMap();
    facts.setValue("Credit Score", 650);
    facts.setValue("Cash on Hand", 20000);
    facts.setValue("First Time Homebuyer", true);

    homeLoanRateRuleBook.run(facts);

    homeLoanRateRuleBook.getResult().ifPresent(result -> System.out.println("Applicant qualified for the following rate: " + result));
  }
}
