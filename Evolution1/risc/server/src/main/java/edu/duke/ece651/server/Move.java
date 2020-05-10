package edu.duke.ece651.server;
import java.util.ArrayList;

import edu.duke.ece651.shared.*;
public class Move implements Executor{
  private String playerName;
  private String src_Terr;
  private String dst_Terr;
  private Integer unitNum;
  private GameMap gmap;

  public Move(String playername, String src_terr, String dst_terr, Integer unitnum, GameMap gamemap) {
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
  }


  /* this part is validator, we may need to use them on next evolution
  public boolean Validator(){
    Territory src_territory = gmap.findTerritory(src_Terr);
    Territory dst_territory = gmap.findTerritory(dst_Terr);
    //
    //if src_terr doesn't belong to player
    if(!src_territory.getPlayerName().equals(playerName)){
      System.out.println("Territory: " + src_Terr +  " doesn't belong to player");
      return false;
    }
    //if dst_terr doesn't belong to player
    if(!dst_territory.getPlayerName().equals(playerName)){
      System.out.println("Territory: " + dst_Terr +  " doesn't belong to player");
      return false;
    }
    //if src_terr doesn't have enough units to move
    if (src_territory.getUnit().getNumUnits() < unitNum) {
      System.out.println("Territory: " + src_Terr + " doesn't have enough units to move!");
      return false;
    }
    return true;
  }
  */
  
  public void action(){
    //remove unit from src territory and add to dst territory
    gmap.findTerritory(src_Terr).removeUnit(unitNum);
    gmap.findTerritory(dst_Terr).addUnit(unitNum);
  }

}
