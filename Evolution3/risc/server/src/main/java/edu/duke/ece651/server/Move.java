package edu.duke.ece651.server;
import java.util.ArrayList;

import edu.duke.ece651.shared.*;
public class Move implements Executor{
  private String playerName;
  private String src_Terr;
  private String dst_Terr;
  private Integer unitNum;
  private GameMap gmap;
  private Integer lev;

  public Move(String playername, String src_terr, String dst_terr,
              Integer unitnum, GameMap gamemap, Integer level) {
    //move units from one place to another
    //right now we just make changes to the number
    //but later we need to modify it
    //maynot: every unit has a number. we just modify that number
    //every territory has all kinds of unit, and we just manipulate them by number
    playerName = playername;
    src_Terr = src_terr;
    dst_Terr = dst_terr;
    unitNum = unitnum;
    gmap = gamemap;
    lev = level;
  }
  
  public void action(){
    System.out.println("move - action");
    System.out.println(gmap.getTerritoryNames());
    System.out.println(this.src_Terr);
    System.out.println(this.dst_Terr);        
    // Consume food
    int cost = this.gmap.getCostBetweenTerritories(gmap.getTerritory(this.src_Terr),
                                                   gmap.getTerritory(this.dst_Terr))*unitNum;
    gmap.getPly_resource().consumeFood(this.playerName, cost);
    // remove unit from src territory and add to dst territory
    gmap.findTerritory(src_Terr).removeUnit(lev, unitNum);
    gmap.findTerritory(dst_Terr).addUnit(lev, unitNum);
  }
  
}
