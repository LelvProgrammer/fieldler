package org.lelv.fieldlertest;

import org.lelv.fieldler.annotation.FieldComparator;

import java.util.Objects;

@FieldComparator
public class Person extends LivingBeing {

  // public field
  public int age;

  // getter accessor
  private String name;

  // same name accessor
  private String lastName;

  // no accessor
  private String nationality;

  public int getAge() {
    throw new IllegalArgumentException("Should not be used by the library");
  }

  public String getName() {
    return name;
  }

  public String getName(String random) {
    throw new IllegalArgumentException("Should not be used by the library");
  }

  public String name(String random) {
    throw new IllegalArgumentException("Should not be used by the library");
  }

  public String lastName() {
    return lastName;
  }

  public String getLastName(String random) {
    throw new IllegalArgumentException("Should not be used by the library");
  }

  public String getLaStNaMe() {
    throw new IllegalArgumentException("Should not be used by the library");
  }

  public void setAge(int age) {
    this.age = age;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Person person = (Person) o;

    if (age != person.age) {
      return false;
    }
    if (!Objects.equals(name, person.name)) {
      return false;
    }
    if (!Objects.equals(lastName, person.lastName)) {
      return false;
    }
    if (!Objects.equals(isAlive(), person.isAlive())) {
      return false;
    }
    if (!Objects.equals(getRequiresOxygen(), person.getRequiresOxygen())) {
      return false;
    }
    return Objects.equals(nationality, person.nationality);
  }

  @Override
  public int hashCode() {
    int result = age;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
    result = 31 * result + (nationality != null ? nationality.hashCode() : 0);
    return result;
  }
}
