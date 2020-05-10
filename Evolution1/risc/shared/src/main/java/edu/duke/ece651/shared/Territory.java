package edu.duke.ece651.shared;

public class Territory implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
  
  private String playerName;
  private String territoryName;
  private Unit s;
  private int groupId;
  private String troopOwner;
  
  /*
    Initializes territory
    @param territoryName This is a string representing the territory name
    @param playerName This is a string representing the player naem
    @param s This is a squad that will be assigned to this territory
  */
  public Territory(String territoryName, String playerName,Unit s) {
    this.playerName = playerName;
    this.territoryName = territoryName;
    this.s = s;
  }

  /*
    Initializes territory
    @param territoryName This is a string representing the territory name
    @param playerName This is a string representing the player name
  */
  public Territory(String territoryName, String playerName) {
    this.playerName = playerName;
    this.territoryName = territoryName;
  }

  /*
    Initializes territory
    @param territoryName This is a string representing the territory name
  */  
  public Territory(String territoryName) {
    this.territoryName = territoryName;
    this.playerName = null;    
    this.s = new BasicUnit();
  }


  /*
    Initializes territory
    @param territoryName This is a string representing the territory name
    @param groupId This is a integer representing group terr belongs to
  */  
  public Territory(String territoryName,int groupId) {
    this.territoryName = territoryName;
    this.playerName = null;    
    this.s = new BasicUnit();
    this.groupId = groupId;
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
    /*
      System.out.println(String.format("<Territory.java> setting terr: %s | pName: %s",
                                     this.territoryName,
                                     this.playerName)); 
    */   

  }
  
  /*
    Getter method for getting Unit name
    @returns squad that is occupying territory
  */  
  public Unit getUnit() {
    return this.s;
  }
  
  /*
    Setter method for getting Unit name
    @returns squad that is occupying territory
  */ 
  public void setUnit(Unit s) {
    this.s = s;
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
  public boolean addUnit(int numUnit){
    return this.s.addUnit(numUnit);
  }

  /*
    Method to set num unit to territory
    @param int numUnit of how many units to be added
    @returns boolean representing success or failure
  */      
  public boolean setUnit(int numUnit){
    return this.s.setUnit(numUnit);
  }  
  
  /*
    Method to remove unit from territory
    @param int numUnit of how many units to be removed
    @returns boolean representing success or failure
  */
  public boolean removeUnit(int numUnit){
    // this will return false if num units are negative
    return this.s.removeUnit(numUnit);
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
