package org.lelv.fieldlertest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lelv.fieldlertest.PersonField.AGE;
import static org.lelv.fieldlertest.PersonField.ALIVE;
import static org.lelv.fieldlertest.PersonField.LAST_NAME;
import static org.lelv.fieldlertest.PersonField.NAME;
import static org.lelv.fieldlertest.PersonField.REQUIRES_OXYGEN;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lelv.fieldler.output.FieldComparison;

import java.util.Arrays;
import java.util.List;

public class FieldComparatorTest {

  private static final List<PersonField> DEFAULT_EQUAL_ATTRIBUTES = Arrays.asList(NAME, AGE, ALIVE);
  private static final List<PersonField> DEFAULT_DIFFERENT_ATTRIBUTES = Arrays.asList(LAST_NAME, REQUIRES_OXYGEN);
  private static final List<PersonField> DEFAULT_EQUAL_ATTRIBUTES_EXCEPT_ONE = Arrays.asList(NAME, AGE, LAST_NAME, ALIVE);
  private static final List<PersonField> DEFAULT_DIFFERENT_ATTRIBUTES_EXCEPT_ONE = Arrays.asList(LAST_NAME, AGE, REQUIRES_OXYGEN);

  private Person personA = new Person();
  private Person personB = new Person();
  private FieldComparison<Person, PersonField> comparison;

  @BeforeEach
  public void beforeEach() {
    personA = new Person();
    personB = new Person();
    defaultAttributes();
    comparison = PersonFieldComparator.compare(personA, personB);
  }

  @Test
  public void testGetObjects() {
    assertThat(comparison.getObjectA()).isEqualTo(personA);
    assertThat(comparison.getObjectB()).isEqualTo(personB);
  }

  @Test
  public void testEqualityAndDifference() {
    DEFAULT_EQUAL_ATTRIBUTES.forEach(attribute -> {
      assertThat(comparison.isEqual(attribute)).isTrue();
      assertThat(comparison.isDifferent(attribute)).isFalse();
    });
    DEFAULT_DIFFERENT_ATTRIBUTES.forEach(attribute -> {
      assertThat(comparison.isEqual(attribute)).isFalse();
      assertThat(comparison.isDifferent(attribute)).isTrue();
    });
  }

  @Test
  public void testHasEqualitiesAndDifferences() {
    assertThat(comparison.hasEqualities()).isEqualTo(!DEFAULT_EQUAL_ATTRIBUTES.isEmpty());
    assertThat(comparison.hasDifferences()).isEqualTo(!DEFAULT_DIFFERENT_ATTRIBUTES.isEmpty());

    allDifferentAttributes();
    comparison.clearTests();
    assertThat(comparison.hasEqualities()).isFalse();
    assertThat(comparison.hasDifferences()).isTrue();

    allEqualAttributes();
    comparison.clearTests();
    assertThat(comparison.hasEqualities()).isTrue();
    assertThat(comparison.hasDifferences()).isFalse();
  }

  @Test
  public void testEqualAndDifferentFields() {
    assertThat(comparison.equalFields()).containsExactlyInAnyOrderElementsOf(DEFAULT_EQUAL_ATTRIBUTES);
    assertThat(comparison.differentFields()).containsExactlyInAnyOrderElementsOf(DEFAULT_DIFFERENT_ATTRIBUTES);
  }

  @Test
  public void testNumberOfEqualitiesAndDifferences() {
    assertThat(comparison.numberOfEqualities()).isEqualTo(DEFAULT_EQUAL_ATTRIBUTES.size());
    assertThat(comparison.numberOfDifferences()).isEqualTo(DEFAULT_DIFFERENT_ATTRIBUTES.size());
  }

  @Test
  public void testAnyEqualAndDifferent() {
    assertThat(comparison.isAnyEqual()).isTrue();
    assertThat(comparison.isAnyDifferent()).isTrue();

    assertThat(comparison.isAnyEqual(DEFAULT_DIFFERENT_ATTRIBUTES)).isFalse();
    assertThat(comparison.isAnyDifferent(DEFAULT_EQUAL_ATTRIBUTES)).isFalse();

    assertThat(comparison.isAnyEqual(DEFAULT_DIFFERENT_ATTRIBUTES_EXCEPT_ONE)).isTrue();
    assertThat(comparison.isAnyDifferent(DEFAULT_EQUAL_ATTRIBUTES_EXCEPT_ONE)).isTrue();

    allEqualAttributes();
    comparison.clearTests();
    assertThat(comparison.isAnyEqual()).isTrue();
    assertThat(comparison.isAnyDifferent()).isFalse();

    allDifferentAttributes();
    comparison.clearTests();
    assertThat(comparison.isAnyEqual()).isFalse();
    assertThat(comparison.isAnyDifferent()).isTrue();
  }

