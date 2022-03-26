package org.lelv.fieldler.generator.data;

import java.util.List;

public class ClassData {

  private final String className;
  private final String packagePath;
  private final String canonicalName;
  private final List<FieldData> fieldsData;

  public ClassData(String canonicalName, List<FieldData> fieldsData) {
    this.fieldsData = fieldsData;
    this.canonicalName = canonicalName;
    int classNameIndex = canonicalName.lastIndexOf('.');
    this.className = canonicalName.substring(classNameIndex + 1);
    this.packagePath = canonicalName.substring(0, classNameIndex);
  }

  public String getClassName() {
    return className;
  }

  public String getPackagePath() {
    return packagePath;
  }

  public List<FieldData> getFieldsData() {
    return fieldsData;
  }

  public String getCanonicalName() {
    return canonicalName;
  }

  public boolean hasAccessibleFields() {
    return fieldsData != null && fieldsData.stream().anyMatch(FieldData::isAccessible);
  }

  @Override
  public String toString() {
    return "ClassData{" +
        "canonicalName='" + canonicalName + '\'' +
        ", fieldsData=" + fieldsData +
        '}';
  }
}
