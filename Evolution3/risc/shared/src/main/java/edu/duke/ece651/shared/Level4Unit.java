package edu.duke.ece651.shared;

public class Level4Unit implements Unit{
  private static final long serialVersionUID = 1L;

  private int numUnits;
  private String type;
  private Integer value;
  private Integer techLevel;
  private Integer bonus;

  public Level4Unit() {
    numUnits = 0;
    bonus = 8;
    value = 55;
    techLevel = 4;
  }

  public Level4Unit(int numUnits) {
    this.numUnits = numUnits;
    bonus = 8;
    value = 55;
    techLevel = 4;
  }
  
  public int getNumUnits() {
    return numUnits;
  }

  public boolean removeUnit(int numUnits) {
    int result = this.numUnits - numUnits;
    if (result < 0){
      return false;
    }
    this.numUnits = result;
    return true;
  }
  
  public boolean addUnit(int numUnits) {
    int result = this.numUnits + numUnits;
    this.numUnits = result;
    return true;
  }

  public int getBonus(){
    return bonus;
  }

  public int getValue() {
    return value;
  }
  
  public int getLevel() {
    return techLevel;
  }

  public String getType() {
    return type;
  }

  public boolean setUnit(int numUnits) {
    this.numUnits = numUnits;
    return true;
  }
}
