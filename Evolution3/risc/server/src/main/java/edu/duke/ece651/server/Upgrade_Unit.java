package edu.duke.ece651.server;

import edu.duke.ece651.shared.*;

public class Upgrade_Unit implements Executor {
  private String playerName;
  //private String src_Terr;
  private String dst_Terr;
  private Integer unitNum;
  private GameMap gmap;
  private Integer level;
  
  public Upgrade_Unit(String playername, String dst_terr, Integer unitnum, GameMap gamemap, Integer level) {
    this.playerName = playername;
    this.dst_Terr = dst_terr;
    this.unitNum = unitnum;
    this.level = level;
    this.gmap = gamemap;
  }

  //find the certain level unit in terriory, and upgrade them
  public void action() {
    gmap.upgradeUnit(playerName, dst_Terr, level, unitNum);
  }
}
