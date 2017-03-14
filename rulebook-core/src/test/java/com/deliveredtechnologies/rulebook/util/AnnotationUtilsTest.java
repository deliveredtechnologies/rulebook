package com.deliveredtechnologies.rulebook.util;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;
import com.deliveredtechnologies.rulebook.runner.SampleRuleWithResult;
import com.deliveredtechnologies.rulebook.runner.SubRuleAnnotation;
import com.deliveredtechnologies.rulebook.runner.SubRuleWithResult;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedField;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedFields;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedMethod;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedMethods;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;

/**
 * Tests for {@link com.deliveredtechnologies.rulebook.util.AnnotationUtils}
 */
public class AnnotationUtilsTest {
  @Test
  public void getAnnotatedFieldsShouldFindAnnotatedFields() {
    List<Field> givenFields = getAnnotatedFields(Given.class, SampleRuleWithResult.class);
    List<Field> resultFields = getAnnotatedFields(Result.class, SampleRuleWithResult.class);
    Assert.assertEquals(10, givenFields.size());
    Assert.assertEquals(1, resultFields.size());
  }

  @Test
  public void getAnnotatedFieldsShouldFindAnnotatedFieldsInParentClasses() {
    List<Field> givenFields = getAnnotatedFields(Given.class, SubRuleWithResult.class);
    List<Field> resultFields = getAnnotatedFields(Result.class, SubRuleWithResult.class);
    Assert.assertEquals(11, givenFields.size());
    Assert.assertEquals(1, resultFields.size());
  }

  @Test
  public void getAnnotatedFieldShouldFindFirstAnnotatedField() {
    Optional<Field> givenField = getAnnotatedField(Given.class, SampleRuleWithResult.class);
    Assert.assertEquals(givenField.get().getType(), Fact.class);
    Assert.assertEquals(givenField.get().getName(), "_fact1");
  }

  @Test
  public void getAnnotatedFieldShouldFindFirsAnnotatedFieldInCurrentClassBeforeParent() {
    Optional<Field> givenField = getAnnotatedField(Given.class, SubRuleWithResult.class);
    Assert.assertEquals(givenField.get().getType(), String.class);
    Assert.assertEquals(givenField.get().getName(), "_subFact");
  }

  @Test
  public void getAnnotatedMethodsShouldFindAnnotatedMethods() {
    List<Method> thenMethods = getAnnotatedMethods(Then.class, SampleRuleWithResult.class);
    List<Method> whenMethods = getAnnotatedMethods(When.class, SampleRuleWithResult.class);
    Assert.assertEquals(1, thenMethods.size());
    Assert.assertEquals(1, whenMethods.size());
  }

  @Test
  public void getAnnotatedMethodsShouldFindAnnotatedMethodsInParent() {
    List<Method> thenMethods = getAnnotatedMethods(Then.class, SubRuleWithResult.class);
    List<Method> whenMethods = getAnnotatedMethods(When.class, SubRuleWithResult.class);
    Assert.assertEquals(1, thenMethods.size());
    Assert.assertEquals(2, whenMethods.size());
  }

  @Test
  public void getAnnotatedMethodShouldFindFirstMethod() {
    Optional<Method> whenMethod = getAnnotatedMethod(When.class, SubRuleWithResult.class);
    Assert.assertEquals(whenMethod.get().getName(), "condition");
  }

  @Test
  public void getAnnotationShouldFindTheAnnotationOfAnAnnotation() {
    Rule ruleAnnotation = getAnnotation(Rule.class, SubRuleWithResult.class);
    SubRuleAnnotation subRuleAnnotation = getAnnotation(SubRuleAnnotation.class, SubRuleWithResult.class);
    Assert.assertNotNull(ruleAnnotation);
    Assert.assertNotNull(subRuleAnnotation);
  }
}
