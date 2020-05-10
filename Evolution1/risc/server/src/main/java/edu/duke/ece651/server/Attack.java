package edu.duke.ece651.server;

import edu.duke.ece651.shared.BasicUnit;
import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Unit;
import java.util.*;

public class Attack implements Executor {
  private String playerName;
  private String srcTerritory;
  private String dstTerritory;
  private String atkType;
  private Integer unitNum;
  private GameMap gmap;

  public Attack(String playername, String srcTerritoryName,
                String dstTerritoryName, Integer unitnum,
                GameMap gamemap,String attackType) {
    playerName = playername;
    srcTerritory = srcTerritoryName; 
    dstTerritory = dstTerritoryName;
    unitNum = unitnum;
    gmap = gamemap;
    atkType = attackType;
  }

  /*Left for use in later version
  public boolean Validator(){
    return true;
  }
  */
  
  public void action(){
    System.out.println(this.atkType);
    if(atkType.equals("sendTroop")){
      gmap.getTerritory(srcTerritory).removeUnit(unitNum);
      gmap.getTerritory(srcTerritory).setTroopOwner(playerName);
    }
    else{
      while((unitNum != 0) &&
            (gmap.getTerritory(dstTerritory).getUnit().getNumUnits() !=0) ){
        Random random1=new Random();
        int result1 = random1.nextInt(20); // return integer in [0,20) 
        int atkDice = result1 +1;
        Random random2=new Random();
        int result2 = random2.nextInt(20); // return integer in [0,20)
        int defDice = result2 + 1;
        if (defDice >= atkDice) { //Defender wins this round
          unitNum -= 1;
        }
        else {//Attacker wins this round
          gmap.getTerritory(dstTerritory).removeUnit(1);
        }
      }
      if (unitNum == 0) {//Defender wins this attack
        return;
      }
      else {//Attacker wins the attack
        gmap.getTerritory(dstTerritory).setPlayerName(gmap.getTerritory(srcTerritory).getTroopOwner());
        Unit attackUnit = new BasicUnit(unitNum);
        gmap.getTerritory(dstTerritory).setUnit(attackUnit);
      }
    }
  } 
  /*
  public void setAttackType(String s){
    this.atkType = s;
  }
  */
}
