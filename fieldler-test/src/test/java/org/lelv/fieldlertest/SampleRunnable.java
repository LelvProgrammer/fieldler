package org.lelv.fieldlertest;

public class SampleRunnable implements Runnable {

  private boolean hasRun = false;

  @Override
  public void run() {
    hasRun = true;
  }

  public boolean hasRun() {
    return hasRun;
  }
}
