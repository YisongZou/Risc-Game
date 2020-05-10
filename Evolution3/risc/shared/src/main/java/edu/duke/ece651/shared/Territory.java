package edu.duke.ece651.shared;

import java.util.ArrayList;

public class Territory implements java.io.Serializable {
  private static final long serialVersionUID = 1L;

  private String playerName;
  private String territoryName;
  private Unit s;
  private int groupId;
  private String troopOwner;
  private int size;
  private Integer CloakTurns;
  
  private ArrayList<Unit> allUnits;
  private Integer foodProduction;
  private Integer techProduction;

  public void setAllUnits( ArrayList<Unit> temp) {
    this.allUnits = temp;
  }

  public void initCloakTurn() {
    this.CloakTurns = -1;
  }

  public void setCloakTurn(Integer num) {
    this.CloakTurns = num;
  }

  public Integer getCloakTurn() {
    return this.CloakTurns;
  }

  //Return true if there is CloakTurns remaining
  public boolean consumeCloakTurn() {
    if (this.CloakTurns > 0) {
      this.CloakTurns--;
      return true;
    }
    else {
      return false;
    }
  }

  

  //initialize helper for arraylist<units>
  public void init_unitlist() {
    this.allUnits = new ArrayList<Unit>();
    this.allUnits.add(new BasicUnit());
    this.allUnits.add(new Level1Unit());
    this.allUnits.add(new Level2Unit());
    this.allUnits.add(new Level3Unit());
    this.allUnits.add(new Level4Unit());
    this.allUnits.add(new Level5Unit());
    this.allUnits.add(new Level6Unit());
  }
  /*
    Initializes territory
    @param territoryName This is a string representing the territory name
    @param playerName This is a string representing the player naem
    @param s This is a squad that will be assigned to this territory
  */
  public Territory(String territoryName, String playerName,Unit s) {
    this(territoryName, playerName);    
    this.s = s;
    this.foodProduction = 50;
    this.techProduction = 50;
    init_unitlist();
    this.initCloakTurn();
  }
  
  /*
    Initializes territory
    @param territoryName This is a string representing the territory name
    @param playerName This is a string representing the player name
  */
  public Territory(String territoryName, String playerName) {
    this(territoryName);    
    this.playerName = playerName;
    this.foodProduction = 50;
    this.techProduction = 50;
    init_unitlist();
    this.initCloakTurn();
  }

  /*
    Initializes territory
    @param territoryName This is a string representing the territory name
    @param groupId This is a integer representing group terr belongs to
  */  
  public Territory(String territoryName,int groupId) {
    this(territoryName);    
    this.groupId = groupId;
    this.foodProduction = 50;
    this.techProduction = 50;
    init_unitlist();
    this.initCloakTurn();
  }
  

  /*
    Initializes territory
    @param territoryName This is a string representing the territory name
  */  
  public Territory(String territoryName) {
    this.territoryName = territoryName;
    this.playerName = null;    
    this.s = new BasicUnit();
    this.size = 1;
    this.foodProduction = 50;
    this.techProduction = 50;
    init_unitlist();
    this.initCloakTurn();
  }  

  /*
    Set the size of the territory
   */  
  public void setSize(int size){
    this.size = size;
  }
  
  public int getSize(){
    return this.size;
  }

  public ArrayList<Unit> getAllUnits() {
    return this.allUnits;
  }
  
  public String getTroopOwner(){
    return this.troopOwner;
  }
  
  public void setTroopOwner(String owner) {
    this.troopOwner = owner;
  }
  
  public int getGroupId() {
    return this.groupId;
  }
  
  
  /*
    Getter method for getting player name
    @returns playername
  */    
  public String getPlayerName() {
    return this.playerName;
  }
  
  /*
    Setter method for getting Unit name
    @returns squad that is occupying territory
  */ 
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  /*
    get method for add/set unit
    @find unit of certain level
   */  
  public Unit getUnit(int unitLevel) {
    for(Unit units : allUnits){
      if (units.getLevel() == unitLevel) {
        return units;
      }
    }
    return null;
  }
  /*
    Getter method for getting Unit name
    @returns squad that is occupying territory
  */  
  public Unit getUnit() {
    return this.getUnit(0);
  }
  
  /*
    Setter method for getting Unit name
    @returns squad that is occupying territory
  */ 
  public void setUnit(Unit s) {
    this.allUnits.set(0, s);
  }

  /*
    Getter method for getting territory name
    @returns territoryName
  */    
  public String getTerritoryName() {
    return this.territoryName;
  }

  /*
    Method to add num unit to territory
    @param int numUnit of how many units to be added
    @returns boolean representing success or failure
  */    
  public boolean addUnit(int numUnit) {
    return this.s.addUnit(numUnit);
  }

  public boolean addUnit(int unitLevel, int numUnit) {
    Unit myunit = getUnit(unitLevel);
    assert (myunit != null);
    return myunit.addUnit(numUnit);
  }
  /*
    Method to set num unit to territory
    @param int numUnit of how many units to be added
    @returns boolean representing success or failure
  */      
  public boolean setUnit(int numUnit){
    return this.s.setUnit(numUnit);
  }  

  public boolean setUnit(int unitLevel, int numUnit) {
    Unit myunit = getUnit(unitLevel);
    return myunit.setUnit(numUnit);
  }
  /*
    Method to remove unit from territory
    @param int numUnit of how many units to be removed
    @returns boolean representing success or failure
  */
  public boolean removeUnit(int numUnit) {
    // this will return false if num units are negative
    return this.s.removeUnit(numUnit);
  }
  public boolean removeUnit(int unitLevel, int numUnit) {
    Unit myunit = getUnit(unitLevel);
    return myunit.removeUnit(numUnit);
  }

  /*
    this function upgrade certain amount of level x unit to level x + 1
    @it takes 2 parameter:current unitlevel, number of units
    @returns true or false indicating the result
   */
  public boolean upgradeUnit(int unitLevel, int unitNum) {
    Unit srcunit = getUnit(unitLevel);
    Unit dstunit = getUnit(unitLevel + 1);
    if (srcunit == null) {
      return false;
    }
    if (dstunit == null) {
      return false;
    }
    if (srcunit.removeUnit(unitNum) == false) {
      return false;
    }
    if (dstunit.addUnit(unitNum) == false) {
      return false;
    }
    return true;
  }
  /*
    These 2 fucntion returns the food and tech production of this territory
   */
  public Integer getFoodProduction() {
    return foodProduction;
  }

  public Integer getTechProduction() {
    return techProduction;
  }
  
  public int getgroupId() {
    return groupId;
  }

  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }
}
