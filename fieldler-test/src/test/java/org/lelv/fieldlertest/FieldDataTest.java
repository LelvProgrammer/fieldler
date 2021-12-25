package org.lelv.fieldlertest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class FieldDataTest {

  private static final List<String> EXPECTED_ENUM_NAMES = Arrays.asList("AGE", "NAME", "LAST_NAME", "ALIVE", "REQUIRES_OXYGEN");
  private static final List<String> EXPECTED_ENUM_STRINGS = Arrays.asList("age", "name", "lastName", "alive", "requiresOxygen");

  @Test
  public void testCreatedFieldDataEnumNames() {
    List<String> actualEnumNames = EnumSet.allOf(PersonField.class)
                                          .stream()
                                          .map(Enum::name)
                                          .collect(Collectors.toList());
    assertThat(actualEnumNames).containsExactlyInAnyOrderElementsOf(EXPECTED_ENUM_NAMES);
  }

  @Test
  public void testCreatedFieldDataEnumStrings() {
    List<String> actualEnumNames = EnumSet.allOf(PersonField.class)
                                          .stream()
                                          .map(Object::toString)
                                          .collect(Collectors.toList());
    assertThat(actualEnumNames).containsExactlyInAnyOrderElementsOf(EXPECTED_ENUM_STRINGS);
  }

}
