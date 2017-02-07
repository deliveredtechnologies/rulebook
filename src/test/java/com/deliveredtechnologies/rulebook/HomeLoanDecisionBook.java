package com.deliveredtechnologies.rulebook;

import java.math.BigDecimal;

import static com.deliveredtechnologies.rulebook.RuleState.BREAK;
import static com.deliveredtechnologies.rulebook.RuleState.NEXT;

/**
 * Created by clong on 2/7/17.
 */
public class HomeLoanDecisionBook extends DecisionBook<ApplicantBean, Boolean> {

    @Override
    protected void defineRules() {
        //if there are more than 3 applicants then the loan is denied
        addRule(StandardRule.create(ApplicantBean.class)
                .when(factMap -> factMap.size() > 3)
                .then(f -> BREAK)
        );

        //if everyone has a credit score of over 700 then the loan is approved
        addRule(StandardDecision.create(ApplicantBean.class, Boolean.class)
                .when(factMap -> factMap.values().stream()
                        .allMatch(applicantFact -> applicantFact.getValue().getCreditScore() >= 700))
                .then((f, result) -> {
                    result.setValue(true);
                    return NEXT;
                })
        );

        //if everyone has cash on hand of greater than or equal to $50,000 then the loan is approved
        addRule(StandardDecision.create(ApplicantBean.class, Boolean.class)
                .when(factMap -> factMap.values().stream()
                        .allMatch(applicantFact -> applicantFact.getValue().getCashOnHand().compareTo(BigDecimal.valueOf(50000)) >= 0))
                .then((f, result) -> {
                    result.setValue(true);
                    return BREAK;
                })
        );
    }
}
