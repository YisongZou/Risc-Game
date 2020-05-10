package edu.duke.ece651.server;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Territory;

public class VirusTest {
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
    ArrayList<String> names = new ArrayList<>();
    names.add("p1");
    names.add("p2");
    names.add("p3");
    gmap.init_ply_resource(names);
    gmap.getPly_resource().setFoodRsc("p1", 100);
    gmap.getPly_resource().setFoodRsc("p2", 100);
    gmap.getPly_resource().setFoodRsc("p3", 100);
    gmap.getPly_resource().setTechRsc("p1", 2200);;
    
    Virus vir = new Virus("p1", gmap);
    assertEquals(33, vir.twentyPercent(167));
    assertEquals(0, vir.twentyPercent(0));
    assertEquals(0, vir.twentyPercent(1));

    vir.action();
    assertEquals(gmap.getPly_resource().getFoodRsc("p2"), 0);
    assertEquals(gmap.getPly_resource().getFoodRsc("p3"), 0);
    assertEquals(gmap.getPly_resource().getFoodRsc("p1"), 100);
    assertEquals(2166, gmap.getPly_resource().getTechRsc("p1"));
  }

}
