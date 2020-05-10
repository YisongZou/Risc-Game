package edu.duke.ece651.server;

import edu.duke.ece651.shared.*;

public class Use_Cloak implements Executor {
  private String playerName;
  private String dstTerr;
  private GameMap gmap;
  
  public Use_Cloak(String plyName, String dstTerritory, GameMap gmap) {
    this.playerName = plyName;
    this.dstTerr = dstTerritory;
    this.gmap = gmap;
  }
  
  public void action() {
    gmap.getTerritory(dstTerr).setCloakTurn(3);
    gmap.getPly_resource().consumeTech(playerName, 20);
  }
}
