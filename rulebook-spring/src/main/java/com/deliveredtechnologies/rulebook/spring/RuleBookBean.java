package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.DecisionBook;
import com.deliveredtechnologies.rulebook.runner.RuleAdapter;
import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by clong on 2/23/17.
 *
 */
public class RuleBookBean extends DecisionBook {
  private static Logger LOGGER;

  @Override
  protected void defineRules() {
    //intentionally left blank
  }

  public void addRule(Object rule) {
    try {
      super.addRule(new RuleAdapter(rule));
    } catch (IOException ex) {
      LOGGER.error("Unable to add rule!", ex);
    }
  }
}
