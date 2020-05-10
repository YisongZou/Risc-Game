package edu.duke.ece651.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

public class Parser {
  private GameMap gmap;
  private InitializerFactory initFactory;
  private ExecutorFactory execFactory;

  private ArrayList<Message> moveList;
  private ArrayList<Message> attackList;
  private ArrayList<Message> upgradeUnitList;
  private ArrayList<Message> upgradeTechList;
  private ArrayList<Message> researchCloakList;
  private ArrayList<Message> useCloakList;

  private ArrayList<Message> virusList;
  private ArrayList<Message> nuclearList;

  private ArrayList<Message> upgradeSpyList;
  private ArrayList<Message> moveSpyList;

  private boolean nuclearUsed;
    
  //Do the assgin territory group for this player who send the message
  public void executeAssignTerritoryGroup(Message message) {
    Initializer action = initFactory.create(message, gmap);
    action.action();
    this.gmap.add_player(message.getPlayerName());
    //Initially set the Refresh
    /*
    //Update the FogInfo field inside map
    HashMap<String, HashMap<String, String>> FogInfo = new HashMap<String, HashMap<String, String>>();
    FogInfo = this.gmap.getFogInfo();
    for (String own : this.gmap.getPlayerTerritoryStrings(message.getPlayerName())) {
    FogInfo.get(message.getPlayerName()).replace(own, "Refresh");
    }*/
  }
  
    
  //Input the mixed ArrayList of Move and Attack Messages and this function will automatically do the move and attacks in correct order
  public void messageExecutor(ArrayList<Message> messages) {
    for (String temp : this.gmap.getTerritoryNames()) {
      this.gmap.getTerritory(temp).consumeCloakTurn();
    }
      
    for (Message temp : messages) {
      if (temp.getType().equals("Move")) {//It is a move
        moveList.add(temp);
      } else if (temp.getType().equals("Attack")) {//It is an attack
        attackList.add(temp);
      } else if (temp.getType().equals("Upgrade_Unit")) {
        upgradeUnitList.add(temp);
      } //Upgrade_Tech
      else if (temp.getType().equals("Upgrade_Tech")) {
        upgradeTechList.add(temp);
      }//Upgrade_Tech
      else if (temp.getType().equals("Virus")) {
        virusList.add(temp);
      }
      else if (temp.getType().equals("Nuclear")) {
        nuclearList.add(temp);
        this.nuclearUsed = true;
      }
      else if (temp.getType().equals("Research_Cloak")) {
        researchCloakList.add(temp);
      }
      else if (temp.getType().equals("Use_Cloak")) {
        useCloakList.add(temp);
      }
      else if (temp.getType().equals("Upgrade_Spy")) {
        upgradeSpyList.add(temp);
      }
      else if (temp.getType().equals("Move_Spy")) {
        moveSpyList.add(temp);
      }
      
    }
    executeUpgradeUnit();
    executeMove();
    executeSendTroop();
    executeAttack();
    executeUpgradeTech();
    executeResearchCloak();
    executeUseCloak();

    executeUpgradeSpy();
    executeMoveSpy();
    
    executeVirus();
    executeNuclear();
    
    //Clear the attack and move lists after the current round
    this.moveList.clear();
    this.attackList.clear();
    this.upgradeTechList.clear();
    this.upgradeUnitList.clear();
    this.researchCloakList.clear();
    this.useCloakList.clear();
    this.upgradeSpyList.clear();
    this.moveSpyList.clear();
    this.virusList.clear();
    this.nuclearList.clear();
    
    
    if (!this.nuclearUsed) {
    
      //Update the FogInfo field inside map
      HashMap<String, HashMap<String, String>> FogInfo = new HashMap<String, HashMap<String, String>>();
      FogInfo = this.gmap.getFogInfo();
    
      /*
        System.out.println("---------------------------");
        //For test only
        for(String playername : this.gmap.getPlayerNames()){
        HashMap<String,String> terrInfo = new  HashMap<String,String>();
        terrInfo  = FogInfo.get(playername);
        for (String terrName : this.gmap.getTerritoryNames()) {
        System.out.println(terrName);
        System.out.println(terrInfo.get(terrName));
        }
        }
        System.out.println("---------------------------");  */
    
      //Set the Nrefresh
      for(String playername : this.gmap.getPlayerNames()){
        HashMap<String,String> terrInfo = new  HashMap<String,String>();
        terrInfo  = FogInfo.get(playername);
        for (String terrName : this.gmap.getTerritoryNames()) {
          if(terrInfo.get(terrName).equals("Refresh")){
            terrInfo.replace(terrName, "Nrefresh");
          }
        }
      }
      //Set the Refresh
      for(String playername : this.gmap.getPlayerNames()){
        for (String own : this.gmap.getPlayerTerritoryStrings(playername)) {
          FogInfo.get(playername).replace(own, "Refresh");
          for (Territory neighbor : this.gmap.getEnemyNeighbors(this.gmap.getTerritory(own), playername) )   {
            String neighName = neighbor.getTerritoryName();
            FogInfo.get(playername).replace(neighName, "Refresh");
          }
          for(String temp : this.gmap.getSpy(playername).getValidSpyLocation()){
            FogInfo.get(playername).replace(temp, "Refresh");
          }
        }
      }
    
      //Add one basic unit after each round
      for (String tempTerry : this.gmap.getTerritoryNames()) {
        gmap.getTerritory(tempTerry).addUnit(0, 1);
      }
      this.gmap.addFoodForPlayer();
      this.gmap.addTechForPlayer();

    }
  }
    
