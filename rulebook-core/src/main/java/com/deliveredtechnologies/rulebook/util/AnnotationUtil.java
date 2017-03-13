package com.deliveredtechnologies.rulebook.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A utility class for annotations.
 */
public class AnnotationUtil {
  private AnnotationUtil() {}

  /**
   * Gets the fields annotated of a specific type from the class and its parent classes
   * @param annotationClass the annotation type
   * @param clazz           the class that is expected to be annotated
   * @return                a {@link Set} of {@link Field} objects annotated with the annotatedClass
   */
  @SuppressWarnings("unchecked")
  public static Set<Field> getAnnotatedFields(Class annotationClass, Class clazz) {
    if (clazz == Object.class) {
      return new HashSet<>();
    }
    Set<Field> fields = getAnnotatedFields(annotationClass, clazz.getSuperclass());
    fields.addAll((Set<Field>)
      Arrays.stream(clazz.getDeclaredFields())
        .filter(field -> field.getAnnotation(annotationClass) != null)
        .collect(Collectors.toSet()));
    return fields;
  }
}
