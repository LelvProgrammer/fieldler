# Fieldler

**_Fieldler_** is a java library for working with the fields of a class. It works by dynamically creating classes based
on the annotations defined on the source class. The FieldData annotation creates an enum of the class with an entry for
every accessible field. The FieldComparator annotation creates the enum and a comparison util class, which helps to
compare two objects beyond the usage of the _equals_ method.

## Usage

Add the annotation @FieldComparator on the class which you're interested in comparing.

```java

@FieldComparator
public class Person {

  private String name;
  private String lastName;
  private int age;
  private int height;
  private String nationality;

  // getters and setters
}
```

When compiled this will create two classes: **PersonField** and **PersonFieldComparator**

* _PersonField_ is an enum that has five entries: NAME, LAST_NAME, AGE, HEIGHT, and NATIONALITY
* _PersonFieldComparator_ is an util class that has a single static method **compare** for comparing two persons. It
  returns a FieldComparison, which can be used to query information regarding the fields of the compared objects.

### Comparison Example (not exhaustive)

```java
FieldComparison<Person, PersonField> comparison = PersonFieldComparator.compare(personA, personB);

// see if there are equalities among the fields
comparison.hasEqualities();
// see if there are differences among the fields
comparison.hasDifferences();

// returns the set of all fields that are equal
comparison.equalFields();
// returns the set of all fields that are different
comparison.differentFields();

// see if people are equal by name
comparison.isEqual(NAME);
// see if people are equal by name, last name, or nationality
comparison.isAnyEqual(NAME, LAST_NAME, NATIONALITY);
// see if people are equal by age, nationality, and height
comparison.areAllEqual(AGE, NATIONALITY, HEIGHT);

// see if people are different by name
comparison.isDifferent(NAME);
// see if people are different by age, last name, or name
comparison.isAnyDifferent(AGE, LAST_NAME, NAME);
// see if people are different by nationality, age, and last name
comparison.areAllDifferent(NATIONALITY, AGE, LAST_NAME);

// execute actions based on differences or equalities
comparison.doWhenEqual(NAME, () -> log("The name remains the same"))
          .throwWhenDifferent(NATIONALITY, NationalityChangedException::new)
          .doWhenDifferent(LAST_NAME, () -> assertPermission(PersonPermission.LAST_NAME_MODIFICATION))
          .doWhenDifferent(AGE, (before, after) -> publishEvent(new PersonAgeModifiedEvent(before.getAge(), after.getAge())));
```

## Installation

Add the dependency to your pom file

```xml

<dependency>
    <groupId>org.lelv</groupId>
    <artifactId>fieldler</artifactId>
    <version>1.0.0</version>
</dependency>
```

and configure the maven compiler plugin to execute the annotation processor, like the example below

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>${maven-compiler-plugin.version}</version>
    <configuration>
        <source>${maven-compiler-plugin.source.version}</source>
        <target>${maven-compiler-plugin.target.version}</target>
        <generatedSourcesDirectory>${project.build.directory}/generated-sources/</generatedSourcesDirectory>
        <annotationProcessors>
            <annotationProcessor>
                org.lelv.fieldler.processor.FieldlerAnnotationProcessor
            </annotationProcessor>
        </annotationProcessors>
    </configuration>
</plugin>
```

### Lombok Compatibility

The library is compatible with Lombok, just make sure that when declaring the Fieldler annotation processor, you also
add Lombok, like this:

```xml
<annotationProcessor>
lombok.launch.AnnotationProcessorHider$AnnotationProcessor,org.lelv.fieldler.processor.FieldlerAnnotationProcessor
</annotationProcessor>
```

## Application

If no comparator is needed, simply use **@FieldData**.

The **FieldComparison** doesn't have any groundbreaking code, as the logic there can be replaced by calling manually
each _.equals()_ on the field that wants to be compared, along with some _if_ and _boolean_ operands. The idea here is
to have nicer code, which is shorter yet more expressive.

```java
// With FieldComparison
FieldComparison<Person, PersonField> comparison = PersonFieldComparator.compare(personA, personB);
comparison.throwWhenDifferent(NATIONALITY, NationalityChangedException::new)
          .doWhenAllDifferent(Set.of(NAME, LAST_NAME), () -> assertPermission(PersonPermission.PERSON_NAME_AND_LAST_NAME_MODIFICATION))
          .doWhenEqual(HEIGHT, () -> log("Person height remains the same"))
          .doWhenDifferent(AGE, (before, after) -> publishEvent(new PersonAgeModifiedEvent(before.getAge(), after.getAge())));

// Equivalent code without FieldComparison

// The saved comparisons in a variable is because the library caches every result so that it can be used multiple times
boolean nationalityComparison = Objects.equals(personA.getNationality(), personB.getNationality());
if (!nationalityComparison) {
  throw new NationalityChangedException();
}
boolean nameComparison = Objects.equals(personA.getName(), personB.getName());
boolean lastNameComparison = Objects.equals(personA.getLastName(), personB.getLastName());
if (!nameComparison && !lastNameComparison) {
  assertPermission(PersonPermission.PERSON_NAME_AND_LAST_NAME_MODIFICATION);
}
boolean heightComparison = Objects.equals(personA.getHeight(), personB.getHeight());
if (heightComparison) {
  log("Person height remains the same");
}
boolean ageComparison = Objects.equals(personA.getAge(), personB.getAge());
if (!ageComparison) {
  publishEvent(new PersonAgeModifiedEvent(personA.getAge(), personB.getAge()));
}
```

Additionally, if you wonder why not just create your own enum and comparator yourself - well, you're more than welcome
to do so. The benefit of using the library is that this is all created in compilation time. This means that if you were
to add new fields, remove some, or even rename them, with the manual approach you'd have to keep this up to date
yourself, but with the library, you can just recompile and all this information will be updated.

## More info

* Requires Java 8+
* Supports inheritance
* Comparison takes place only for the fields which were generated on the enum
* The enum is created by scanning the fields of the class and its parent classes, and obtaining those which are not
  static and are accessible. A field (xxx) is considered accessible if it has one of the following (in order of
  importance):
    * a public visibility
    * a public method named _**get**Xxx()_ (or _**is**Xxx()_ in the case of booleans)
    * a public method named _xxx()_
* Comparison is only done once and at the moment of querying. This means that at the moment of creation of the **
  FieldComparison**, no equality check has been made yet. Once a method is called which requires a specific equality
  check, then the comparison does in fact take place. If later another method requires knowing if that same field is
  equal or not among the two objects, the comparison is not done again, as the previous result is stored and obtained.
  If the comparison of all (or some) fields is required at the moment of creation of the **FieldComparison**, then one
  can call the method _testAllFields_ (or _testFields_)

## Contributing

* If there's a feature you'd like to see, please open an issue with the "enhancement" label
* Pull requests are welcome, make sure that you
    * create tests
    * branch out from develop and target develop
    * follow the code standards as present in the code base
* Any other type of feedback (in the form of issue) is also welcome

## Licensing

[Apache License 2.0](https://choosealicense.com/licenses/apache-2.0/)
