package com.deliveredtechnologies.rulebook.model;

/**
 * Created by clong on 9/2/17.
 */
public interface Auditable {

  void setAuditor(Auditor auditor);

  /**
   * Gets the name of the audited rule.
   * @return  the the class name by default.
   */
  default String getName() {
    return this.getClass().getSimpleName();
  }
}
