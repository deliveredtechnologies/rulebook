package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;
import org.springframework.beans.factory.FactoryBean;

import java.util.Optional;

/**
 * Spring FactoryBean for RuleBooks.
 */
public class RuleBookFactoryBean implements FactoryBean<RuleBook> {

  private Class<? extends RuleBook> _ruleBookType;
  private String _package;

  @Deprecated
  public RuleBookFactoryBean(Class<? extends RuleBook> ruleBookType, String pkg) {
    _ruleBookType = ruleBookType;
    _package = pkg;
  }

  public RuleBookFactoryBean(Class<? extends RuleBook> ruleBookType) {
    this(ruleBookType,null);
  }

  @Deprecated
  public RuleBookFactoryBean(String pkg) {
    this(null, pkg);
  }

  @Override
  public RuleBook getObject() throws Exception {
    if (_package != null) {
      if (_ruleBookType != null) {
        return new RuleBookRunner(RuleBookBuilder.create(_ruleBookType).build(), _package);
      }
      return new RuleBookRunner(_package);
    }
    if (_ruleBookType != null) {
      return RuleBookBuilder.create(_ruleBookType).build();
    }
    return new CoRRuleBook();
  }

  @Override
  public Class<?> getObjectType() {
    return _ruleBookType;
  }

  @Override
  public boolean isSingleton() {
    return false;
  }
}
