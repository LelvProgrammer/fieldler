package org.lelv.fieldlertest.util;

import org.lelv.fieldlertest.Person;

import java.util.function.BiConsumer;

public class SampleBiConsumer implements BiConsumer<Person, Person> {

  private boolean hasRun = false;

  public boolean hasRun() {
    return hasRun;
  }

  @Override
  public void accept(Person person, Person person2) {
    hasRun = true;
  }
}
