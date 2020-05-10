package edu.duke.ece651.shared;

public class Level6Unit implements Unit{
  private static final long serialVersionUID = 1L;

  private int numUnits;
  private String type;
  private Integer value;
  private Integer techLevel;
  private Integer bonus;

  public Level6Unit() {
    numUnits = 0;
    bonus = 15;
    value = 140;
    techLevel = 6;
  }

  public Level6Unit(int numUnits) {
    this.numUnits = numUnits;
    bonus = 15;
    value = 140;
    techLevel = 6;
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
