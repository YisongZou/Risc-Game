package edu.duke.ece651.client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

public class Player {

  private String name;
  private GameMap gmap;

  private Scanner scanner;

  private ActionListener listener;

  private TextFieldPanel tpanel;
  private Message message;

  public ArrayList<Message> messages;

  private ButtonsPanel buttonsPanel;

  public int upgradetech_flag;// = 1 if already upgrade tech once
  public int researchCloak;
  public int movedFlag;
  public int upgradeFlag;    
  public int updatemap_flag;



  public Player(String name){
    messages = new ArrayList<Message>();
    this.name = name;    
    // initialize JFrame
    // this.frame = new JFrame("Game");
    
    // initialize order controller
    this.researchCloak = 0;
    this.upgradetech_flag = 0;
    this.updatemap_flag = 0;
  }

  public Player(String name,GameMap gmap){
    this(name);
    this.gmap = gmap;
    this.upgradetech_flag = 0;    
    //  msg = new Message("");   // initialize msg to be empty
    gmap = new GameMap(1);
    this.upgradetech_flag = 0;
    this.updatemap_flag = 0;
    this.movedFlag = 0;
    this.upgradeFlag = 0;    
  }
  
  
  public Player(String name,GameMap gmap,
                ActionListener listener){
    this(name, gmap);
    this.listener = listener;
    this.updatemap_flag = 0;
    this.upgradetech_flag = 0;
    this.updatemap_flag = 0;
    this.movedFlag = 0;
    this.upgradeFlag = 0;        
  }

  public String promptTerritoryString(String territoryName){
    String prompt = "";
    Territory terr = this.gmap.getTerritory(territoryName);
    
    //get number of unit for each level
    Integer temp0 = terr.getUnit(0).getNumUnits();
    Integer temp1 = terr.getUnit(1).getNumUnits();
    Integer temp2 = terr.getUnit(2).getNumUnits();
    Integer temp3 = terr.getUnit(3).getNumUnits();
    Integer temp4 = terr.getUnit(4).getNumUnits();
    Integer temp5 = terr.getUnit(5).getNumUnits();
    Integer temp6 = terr.getUnit(6).getNumUnits();
    prompt = prompt + terr.getTerritoryName() + "<br>"+
      "Occupied by: " + terr.getPlayerName() + "<br>";
    

    int numSpies = this.gmap.getSpyNum(this.name, territoryName);      
    prompt = prompt  + numSpies + " spies <br>";

    
    prompt = prompt + 
      " LV0:" +  temp0.toString() + "<br>"+
      " LV1:" +  temp1.toString()+ "<br>"+
      " LV2:" +  temp2.toString() + "<br>"+
      " LV3:" +  temp3.toString() +"<br>"+
      " LV4:" +  temp4.toString() +"<br>"+
      " LV5:" +  temp5.toString() +"<br>"+
      " LV6:" +  temp6.toString();    
    return prompt;
  }

  private String promptGeneratorHelper(String prompt, String name) {
    //prompt = prompt + name + " player:\n-----------\n";
    List<Territory> territoryList = gmap.getPlayerTerritories(name);
    for (int i = 0; i < territoryList.size(); i++) {
      //get number of unit for each level
      Integer temp0 = territoryList.get(i).getUnit(0).getNumUnits();
      Integer temp1 = territoryList.get(i).getUnit(1).getNumUnits();
      Integer temp2 = territoryList.get(i).getUnit(2).getNumUnits();
      Integer temp3 = territoryList.get(i).getUnit(3).getNumUnits();
      Integer temp4 = territoryList.get(i).getUnit(4).getNumUnits();
      Integer temp5 = territoryList.get(i).getUnit(5).getNumUnits();
      Integer temp6 = territoryList.get(i).getUnit(6).getNumUnits();
      prompt = prompt + territoryList.get(i).getTerritoryName() + ":( LV0:" + temp0.toString() + " LV1:"
          + temp1.toString() + " LV2:" + temp2.toString() + " LV3:" + temp3.toString() + " LV4:" + temp4.toString()
        + " LV5:" + temp5.toString() + " LV6:" + temp6.toString() + " ) units  \n";// + " (next to:";
      /* ArrayList<String> neighborArray = gmap.getNeighborStrings(territoryList.get(i));
      int j = 0;
      for (j = 0; j < neighborArray.size() - 1; j++) {
        prompt = prompt + " " + neighborArray.get(j) + ",";
      }
      prompt = prompt + " " + neighborArray.get(j) + ")\n";
      */
      if (territoryList.get(i).getCloakTurn() != -1) {
        prompt = prompt + "...Cloak has " + territoryList.get(i).getCloakTurn().toString() + " turn(s) remaining\n";
      }
    }
    return prompt;
  }