  @Test
  public void testAllEqualAndDifferent() {
    assertThat(comparison.areAllEqual()).isFalse();
    assertThat(comparison.areAllDifferent()).isFalse();

    assertThat(comparison.areAllEqual(DEFAULT_EQUAL_ATTRIBUTES)).isTrue();
    assertThat(comparison.areAllDifferent(DEFAULT_DIFFERENT_ATTRIBUTES)).isTrue();

    assertThat(comparison.areAllEqual(DEFAULT_EQUAL_ATTRIBUTES_EXCEPT_ONE)).isFalse();
    assertThat(comparison.areAllDifferent(DEFAULT_DIFFERENT_ATTRIBUTES_EXCEPT_ONE)).isFalse();

    allEqualAttributes();
    comparison.clearTests();
    assertThat(comparison.areAllEqual()).isTrue();
    assertThat(comparison.areAllDifferent()).isFalse();

    allDifferentAttributes();
    comparison.clearTests();
    assertThat(comparison.areAllEqual()).isFalse();
    assertThat(comparison.areAllDifferent()).isTrue();
  }

  @Test
  public void testDoWhenEqualAndDifferent() {
    DEFAULT_EQUAL_ATTRIBUTES.forEach(attribute -> {
      SampleRunnable equalRunnable = new SampleRunnable();
      comparison.doWhenEqual(attribute, equalRunnable);
      assertThat(equalRunnable.hasRun()).isTrue();

      SampleBiConsumer equalBiConsumer = new SampleBiConsumer();
      comparison.doWhenEqual(attribute, equalBiConsumer);
      assertThat(equalBiConsumer.hasRun()).isTrue();

      SampleRunnable differenceRunnable = new SampleRunnable();
      comparison.doWhenDifferent(attribute, differenceRunnable);
      assertThat(differenceRunnable.hasRun()).isFalse();

      SampleBiConsumer differenceBiConsumer = new SampleBiConsumer();
      comparison.doWhenDifferent(attribute, differenceBiConsumer);
      assertThat(differenceBiConsumer.hasRun()).isFalse();
    });

    DEFAULT_DIFFERENT_ATTRIBUTES.forEach(attribute -> {
      SampleRunnable equalRunnable = new SampleRunnable();
      comparison.doWhenEqual(attribute, equalRunnable);
      assertThat(equalRunnable.hasRun()).isFalse();

      SampleBiConsumer equalBiConsumer = new SampleBiConsumer();
      comparison.doWhenEqual(attribute, equalBiConsumer);
      assertThat(equalBiConsumer.hasRun()).isFalse();

      SampleRunnable differenceRunnable = new SampleRunnable();
      comparison.doWhenDifferent(attribute, differenceRunnable);
      assertThat(differenceRunnable.hasRun()).isTrue();

      SampleBiConsumer differenceBiConsumer = new SampleBiConsumer();
      comparison.doWhenDifferent(attribute, differenceBiConsumer);
      assertThat(differenceBiConsumer.hasRun()).isTrue();
    });
  }

  @Test
  public void testDoWhenAnyEqualAndDifferent() {
    SampleRunnable equalRunnable = new SampleRunnable();
    comparison.doWhenAnyEqual(DEFAULT_DIFFERENT_ATTRIBUTES, equalRunnable);
    assertThat(equalRunnable.hasRun()).isFalse();

    SampleBiConsumer equalBiConsumer = new SampleBiConsumer();
    comparison.doWhenAnyEqual(DEFAULT_DIFFERENT_ATTRIBUTES_EXCEPT_ONE, equalBiConsumer);
    assertThat(equalBiConsumer.hasRun()).isTrue();

    SampleRunnable differenceRunnable = new SampleRunnable();
    comparison.doWhenAnyDifferent(DEFAULT_EQUAL_ATTRIBUTES, differenceRunnable);
    assertThat(differenceRunnable.hasRun()).isFalse();

    SampleBiConsumer differenceBiConsumer = new SampleBiConsumer();
    comparison.doWhenAnyDifferent(DEFAULT_EQUAL_ATTRIBUTES_EXCEPT_ONE, differenceBiConsumer);
    assertThat(differenceBiConsumer.hasRun()).isTrue();
  }

  @Test
  public void testDoWhenAllEqualAndDifferent() {
    SampleRunnable equalRunnable = new SampleRunnable();
    comparison.doWhenAllEqual(DEFAULT_EQUAL_ATTRIBUTES, equalRunnable);
    assertThat(equalRunnable.hasRun()).isTrue();

    SampleBiConsumer equalBiConsumer = new SampleBiConsumer();
    comparison.doWhenAllEqual(DEFAULT_EQUAL_ATTRIBUTES_EXCEPT_ONE, equalBiConsumer);
    assertThat(equalBiConsumer.hasRun()).isFalse();

    SampleRunnable differenceRunnable = new SampleRunnable();
    comparison.doWhenAllDifferent(DEFAULT_DIFFERENT_ATTRIBUTES, differenceRunnable);
    assertThat(differenceRunnable.hasRun()).isTrue();

    SampleBiConsumer differenceBiConsumer = new SampleBiConsumer();
    comparison.doWhenAllDifferent(DEFAULT_DIFFERENT_ATTRIBUTES_EXCEPT_ONE, differenceBiConsumer);
    assertThat(differenceBiConsumer.hasRun()).isFalse();
  }

