package org.lelv.fieldler.generator;

import java.io.IOException;

public class FieldlerGenerationException extends RuntimeException {

  private static final long serialVersionUID = -5491094053871164433L;

  public FieldlerGenerationException(IOException exception) {
    super(exception);
  }

}
