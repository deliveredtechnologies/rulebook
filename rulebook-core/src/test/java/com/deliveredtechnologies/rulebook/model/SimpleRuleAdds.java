package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.lang.RuleBuilder;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

public class SimpleRuleAdds extends RuleBookAuditor {

  public SimpleRuleAdds() {
    super(new CoRRuleBook<>());
  }

  @Override
  public void defineRules() {
        
    addRule(
              RuleBuilder.create().withName("Rule1")
              .when(facts -> true)
              .then(facts -> { } )
              .build()
    );

    addRule(
              RuleBuilder.create().withName("Rule2")
              .when(facts -> false)
              .then(facts -> { } )
              .build()
    );

    addRule(
              RuleBuilder.create().withName("Rule3")
              .when(facts -> true)
              .then(facts -> { } )
              .build()
    );
  }

}
