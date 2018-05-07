package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Runs the POJO Rules that can be Spring aware beans in a specified package as a RuleBook.
 *
 * <p>POJO rule classes in the package may or may not have the @Component/@Service/etc. annotation. If they
 * do, the bean will be fetched and @Autowired variables will be populated. If not, an instance of the
 * POJO rule will be created and used.
 */
public class SpringAwareRuleBookRunner extends RuleBookRunner implements ApplicationContextAware {

  private ApplicationContext _applicationContext;

  private static Logger LOGGER = LoggerFactory.getLogger(SpringAwareRuleBookRunner.class);

  public SpringAwareRuleBookRunner(String rulePackage) {
    super(rulePackage);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this._applicationContext = applicationContext;
  }

  @Override
  protected Object getRuleInstance(Class<?> rule) {
    if (_applicationContext == null) {
      throw new IllegalStateException("Cannot instantiate RuleBookRunner because "
              + "Spring application context is not available.");
    }

    try {
      // Spring bean POJO rule found
      return _applicationContext.getBean(rule);
    } catch (BeansException e) {
      // POJO rule isn't a Spring bean
      return super.getRuleInstance(rule);
    }
  }
}
