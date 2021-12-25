package org.lelv.fieldler.output;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that provides ways of comparing the fields of objects in a clean way.
 *
 * The comparison of each field is done only once, and at the moment of requesting a method which in part
 * requires the comparison to be executed.
 *
 * Important: the logic works under the premise that either the objects being compared are immutable, or that at the times of
 * calling different methods, the objects have not been altered. Internally they are stored in a final manner, but their fields may not be.
 * Therefore, it is the responsibility of the user to make sure that at the moment of calling certain methods that the state of the objects hasn't
 * been modified in the middle. For example, if a FieldComparison is created for two cars, one red and one blue, and if the method
 * {@code isDifferent(COLOR)} is called, then it will return true. But if the blue car is then updated to have a red color and the method is
 * called yet again, it will still return true, as the comparison of said field has already been done and stored. If there is a change, then the
 * recommendation would be to create a new FieldComparison out of the modified object, or calling the method {@code clearTests()}
 *
 * @param <T> type of the class whose objects are being compared
 * @param <U> type of the generated Field Enum created for class T
 * @author Lelv
 */
public final class FieldComparison<T, U> {

  private final T objectA;
  private final T objectB;
  private final Map<U, BiPredicate<T, T>> equalityTests;
  private final Map<U, Boolean> equalityResults = new HashMap<>();

  public FieldComparison(T objectA, T objectB, Map<U, BiPredicate<T, T>> equalityTests) {
    this.objectA = Objects.requireNonNull(objectA);
    this.objectB = Objects.requireNonNull(objectB);
    this.equalityTests = Objects.requireNonNull(equalityTests);
  }

  /**
   * Returns the first object compared. In order to avoid problems, it is better to not modify the value of any field
   * if the idea is to keep on working with this class.
   *
   * @return the first object compared
   */
  public T getObjectA() {
    return objectA;
  }

  /**
   * Returns the second object compared. In order to avoid problems, it is better to not modify the value of any field
   * if the idea is to keep on working with this class.
   *
   * @return the second object compared
   */
  public T getObjectB() {
    return objectB;
  }

  // Basic functions

  /**
   * Informs whether a field is equal between the two objects
   *
   * @param field field to compare
   * @return true if the field is equal between the two objects, false otherwise
   */
  public boolean isEqual(U field) {
    if (!equalityResults.containsKey(field)) {
      testField(field);
    }
    return equalityResults.get(field);
  }

  /**
   * Informs whether a field is different between the two objects
   *
   * @param field field to compare
   * @return true if the field is different between the two objects, false otherwise
   */
  public boolean isDifferent(U field) {
    if (!equalityResults.containsKey(field)) {
      testField(field);
    }
    return !equalityResults.get(field);
  }

  /**
   * Informs whether there are equalities among the fields of the two objects
   *
   * @return true if any field value is the same between the objects, false otherwise
   */
  public boolean hasEqualities() {
    if (equalityResults.values().stream().anyMatch(Boolean.TRUE::equals)) {
      return true;
    }
    return equalityTests.keySet().stream().anyMatch(this::isEqual);
  }

  /**
   * Informs whether there are differences among the fields of the two objects
   *
   * @return true if any field value differs between the objects, false otherwise
   */
  public boolean hasDifferences() {
    if (equalityResults.values().stream().anyMatch(Boolean.FALSE::equals)) {
      return true;
    }
    return equalityTests.keySet().stream().anyMatch(this::isDifferent);
  }

  /**
   * Returns the set of fields that are equal between the two objects
   *
   * @return set of fields that are equal between the two objects
   */
  public Set<U> equalFields() {
    return equalityTests.keySet().stream().filter(this::isEqual).collect(Collectors.toSet());
  }

  /**
   * Returns the set of fields that differ between the two objects
   *
   * @return set of fields that differ between the two objects
   */
  public Set<U> differentFields() {
    return equalityTests.keySet().stream().filter(this::isDifferent).collect(Collectors.toSet());
  }

  /**
   * Returns the number of fields that are equal between the two objects
   *
   * @return number of fields that are equal between the two objects
   */
  public int numberOfEqualities() {
    return equalFields().size();
  }

  /**
   * Returns the number of fields that differ between the two objects
   *
   * @return number of fields that differ between the two objects
   */
  public int numberOfDifferences() {
    return differentFields().size();
  }

  // Collection functions