  private void executeResearchCloak() {
    for (Message temp : researchCloakList) {
      Executor action = this.execFactory.create(temp, gmap);
      action.action();
    }
  }
        
  public void executeNuclear() {
    // only get the first nuclear; if two players both carry out this action,
    // only the faster one win
    if (nuclearList.size() > 0) {
      Message bomb = this.nuclearList.get(0);
      Executor action = this.execFactory.create(bomb, gmap);
      action.action();
    }
  }
    
  private void executeUseCloak() {
    for (Message temp: useCloakList) {
      Executor action = this.execFactory.create(temp, gmap);
      action.action();
    }
  }
    
    
  public void executeVirus() {
    for (Message msg : virusList) {
      Executor action = this.execFactory.create(msg, gmap);
      action.action();
    }
  }

  public void executeUpgradeSpy() {
    for (Message msg : upgradeSpyList) {
      Executor action = this.execFactory.create(msg, gmap);
      action.action();
    }
  }

  public void executeMoveSpy() {
    for (Message msg : moveSpyList) {
      Executor action = this.execFactory.create(msg, gmap);
      action.action();
    }
  }
    
  //Input the ArrayList of AssignUnit Messages and this function will automatically assign the units
  public void executeAssignUnit(ArrayList<Message> assignunitList) {
    for (Message temp : assignunitList) {
      Initializer action = this.initFactory.create(temp, this.gmap);
      action.action();
    }
  }
    
  private void executeUpgradeUnit() {
    for (Message temp : upgradeUnitList) {
      Executor action = this.execFactory.create(temp, gmap);
      action.action();
    }
  }
    
  private void executeUpgradeTech() {
    for (Message temp : upgradeTechList) {
      Executor action = this.execFactory.create(temp, gmap);
      action.action();
    }
  }
    
  private void executeMove() {
    for (Message temp : moveList) {
      Executor action = this.execFactory.create(temp, gmap);
      action.action();
    }
  }
    
  private void executeAttack() {
    for (Message temp : attackList) {
      temp.setAttackType("attack");
      if (gmap.findTerritory(temp.getDestTerritory()).getPlayerName()
          .equals(gmap.findTerritory(temp.getSrcTerritory()).getTroopOwner())) {
        temp.setType("Move");
        Executor action = this.execFactory.create(temp, gmap);
        action.action();
      } else {
        Executor action = this.execFactory.create(temp, gmap);
        action.action();
      }
    }
  }
    
  private void executeSendTroop() {
    for (Message temp : attackList) {
      temp.setAttackType("sendTroop");
      Executor action = this.execFactory.create(temp, gmap);
      action.action();
    }
  }
    
