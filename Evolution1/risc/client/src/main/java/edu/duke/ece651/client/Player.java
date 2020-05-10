package edu.duke.ece651.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

public class Player {
  private String name;
  //  private Message msg;
  private GameMap gmap;
  private Socket skt;
  private BufferedReader inB;
  private Scanner scanner;
  private int groupID;
  
  public Player(String name){
    this.name = name;
    gmap = new GameMap(1);
  }

  public Player(String name, InputStream stream, int debug) {
    this.name = name;
    gmap = new GameMap(debug);
    this.scanner = new Scanner(stream);
  }

  public Player(String name,InputStream stream){
    this.name = name;
    this.scanner = new Scanner(stream);
  }
  
  public Player(String name,GameMap gmap){
    this.name = name;
    this.gmap = gmap;
    //  msg = new Message("");   // initialize msg to be empty
  }
  public Player(String name,GameMap gmap,InputStream stream){
    this.name = name;
    this.gmap = gmap;
    this.scanner = new Scanner(stream);    
  }
  public GameMap getGmap() {
    return gmap;
  }

  public void setGmap(GameMap gmap) {
    this.gmap = gmap;
  }

  /*
    prompts user with List<Territory> ts options, returns int selection
    indexed to List
   */  
  public Integer promptSelectTerritory(List<Territory> ts,String prompt){
    HashMap<Integer, Territory> srcTerritories = new HashMap<>();    
    int i = 0;
    for (Territory t : ts){
      prompt += "("+String.valueOf(i)+") " + t.getTerritoryName()+"\n";
      srcTerritories.put(i,t);
      i++;
    }
    System.out.print(prompt);    
    String input = this.scanner.nextLine();
    return Integer.valueOf(input);
  }

  /*
  
   */  
  public String promptGenerator(){
    String currPlayerName = this.name;
    String prompt = "";
    
    for (String playerName: this.gmap.getPlayerNames()){
      prompt = promptGeneratorHelper(prompt, playerName);
    }
  
    prompt = prompt + "\nYou are the " + currPlayerName +
      " player, what would you like to do?\n(M)ove\n(A)ttack\n(D)one\n";
    
    return prompt;
  }  

  
  /*
    method for prompting user which territory group they would like to select
   */  
  public ArrayList<Message> promptInitializerAction(){
    ArrayList<Message> ms = new ArrayList<>();
    // now prompt user for input
    while (true) {
    
      String prompt = "Please select one of the following territory groups:\n";
    
      ArrayList<Integer> unclaimedGroups = this.gmap.getUnclaimedGroupIds();

      for(Integer groupId : unclaimedGroups){
        ArrayList<Territory> ts = this.gmap.getTerritoryGroup(groupId);
        prompt = prompt + String.format("(%s) ",groupId);
        int i = 0;
        for(Territory t: ts){
          if(i>0){
            prompt = prompt + ", ";
          }
          prompt = prompt+t.getTerritoryName();
          i++;
        }
        prompt = prompt + "\n";
      }
      System.out.print(prompt);
    
      try {
        String input = this.scanner.nextLine();
        if (input.length() < 1){
          throw new Exception("Please enter a number");
        }
        int groupId = Integer.parseInt(input);

        if(unclaimedGroups.contains(groupId) == false){
          throw new Exception("Invalid group ID selected.");
        }
        
        Message message = new Message("AssignTerritoryGroup", this.name,
                                      "Assign", groupId, this.getGmap());
        
        System.out.print(String.format("( Player %s assigned territory group %s )",
                                       this.name, groupId));        
        ms.add(message);
        
        // now get unit assignments ,and add to messagees arraylist
        ArrayList<Territory> terrGroup = this.gmap.getTerritoryGroup(groupId);
        
        for (Territory terrTemp : terrGroup) {
          terrTemp.setPlayerName(this.name);
        }
        ms.addAll(promptAssignUnits(terrGroup));

        break;
        
      } catch (Exception e) {
        System.out.println("Exception occured: " + e.getMessage());
      }
    }
    // now return group id sassignments
    return ms;
  }

  
  public ArrayList<Message> promptAssignUnits(ArrayList<Territory> ts){
    ArrayList<Territory> tsTemp = ts;    
    ArrayList<Message> ms = new ArrayList<>();
    int totalNumUnits = ts.size()*5;

    String input;
    // keep iterating while there is a valid number of units    
    while (totalNumUnits > 0){
      // iterate through each territory
      for (Territory t : ts){
        String prompt = promptGeneratorHelper("", this.name);
    
        prompt = prompt + String.format("You have %s territories. You have %s units you can assign.\n",ts.size(),totalNumUnits);
        prompt = prompt + "-----------\n";
        System.out.print(prompt);
        
        boolean validInput = false;
        
        while (validInput == false) {
          // if the user does not have anymore units, break
          if (totalNumUnits < 1) {
            break;
          }
          
          // inform user which territory and num units left
          String s = String.format("How many units would you like to assign for Territory %s\n",
                                   t.getTerritoryName());
          // s = s + String.format("You have %s units you can assign:\n", totalNumUnits);
          System.out.print(s);
          
          // get input from user for num units
          input = this.scanner.nextLine();
          int numUnits = Integer.valueOf(input);
          // make sure valid input
          int temp = totalNumUnits - numUnits;
          if (numUnits >= 0 && numUnits <= totalNumUnits) {
            totalNumUnits = totalNumUnits - numUnits;
            Message m = new Message("AssignUnits", this.name,
                                    "Assign", t.getTerritoryName(), numUnits, null);
            m.setUnitNum(numUnits);
            ms.add(m);
            
            validInput = true;
          }
          else{
            System.out.print("Sorry, that is not a valid selection\n");
          }
        }
      }
    }
    return ms;
    
  }