  /**
   * Informs whether any of the fields are equal between the two objects
   *
   * @param fields fields to consider
   * @return true if at least one of the provided fields is equal, false otherwise.
   * If no field is provided, then returns true if there are equalities, false otherwise.
   */
  public boolean isAnyEqual(Collection<U> fields) {
    if (fields.size() == 0) {
      return hasEqualities();
    }
    return fields.stream().anyMatch(this::isEqual);
  }

  /**
   * Informs whether any of the fields are equal between the two objects
   *
   * @param fields fields to consider
   * @return true if at least one of the provided fields is equal, false otherwise.
   * If no field is provided, then returns true if there are equalities, false otherwise.
   */
  @SafeVarargs
  public final boolean isAnyEqual(U... fields) {
    if (fields.length == 0) {
      return hasEqualities();
    }
    return Stream.of(fields).anyMatch(this::isEqual);
  }

  /**
   * Informs whether any of the fields is different between the two objects
   *
   * @param fields fields to consider
   * @return true if at least one of the provided fields is different, false otherwise.
   * If no field is provided, then returns true if there are differences, false otherwise.
   */
  public boolean isAnyDifferent(Collection<U> fields) {
    if (fields.size() == 0) {
      return hasDifferences();
    }
    return fields.stream().anyMatch(this::isDifferent);
  }

  /**
   * Informs whether any of the fields is different between the two objects
   *
   * @param fields fields to consider
   * @return true if at least one of the provided fields is different, false otherwise.
   * If no field is provided, then returns true if there are differences, false otherwise.
   */
  @SafeVarargs
  public final boolean isAnyDifferent(U... fields) {
    if (fields.length == 0) {
      return hasDifferences();
    }
    return Stream.of(fields).anyMatch(this::isDifferent);
  }

  /**
   * Informs whether all the fields are equal between the two objects
   *
   * @param fields fields to consider
   * @return true if the provided fields are equal, false otherwise.
   * If no field is provided, then returns true if there are no differences, and false otherwise.
   */
  public boolean areAllEqual(Collection<U> fields) {
    if (fields.size() == 0) {
      return !hasDifferences();
    }
    return fields.stream().allMatch(this::isEqual);
  }

  /**
   * Informs whether all the fields are equal between the two objects
   *
   * @param fields fields to consider
   * @return true if the provided fields are equal, false otherwise.
   * If no field is provided, then returns true if there are no differences, and false otherwise.
   */
  @SafeVarargs
  public final boolean areAllEqual(U... fields) {
    if (fields.length == 0) {
      return !hasDifferences();
    }
    return Stream.of(fields).allMatch(this::isEqual);
  }

  /**
   * Informs whether all the fields are different between the two objects
   *
   * @param fields fields to consider
   * @return true if the provided fields are different, false otherwise.
   * If no field is provided, returns true if all fields are different, false otherwise.
   */
  public boolean areAllDifferent(Collection<U> fields) {
    if (fields.size() == 0) {
      return !hasEqualities();
    }
    return fields.stream().allMatch(this::isDifferent);
  }

  /**
   * Informs whether all the fields are different between the two objects
   *
   * @param fields fields to consider
   * @return true if the provided fields are different, false otherwise.
   * If no field is provided, returns true if all fields are different, false otherwise.
   */
  @SafeVarargs
  public final boolean areAllDifferent(U... fields) {
    if (fields.length == 0) {
      return !hasEqualities();
    }
    return Stream.of(fields).allMatch(this::isDifferent);
  }

  // Runnable actions

