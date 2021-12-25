package org.lelv.fieldler.generator.data;

import org.lelv.fieldler.generator.util.SnakeCaseUtil;

public class FieldData {

  private final String name;
  private final String access;
  private final String snakeCaseName;

  public FieldData(String name, String access) {
    this.name = name;
    this.access = access;
    this.snakeCaseName = SnakeCaseUtil.snakeCase(name);
  }

  public String getName() {
    return name;
  }

  public String getAccess() {
    return access;
  }

  public String getSnakeCaseName() {
    return snakeCaseName;
  }

  public boolean isAccessible() {
    return access != null;
  }

  @Override
  public String toString() {
    return "FieldData{" +
        "name='" + name + '\'' +
        ", access='" + access + '\'' +
        '}';
  }
}
