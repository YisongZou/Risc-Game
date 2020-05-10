package edu.duke.ece651.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

public class GameMapTest {

  @Test
  public void testInitilization(){

  }
  
  @Test
  public void testGameMap() {
    GameMap gmap = new GameMap();
    Territory t1 = new Territory("America");
    Territory t2 = new Territory("Mexico");
    Territory t3 = new Territory("Canada");
    Territory t4 = new Territory("China", "Mao", new BasicUnit());
    Territory t5 = new Territory("Europe", "Napoleon");
    
    // test territory was added correctly
    assertEquals(true,gmap.addTerritory(t1));
    // test whether getTerritoryNames
    ArrayList<String> tnames = gmap.getTerritoryNames();
    for (String name: tnames){
      assertEquals("America", name);
    }

    // test find territory
    assertEquals(t1,gmap.findTerritory("America"));
    // territory that doesnt exist
    assertEquals(null,gmap.findTerritory("Narnia"));

    // test adding a territory
    gmap.addTerritory(t2,t1);
    assertEquals(t2,gmap.findTerritory("Mexico"));

    // test creating a connection between two territories
    gmap.addTerritory(t3);
    assertEquals(t3,gmap.findTerritory("Canada"));
    
    // confirm that a newly created territory has player
    // name set to null
    assertEquals(null,gmap.findTerritory("Canada").getPlayerName());
    gmap.getMapGraph();
    
    // check that squad is being init properly
    BasicUnit s2 = new BasicUnit(2);
    assertEquals(2, s2.getNumUnits());

    BasicUnit s = (BasicUnit) t1.getUnit();
    assertEquals(0,s.getNumUnits());

    //BasicUnit s =  (BasicUnit) t1.getUnit(0);

    assertEquals(0,s.getNumUnits());
    s.addUnit(1);
    assertEquals(1,t1.getUnit(0).getNumUnits());

    //add level 3 unit in territory
    assert (t2.getAllUnits() != null);
    t2.addUnit(3, 10);
    Unit s3 = t2.getUnit(3);
    assertEquals(true,s3.removeUnit(2));
    assertEquals(8,t2.getUnit(3).getNumUnits());
    assertEquals(s3.getBonus(), 5);
    assertEquals(s3.getValue(), 30);
    assertEquals(8,s3.getNumUnits());
    // number of units in squad cannot be less than 0
    assertEquals(false,s.removeUnit(13));
    assertEquals(false,t2.removeUnit(3,13));
    assertEquals(t4.getPlayerName(), "Mao");
    
    // test set player name    
    t4.setPlayerName("Xi");
    assertEquals(t4.getPlayerName(), "Xi");
    

    // test get territory that returns null
    assertEquals(null, gmap.getTerritory("Mars"));
    
    // to do: add case to make sure territory name does NOT
    // already exist!
    gmap.connectTerritory(gmap.getTerritory("America"), gmap.getTerritory("Canada"));
    gmap.connectTerritory(gmap.getTerritory("America"), gmap.getTerritory("Mexico"));
    gmap.connectTerritory(t1, t3);
    
    // test get neighbor territories getNeighbors()
    List<Territory> neighbors = gmap.getNeighbors(t1);
    assertEquals(true, neighbors.contains(gmap.getTerritory("Canada")));
    assertEquals(true, neighbors.contains(gmap.getTerritory("Mexico")));
    
    // test get neighbor strings given a territory getNeighborStrings()
    ArrayList<String> stringNs = gmap.getNeighborStrings(t1);
    assertEquals(true, stringNs.contains("Canada"));
    assertEquals(true, stringNs.contains("Mexico"));
        
    // test getPlayerTerritories()
    gmap.addTerritory(t4);
    assertEquals(true,gmap.getPlayerTerritories("Xi").contains(gmap.getTerritory("China")));
    
    // test getTerritory
  }

