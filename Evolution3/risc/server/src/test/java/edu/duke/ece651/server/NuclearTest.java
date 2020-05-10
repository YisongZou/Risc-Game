package edu.duke.ece651.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Territory;

public class NuclearTest {
  @Test
  public void test_() {
    GameMap gmap = new GameMap();
    Territory t1 = new Territory("America");
    Territory t2 = new Territory("Mexico");
    Territory t3 = new Territory("Canada");

    gmap.addTerritory(t1);
    gmap.addTerritory(t2);
    gmap.addTerritory(t3);
    
    gmap.getTerritory("America").setPlayerName("p1");
    gmap.getTerritory("Mexico").setPlayerName("p2");
    gmap.getTerritory("Canada").setPlayerName("p3");

    NuclearBomb bomb = new NuclearBomb("p1", gmap);
    bomb.action();
    assertEquals(gmap.getPlayerTerritories("p1").size(), 3);
    assertEquals(gmap.getPlayerTerritories("p2").size(), 0);
    assertEquals(gmap.getPlayerTerritories("p3").size(), 0);
  }

}
