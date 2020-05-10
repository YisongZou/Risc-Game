package edu.duke.ece651.server;

import edu.duke.ece651.shared.*;

public class Upgrade_Spy implements Executor{
  private String playerName;
  //private String src_Terr;
  private String dst_Terr;
  private GameMap gmap;

  public Upgrade_Spy(String playername, String dst_terr, GameMap gamemap) {
    this.playerName = playername;
    this.dst_Terr = dst_terr;
    this.gmap = gamemap;
  }

  public void action() {
    gmap.upgradeSpy(playerName, dst_Terr);
  }
}