  @Test
  public void testGraph(){
    GameMap gmap = new GameMap(3);

    assertEquals(3,gmap.getUnclaimedGroupIds().size());
    
    gmap.setTerritoryGroupPlayer(0, "Green");
    gmap.setTerritoryGroupPlayer(1, "Blue");
    gmap.setTerritoryGroupPlayer(1, "Green");
    gmap.setTerritoryGroupPlayer(2, "Red");
    assertEquals(false,gmap.gameOver());    
    gmap.findTerritory("Roshar").setPlayerName("Green");
    
    
    gmap.setTerritoryGroupPlayer(1, "Red");
    gmap.setTerritoryGroupPlayer(2, "Blue");
    
    assertEquals(false,gmap.hasWon("Red"));
    assertEquals(false,gmap.gameOver());
    gmap.findTerritory("Elantris").setPlayerName("Red");
    
    assertEquals(3,gmap.getNumGroups());
    assertEquals(0, gmap.getEnemyNeighbors(gmap.findTerritory("Elantris"),"Red").size() );
    
    gmap.setTerritoryGroupPlayer(0, "Green");
    gmap.setTerritoryGroupPlayer(1, "Green");
    gmap.setTerritoryGroupPlayer(2, "Green");
    

    assertEquals(true,gmap.hasWon("Green"));
    assertEquals(false,gmap.hasWon("Red"));    
    assertEquals(true,gmap.gameOver());
    assertEquals(true,gmap.gameOver("Red"));
    assertEquals(false,gmap.gameOver("Green"));
    
    gmap.basicInitializer();
    assertEquals(0,gmap.findTerritory("A").getGroupId());
    
    gmap = new GameMap(1);
    gmap = new GameMap(2);
    gmap = new GameMap(4);
    gmap = new GameMap(5);
    gmap = new GameMap(6);
  }

  @Test
  public void isConnected(){
    GameMap gmap = new GameMap(2);
    gmap.setTerritoryGroupPlayer(0, "a");
    gmap.setTerritoryGroupPlayer(1, "b");
    assertEquals(true,gmap.isConnected(gmap.getTerritory("Narnia"),
                                       gmap.getTerritory("Midkemia")));
    
    Territory a =  gmap.getTerritory("Narnia");
    Territory b =  gmap.getTerritory("Midkemia");
    
    int correctSize = a.getSize()+b.getSize();
    // test get cost of moving correctly between tehse territories
    assertEquals(correctSize, gmap.getCostBetweenTerritories(a, b));
    
    gmap.getTerritory("Midkemia").setPlayerName("b");
    gmap.getTerritory("Oz").setPlayerName("b");
    gmap.getTerritory("Gondor").setPlayerName("a");

    assertEquals(false,gmap.isConnected(gmap.getTerritory("Narnia"),
                                       gmap.getTerritory("Gondor")));


    // test get cost of moving between three territories
    gmap.getTerritory("Midkemia").setPlayerName("b");
    gmap.getTerritory("Gondor").setPlayerName("b");    
    gmap.getTerritory("Oz").setPlayerName("b");
    a = gmap.getTerritory("Midkemia");
    b = gmap.getTerritory("Gondor");
    Territory c = gmap.getTerritory("Oz");
    correctSize = a.getSize() + b.getSize() + c.getSize();
    assertEquals(correctSize, gmap.getCostBetweenTerritories(a, c));    
    
    gmap = new GameMap(3);
    gmap.setTerritoryGroupPlayer(0, "Green");
    gmap.setTerritoryGroupPlayer(1, "Blue");
    gmap.setTerritoryGroupPlayer(2, "Blue");
    gmap.addFoodForPlayer();
    gmap.addTechForPlayer();
    // territory does not belong to player in narnia
    assertEquals(false,gmap.isConnected(gmap.getTerritory("Narnia"),
                                        gmap.getTerritory("Hogwarts")));

    // territory is not connected
    gmap.getTerritory("Hogwarts").setPlayerName("Green");
    assertEquals(false,gmap.isConnected(gmap.getTerritory("Narnia"),
                                        gmap.getTerritory("Hogwarts")));

    gmap.setTerritoryGroupPlayer(1, "Green");
    assertEquals(true,gmap.isConnected(gmap.getTerritory("Narnia"),
                                        gmap.getTerritory("Hogwarts")));    
  }
  
