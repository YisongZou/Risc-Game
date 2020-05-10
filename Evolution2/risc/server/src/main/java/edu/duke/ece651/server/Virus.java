package edu.duke.ece651.server;

import edu.duke.ece651.shared.GameMap;

public class Virus implements Executor {
  private String playerName;
  private GameMap gmap;
  private int techRscCost;

  public Virus(String playerName, GameMap gmap) {
    this.playerName = playerName;
    this.gmap = gmap;
    this.techRscCost = 34;
  }

  public int twentyPercent(Integer rsc) {
    double dle = rsc;
    dle *= 0.2;
    int ret = (int)dle;
    return ret;
  }
  
  @Override
  public void action() {
    for (String plyName : this.gmap.getPlayerNames()) {
      // clear food resource for every other player 
      if (!plyName.equals(this.playerName)) {
        int curFoodRsc = this.gmap.getPly_resource().getFoodRsc(plyName);
        this.gmap.getPly_resource().consumeFood(plyName, curFoodRsc);   
      }
    }
    System.out.println("playreName: " + this.playerName);
    this.gmap.getPly_resource().consumeTech(this.playerName, this.techRscCost);   // cost 2000 techRsc to trigger virus
  }
  
}
