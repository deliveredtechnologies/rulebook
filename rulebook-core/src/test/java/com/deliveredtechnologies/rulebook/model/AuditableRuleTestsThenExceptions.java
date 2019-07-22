package com.deliveredtechnologies.rulebook.model;

import java.io.InvalidClassException;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.model.pojotestrules.exceptions.RuleWhereThenThrowsAnExceptionButNoStopOnFailure;
import com.deliveredtechnologies.rulebook.model.pojotestrules.exceptions.RuleWhereThenThrowsAnExceptionErrorOnFailure;
import com.deliveredtechnologies.rulebook.model.pojotestrules.exceptions.RuleWhereThenThrowsAnExceptionStopOnFailure;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import com.deliveredtechnologies.rulebook.model.runner.RuleAdapter;

public class AuditableRuleTestsThenExceptions {

  @Test
  @SuppressWarnings("unchecked")
  public void pojoRulesSecondRuleDoesntFireIfThereIsAnExceptionInThenAndStopOnFailureStatusExecuted() {
    RuleBook ruleBook = new RuleBookAuditor<>( new CoRRuleBook<>());
    Rule rule2 = Mockito.mock(Rule.class);
    Mockito.when(rule2.getRuleState()).thenReturn(RuleState.NEXT);

    try {
      RuleAdapter ruleAdapter = new RuleAdapter( new RuleWhereThenThrowsAnExceptionStopOnFailure() );

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
  public void pojoRulesSecondRuleFiresIfThereIsAnExceptionInThenOnTheFirstButThereIsNoStopOnFailureStatusExecuted() {
    RuleBook ruleBook = new RuleBookAuditor<>( new CoRRuleBook<>());
    Rule rule2 = Mockito.mock(Rule.class);
    Mockito.when(rule2.getRuleState()).thenReturn(RuleState.NEXT);

    try {
      RuleAdapter ruleAdapter = new RuleAdapter( new RuleWhereThenThrowsAnExceptionButNoStopOnFailure() );

      AuditableRule auditableRule1 = new AuditableRule<String , String>(ruleAdapter);
      AuditableRule auditableRule2 = new AuditableRule<String , String>(rule2 , "Rule2");
      Auditor auditor = Mockito.mock(Auditor.class);

      ruleBook.addRule(auditableRule1);
      ruleBook.addRule(auditableRule2);

      auditableRule1.setAuditor(auditor);
      auditableRule2.setAuditor(auditor);
      
      ruleBook.run(new FactMap());
      Mockito.verify(rule2,Mockito.times(1)).invoke(Mockito.any(NameValueReferableMap.class));
      Mockito.verify(auditor, Mockito.times(1)).updateRuleStatus(auditableRule1, RuleStatus.EXECUTED);
    } catch (Exception exc) {
      Assert.fail(exc.getMessage());
    }
  }

  @Test(expected = RuleException.class)
  @SuppressWarnings("unchecked")
  public void pojoRulesSecondRuleDoesntFireIfThereIsAnExceptionInThenAndErrorOnFailureStatusExecutedExceptionThrown() {
    RuleBook ruleBook = new RuleBookAuditor<>( new CoRRuleBook<>());
    Rule rule2 = Mockito.mock(Rule.class);
    Mockito.when(rule2.getRuleState()).thenReturn(RuleState.NEXT);

    try {
      RuleAdapter ruleAdapter = new RuleAdapter( new RuleWhereThenThrowsAnExceptionErrorOnFailure() );

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

    } catch (InvalidClassException exc) {
      Assert.fail(exc.getMessage());
    }
  }

}
