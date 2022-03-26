package org.lelv.fieldlertest.util;

import org.lelv.fieldlertest.Person;
import org.lelv.fieldlertest.PersonField;

import java.util.Arrays;
import java.util.List;

import static org.lelv.fieldlertest.PersonField.*;

public class PersonTestUtil {

    public static final Person PERSON_A = new Person();
    public static final Person PERSON_B = new Person();
    public static final List<PersonField> DEFAULT_EQUAL_ATTRIBUTES = Arrays.asList(NAME, AGE, ALIVE);
    public static final List<PersonField> DEFAULT_DIFFERENT_ATTRIBUTES = Arrays.asList(LAST_NAME, REQUIRES_OXYGEN);
    public static final List<PersonField> DEFAULT_EQUAL_ATTRIBUTES_EXCEPT_ONE = Arrays.asList(NAME, AGE, LAST_NAME, ALIVE);
    public static final List<PersonField> DEFAULT_DIFFERENT_ATTRIBUTES_EXCEPT_ONE = Arrays.asList(LAST_NAME, AGE, REQUIRES_OXYGEN);

    public static void defaultAttributes() {
        sameAge();
        sameName();
        sameAlive();
        differentLastName();
        differentRequiresOxygen();
    }

    public static void allEqualAttributes() {
        sameAge();
        sameName();
        sameAlive();
        sameLastName();
        sameRequiresOxygen();
    }

    public static void allDifferentAttributes() {
        differentAge();
        differentName();
        differentAlive();
        differentLastName();
        differentRequiresOxygen();
    }

    private static void sameName() {
        PERSON_A.setName("John");
        PERSON_B.setName("John");
    }

    private static void sameAge() {
        PERSON_A.setAge(12);
        PERSON_B.setAge(12);
    }

    private static void differentName() {
        PERSON_A.setName("John");
        PERSON_B.setName("Maria");
    }

    private static void differentAge() {
        PERSON_A.setAge(12);
        PERSON_B.setAge(34);
    }

    private static void sameLastName() {
        PERSON_A.setLastName("Williams");
        PERSON_B.setLastName("Williams");
    }

    private static void differentLastName() {
        PERSON_A.setLastName("Williams");
        PERSON_B.setLastName("Johnson");
    }

    private static void sameAlive() {
        PERSON_A.setAlive(true);
        PERSON_B.setAlive(true);
    }

    private static void differentAlive() {
        PERSON_A.setAlive(true);
        PERSON_B.setAlive(false);
    }

    private static void sameRequiresOxygen() {
        PERSON_A.setRequiresOxygen(true);
        PERSON_B.setRequiresOxygen(true);
    }

    private static void differentRequiresOxygen() {
        PERSON_A.setRequiresOxygen(true);
        PERSON_B.setRequiresOxygen(false);
    }
}