  /**
   * Executes a function if the field is equal between the two objects
   *
   * @param field field to assess
   * @param runnable  function to execute
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenEqual(U field, Runnable runnable) {
    if (isEqual(field)) {
      runnable.run();
    }
    return this;
  }

  /**
   * Executes a function if the field is different between the two objects
   *
   * @param field field to assess
   * @param runnable  function to execute
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenDifferent(U field, Runnable runnable) {
    if (isDifferent(field)) {
      runnable.run();
    }
    return this;
  }

  /**
   * Executes a function if at least one of the fields is different between the two objects. If the set is empty, then
   * it executes if there exists one field which is equal.
   *
   * @param fields       fields to assess
   * @param runnable  function to execute
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAnyEqual(Collection<U> fields, Runnable runnable) {
    if (isAnyEqual(fields)) {
      runnable.run();
    }
    return this;
  }

  /**
   * Executes a function if at least one of the fields is different between the two objects. If the set is empty, then
   * it executes if there exists one field which is different.
   *
   * @param fields       fields to assess
   * @param runnable  function to execute
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAnyDifferent(Collection<U> fields, Runnable runnable) {
    if (isAnyDifferent(fields)) {
      runnable.run();
    }
    return this;
  }

  /**
   * Executes a function if all the provided fields are equal between the two objects. If the set is empty, then it executes
   * if all possible fields are equal.
   *
   * @param fields       fields to assess
   * @param runnable  function to execute
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAllEqual(Collection<U> fields, Runnable runnable) {
    if (areAllEqual(fields)) {
      runnable.run();
    }
    return this;
  }

  /**
   * Executes a function if all the provided fields are different between the two objects. If the set is empty, then it executes
   * if all possible fields are different.
   *
   * @param fields       fields to assess
   * @param runnable  function to execute
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAllDifferent(Collection<U> fields, Runnable runnable) {
    if (areAllDifferent(fields)) {
      runnable.run();
    }
    return this;
  }

  // BiConsumer actions

  /**
   * Executes a function if the field is equal between the two objects.
   *
   * @param field       field to assess
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenEqual(U field, BiConsumer<T, T> objectsConsumer) {
    if (isEqual(field)) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }

  /**
   * Executes a function if the field is different between the two objects.
   *
   * @param field       field to assess
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenDifferent(U field, BiConsumer<T, T> objectsConsumer) {
    if (isDifferent(field)) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }


  /**
   * Executes a function if at least one of the fields is different between the two objects.
   *
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAnyEqual(BiConsumer<T, T> objectsConsumer) {
    if (isAnyEqual()) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }

  /**
   * Executes a function if at least one of the fields is different between the two objects. If the set is empty, then
   * it executes if there exists one field which is equal.
   *
   * @param fields       fields to assess
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAnyEqual(Collection<U> fields, BiConsumer<T, T> objectsConsumer) {
    if (isAnyEqual(fields)) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }

  /**
   * Executes a function if at least one of the fields is different between the two objects.
   *
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAnyDifferent(BiConsumer<T, T> objectsConsumer) {
    if (isAnyDifferent()) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }

  /**
   * Executes a function if at least one of the fields is different between the two objects. If the set is empty, then
   * it executes if there exists one field which is different.
   *
   * @param fields       fields to assess
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAnyDifferent(Collection<U> fields, BiConsumer<T, T> objectsConsumer) {
    if (isAnyDifferent(fields)) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }

  /**
   * Executes a function if all fields are equal.
   *
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAllEqual(BiConsumer<T, T> objectsConsumer) {
    if (areAllEqual()) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }

  /**
   * Executes a function if all the provided fields are equal between the two objects. If the set is empty, then it executes
   * if all possible fields are equal.
   *
   * @param fields       fields to assess
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAllEqual(Collection<U> fields, BiConsumer<T, T> objectsConsumer) {
    if (areAllEqual(fields)) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }


  /**
   * Executes a function if all fields are different.
   *
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAllDifferent(BiConsumer<T, T> objectsConsumer) {
    if (areAllDifferent()) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }

  /**
   * Executes a function if all the provided fields are different between the two objects. If the set is empty, then it executes
   * if all possible fields are different.
   *
   * @param fields       fields to assess
   * @param objectsConsumer consumer of the compared objects
   * @return self - helpful for chaining with other do* methods
   */
  public FieldComparison<T, U> doWhenAllDifferent(Collection<U> fields, BiConsumer<T, T> objectsConsumer) {
    if (areAllDifferent(fields)) {
      objectsConsumer.accept(objectA, objectB);
    }
    return this;
  }

  // Throw