  //Parser constructor
  public Parser(int playerNum) {
    this.gmap = new GameMap(playerNum);
    initFactory = new InitializerFactory();
    execFactory = new ExecutorFactory();
    moveList = new ArrayList<Message>();
    attackList = new ArrayList<Message>();
    upgradeUnitList = new ArrayList<Message>();
    upgradeTechList = new ArrayList<Message>();
    researchCloakList = new ArrayList<Message>();
    useCloakList = new ArrayList<Message>();
    virusList = new ArrayList<Message>();
    nuclearList = new ArrayList<Message>();
    upgradeSpyList =new ArrayList<Message>();
    moveSpyList = new ArrayList<Message>();
    nuclearUsed = false;
  }
    
  //Parser constructor
  // debug represents what state to init territory
  // 1: all territories are claimed
  // 2: no territories claimed
  public Parser(GameMap gmap) {
    this.gmap = gmap;
    initFactory = new InitializerFactory();
    execFactory = new ExecutorFactory();
    moveList = new ArrayList<Message>();
    attackList = new ArrayList<Message>();
    upgradeUnitList = new ArrayList<Message>();
    upgradeTechList = new ArrayList<Message>();
    researchCloakList = new ArrayList<Message>();
    useCloakList = new ArrayList<Message>();
    virusList = new ArrayList<Message>();
    nuclearList = new ArrayList<Message>();
    upgradeSpyList =new ArrayList<Message>();
    moveSpyList = new ArrayList<Message>();
    nuclearUsed = false;
  }
    
  public void setGmap(GameMap gmap) {
    this.gmap = gmap;
    virusList = new ArrayList<Message>();
    nuclearList = new ArrayList<Message>();
  }
  public GameMap getMap() {
    return this.gmap;
  }
    
  public void parseMessage(ArrayList<Message> ms) {
    for (Message m : ms) {
      parseMessage(m);
    }
  }
    
  public void parseMessage(Message message) {
    /////// udpate gamemap here?
    message.setGmap(this.getMap());
    
    if (message.getMessageType().equals("Execute")) {
      // if executor
    
      Executor action1 = this.execFactory.create(message, gmap);
      action1.action();
    } else if (message.getMessageType().equals("Assign")) {
      // if it is initializer
      Initializer action = this.initFactory.create(message, gmap);
      action.action();
    }
    //throw new Exception("Unknown message type");
    else {
      System.out.println("UNKNOWN MESSAGE TYPE");
    }
  }
    
  private String initialPromptHelper(String prompt, Integer groupID) {
    prompt = prompt + "Group " + Integer.toString(groupID) + "\n-----------\n";
    List<Territory> territoryList = gmap.getTerritoryGroup(groupID);
    for (int i = 0; i < territoryList.size(); i++) {
      prompt = prompt + territoryList.get(i).getTerritoryName() + " (next to:";
      ArrayList<String> neighborArray = gmap.getNeighborStrings(territoryList.get(i));
      int j = 0;
      for (j = 0; j < neighborArray.size() - 1; j++) {
        prompt = prompt + " " + neighborArray.get(j) + ",";
      }
      prompt = prompt + " " + neighborArray.get(j) + ")\n";
    }
    return prompt;
  }
    
