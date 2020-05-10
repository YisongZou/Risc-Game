package edu.duke.ece651.server;

import edu.duke.ece651.shared.*;

public class Research_Cloak implements Executor {
  private String playerName;
  private GameMap gmap;

  public Research_Cloak(String player, GameMap gmap) {
    this.playerName = player;
    this.gmap = gmap;
  }
  
  public void action() {
    this.gmap.researchCloak(playerName);
    gmap.getPly_resource().consumeTech(playerName, 100);
  }
}
