package edu.duke.ece651.server;
import edu.duke.ece651.shared.*;
//assign units to the territory
public class AssignUnits implements Initializer {
  private String playerName;
  private String territoryName;
  private Integer unitNum;
  private GameMap gmap;

  public AssignUnits(String playername, String territoryname,
                     Integer unitnum, GameMap gamemap) {
    this.playerName = playername;
    this.territoryName = territoryname;
    this.unitNum = unitnum;
    this.gmap = gamemap;
  }
  
  public void action() {
    Territory myterritory = gmap.findTerritory(territoryName);
    myterritory.getUnit(0).addUnit(unitNum); //addUnit(unitNum);
  }

  /*save for use in next evolution 
  public boolean validator() {
    Territory myterritory = gmap.findTerritory(territoryName);
    if (!myterritory.getPlayerName().equals(playerName)) {
      System.out.println("Territory: " + territoryName + "doesn't belong to player");
      return false;
    }
    return true;
  }
  */
}
