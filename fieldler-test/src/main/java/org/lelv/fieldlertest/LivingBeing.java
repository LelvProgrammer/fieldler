package org.lelv.fieldlertest;

public abstract class LivingBeing {

  // getter (is) accessor
  private boolean alive;

  // getter accessor
  private Boolean requiresOxygen;

  public boolean isAlive() {
    return alive;
  }

  public Boolean getRequiresOxygen() {
    return requiresOxygen;
  }

  public void setAlive(boolean alive) {
    this.alive = alive;
  }

  public void setRequiresOxygen(Boolean requiresOxygen) {
    this.requiresOxygen = requiresOxygen;
  }
}
