package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Predicate;

/**
 * Runs the POJO Rules that can be Spring aware beans in a specified package as a RuleBook.
 *
 * <p>POJO rule classes in the package may or may not have the @Component/@Service/etc. annotation. If they
 * do, the bean will be fetched and @Autowired variables will be populated. If not, an instance of the
 * POJO rule will be created and used.
 */
public class SpringAwareRuleBookRunner extends RuleBookRunner implements ApplicationContextAware {

  private static Logger LOGGER = LoggerFactory.getLogger(SpringAwareRuleBookRunner.class);


  private ApplicationContext _applicationContext;

  private boolean allowNonSpringRules = true;

  public SpringAwareRuleBookRunner(String rulePackage) {
    super(rulePackage);
  }

  public SpringAwareRuleBookRunner(Class<? extends RuleBook> ruleBookClass, String rulePackage) {
    super(ruleBookClass, rulePackage);
  }

  public SpringAwareRuleBookRunner(String rulePackage, Predicate<String> subPkgMatch) {
    super(rulePackage, subPkgMatch);
  }

  public SpringAwareRuleBookRunner(
      Class<? extends RuleBook> ruleBookClass, String rulePackage, Predicate<String> subPkgMatch) {
    super(ruleBookClass, rulePackage, subPkgMatch);
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
      LOGGER.warn("Rule {} could not be loaded as a spring bean", rule);
      if(allowNonSpringRules){
        // POJO rule isn't a Spring bean
        LOGGER.info("Loading rule as POJO rule {}", rule);
        return super.getRuleInstance(rule);
      }else{
        throw new IllegalStateException("Cannot instantiate rule "+rule.toString()+" as a spring bean");
      }

    }
  }

  public boolean isAllowNonSpringRules() {
    return allowNonSpringRules;
  }

  /**
   * Whether or not this runner should try to instantiate POJO rules or only Spring Beans rules
   * @param allowNonSpringRules {@code true} allows POJO rules while {@code false} throws {@link IllegalStateException} for non spring rules
   */
  public void setAllowNonSpringRules(boolean allowNonSpringRules) {
    this.allowNonSpringRules = allowNonSpringRules;
  }
}