  private String promptNeighborHelper(String prompt, String name) {
    List<Territory> territoryList = gmap.getPlayerTerritories(name);
    for (int i = 0; i < territoryList.size(); i++) {
      List<Territory> currentNeighbor = gmap.getEnemyNeighbors(territoryList.get(i), name);
      //prompt += "Adjacent enemy territory for " + territoryList.get(i).getTerritoryName() + "\n";
      for (int j = 0; j < currentNeighbor.size(); j++) {
        //get number of unit for each level
        if (!currentNeighbor.get(j).consumeCloakTurn()) {
          Integer temp0 = currentNeighbor.get(j).getUnit(0).getNumUnits();
          Integer temp1 = currentNeighbor.get(j).getUnit(1).getNumUnits();
          Integer temp2 = currentNeighbor.get(j).getUnit(2).getNumUnits();
          Integer temp3 = currentNeighbor.get(j).getUnit(3).getNumUnits();
          Integer temp4 = currentNeighbor.get(j).getUnit(4).getNumUnits();
          Integer temp5 = currentNeighbor.get(j).getUnit(5).getNumUnits();
          Integer temp6 = currentNeighbor.get(j).getUnit(6).getNumUnits();
          prompt = prompt + "( LV0:" + temp0.toString() + " LV1:" + temp1.toString() + " LV2:" + temp2.toString()
              + " LV3:" + temp3.toString() + " LV4:" + temp4.toString() + " LV5:" + temp5.toString() + " LV6:"
            + temp6.toString() + " ) units in " + currentNeighbor.get(j).getTerritoryName();// + " (next to:";
          
          ArrayList<String> neighborArray = gmap.getNeighborStrings(currentNeighbor.get(j));
          int k = 0;
          for (; k < neighborArray.size() - 1; k++) {
            prompt = prompt + " " + neighborArray.get(k) + ",";
          }
          //prompt = prompt + " " + neighborArray.get(k) + ")\n";
          prompt = prompt +" \n";          
        }
      }
    }
    return prompt;
  }

  
  
  //New verion for evolution2 used for generating the prompt before user selecting
  // an option from move attack and done and Upgrade_Tech and Upgrade_Unit
  public String promptGenerator(){
    String currPlayerName = this.name;
    String prompt = "";
    /*
    //////////////////////////////////For test use, need to be deleted
    prompt += "---------------For test use, need to be deleted-------------------------------\n";
    for (String playerName: this.gmap.getPlayerNames()){
      prompt = promptGeneratorHelper(prompt, playerName);
    }
    prompt += "---------------For test use, need to be deleted-------------------------------\n";
    /////////////////////////////////////////////////////////////////
    */
    prompt = prompt + "------------------------\nYou are the " + currPlayerName +
      " player\n------------------------\n";
    prompt += "Your own territories:\n";
    prompt = promptGeneratorHelper(prompt, currPlayerName);
    /*
      prompt += "\n------------------------\nAll immediately adjacent enemy territory:\n";
      prompt = promptNeighborHelper(prompt, currPlayerName);
    */
    prompt = prompt + "\nYou have:\nFood Resource:" + this.gmap.getPly_resource().getFoodRsc(currPlayerName).toString()
        + "\nTech Resource:" + this.gmap.getPly_resource().getTechRsc(currPlayerName).toString()
        + "\nYour tech Level is:" + this.gmap.getPly_resource().getTechLevel(currPlayerName).toString();
        prompt = prompt
          + "\nPlayer, what would you like to do?\nPlease click the following button\n";
    
    return prompt;
  }

  
  public void clearMessages(){
    this.messages = new ArrayList<>();
  }
  
  public GameMap getGmap() {
    return gmap;
  }
  
  public void setGmap(GameMap newGmap, String ini) {
    System.out.println("-------Updating all gamemap");
    this.gmap = newGmap;
  }
  
  
  public void setGmap(GameMap newGmap) {
     
    // itereate through territories, updating
    // based on new game map
     if (this.updatemap_flag != 0){
       System.out.println("-------Updating partially gamemap");
       this.gmap.setPlayerResource(newGmap.getPly_resource());
       this.gmap.setHasCloak(newGmap.getHasCloak());
       this.gmap.setFogInfo(newGmap.getFogInfo());
       this.gmap.setSpyMap(newGmap.getSpyMap());
    for(String territoryName:newGmap.getTerritoryNames()){
      // get hashmap visibility
      // if visible, replace vertex
      if(newGmap.getFogInfo().get(this.name).get(territoryName).equals("Refresh")){
        Territory oldTerr = this.gmap.getTerritory(territoryName);
        Territory newTerr = newGmap.getTerritory(territoryName);        
        this.gmap.updateMap(oldTerr, newTerr);
      }
      }
     }
     else{
      System.out.println("-------Updating initial assigned gamemap"); 
    this.gmap = newGmap;
    this.updatemap_flag++;
     }
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<Message> getMessages() {
    return messages;
  }
}
