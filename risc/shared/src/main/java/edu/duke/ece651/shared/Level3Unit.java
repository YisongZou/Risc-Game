package edu.duke.ece651.shared;

public class Level3Unit implements Unit{
  private static final long serialVersionUID = 1L;

  private int numUnits;
  private String type;
  private Integer value;
  private Integer techLevel;
  private Integer bonus;

  public Level3Unit() {
    numUnits = 0;
    bonus = 5;
    value = 30;
    techLevel = 3;
  }

  public Level3Unit(int numUnits) {
    this.numUnits = numUnits;
    bonus = 5;
    value = 30;
    techLevel = 3;
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
