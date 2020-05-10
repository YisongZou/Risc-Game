package edu.duke.ece651.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
  private PlayerResources ply_resource;
  private HashMap<String, Boolean> hasCloak;
  private HashMap<String, HashMap<String, String>> FogInfo;
  private HashMap<String,Spy> spyMap;
  //this records the food/tech recourse of each player
  // private Map<String, Integer> foodResource;
  //private Map<String, Integer> techResource;
  //private Map<String, Integer> techLevel;
  //public static final Integer MAX_TECH = 6;

   public HashMap<String,Spy> getSpyMap() {
     return this.spyMap;
  }

  public void setSpyMap(HashMap<String,Spy> temp) {
    this.spyMap = temp;
  }

  public void setFogInfo(HashMap<String, HashMap<String, String>> temp) {
    this.FogInfo = temp;
  }

  public HashMap<String, Boolean> getHasCloak() {
    return this.hasCloak;
  }
  
  public void setHasCloak(HashMap<String, Boolean> temp) {
    this.hasCloak = temp;
  }
  
  public void setPlayerResource(PlayerResources temp) {
    this.ply_resource = temp;
  }
  
  
  public HashMap<String, HashMap<String, String>> getFogInfo() {
    return this.FogInfo;
  }

  public String getVisibility(String playerName, String territoryName){
    return this.FogInfo.get(playerName).get(territoryName);
  }

  public Spy getSpy(String playername) {
    return spyMap.get(playername);
  }

  //upgrade one level1 unit to spy
  public boolean upgradeSpy(String playername, String Territory_name){
    Territory terr = getTerritory(Territory_name);
    if (terr.removeUnit(1, 1) == false) {
      return false;
    }
    Spy myspy = getSpy(playername);
    myspy.addSpy(Territory_name);
    return true;
  }

  //init the hashmap in spy hashmap
  public void init_spies(ArrayList<String> names){    
    for(String name : names){
      spyMap.put(name, new Spy());
    }
  }

  //get spy number in terrtory
  public Integer getSpyNum(String playername, String territoryname) {
    Spy my_spy = getSpy(playername);
    return my_spy.getSpies(territoryname);
    
  }
  
  public PlayerResources getPly_resource(){
    return ply_resource;
  }
  
  public GameMap() {
    this.hasCloak = new HashMap<String, Boolean>();
    mapGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
    playerNames = new ArrayList<String>();
    spyMap = new HashMap<String, Spy>();
  }

  public void add_player(String name) {
    playerNames.add(name);
  }

  public boolean hasCloak(String playerName) {
    return this.hasCloak.get(playerName);
  }

  public void researchCloak(String playerName) {
    this.hasCloak.replace(playerName, true);
  }

  public void updateMap(Territory oldTerr, Territory newTerr){
    oldTerr.setPlayerName(newTerr.getPlayerName());
    oldTerr.setUnit(newTerr.getUnit());
    oldTerr.setTroopOwner(newTerr.getTroopOwner());
    oldTerr.setCloakTurn(newTerr.getCloakTurn());
    oldTerr.setAllUnits(newTerr.getAllUnits());
  }
  
   public void init_ply_resource(){
    this.ply_resource = new PlayerResources(this.playerNames);
    for (String name : this.playerNames) {
      this.hasCloak.put(name, false);
    }
    //Init FogInfo to set every Territory to "Never" been to
    this.FogInfo = new HashMap<String, HashMap<String, String>>();
    for (String name : this.playerNames) {
      HashMap<String, String> Info = new HashMap<String, String>();
      ArrayList<String> Terrs =  this.getTerritoryNames();
      for(String Terr: Terrs){
        Info.put(Terr, "Never");
      }
      this.FogInfo.put(name, Info);
    }
    init_spies(this.playerNames);

    // for test
    System.out.println("*****INITIALIZING*********");
    for(String plyn : this.playerNames) {
      System.out.println(plyn);
    }
    System.out.println("**************");    
  }
  
  
  public void init_ply_resource(ArrayList<String> names){
    this.ply_resource = new PlayerResources(names);
    //Init the hasCloak by the way
    for (String name : names) {
      this.hasCloak.put(name, false);
    }
    
    //Init FogInfo to set every Territory to "Never" been to
    this.FogInfo = new HashMap<String, HashMap<String, String>>();
    for (String name : names) {
      HashMap<String, String> Info = new HashMap<String, String>();
      ArrayList<String> Terrs =  this.getTerritoryNames();
      for(String Terr: Terrs){
        Info.put(Terr, "Never");
      }
      this.FogInfo.put(name, Info);
      }
      if (!names.isEmpty()) {
     init_spies(names);
       }
  }

  public GameMap(int playerNumber){
    this.hasCloak = new HashMap<String, Boolean>();
    mapGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
    playerNames = new ArrayList<String>();
    spyMap = new HashMap<String, Spy>();
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

  public Integer getUnitnum(int level, String terr){
    return getTerritory(terr).getUnit(level).getNumUnits();
  }
  
  public boolean upgradeUnit(String playerName, String dst_terr, int level, int unitNum){
    //not enough unit number
    if (level + 1 > this.ply_resource.getTechLevel(playerName)) {
      return false;
    }
    if (getUnitnum(level, dst_terr) < unitNum) {
      return false;
    }
    int value1 = getTerritory(dst_terr).getUnit(level).getValue();
    
    Unit upgradedUnit = getTerritory(dst_terr).getUnit(level + 1);
    if (upgradedUnit == null) {
      return false;
    }
    int techcost = (upgradedUnit.getValue() - value1) * unitNum;
    //not enough resource
    if (getPly_resource().consumeTech(playerName, techcost) == false) {
      return false;
    }
    getTerritory(dst_terr).upgradeUnit(level, unitNum);
    return true;
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
    AllDirectedPaths<Territory, DefaultEdge> pathFindingAlg =
      new AllDirectedPaths<>(this.mapGraph);

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
    given territory a, and territory b
    with same player name, return the shortest valid path
    returns null if there is no valid path  
    @param a src territory  
    @param  b dest territory
    @returns GraphPath of valid paths  
  */
  
  public GraphPath<Territory,DefaultEdge>  getPath(Territory a, Territory b){
    AllDirectedPaths<Territory, DefaultEdge> pathFindingAlg =
      new AllDirectedPaths<>(this.mapGraph);

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
        return g;
      }
        
    }
    
    return null;
  }
  
  /*
    Get the cost between two territories
    returns 0 if these are uninitialized
    @param a territory source  
    @param b territory dest  
    @return int representing cost  
   */  
  public int getCostBetweenTerritories(Territory a, Territory b){
    int cost = a.getSize();
    GraphPath<Territory, DefaultEdge> g = getPath(a, b);

    
    if (g != null){
      List<DefaultEdge> gEdges = g.getEdgeList();      
      for (DefaultEdge edge : gEdges) {
        Territory target = this.mapGraph.getEdgeTarget(edge);
        cost = cost + target.getSize();
      }
    }
    
    return cost;
  }  
    
  
  /*
    Get unclaimd groupids
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
  public SimpleDirectedGraph <Territory, DefaultEdge> getMapGraph() {
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
    System.out.println(this.getPlayerNames());
    List<Territory> neighbors = this.getNeighbors(srcTerr);
    ArrayList<Territory> enemyList = new ArrayList<>();
    for(Territory t: neighbors){
      System.out.println(String.format("currname %s ,  tname %s", currPlayerName, t.getPlayerName()));
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
  
  /*
    Method for add food for all players each round.
    @call the addFood function inside PlayerRsources 
  */
  public boolean addFoodForPlayer(){
    for(String names : playerNames){
      ArrayList<Territory> myterrs = getPlayerTerritories(names);
      Integer newfood = 0;
      for(Territory terr : myterrs){
        newfood += terr.getFoodProduction();
      }
      ply_resource.addFoodRsc(names, newfood);
    }
    return true;
  }
  /*
    Method for add tech for all players each round.
    @call the addTech function inside PlayerRsources 
  */
  public boolean addTechForPlayer(){
    for(String names : playerNames){
      ArrayList<Territory> myterrs = getPlayerTerritories(names);
      Integer newtech = 0;
      for(Territory terr : myterrs){
        newtech += terr.getTechProduction();
      }
      ply_resource.addTechRsc(names, newtech);
    }
    return true;
  }
  /* 
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

  /*
    Method that returns a arraylist of strings territory names
   */
  public ArrayList<String> getPlayerTerritoryStrings(String playerName){
    List<Territory> ts = this.getPlayerTerritories(playerName);
    ArrayList<String> result = new ArrayList<>();
    for (Territory t : ts){
      result.add(t.getTerritoryName());
    }
    return result;
  }

  public void setMapGraph(SimpleDirectedGraph<Territory, DefaultEdge> mapGraph) {
    this.mapGraph = mapGraph;
  }
}