  /**
   * Throws an exception created by the supplier if the provided field is equal between the two objects.
   *
   * @param field             field to assess
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if the field is equal between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenEqual(U field, Supplier<? extends X> exceptionSupplier) throws X {
    if (isEqual(field)) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  /**
   * Throws an exception created by the supplier if the provided field is different between the two objects.
   *
   * @param field             field to assess
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if the field is different between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenDifferent(U field, Supplier<? extends X> exceptionSupplier) throws X {
    if (isDifferent(field)) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  /**
   * Throws an exception created by the supplier if any field is equal between the two objects
   *
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if any field is equal between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenAnyEqual(Supplier<? extends X> exceptionSupplier) throws X {
    if (isAnyEqual()) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  /**
   * Throws an exception created by the supplier if any of the provided fields is equal between the two objects.
   * If the collection is empty, then it behaves the same way as {@code throwWhenAnyEqual(Supplier<? extends X> exceptionSupplier)}
   *
   * @param fields fields to assess for equality
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if any field is equal between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenAnyEqual(Collection<U> fields, Supplier<? extends X> exceptionSupplier) throws X {
    if (isAnyEqual(fields)) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  /**
   * Throws an exception created by the supplier if any field is different between the two objects
   *
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if any field is different between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenAnyDifferent(Supplier<? extends X> exceptionSupplier) throws X {
    if (isAnyDifferent()) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  /**
   * Throws an exception created by the supplier if any of the provided fields is different between the two objects.
   * If the collection is empty, then it behaves the same way as {@code throwWhenAnyDifferent(Supplier<? extends X> exceptionSupplier)}
   *
   * @param fields fields to assess for difference
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if any field is different between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenAnyDifferent(Collection<U> fields, Supplier<? extends X> exceptionSupplier) throws X {
    if (isAnyDifferent(fields)) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  /**
   * Throws an exception created by the supplier if all fields are equal between the two objects
   *
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if all fields are equal between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenAllEqual(Supplier<? extends X> exceptionSupplier) throws X {
    if (areAllEqual()) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  /**
   * Throws an exception created by the supplier if all the provided fields are equal between the two objects.
   * If the collection is empty, then it behaves the same way as {@code throwWhenAllEqual(Supplier<? extends X> exceptionSupplier)}
   *
   * @param fields fields to assess for equality
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if all fields are equal between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenAllEqual(Collection<U> fields, Supplier<? extends X> exceptionSupplier) throws X {
    if (areAllEqual(fields)) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  /**
   * Throws an exception created by the supplier if all fields are different between the two objects
   *
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if all fields are different between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenAllDifferent(Supplier<? extends X> exceptionSupplier) throws X {
    if (areAllDifferent()) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  /**
   * Throws an exception created by the supplier if all the provided fields are different between the two objects.
   * If the collection is empty, then it behaves the same way as {@code throwWhenAllDifferent(Supplier<? extends X> exceptionSupplier)}
   *
   * @param fields fields to assess for difference
   * @param exceptionSupplier supplier of exception to throw
   * @param <X> type of the exception to be thrown
   * @return self
   * @throws X if all fields are different between the two objects
   */
  public <X extends Throwable> FieldComparison<T, U> throwWhenAllDifferent(Collection<U> fields, Supplier<? extends X> exceptionSupplier) throws X {
    if (areAllDifferent(fields)) {
      throw exceptionSupplier.get();
    }
    return this;
  }

  // Test executing & clearing

  /**
   * Clears the result of the equality tests that have already been executed
   * @return self
   */
  public FieldComparison<T, U> clearTests() {
    equalityResults.clear();
    return this;
  }

  /**
   * Runs the equality check of all the fields that have not been checked.
   * The class doesn't run any comparison until the moment of accessing a method that requires the assessment of equality. This method
   * provides a way to not do it in the "lazy" way, and immediately make the check for all the fields.
   * @return self
   */
  public FieldComparison<T, U> testAllFields() {
    if (noPendingTests()) {
      return this;
    }
    equalityTests.keySet().stream().filter(key -> !equalityResults.containsKey(key)).forEach(this::testField);
    return this;
  }

  /**
   * Runs the equality check of all the provided fields that have not been checked.
   * The class doesn't run any comparison until the moment of accessing a method that requires the assessment of equality. This method
   * provides a way to not do it in the "lazy" way, and immediately make the check for all the provided fields.
   * @param fields the fields to test
   * @return self
   */
  @SafeVarargs
  public final FieldComparison<T, U> testFields(U... fields) {
    if (fields == null || fields.length == 0 || noPendingTests()) {
      return this;
    }
    Arrays.stream(fields).filter(key -> !equalityResults.containsKey(key)).forEach(this::testField);
    return this;
  }

  private void testField(U field) {
    equalityResults.put(field, equalityTests.get(field).test(objectA, objectB));
  }

  private boolean noPendingTests() {
    return equalityResults.size() == equalityTests.size();
  }

}
