package com.deliveredtechnologies.rulebook.model;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.model.pojotestrules.failsandsucceeds.RuleWhereWhenIsFailAndStopOnFailure;
import com.deliveredtechnologies.rulebook.model.pojotestrules.failsandsucceeds.RuleWhereWhenIsSuccessAnThenHasABreakNoStop;
import com.deliveredtechnologies.rulebook.model.pojotestrules.failsandsucceeds.RuleWhereWhenIsSuccessAnThenHasABreakStopOnFailure;
import com.deliveredtechnologies.rulebook.model.pojotestrules.failsandsucceeds.RuleWhereWhenIsSuccessAndStopOnFailure;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import com.deliveredtechnologies.rulebook.model.runner.RuleAdapter;

public class AuditableRuleTestsNoExceptions {

  @Test
  @SuppressWarnings("unchecked")
  public void pojoRulesSecondRuleFiresAndBothSuccessIfWhenIsSuccessfull() {
    RuleBook ruleBook = new RuleBookAuditor<>( new CoRRuleBook<>());
    Rule rule2 = Mockito.mock(Rule.class);
    Mockito.when(rule2.getRuleState()).thenReturn(RuleState.NEXT);

    try {
      RuleAdapter ruleAdapter = new RuleAdapter( new RuleWhereWhenIsSuccessAndStopOnFailure() );

      AuditableRule auditableRule1 = new AuditableRule<String , String>(ruleAdapter);
      AuditableRule auditableRule2 = new AuditableRule<String , String>(rule2);
      Auditor auditor = Mockito.mock(Auditor.class);

      ruleBook.addRule(auditableRule1);
      ruleBook.addRule(auditableRule2);

      auditableRule1.setAuditor(auditor);
      auditableRule2.setAuditor(auditor);

      ruleBook.run(new FactMap());
      Mockito.verify(rule2,Mockito.times(1)).invoke(Mockito.any(NameValueReferableMap.class));
      Mockito.verify(auditor, Mockito.times(1)).updateRuleStatus(auditableRule1, RuleStatus.EXECUTED);
      Mockito.verify(auditor, Mockito.times(1)).updateRuleStatus(auditableRule2, RuleStatus.SKIPPED);

    } catch (Exception exc) {
      Assert.fail(exc.getMessage());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void pojoRulesSecondRuleDoesntFireIfWhenIsFailAndStopOnFailure() {
    RuleBook ruleBook = new RuleBookAuditor<>( new CoRRuleBook<>());
    Rule rule2 = Mockito.mock(Rule.class);
    Mockito.when(rule2.getRuleState()).thenReturn(RuleState.NEXT);

    try {
      RuleAdapter ruleAdapter = new RuleAdapter( new RuleWhereWhenIsFailAndStopOnFailure() );

      AuditableRule auditableRule1 = new AuditableRule<String , String>(ruleAdapter);
      AuditableRule auditableRule2 = new AuditableRule<String , String>(rule2);
      Auditor auditor = Mockito.mock(Auditor.class);

      ruleBook.addRule(auditableRule1);
      ruleBook.addRule(auditableRule2);

      auditableRule1.setAuditor(auditor);
      auditableRule2.setAuditor(auditor);

      ruleBook.run(new FactMap());
      Mockito.verify(rule2,Mockito.times(0)).invoke(Mockito.any(NameValueReferableMap.class));
      Mockito.verify(auditor, Mockito.times(1)).updateRuleStatus(auditableRule1, RuleStatus.EXECUTED);
    } catch (Exception exc) {
      Assert.fail(exc.getMessage());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  // This is the current logic, if we have STOP_OF FAILURE the State doesn't matter
  // as it checks it in invoke (check for STOP_ON_FAILURE) and then sets the State to NEXT anyway
  // because it assumes that STOP_ON_FAILURE relates to the when() 
  // for this to work we have to remove the STOP_ON_FAILURE , see below test
  public void pojoRulesSecondRuleFiresIfWhenIsTrueButThenHasABreakInThenButHasAStopOnFailure() {
    RuleBook ruleBook = new RuleBookAuditor<>( new CoRRuleBook<>());
    Rule rule2 = Mockito.mock(Rule.class);
    Mockito.when(rule2.getRuleState()).thenReturn(RuleState.NEXT);

    try {
      RuleAdapter ruleAdapter = new RuleAdapter( new RuleWhereWhenIsSuccessAnThenHasABreakStopOnFailure() );

      AuditableRule auditableRule1 = new AuditableRule<String , String>(ruleAdapter);
      AuditableRule auditableRule2 = new AuditableRule<String , String>(rule2);
      Auditor auditor = Mockito.mock(Auditor.class);

      ruleBook.addRule(auditableRule1);
      ruleBook.addRule(auditableRule2);

      auditableRule1.setAuditor(auditor);
      auditableRule2.setAuditor(auditor);

      ruleBook.run(new FactMap());
      Mockito.verify(rule2,Mockito.times(1)).invoke(Mockito.any(NameValueReferableMap.class));
      Mockito.verify(auditor, Mockito.times(1)).updateRuleStatus(auditableRule1, RuleStatus.EXECUTED);
      Mockito.verify(auditor, Mockito.times(1)).updateRuleStatus(auditableRule2, RuleStatus.SKIPPED);
    } catch (Exception exc) {
      Assert.fail(exc.getMessage());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  // See Above
  // The second rule won't fire which is correct because Then() has a RuleState.BREAK
  // but you need to Remove sTOP_ON_FAILURE (The STOP_ONFAILURE) related to when() on really
  public void pojoRulesSecondRuleDoesntFireIfWhenIsTrueButThenHasABreakInThenAndWeDontHaveAStopOnFailure() {
    RuleBook ruleBook = new RuleBookAuditor<>( new CoRRuleBook<>());
    Rule rule2 = Mockito.mock(Rule.class);
    Mockito.when(rule2.getRuleState()).thenReturn(RuleState.NEXT);

    try {
      RuleAdapter ruleAdapter = new RuleAdapter( new RuleWhereWhenIsSuccessAnThenHasABreakNoStop() );

      AuditableRule auditableRule1 = new AuditableRule<String , String>(ruleAdapter);
      AuditableRule auditableRule2 = new AuditableRule<String , String>(rule2);
      Auditor auditor = Mockito.mock(Auditor.class);

      ruleBook.addRule(auditableRule1);
      ruleBook.addRule(auditableRule2);

      auditableRule1.setAuditor(auditor);
      auditableRule2.setAuditor(auditor);

      ruleBook.run(new FactMap());
      Mockito.verify(rule2,Mockito.times(0)).invoke(Mockito.any(NameValueReferableMap.class));
      Mockito.verify(auditor, Mockito.times(1)).updateRuleStatus(auditableRule1, RuleStatus.EXECUTED);
    } catch (Exception exc) {
      Assert.fail(exc.getMessage());
    }
  }

}
