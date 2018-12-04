package com.deliveredtechnologies.rulebook.model;

/**
 * Enumeration for what the supporting rule chain's response is to rule failures and false conditions.
 */
public enum RuleChainActionType {
  STOP_ON_FAILURE,
  ERROR_ON_FAILURE,
  CONTINUE_ON_FAILURE
}