  public String promptGeneratorHelper(String prompt, String name) {
    prompt = prompt + name + " player:\n-----------\n";
    List<Territory> territoryList = gmap.getPlayerTerritories(name);
    for (int i = 0; i < territoryList.size(); i++) {
      Integer temp = territoryList.get(i).getUnit().getNumUnits();
      prompt = prompt + temp.toString() + " units in " +
        territoryList.get(i).getTerritoryName() + " (next to:";
      ArrayList<String> neighborArray = gmap.getNeighborStrings(territoryList.get(i));
      int j = 0;
      for (j = 0; j < neighborArray.size() - 1; j++) {
        prompt = prompt + " " + neighborArray.get(j) + ",";
      }
      prompt = prompt + " " + neighborArray.get(j) + ")\n";
    }
    return prompt;
  }

  /*
    This method is for managing player action, whether they choose
    (m)ove or (a)ttack
    @returns a message of their action of choice
   */
  public ArrayList<Message> getOrder(){
    ArrayList<Message> ms = new ArrayList<>();
    boolean validInput = false;
    String input;
    while (validInput == false) {
      // give player options      
      String prompt = promptGenerator();    
      System.out.print("\n\n\n"+prompt);
      
      input = this.scanner.nextLine().toLowerCase();
      try{
        if (input.equals("m")) {
          ms.add(this.promptMoveOrder());
        }
        
        else if (input.equals("a")) {
          ms.add( this.promptAttackOrder());
        }
        
        else if (input.equals("d")){
          System.out.print("Turn finished.\n");
          validInput = true;
        }
        
        else{
          throw new Exception("Invalid selection. Please choose 'm' or 'a' for a order" +
                              " or 'd' when done inputting orders.\n");
        }
      }
      catch (Exception e){
        // e.printStackTrace();
        System.out.println("\n\n\n==== Error occured: ===== \n"+e.getMessage());
        System.out.println("==== Please try again. (Enter any key to continue) ==== ");
        this.scanner.nextLine();        // wait for player to confirm
      }
    }
    return ms;
  }

