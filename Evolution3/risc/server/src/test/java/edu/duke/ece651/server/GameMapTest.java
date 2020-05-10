package edu.duke.ece651.projectRisc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import edu.duke.ece651.shared.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class GameMapTest {
  @Test
  public void testGameMap() {
    GameMap gmap = new GameMap();
    Territory t1 = new Territory("America");
    Territory t2 = new Territory("Mexico");
    Territory t3 = new Territory("Canada");
    Territory t4 = new Territory("China", "Mao", new BasicUnit());    
    
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
    BasicUnit s =  (BasicUnit) t1.getUnit(0);
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
}
