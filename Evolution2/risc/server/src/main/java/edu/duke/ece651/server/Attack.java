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
  private ArrayList<Integer> atkLevels;

  public Attack(String playername, String srcTerritoryName,
                String dstTerritoryName, Integer unitnum,
                GameMap gamemap,String attackType,ArrayList<Integer> attackLevels) {
    playerName = playername;
    srcTerritory = srcTerritoryName; 
    dstTerritory = dstTerritoryName;
    unitNum = unitnum;
    gmap = gamemap;
    atkType = attackType;
    atkLevels = attackLevels;
  }

  /*Left for use in later version
  public boolean Validator(){
    return true;
  }
  */

  private int getLowestAttackerLevel(){
    for (int j = 0 ; j < atkLevels.size(); ++j) {
          if (atkLevels.get(j) > 0) {
            return j;
          }
        }
        return 0;//should never reach here
  }

  private int getHighestAttackerLevel(){
    for (int j = atkLevels.size()- 1 ; j >= 0; --j) {
          if (atkLevels.get(j) > 0) {
            return j;
          }
        }
        return 0;//should never reach here
  }

  private int getLowestDefenderLevel(ArrayList<Unit> allUnits){
    for (int j = 0 ; j < allUnits.size(); ++j) {
          if (allUnits.get(j).getNumUnits() > 0) {
            return j;
          }
        }
        return 0;//should never reach here
  }

  private int getHighestDefenderLevel(ArrayList<Unit> allUnits){
    for (int j = allUnits.size()-1 ; j >= 0; --j) {
          if (allUnits.get(j).getNumUnits() > 0) {
            return j;
          }
        }
    return 0;//should never reach here
  }
  
  public void action(){
    System.out.println(this.atkType);
    if(atkType.equals("sendTroop")){
      for(int i = 0 ; i < this.atkLevels.size(); ++i){
        gmap.getTerritory(srcTerritory).removeUnit(i,atkLevels.get(i));
        gmap.getTerritory(srcTerritory).setTroopOwner(playerName);
      }
    }
    else{
      //Consume the foods
      //System.out.println("Consuming food");
      int foodConsume = 0;
      for (Integer i : atkLevels) {
        foodConsume += i;
      }
      this.gmap.getPly_resource().consumeFood(playerName, foodConsume);

      //System.out.println("Checking if defender has any units");
      //Check if the defender has any units
      boolean flag = false;
      for (Unit i : gmap.getTerritory(dstTerritory).getAllUnits()) {
        if (i.getNumUnits() > 0) {
          flag = true;
        }
      }
      boolean attackerLost = false;//Used to determine who lost
      boolean defenderLost = true;
      int roundLoser = 0;//0 represents that attacker lost the previous battle of units, 1 represents that defender lost, it is initially set to 0 because we will let the higest bonus unit of attacker to attack the lowest bonus of defender unit first
      System.out.println("Battle starts");
      while (flag) {
        int attackHighestLevel = this.getHighestAttackerLevel();
        int attackLowestLevel = this.getLowestAttackerLevel();
        int defendHighestLevel = this.getHighestDefenderLevel(gmap.getTerritory(dstTerritory).getAllUnits());
        int defendLowestLevel = this.getLowestDefenderLevel(gmap.getTerritory(dstTerritory).getAllUnits());
        int attackBonus = 0;
        int defenderBonus = 0;
        int attackLevel;
        int defendLevel;
        if (roundLoser == 0) {//Attacker Lost last round
          attackBonus = gmap.getTerritory(srcTerritory).getAllUnits().get(attackHighestLevel).getBonus();
          defenderBonus = gmap.getTerritory(dstTerritory).getAllUnits().get(defendLowestLevel).getBonus();
          attackLevel = attackHighestLevel;
          defendLevel = defendLowestLevel;
        } else {//Defender lost last round
          attackBonus = gmap.getTerritory(srcTerritory).getAllUnits().get(attackLowestLevel).getBonus();
          defenderBonus = gmap.getTerritory(dstTerritory).getAllUnits().get(defendHighestLevel).getBonus();
          attackLevel = attackLowestLevel;
          defendLevel = defendHighestLevel;
        }
        Random random1 = new Random();
        int result1 = random1.nextInt(20); // return integer in [0,20) 
        int atkDice = result1 + 1 + attackBonus;
        Random random2 = new Random();
        int result2 = random2.nextInt(20); // return integer in [0,20)
        int defDice = result2 + 1 + defenderBonus;
        if (defDice >= atkDice) { //Defender wins this round
          System.out.println("Defender wins this round");
          atkLevels.set(attackLevel, atkLevels.get(attackLevel) - 1);
          roundLoser = 0;
        } else {//Attacker wins this round
          System.out.println("Attacker wins this round");
          gmap.getTerritory(dstTerritory).removeUnit(defendLevel, 1);
          roundLoser = 1;
        }
        //check if someone lose
        defenderLost = true;
        for (Unit i : gmap.getTerritory(dstTerritory).getAllUnits()) {
          if (i.getNumUnits() > 0) {
            defenderLost = false;
          }
        }
        attackerLost = true;
        for (Integer i : this.atkLevels) {
          if (i > 0) {
            attackerLost = false;
          }
        }
        flag = (!attackerLost) && (!defenderLost);
      }
      
      if (attackerLost) {//Defender wins this attack
        System.out.println("Defender wins the whole attack");
        return;
      }
      else {//Attacker wins the attack
        System.out.println("Attacker wins the whole attack");
        gmap.getTerritory(dstTerritory).setPlayerName(gmap.getTerritory(srcTerritory).getTroopOwner());
        gmap.getTerritory(dstTerritory).setCloakTurn(-1);
        for (int i = 0; i < atkLevels.size();++i) {
          gmap.getTerritory(dstTerritory).addUnit(i, atkLevels.get(i));
        }
      }
    }
  } 
  /*
  public void setAttackType(String s){
    this.atkType = s;
  }
  */
}
