package edu.duke.ece651.server;
import edu.duke.ece651.shared.*;
public class Move_Spy implements Executor{
  private String playerName;
  private String src_Terr;
  private String dst_Terr;
  private GameMap gmap;

  public Move_Spy(String playername, String src_terr, String dst_terr, GameMap gamemap) {
    playerName = playername;
    src_Terr = src_terr;
    dst_Terr = dst_terr;
    gmap = gamemap;
  }
  
  public void action() {
    gmap.getSpy(playerName).moveSpy(src_Terr, dst_Terr);
  }
}