  /*
    Prompt user for source territory  
   */  
  public Territory promptGetTerritory(List<Territory> ts,String prompt){
    int selection = this.promptSelectTerritory(ts,prompt);
    Territory srcTerr = ts.get(selection);
    return srcTerr;
  }

  /*
    Prompt user to get number of units they want
   */  
  public int promptGetNumUnits(Territory t) throws Exception{
    // ask how many units
    System.out.print(String.format("How many units? (You can move %s units)\n",t.getUnit().getNumUnits() ));
  
    // get inpuct from user...
    String input = this.scanner.nextLine();
    int val = Integer.valueOf(input);
    // if input is not valid, throw exception
    if ((val >= 0 && val <= t.getUnit().getNumUnits()) == false){
      throw new Exception("Invalid number of units");
    }    
    System.out.print(String.format("( Moving %s units )\n",input));
    
    return (Integer.valueOf(input));        
  }
  
  public Message promptMoveOrder() throws Exception{
    // init msg to be returned
    Message m = new Message();
    
    // Move units from one territory to another
    m.setMessageType("Execute");
    m.setType("Move");    
  
    System.out.print("( Selected (m)ove order )\n");
    
    List<Territory> ts = this.gmap.getPlayerTerritories(this.name);    
    System.out.print(String.format("=== territory size in prompt move order %s\n",ts.size()));
    Territory srcTerr = promptGetTerritory(ts,"Please select source territory\n");
    System.out.println(String.format("( Player %s moving units from territory %s )\n",this.name,
                                     srcTerr.getTerritoryName()));
    
    ts.remove(srcTerr); // remove the selected territory from options
    Territory destTerr = promptGetTerritory(ts,"Please select destination territory\n");
    System.out.print(String.format("( Player %s moving units to territory %s )\n",this.name,
                                     destTerr.getTerritoryName()));

    if(this.gmap.isConnected(srcTerr, destTerr)==false){
      throw new Exception("There is no connection between these territories");
    }

    // update message contents
    m.setSrcTerritory(srcTerr.getTerritoryName());
    m.setDestTerritory(destTerr.getTerritoryName());
    // precondition: num of units is valid
    int numUnits = promptGetNumUnits(srcTerr);
    m.setUnitNum(numUnits); // update message    
    srcTerr.removeUnit(numUnits);
    destTerr.addUnit(numUnits);    
    return m;
  }
  
  /*
    method for giving player choices for what action they want to take
   */
  public Message promptAttackOrder() throws Exception{
    // init msg to be returned
    Message m = new Message();
    
    // Move units from one territory to another
   
    
    List<Territory> ts = this.gmap.getPlayerTerritories(this.name);
    
    System.out.println("( Selected (a)ttack order )");

    Territory srcTerr = this.promptGetTerritory(ts, "Please select source territory to attack from\n");
    System.out.println(String.format("( Player %s attacking from territory %s )",this.name,
                                     srcTerr.getTerritoryName()));
    
    // get adjacent territories that do not belong to player
    List<Territory> neighbors = gmap.getEnemyNeighbors(srcTerr, this.name);
    // now ask user for destination territory
    Territory destTerr = this.promptGetTerritory(neighbors,
                                                 "Please select territory you want to attack \n");
    System.out.println(String.format("( Units from %s attacking %s )",
                                     srcTerr.getTerritoryName(),
                                     destTerr.getTerritoryName()));
    
    // update message contents
    m.setType("Attack");
    m.setMessageType("Execute");
    m.setPlayerName(this.name);
    m.setSrcTerritory(srcTerr.getTerritoryName());
    m.setDestTerritory(destTerr.getTerritoryName());

    // precondition: this numUnits is valid
    int numUnits = promptGetNumUnits(srcTerr);    
    m.setUnitNum(numUnits); // update message    
    srcTerr.removeUnit(numUnits);
    return m;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
}



