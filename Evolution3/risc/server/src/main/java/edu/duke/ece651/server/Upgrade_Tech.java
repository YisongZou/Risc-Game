package edu.duke.ece651.server;

import edu.duke.ece651.shared.*;

public class Upgrade_Tech implements Executor {
  private String playerName;
  private GameMap gmap;

  public Upgrade_Tech(String playerName, GameMap gmap) {
    this.playerName = playerName;
    this.gmap = gmap;
  }
  
  public void action() {
    //gmap.upgradeTech(playerName);
    gmap.getPly_resource().upgradeTech(playerName);
  }
}
