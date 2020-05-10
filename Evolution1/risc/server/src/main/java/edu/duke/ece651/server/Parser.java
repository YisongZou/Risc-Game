package edu.duke.ece651.server;
import edu.duke.ece651.shared.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {
  private GameMap gmap;
  private InitializerFactory initFactory;
  private ExecutorFactory execFactory;


  private ArrayList<Message> moveList;
  private ArrayList<Message> attackList;

  //Do the assgin territory group for this player who send the message
  public void executeAssignTerritoryGroup(Message message) {
    Initializer action = initFactory.create(message, gmap);   
    action.action();
  }

  //Input the mixed ArrayList of Move and Attack Messages and this function will automatically do the move and attacks in correct order
  public void messageExecutor(ArrayList<Message> messages) {
    for (Message temp : messages) {
      if(temp.getType().equals("Move")){//It is a move
        moveList.add(temp);
        }
      else {//It is an attack
        attackList.add(temp);
        }
    }
    executeMove();
    executeSendTroop();
    executeAttack();
    
    //Clear the attack and move lists after the current round
    this.moveList.clear();
    this.attackList.clear();
    for (String tempTerry : this.gmap.getTerritoryNames()) {
      gmap.getTerritory(tempTerry).addUnit(1);
    }
  }
  
  //Input the ArrayList of AssignUnit Messages and this function will automatically assign the units
  public void executeAssignUnit(ArrayList<Message> assignunitList) {
    for (Message temp : assignunitList) {
      Initializer action = this.initFactory.create(temp,this.gmap);
      action.action();
    }
  }

  private void executeMove() {
    for (Message temp : moveList) {
      Executor action = this.execFactory.create(temp, gmap);
      action.action();
    }
  }
  
  private void executeAttack(){
    for(Message temp : attackList){
      temp.setAttackType("attack");
      if(gmap.findTerritory(temp.getDestTerritory()).getPlayerName().equals(gmap.findTerritory(temp.getSrcTerritory()).getTroopOwner())){
        temp.setType("Move");
        Executor action = this.execFactory.create(temp,gmap);
        action.action();
      }
      else {
        Executor action = this.execFactory.create(temp,gmap);
        action.action();
      }
      }
  }
  
  private void executeSendTroop(){
    for(Message temp : attackList){
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
  }

  public GameMap getMap() {
    return this.gmap;
  }

  public void parseMessage(ArrayList<Message> ms){
    for (Message m : ms){
      parseMessage(m);
    }
  }
  
  public void parseMessage(Message message)  {
    /////// udpate gamemap here?
    message.setGmap(this.getMap());      
    
    if(message.getMessageType().equals("Execute")){
      // if executor
      
      Executor action1 = this.execFactory.create(message, gmap);
      action1.action();      
    }
    else if(message.getMessageType().equals("Assign")) {
      // if it is initializer
      Initializer action = this.initFactory.create(message, gmap);
      action.action();
    }
    //throw new Exception("Unknown message type");
    else {
      System.out.println("UNKNOWN MESSAGE TYPE");
    }
  }
  
  private String promptGeneratorHelper(String prompt, String name) {
    prompt = prompt + name + " player:\n-----------\n";
    List<Territory> territoryList = gmap.getPlayerTerritories(name);
    for (int i = 0; i < territoryList.size(); i++) {
      Integer temp = territoryList.get(i).getUnit().getNumUnits();
      prompt = prompt + temp.toString() + " units in " + territoryList.get(i).getTerritoryName() + " (next to:";
      ArrayList<String> neighborArray = gmap.getNeighborStrings(territoryList.get(i));
      int j = 0;
      for (j = 0; j < neighborArray.size() - 1; j++) {
        prompt = prompt + " " + neighborArray.get(j) + ",";
      }
      prompt = prompt + " " + neighborArray.get(j) + ")\n";
    }
    return prompt;
  }

  // Used for generating the prompt before user selecting
  // an option from move attack and done
  public String promptGenerator(Message message){
    String currPlayerName = message.getPlayerName();
    String prompt = "";
    
    for (String playerName: this.gmap.getPlayerNames()){
      prompt = promptGeneratorHelper(prompt, playerName);
    }
    
    prompt = prompt + "\nYou are the " + currPlayerName +
      " player, what would you like to do?\n(M)ove\n(A)ttack\n(D)one\n";
    
    return prompt;
  }
  
  private String initialPromptHelper(String prompt, Integer groupID) {
    prompt = prompt +  "Group " + Integer.toString(groupID) + "\n-----------\n";
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
  public Message initialPrompt(){
    Message message = new Message(null, null, null, null, null, null, gmap);
    String prompt = "Available Remaining Groups to Choose:\n";
    //Iterate through all groups, later should change to all unchosen groups
    //System.out.println("IDS");
    //System.out.println(gmap.getUnclaimedGroupIds());
    for (Integer temp: gmap.getUnclaimedGroupIds()){
      prompt = initialPromptHelper(prompt, temp);
    }
    prompt = prompt + " Welcome player, which group do you want to choose?\n";
    message.setContent(prompt);
    message.setValidGroupIds(gmap.getUnclaimedGroupIds());
    return message;
  }
}
