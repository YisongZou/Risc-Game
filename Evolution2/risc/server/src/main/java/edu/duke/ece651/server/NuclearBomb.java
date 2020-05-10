package edu.duke.ece651.server;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Territory;

public class NuclearBomb implements Executor {
  private String playerName;
  private GameMap gmap;

  public NuclearBomb(String plyName, GameMap gmap) {
    this.playerName = plyName;
    this.gmap = gmap;
  }

  public void action() {
    for (Territory t : this.gmap.getMapGraph().vertexSet()) {
      // change all other players' territory's plyname to be this playerName
      if (!t.getPlayerName().equals(this.playerName)) {
        t.setPlayerName(playerName);
      }
    }
  }
}
