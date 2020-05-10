package edu.duke.ece651.shared;

public class BasicUnit implements Unit{
  private static final long serialVersionUID = 1L;
  
  private int numUnits;
  
  public BasicUnit(){
    numUnits = 0;
  }

  public BasicUnit(int numUnits){
    this.numUnits = numUnits;
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
