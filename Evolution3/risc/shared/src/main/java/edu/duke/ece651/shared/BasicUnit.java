package edu.duke.ece651.shared;

public class BasicUnit implements Unit{
  private static final long serialVersionUID = 1L;
  
  private int numUnits;
  private String type;
  private Integer value;
  private Integer techLevel;
  private Integer bonus;

  public int getBonus() {
    return bonus;
  }

  public int getLevel() {
    return techLevel;
  }

  public String getType() {
    return type;
  }

  public int getValue() {
    return value;
  }
  
  public BasicUnit(){
    numUnits = 0;
    bonus = 0;
    value = 0;
    techLevel = 0;
  }

  public BasicUnit(int numUnits){
    this.numUnits = numUnits;
    bonus = 0;
    value = 0;
    techLevel = 0;
  }
  
  public int getNumUnits(){
    return numUnits;
  }
  /*
    Method to add num unit to basic squad
    @param int numUnit of how many units to be added
    @returns boolean representing success or failure
  */
  public boolean addUnit(int numUnits){
    int result = this.numUnits + numUnits;
    this.numUnits = result;
    return true;
  }
  public boolean removeUnit(int numUnits){
    int result = this.numUnits - numUnits;
    if (result < 0){
      return false;
    }
    this.numUnits = result;
    return true;
  }

  public boolean setUnit(int numUnits){
    this.numUnits = numUnits;
    return true;
  }
}