  //Used for generating the initial prompt for user to know about the group that he can choose from initially as well as assign
  public Message initialPrompt() {
    Message message = new Message(null, null, null, null, null, null, gmap);
    String prompt = "Available Remaining Groups to Choose:\n";
    //Iterate through all groups, later should change to all unchosen groups
    //System.out.println("IDS");
    //System.out.println(gmap.getUnclaimedGroupIds());
    for (Integer temp : gmap.getUnclaimedGroupIds()) {
      prompt = initialPromptHelper(prompt, temp);
    }
    prompt = prompt + " Welcome player, which group do you want to choose?\n";
    message.setContent(prompt);
    message.setValidGroupIds(gmap.getUnclaimedGroupIds());
    return message;
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
      prompt = prompt + "( LV0:" + temp0.toString() + " LV1:" + temp1.toString() + " LV2:" + temp2.toString() + " LV3:"
        + temp3.toString() + " LV4:" + temp4.toString() + " LV5:" + temp5.toString() + " LV6:" + temp6.toString()
        + " ) units in " + territoryList.get(i).getTerritoryName() + " (next to:";
      ArrayList<String> neighborArray = gmap.getNeighborStrings(territoryList.get(i));
      int j = 0;
      for (j = 0; j < neighborArray.size() - 1; j++) {
        prompt = prompt + " " + neighborArray.get(j) + ",";
      }
      prompt = prompt + " " + neighborArray.get(j) + ")\n";
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
        Integer temp0 = currentNeighbor.get(j).getUnit(0).getNumUnits();
        Integer temp1 = currentNeighbor.get(j).getUnit(1).getNumUnits();
        Integer temp2 = currentNeighbor.get(j).getUnit(2).getNumUnits();
        Integer temp3 = currentNeighbor.get(j).getUnit(3).getNumUnits();
        Integer temp4 = currentNeighbor.get(j).getUnit(4).getNumUnits();
        Integer temp5 = currentNeighbor.get(j).getUnit(5).getNumUnits();
        Integer temp6 = currentNeighbor.get(j).getUnit(6).getNumUnits();
        prompt = prompt + "( LV0:" + temp0.toString() + " LV1:" + temp1.toString() + " LV2:" + temp2.toString()
          + " LV3:" + temp3.toString() + " LV4:" + temp4.toString() + " LV5:" + temp5.toString() + " LV6:"
          + temp6.toString() + " ) units in " + currentNeighbor.get(j).getTerritoryName() + " (next to:";
        ArrayList<String> neighborArray = gmap.getNeighborStrings(currentNeighbor.get(j));
        int k = 0;
        for (; k < neighborArray.size() - 1; k++) {
          prompt = prompt + " " + neighborArray.get(k) + ",";
        }
        prompt = prompt + " " + neighborArray.get(k) + ")\n";
      }
    }
    return prompt;
  }
    
  //New verion for evolution2 used for generating the prompt before user selecting
  // an option from move attack and done and Upgrade_Tech and Upgrade_Unit
  public String promptGenerator(String currPlayerName) {
    //String currPlayerName = this.name;
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
    prompt = prompt + "------------------------\nYou are the " + currPlayerName + " player\n------------------------\n";
    prompt += "Your own territories:\n";
    prompt = promptGeneratorHelper(prompt, currPlayerName);
    prompt += "------------------------\nAll immediately adjacent enemy territory:\n";
    prompt = promptNeighborHelper(prompt, currPlayerName);
    prompt = prompt + "\nYou have:\nFood Resource:" + this.gmap.getPly_resource().getFoodRsc(currPlayerName).toString()
      + "\nTech Resource:" + this.gmap.getPly_resource().getTechRsc(currPlayerName).toString()
      + "\nYour tech Level is:" + this.gmap.getPly_resource().getTechLevel(currPlayerName).toString();
    prompt = prompt + "\nPlayer, what would you like to do?\nPlease click the following button\n";

    return prompt;
  }


  public String promptGenerator(Message message){
    String currPlayerName = message.getPlayerName();
    String prompt = "";
    
    for (String playerName: this.gmap.getPlayerNames()){
      prompt = promptGeneratorHelper(prompt, playerName);
    }
    prompt = prompt + "\nYou have:\nFood Resource:" + this.gmap.getPly_resource().getFoodRsc(currPlayerName).toString()
      + "\nTech Resource:" + this.gmap.getPly_resource().getTechRsc(currPlayerName).toString()
      + "\nYour tech Level is:" + this.gmap.getPly_resource().getTechLevel(currPlayerName).toString();
    prompt = prompt + "\nYou are the " + currPlayerName +
      " player, what would you like to do?\n(M)ove\n(A)ttack\n(U)nit_Upgrade\n(T)ech_Upgrade\n(D)one\n";
    
    return prompt;
  }

  
  
}
  
