package com.deliveredtechnologies.rulebook.model;

import java.util.Map;

/**
 * Created by clong on 9/2/17.
 */
public interface Auditor {
  void registerRule(Auditable rule);
  void updateRuleStatus(Auditable rule, RuleStatus status);
  RuleStatus getRuleStatus(String name);
  Map<String, RuleStatus> getRuleStatusMap();
}
