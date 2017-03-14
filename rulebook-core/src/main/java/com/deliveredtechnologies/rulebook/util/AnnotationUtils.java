package com.deliveredtechnologies.rulebook.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AnnotationUtils is a utility class for annotations.
 */
public class AnnotationUtils {
  private AnnotationUtils() {}

  /**
   * Method getAnnotatedFields gets the fields annotated of a specific type from the class and its parent classes.
   * The List is in order of closest parent => current obj fields, parent obj fields, etc.
   * @param annotationClass the annotation type
   * @param clazz           the class that is expected to be annotated
   * @return                a {@link Set} of {@link Field} objects annotated with the annotatedClass
   */
  @SuppressWarnings("unchecked")
  public static List<Field> getAnnotatedFields(Class annotationClass, Class clazz) {
    List<Field> fields = new ArrayList<>();
    if (clazz == Object.class) {
      return fields;
    }
    fields.addAll((List<Field>)
        Arrays.stream(clazz.getDeclaredFields())
        .filter(field -> field.getAnnotation(annotationClass) != null)
        .collect(Collectors.toList()));
    fields.addAll(getAnnotatedFields(annotationClass, clazz.getSuperclass()));
    return fields;
  }

  /**
   * Method getAnnotatedField the first annotated field of the type of annotation specified.
   * @param annotationClass the type of the annotation
   * @param clazz           the annotated class
   * @return                the first annotated field found in clazz of the type annotationClass
   */
  public static Optional<Field> getAnnotatedField(Class annotationClass, Class clazz) {
    List<Field> fields = getAnnotatedFields(annotationClass, clazz);
    return Optional.ofNullable(fields.size() > 0 ? fields.get(0) : null);
  }

  /**
   * Method getAnnotatedMethods gets the methods annotated of a specific type from the class and its parent classes.
   * The List is in order of closest parent => current obj methods, parent obj methods, etc.
   * @param annotationClass the type of the annotation
   * @param clazz           the annotated class
   * @return                a List of Methods that have been annotated using annotationClass in the class clazz
   */
  @SuppressWarnings("unchecked")
  public static List<Method> getAnnotatedMethods(Class annotationClass, Class clazz) {
    List<Method> methods = new ArrayList<>();
    if (clazz == Object.class) {
      return methods;
    }
    methods.addAll((List<Method>)
        Arrays.stream(clazz.getDeclaredMethods())
        .filter(field -> field.getAnnotation(annotationClass) != null)
        .collect(Collectors.toList()));
    methods.addAll(getAnnotatedMethods(annotationClass, clazz.getSuperclass()));
    return methods;
  }

  /**
   * Method getAnnotatedMethod the first annotated method of the type of annotation specified.
   * @param annotationClass the type of the annotation
   * @param clazz           the annotated class
   * @return                the first annotated field found in clazz of the type annotationClass
   */
  public static Optional<Method> getAnnotatedMethod(Class annotationClass, Class clazz) {
    List<Method> methods = getAnnotatedMethods(annotationClass, clazz);
    return Optional.ofNullable(methods.size() > 0 ? methods.get(0) : null);
  }

  /**
   * Method getAnnotation returns the annotation on a class or its parent annotation.
   * @param clazz       the annotated class
   * @param annotation  the annotation to find
   * @param <A>         the type of the annotation
   * @return            the actual annotation used or null if it doesn't exist
   */
  @SuppressWarnings("unchecked")
  public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotation) {
    return Optional.ofNullable(clazz.getAnnotation(annotation)).orElse((A)
      Arrays.stream(clazz.getDeclaredAnnotations())
        .flatMap(anno -> Arrays.stream(anno.getClass().getInterfaces())
          .flatMap(iface -> Arrays.stream(iface.getDeclaredAnnotations())))
        .filter(annotation::isInstance)
        .findFirst().orElse(null)
    );
  }
}
