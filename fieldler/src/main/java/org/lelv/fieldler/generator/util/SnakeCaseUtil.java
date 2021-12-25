package org.lelv.fieldler.generator.util;

public class SnakeCaseUtil {

  private static final String SEPARATION = "_";

  private enum State {FIRST_LETTER, LOWER_CASE, UPPER_CASE}

  public static String snakeCase(String name) {
    State state = State.FIRST_LETTER;
    char[] charArray = name.toCharArray();
    StringBuilder builder = new StringBuilder();

    for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
      switch (state) {
        case FIRST_LETTER:
          builder.append(Character.toUpperCase(charArray[i]));
          state = Character.isUpperCase(charArray[i]) ? State.UPPER_CASE : State.LOWER_CASE;
          break;
        case UPPER_CASE:
          if (Character.isUpperCase(charArray[i])) {
            if (i + 1 < charArrayLength && Character.isLowerCase(charArray[i + 1])) {
              builder.append(SEPARATION);
            }
          } else {
            state = State.LOWER_CASE;
          }
          builder.append(Character.toUpperCase(charArray[i]));
          break;
        case LOWER_CASE:
          if (Character.isUpperCase(charArray[i])) {
            builder.append(SEPARATION);
            state = State.UPPER_CASE;
          }
          builder.append(Character.toUpperCase(charArray[i]));
          break;
      }
    }
    return builder.toString();
  }

}
