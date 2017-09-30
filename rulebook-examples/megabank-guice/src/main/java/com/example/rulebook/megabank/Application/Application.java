package com.example.rulebook.megabank.Application;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Application {
  public static void main(String[] args) {
    NameValueReferableMap<ApplicantBean> facts = new FactMap<>();
    ApplicantBean applicant1 = new ApplicantBean(650, 20000, true);
    ApplicantBean applicant2 = new ApplicantBean(620, 30000, true);
    facts.put(new Fact<>(applicant1));
    facts.put(new Fact<>(applicant2));

    Injector injector = Guice.createInjector(new RuleBookModule());
    RuleBookService service = injector.getInstance(RuleBookService.class);
    System.out.println("Applicant qualified for the following rate: " + service.run(facts));
  }
}
