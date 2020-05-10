package edu.duke.ece651.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerResources implements java.io.Serializable {
  //this records the food/tech recourse of each player
  private static final long serialVersionUID = 1L;
  private HashMap<String, Integer> foodResource;
  private HashMap<String, Integer> techResource;
  private HashMap<String, Integer> techLevel;
  public static final Integer MAX_TECH = 6;

  public PlayerResources() {
    foodResource = new HashMap <String, Integer>();
    techResource = new HashMap <String, Integer>();
    techLevel = new HashMap <String, Integer>();
  }

  public PlayerResources(ArrayList<String> playernames){
    foodResource = new HashMap <String, Integer>();
    techResource = new HashMap<String, Integer>();
    techLevel = new HashMap<String, Integer>();
    for (String name : playernames) {
      foodResource.put(name, 50);
      techResource.put(name, 50);
      techLevel.put(name, 1);
    }
  }
  
  /*
    give a playername, return the techlevel
   */
  public Integer getTechLevel(String playerName) {
    return techLevel.get(playerName);
  }
  
  /*
    set the tech level
  */
  public void setTechlevel(String playerName, Integer level) {
    techLevel.put(playerName, level);
  }
  
  /*
    given a playername, return his/hers foodresource
  */
  public Integer getFoodRsc(String playerName){
    return foodResource.get(playerName);
  }
  
  /*
    set the food resource
   */
  public void setFoodRsc(String playerName, Integer foodNum) {
    foodResource.put(playerName, foodNum);
  }
  
  /*
    consume the food resource of a player
    @return false if the player doesn't have enough resource 
  */
  public boolean consumeFood(String playerName, Integer foodresource) {
    Integer leftFoodResource = foodResource.get(playerName);

    // for test
    /*
    System.out.println("**************");
    System.out.println("playernme: " + playerName);
    System.out.println("playerName is null: " + Boolean.toString(playerName == null));
    System.out.println("leftFoodResource is null: " + Boolean.toString(leftFoodResource == null));
    System.out.println("foodresource is null: " + Boolean.toString(foodresource == null));
    System.out.println("**************");
    */
    
    if (leftFoodResource < foodresource) {
      return false;
    }
    leftFoodResource -= foodresource;
    foodResource.put(playerName, leftFoodResource);
    return true;
  }
  
  /*
    given a playername, return his/hers techresource
  */
  public Integer getTechRsc(String playerName) {
    return techResource.get(playerName);
  }

  /*
    set the techresource
  */  
  public void setTechRsc(String playerName, Integer techNum) {
    techResource.put(playerName, techNum);
  }
  
  /*
    consume the food resource of a player
    @return false if the player doesn't have enough resource 
  */
  public boolean consumeTech(String playerName, Integer techresource) {
    Integer leftTechResource = techResource.get(playerName);
    System.out.println("leftTechResource: " + leftTechResource);
    System.out.println("techresource: " + techresource);
    if (leftTechResource < techresource) {
      return false;
    }
    leftTechResource -= techresource;
    techResource.put(playerName, leftTechResource);
    return true;
  }
  
  /*
    Add food resource for player
  */
  public void addFoodRsc(String names, Integer newfood) {
    Integer foodamt = foodResource.get(names);
    foodamt += newfood;
    foodResource.put(names, foodamt);
  }
  
  /*
    Add tech resource for player
  */
  public void addTechRsc(String names, Integer newtech) {
    Integer techamt = techResource.get(names);
    techamt += newtech;
    techResource.put(names, techamt);
  }

  /*
    upgrade the tech level of a player and consume the tech resource
    @only upgrade 1 level a time
    @return false if not enough resoure or already max_tech  
  */
  
  public boolean upgradeTech(String playerName) {
    int curr_tech = techLevel.get(playerName);
    curr_tech += 1;
    if (curr_tech > MAX_TECH) {
      return false;
    }
    int tech_cost = 25 * (curr_tech - 1) * (curr_tech - 2) / 2 + 50;
    if (consumeTech(playerName, tech_cost) == false) {
      return false;
    }
    techLevel.put(playerName, curr_tech);
    return true;
  }
  
}
