package edu.duke.ece651.server;

import edu.duke.ece651.shared.*;
// assign a territory to player
public class AssignTerritory implements Initializer {
  private String playerName;
  private GameMap gmap;
  private Integer groupId;

  public AssignTerritory(String playername, Integer groupId, GameMap gamemap) {
    this.playerName = playername;
    this.groupId = groupId;
    gmap = gamemap;
  }

  //assign all territories in group to player
  public void action(){
    gmap.setTerritoryGroupPlayer(groupId, playerName); 
  }

  /*save for use on next evolution
  The territory we are assigning to should not have a player
  public boolean validator() {
    Territory assigned_territory = gmap.findTerritory(territoryName);
    if (!assigned_territory.getPlayerName().equals(null)) {
      System.out.println("Territory: " + territoryName + " has already been occupied");
      return false;
    }
    return true;
  }
  */
}
