package org.lelv.fieldlertest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.lelv.fieldler.generator.util.SnakeCaseUtil;

public class SnakeCaseTest {

  @Test
  public void test() {
    assertThat(SnakeCaseUtil.snakeCase("Field")).isEqualTo("FIELD");
    assertThat(SnakeCaseUtil.snakeCase("field")).isEqualTo("FIELD");
    assertThat(SnakeCaseUtil.snakeCase("aField")).isEqualTo("A_FIELD");
    assertThat(SnakeCaseUtil.snakeCase("someDTO")).isEqualTo("SOME_DTO");
    assertThat(SnakeCaseUtil.snakeCase("someDTOCrazy")).isEqualTo("SOME_DTO_CRAZY");
  }

}
