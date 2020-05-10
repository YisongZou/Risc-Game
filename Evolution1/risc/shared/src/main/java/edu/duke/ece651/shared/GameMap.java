package edu.duke.ece651.shared;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public class GameMap implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
  private SimpleDirectedGraph<Territory, DefaultEdge> mapGraph;
  private ArrayList<String> playerNames;
  private int numGroups;

  public GameMap() {
    mapGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
    playerNames = new ArrayList<String>();

  }


  public GameMap(int playerNumber){
    mapGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
    playerNames = new ArrayList<String>();    
    Context context = new Context();
    // Used for debugging,there are three groups and
    // they have all been assigned to its given player    
    if (playerNumber == 1) {
      context.setMap(new MapAssigned(), this);
    }
    else if(playerNumber == 2){
      context.setMap(new Map2p(), this);
    }
    else if(playerNumber == 3){
      context.setMap(new Map3p(), this);
    }
    else if(playerNumber == 4){
      context.setMap(new Map4p(), this);
    }
    else if (playerNumber == 5) {
      context.setMap(new Map5p(), this);
    }
  }

  
  public void basicInitializer() {
    this.addTerritory(new Territory("A", 0));
    this.addTerritory(new Territory("B", 1));
  }
  
  public int getNumGroups(){
    return this.numGroups;
  }

  public void setNumGroups(int number) {
    this.numGroups = number;
  }

  /* 
     Given territory A, is it connected to Territory B
     with the same player name
     @param A Territory, starting territory
     @param B Territory, starting territory
  */
  public boolean isConnected(Territory a, Territory b){
    AllDirectedPaths<Territory, DefaultEdge> pathFindingAlg = new AllDirectedPaths<>(this.mapGraph);

    List<GraphPath<Territory, DefaultEdge>> allPaths =
        pathFindingAlg.getAllPaths(a, b, true, null);


    int count;    
    for(GraphPath <Territory, DefaultEdge> g : allPaths){
      count = 0;
      for (DefaultEdge edge : g.getEdgeList()) {

        Territory source = this.mapGraph.getEdgeSource(edge);
        Territory target = this.mapGraph.getEdgeTarget(edge);

        if ( source.getPlayerName().equals(target.getPlayerName()) ){
          count++;          
        }
      }
      
      if (count == g.getEdgeList().size()){
        return true;
      }
        
    }
    
    return false;
  }
  
  
  /*
    Get unclaimed groupids
    @returns ArrayList<Integer> of unclaimed territories
  */

  public ArrayList<Integer> getUnclaimedGroupIds() {
    ArrayList<Integer> unclaimedGroupIds = new ArrayList<>();
    for (int i = 0; i < this.numGroups; i++) {
      for (Territory t : this.getTerritoryGroup(i)) {
        if (t.getPlayerName() == null && unclaimedGroupIds.contains(i) == false) {
          unclaimedGroupIds.add(i);
        }
      }
    }
    return unclaimedGroupIds;
  }

  public boolean setTerritoryGroupPlayer(int groupId, String playerName) {
    ArrayList<Territory> territories = getTerritoryGroup(groupId);
    for (Territory t : territories) {
      t.setPlayerName(playerName);
    }
    return true;
  }

  /*
    Get territories in this group
    @param integer representing which territory group to get
    @returns ArrayList<Territory> of matching territories
  */
  public ArrayList<Territory> getTerritoryGroup(int groupId) {
    ArrayList<Territory> result = new ArrayList<>();
    for (Territory t : this.mapGraph.vertexSet()) {
      if (t.getGroupId() == groupId) {
        result.add(t);
      }
    }
    return result;

  }

  /*
    Get all active players in current gamemap
    @returns playerNames this is a ArrayList<String> of player names
  */
  public ArrayList<String> getPlayerNames() {
    ArrayList<String> result = new ArrayList<>();
    for (Territory t : this.mapGraph.vertexSet()) {
      if (result.contains(t.getPlayerName()) == false) {
        result.add(t.getPlayerName());
      }
    }
    return result;
  }

  /*
    adds a territory to the game map
    @param t This is territory vertex that is added to graph
  */
  public boolean addTerritory(Territory t) {
    this.mapGraph.addVertex(t);
    return true;
  }

  /*
    creates and connects two territories using a edge
    precondition: Territory t2 must exist
    @param t1 This is territory vertex that is added to graph
    @param t2 This is what vertex t will be connected to 
    @returns boolean representing edge success
  */
  public boolean addTerritory(Territory t1, Territory t2) {
    this.mapGraph.addVertex(t1);
    this.mapGraph.addEdge(t1, t2);
    return true;
  }

  /*
    connects two territories using a edge
    precondition: both edges must exist
    @param t1 String this is where edge will be from
    @param t2 String this is where edge will connect to
    @returns boolean representing edge success
  */
  public boolean connectTerritory(String t1, String t2) {
    this.mapGraph.addEdge(this.findTerritory(t1), this.findTerritory(t2));
    this.mapGraph.addEdge(this.findTerritory(t2),this.findTerritory(t1));    
    return true;
  }

  /*
    connects two territories using a edge
    precondition: both edges must exist
    @param t1 This is where edge will be from
    @param t2 This is where edge will connect to
    @returns boolean representing edge success
  */
  public boolean connectTerritory(Territory t1, Territory t2) {
    this.mapGraph.addEdge(t1, t2);
    return true;
  }

  /*
    Getter method for getting graph of the map
    @returns the graphmap object
  */
  public Graph<Territory, DefaultEdge> getMapGraph() {
    return this.mapGraph;
  }

  /*
    Gets territory given a string. returns null if doesnt 
    @param territoryName This is a string representing territory name
    @returns territory
  */
  public Territory findTerritory(String s) {
    // find the vertex corresponding to www.jgrapht.org
    for (Territory t : this.mapGraph.vertexSet()) {
      if (t.getTerritoryName().equals(s)) {
        return t;
      }
    }
    return null;
  }

  /*
    Gets all territories inside of map graph
    @param territoryName This is a string representing territory name
    @returns territory
  */
  public ArrayList<String> getTerritoryNames() {
    ArrayList<String> result = new ArrayList<String>();
    for (Territory t : this.mapGraph.vertexSet()) {
      if (result.contains(t.getTerritoryName()) == false) {
        result.add(t.getTerritoryName());
      }
    }
    return result;
  }

  /*
    Gets all enemy territories whose names != currPlayerName
   */
  public List<Territory> getEnemyNeighbors(Territory srcTerr, String currPlayerName) {
    List<Territory> neighbors = this.getNeighbors(srcTerr);
    ArrayList<Territory> enemyList = new ArrayList<>();
    for(Territory t: neighbors){
      if(t.getPlayerName().equals(currPlayerName) == false
         && enemyList.contains(t) == false){
        enemyList.add(t);
      }
    }
    return enemyList;
  }

  /*
    Given a territory t returns a list of 
    all neighboring territories
    @param t Territory of interest
    @returns ArrrayList<Territory> of neighboring territories
  */
  public List<Territory> getNeighbors(Territory t) {
    List<Territory> result = new ArrayList<Territory>();
    for (Territory terr :Graphs.neighborListOf(this.mapGraph, t)){
      if(result.contains(terr) == false){
        result.add(terr);
      }
    }
    return result;
  }

  /*
    Given a territory t returns a ArrayList<String> of 
    the names of neighboring territories
    @param t This is the main parameter
    @returns ArrrayList<String> of neighboring territories
  */
  public ArrayList<String> getNeighborStrings(Territory t) {
    ArrayList<String> result = new ArrayList<>();

    List<Territory> territories = Graphs.neighborListOf(this.mapGraph, t);

    for (Territory terr : territories) {
      if (result.contains(terr.getTerritoryName()) == false) {
        result.add(terr.getTerritoryName());
      }
    }
    return result;
  }

  /*
    Given a player name returns all territories
    belonging to the player
    @param playerName String of the player
    @returns ArrrayList<Territory> of all players belonging to that territory
  */
  public ArrayList<Territory> getPlayerTerritories(String playerName) {
    ArrayList<Territory> result = new ArrayList<>();
    for (Territory t : this.mapGraph.vertexSet()){
      if(t.getPlayerName() != null && t.getPlayerName().equals(playerName)){
        result.add(t);
      }
    }
    return result;
  }

  // returns null if territory not found
  public Territory getTerritory(String territoryName){
    for (Territory t : this.mapGraph.vertexSet()){
      if(t.getTerritoryName().equals(territoryName)){
        return t;
      }
    }
    return null;
  }

  /*©©
    Method for determining if the player has won(conquered all territories)
    @param playerName string of player name
    @returns boolean, true if game over, false if not
  */
  public boolean hasWon(String playerName){
    for (Territory t : this.mapGraph.vertexSet()){
      if(!(t.getPlayerName().equals(playerName))){
        return false;
      }
    }
    return true;
  }

  /*
    Method for determining if the game is over for all players
    @returns boolean, true if game over, false if not
  */
  public boolean gameOver() {
    ArrayList<String> playerNames = this.getPlayerNames();
    int i = 0;
    for (String playerName : playerNames) {
      // gameOver if the number of territories a player has
      // is equal to number of total territories
      //      if (this.gameOver(playerName) == false) {
        i++;
        //}
    }
    if (i == 1) {
      return true;
    }
    return false;
  }

  /*
    Method for determining if the game is over playerName
    @param playerName string of player name
    @returns boolean, true if game over, false if not
  */
  public boolean gameOver(String playerName) {
    if (this.getPlayerTerritories(playerName).size() == 0) {
      return true;
    }
    return false;
  }

}
