package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.*;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Tests for {@link AuditableRule}.
 */
public class
AuditableRuleTest {
  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesSetRuleNames() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    Auditable auditable = new AuditableRule<String, String>(rule, "My Special Rule");

    Assert.assertEquals("My Special Rule", auditable.getName());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesGetClassNameAsDefaultName() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    Auditable auditable = new AuditableRule<String, String>(rule);

    Assert.assertEquals("AuditableRule", auditable.getName());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesRegisterThemselvesWithAuditors() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    Auditable auditable = new AuditableRule<String, String>(rule);

    Auditor auditor = Mockito.mock(Auditor.class);
    auditable.setAuditor(auditor);

    Mockito.verify(auditor, Mockito.times(1)).registerRule(auditable);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesDelegateSetFactsToTheirRule() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    FactMap facts = Mockito.mock(FactMap.class);
    Mockito.when(rule.getFacts()).thenReturn(facts);

    auditableRule.setFacts(facts);

    Assert.assertEquals(facts, auditableRule.getFacts());
    Mockito.verify(rule, Mockito.times(1)).setFacts(facts);
    Mockito.verify(rule, Mockito.times(1)).getFacts();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesDelegateAddFactsToTheirRule() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    FactMap facts = Mockito.mock(FactMap.class);

    auditableRule.addFacts(facts);

    Mockito.verify(rule, Mockito.times(1)).addFacts(facts);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesDelegateAddFactsArrayToTheirRule() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    Fact[] facts = {new Fact("fact", "value")};

    auditableRule.addFacts(facts);

    Mockito.verify(rule, Mockito.times(1)).addFacts(facts);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesDelegateSetConditionToTheirRule() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    Predicate<NameValueReferableTypeConvertibleMap<String>> condition = Mockito.mock(Predicate.class);
    Mockito.when(rule.getCondition()).thenReturn(condition);

    auditableRule.setCondition(condition);

    Assert.assertEquals(condition, auditableRule.getCondition());
    Mockito.verify(rule, Mockito.times(1)).setCondition(condition);
    Mockito.verify(rule, Mockito.times(1)).getCondition();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesDelegateSetRuleStateToTheirRule() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    RuleState ruleState = RuleState.NEXT;
    Mockito.when(rule.getRuleState()).thenReturn(ruleState);

    auditableRule.setRuleState(ruleState);

    Assert.assertEquals(ruleState, auditableRule.getRuleState());
    Mockito.verify(rule, Mockito.times(1)).setRuleState(ruleState);
    Mockito.verify(rule, Mockito.times(1)).getRuleState();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesDelegateActionsToTheirRule() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    BiConsumer<NameValueReferableTypeConvertibleMap<String>, Result<String>> biConsumer = Mockito.mock(BiConsumer.class);
    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer = Mockito.mock(Consumer.class);
    Object[] array = {consumer, biConsumer};
    List<Object> list = Arrays.asList(array);

    Mockito.when(rule.getActions()).thenReturn(list);

    auditableRule.addAction(consumer);
    auditableRule.addAction(biConsumer);

    Assert.assertEquals(auditableRule.getActions(), list);
    Mockito.verify(rule, Mockito.times(1)).addAction(consumer);
    Mockito.verify(rule, Mockito.times(1)).addAction(biConsumer);
    Mockito.verify(rule, Mockito.times(1)).getActions();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesDelegateFactNameFiltersTheirRule() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    String[] filters = {"filter"};

    auditableRule.addFactNameFilter(filters);

    Mockito.verify(rule, Mockito.times(1)).addFactNameFilter(filters);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesUpdateAuditorToExecutedWhenInvokePasses() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    Auditor auditor = Mockito.mock(Auditor.class);

    auditableRule.setAuditor(auditor);

    Mockito.when(rule.invoke(Mockito.any(NameValueReferableMap.class))).thenReturn(true);

    auditableRule.invoke(new FactMap());

    Mockito.verify(auditor, Mockito.times(1)).updateRuleStatus(auditableRule, RuleStatus.EXECUTED);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesUpdateAuditorToExecutedWhenInvokeFails() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    Auditor auditor = Mockito.mock(Auditor.class);

    auditableRule.setAuditor(auditor);

    Mockito.when(rule.invoke(Mockito.any(NameValueReferableMap.class))).thenReturn(false);

    auditableRule.invoke(new FactMap());

    Mockito.verify(auditor, Mockito.times(1)).updateRuleStatus(auditableRule, RuleStatus.SKIPPED);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void auditableRulesDelegateResultToTheirRule() {
    Rule<String, String> rule = Mockito.mock(Rule.class);
    AuditableRule auditableRule = new AuditableRule<String, String>(rule);
    Result<String> result = new Result<>("result");
    Mockito.when(rule.getResult()).thenReturn(Optional.of(result));

    auditableRule.setResult(result);

    Assert.assertEquals(auditableRule.getResult().get(), result);
    Mockito.verify(rule, Mockito.times(1)).setResult(result);
    Mockito.verify(rule, Mockito.times(1)).getResult();
  }
}