  @Test
  public void upgrade_test(){
    Territory t1 = new Territory("Hogwarz");
    t1.setSize(10);
    assertEquals(t1.getSize(), 10);
    t1.setTroopOwner("Ed");
    assertEquals(t1.getTroopOwner(), "Ed");
    assertEquals(t1.getUnit(10), null);
    t1.setUnit(4, 10);
    t1.setUnit(6, 10);
    assertEquals(t1.getUnit(4).getNumUnits(), 10);
    assertEquals(t1.upgradeUnit(6, 1), false);
    assertEquals(t1.upgradeUnit(0, 1), false);
    assertEquals(t1.upgradeUnit(4, 3), true);
    assertEquals(t1.getUnit(4).getNumUnits(), 7);
    assertEquals(t1.getUnit(5).getNumUnits(), 3);
    assertEquals(t1.getFoodProduction(), 50);
    assertEquals(t1.getTechProduction(), 50);
  }
  
  @Test
  public void upgradeunit() {
    GameMap gmap = new GameMap(2);
    ArrayList<String> names = new ArrayList<String>();
    names.add("Green");
    names.add("Blue");
    gmap.setTerritoryGroupPlayer(0, "Green");
    gmap.setTerritoryGroupPlayer(1, "Blue");
    gmap.init_ply_resource(names);
    gmap.add_player("Green");
    gmap.add_player("Blue");
    gmap.addFoodForPlayer();
    gmap.addTechForPlayer();
    assertEquals(gmap.getPly_resource().getFoodRsc("Green"), 150);
    assertEquals(gmap.getPly_resource().getTechRsc("Blue"), 150);
    gmap.getPly_resource().consumeTech("Green", 90);
    assertEquals(gmap.getPly_resource().getTechRsc("Green"), 60);
    gmap.getPly_resource().consumeFood("Blue", 75);
    assertEquals(gmap.getPly_resource().getFoodRsc("Blue"), 75);    
    gmap.getTerritory("Oz").addUnit(0, 10);
    gmap.upgradeUnit("Green", "Oz", 0, 3);
    assertEquals(gmap.getTerritory("Oz").getUnit(0).getNumUnits(), 7);
    assertEquals(gmap.getTerritory("Oz").getUnit(1).getNumUnits(), 3);
    assertEquals(gmap.upgradeUnit("Green", "Oz", 1, 2), false);
    assertEquals(gmap.upgradeUnit("Green", "Oz", 0, 1), true);
  }
  @Test
  public void spy_test() {
    GameMap gmap = new GameMap();
    Spy spy1 = new Spy();
    Spy spy2 = new Spy();
    HashMap<String, Spy> spymp = new HashMap<String, Spy>();
    gmap.setSpyMap(spymp);
    assertEquals(true, gmap.getSpyMap().isEmpty());
    gmap.getSpyMap().put("ply1", spy1);
    gmap.getSpyMap().put("ply2", spy2);
    assertEquals(spy1,gmap.getSpy("ply1"));
    Territory terr1 = new Territory("Oz");
    gmap.addTerritory(terr1);
    assertEquals(false, gmap.upgradeSpy("ply1", "Oz"));
    gmap.getTerritory("Oz").addUnit(1, 10);
    assertEquals(true, gmap.upgradeSpy("ply1", "Oz"));
    assertEquals(1, gmap.getSpyNum("ply1", "Oz"));
    assertEquals(0, gmap.getSpyNum("ply1", "wonderland"));
  }

 }
