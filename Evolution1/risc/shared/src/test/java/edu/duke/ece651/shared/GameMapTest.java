package edu.duke.ece651.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
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
    
    BasicUnit s =  (BasicUnit) t1.getUnit();
    assertEquals(0,s.getNumUnits());
    assertEquals(0,s.getNumUnits());
    // check if set squad properly
    t2.setUnit(s);
    assertEquals(t2.getUnit(), s);
    // check if add unit method works
    s.addUnit(11);
    assertEquals(11,t2.getUnit().getNumUnits());

    assertEquals(true,s.removeUnit(2));
    assertEquals(9,t2.getUnit().getNumUnits());
    t2.addUnit(3);
    assertEquals(12,s.getNumUnits());
    // number of units in squad cannot be less than 0
    assertEquals(false,s.removeUnit(13));
    assertEquals(false,t2.removeUnit(13));
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
    assertEquals(2, gmap.getEnemyNeighbors(gmap.findTerritory("Elantris"),"Red").size() );
    
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

    gmap.getTerritory("Midkemia").setPlayerName("b");
    gmap.getTerritory("Oz").setPlayerName("b");
    gmap.getTerritory("Gondor").setPlayerName("a");

    assertEquals(false,gmap.isConnected(gmap.getTerritory("Narnia"),
                                       gmap.getTerritory("Gondor")));

    gmap = new GameMap(3);
    gmap.setTerritoryGroupPlayer(0, "Green");
    gmap.setTerritoryGroupPlayer(1, "Blue");
    gmap.setTerritoryGroupPlayer(2, "Blue");

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
}
