package com.example.rulebook.megabank;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.Auditor;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleException;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;

public class Application {
  public static void main(String[] args) {
    RuleBook ruleBook = new RuleBookRunner("com.example.rulebook.megabank");

    NameValueReferableMap<ApplicantBean> facts = new FactMap<>();
    ApplicantBean applicant1 = new ApplicantBean(950, 20000, true);
    ApplicantBean applicant2 = new ApplicantBean(620, 30000, true);
    facts.put(new Fact<>(applicant1));
    facts.put(new Fact<>(applicant2));

    ruleBook.setDefaultResult(4.5);
    try {
      ruleBook.run(facts);
    }
    catch (RuleException ex) {
      System.out.println(ex.getCause().getMessage());
    }

    ruleBook.getResult().ifPresent(result -> System.out.println("Applicant qualified for the following rate: " + result));

    //display rules audit results
    Auditor auditor = (Auditor)ruleBook;
    System.out.println("ApplicantNumberRule status: " + auditor.getRuleStatus("ApplicantNumberRule"));
    System.out.println("LowCreditScoreRule status: " + auditor.getRuleStatus("LowCreditScoreRule"));
    System.out.println("ExtraPointRule status: " + auditor.getRuleStatus("ExtraPointRule"));
    System.out.println("QuarterPointReductionRule status: " + auditor.getRuleStatus("QuarterPointReductionRule"));
    System.out.println("FirstTimeHomeBuyerRule status: " + auditor.getRuleStatus("FirstTimeHomeBuyerRule"));
  }
}