  @Test
  public void testThrowWhenEqualAndDifferent() {
    DEFAULT_EQUAL_ATTRIBUTES.forEach(attribute -> {
      assertThatThrownBy(() -> comparison.throwWhenEqual(attribute, SampleException::new)).isInstanceOf(SampleException.class);
      assertThatCode(() -> comparison.throwWhenDifferent(attribute, SampleException::new)).doesNotThrowAnyException();
    });

    DEFAULT_DIFFERENT_ATTRIBUTES.forEach(attribute -> {
      assertThatCode(() -> comparison.throwWhenEqual(attribute, SampleException::new)).doesNotThrowAnyException();
      assertThatThrownBy(() -> comparison.throwWhenDifferent(attribute, SampleException::new)).isInstanceOf(SampleException.class);
    });
  }

  @Test
  public void testThrowWhenAnyEqualAndDifferent() {
    assertThatCode(() -> comparison.throwWhenAnyEqual(DEFAULT_DIFFERENT_ATTRIBUTES, SampleException::new)).doesNotThrowAnyException();
    assertThatThrownBy(() -> comparison.throwWhenAnyEqual(SampleException::new)).isInstanceOf(SampleException.class);
    assertThatThrownBy(() -> comparison.throwWhenAnyEqual(DEFAULT_DIFFERENT_ATTRIBUTES_EXCEPT_ONE, SampleException::new)).isInstanceOf(SampleException.class);

    assertThatCode(() -> comparison.throwWhenAnyDifferent(DEFAULT_EQUAL_ATTRIBUTES, SampleException::new)).doesNotThrowAnyException();
    assertThatThrownBy(() -> comparison.throwWhenAnyDifferent(SampleException::new)).isInstanceOf(SampleException.class);
    assertThatThrownBy(() -> comparison.throwWhenAnyDifferent(DEFAULT_DIFFERENT_ATTRIBUTES_EXCEPT_ONE, SampleException::new)).isInstanceOf(SampleException.class);
  }

  @Test
  public void testThrowWhenAllEqualAndDifferent() {
    assertThatCode(() -> comparison.throwWhenAllEqual(SampleException::new)).doesNotThrowAnyException();
    assertThatCode(() -> comparison.throwWhenAllEqual(DEFAULT_EQUAL_ATTRIBUTES_EXCEPT_ONE, SampleException::new)).doesNotThrowAnyException();
    assertThatThrownBy(() -> comparison.throwWhenAllEqual(DEFAULT_EQUAL_ATTRIBUTES, SampleException::new)).isInstanceOf(SampleException.class);

    assertThatCode(() -> comparison.throwWhenAllDifferent(SampleException::new)).doesNotThrowAnyException();
    assertThatCode(() -> comparison.throwWhenAllDifferent(DEFAULT_DIFFERENT_ATTRIBUTES_EXCEPT_ONE, SampleException::new)).doesNotThrowAnyException();
    assertThatThrownBy(() -> comparison.throwWhenAllDifferent(DEFAULT_DIFFERENT_ATTRIBUTES, SampleException::new)).isInstanceOf(SampleException.class);
  }

  // Util methods

  private void allEqualAttributes() {
    sameAge();
    sameName();
    sameAlive();
    sameLastName();
    sameRequiresOxygen();
  }

  private void allDifferentAttributes() {
    differentAge();
    differentName();
    differentAlive();
    differentLastName();
    differentRequiresOxygen();
  }

  private void defaultAttributes() {
    sameAge();
    sameName();
    sameAlive();
    differentLastName();
    differentRequiresOxygen();
  }

  private void sameName() {
    personA.setName("John");
    personB.setName("John");
  }

  private void sameAge() {
    personA.setAge(12);
    personB.setAge(12);
  }

  private void differentName() {
    personA.setName("John");
    personB.setName("Maria");
  }

  private void differentAge() {
    personA.setAge(12);
    personB.setAge(34);
  }

  private void sameLastName() {
    personA.setLastName("Williams");
    personB.setLastName("Williams");
  }

  private void differentLastName() {
    personA.setLastName("Williams");
    personB.setLastName("Johnson");
  }

  private void sameAlive() {
    personA.setAlive(true);
    personB.setAlive(true);
  }

  private void differentAlive() {
    personA.setAlive(true);
    personB.setAlive(false);
  }

  private void sameRequiresOxygen() {
    personA.setRequiresOxygen(true);
    personB.setRequiresOxygen(true);
  }

  private void differentRequiresOxygen() {
    personA.setRequiresOxygen(true);
    personB.setRequiresOxygen(false);
  }

}
