package edu.duke.ece651.shared;

public interface Unit extends java.io.Serializable {  
  public int getNumUnits();
  public boolean removeUnit(int numUnits);
  public boolean addUnit(int numUnits);
  public boolean setUnit(int numUnits);
}
