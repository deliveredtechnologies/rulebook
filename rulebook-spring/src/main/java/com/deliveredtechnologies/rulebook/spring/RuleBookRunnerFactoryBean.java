package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleBookFactory;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunnerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Spring Factory for RuleBookRunnerFactory.
 * This allows a RuleBookRunnerFactory to create new RuleBooks upon use/invocation.
 */
public class RuleBookRunnerFactoryBean implements FactoryBean<RuleBookFactory> {

  private RuleBookFactory _factory;

  public RuleBookRunnerFactoryBean(Class<? extends RuleBook> ruleBookType, String pkg) {
    _factory = new RuleBookRunnerFactory(ruleBookType, pkg);
  }

  @Override
  public RuleBookFactory getObject() throws Exception {
    return _factory;
  }

  @Override
  public Class<?> getObjectType() {
    return RuleBookRunnerFactory.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
