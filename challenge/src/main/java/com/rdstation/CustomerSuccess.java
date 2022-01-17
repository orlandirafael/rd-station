package com.rdstation;

public class CustomerSuccess {

  private final int id;
  private final int score;

  private int capability = 0;

  public CustomerSuccess(int id, int score) {
    this.id = id;
    this.score = score;
  }

  public int getId() {
    return id;
  }

  public int getScore() {
    return score;
  }

  public int getCapability() {
    return capability;
  }

  public void increaseCapability() {
    this.capability++;
  }
}
