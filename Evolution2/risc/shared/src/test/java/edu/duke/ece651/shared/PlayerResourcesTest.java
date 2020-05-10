package edu.duke.ece651.shared;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class PlayerResourcesTest {
  @Test
  public void test_() {
    PlayerResources ply1= new PlayerResources();
    ArrayList<String> allnames = new ArrayList<String>();
    allnames.add("Michael");
    allnames.add("Holly");
    PlayerResources ply2 = new PlayerResources(allnames);
    assert (ply2.getFoodRsc("ABC") == null);
    assert (ply2.getTechLevel("Holly") == 1);
    ply1.setTechlevel("George", 6);
    assert (ply1.upgradeTech("George") == false);
    ply1.setFoodRsc("Andy", 20);
    ply1.setTechRsc("Andy", 50);
    ply1.setTechlevel("Andy", 1);
    assert (ply1.consumeFood("Andy", 21) == false);
    assert (ply1.consumeTech("Andy", 51) == false);
    ply1.consumeFood("Andy", 10);
    ply1.consumeTech("Andy", 30);
    assert (ply1.getFoodRsc("Andy") == 10);
    assert (ply1.getTechRsc("Andy") == 20);
    ply1.addFoodRsc("Andy", 5);
    ply1.addTechRsc("Andy", 50);
    assert (ply1.getFoodRsc("Andy") == 15);
    assert (ply1.getTechRsc("Andy") == 70);
    assert (ply1.upgradeTech("Andy") == true);
    assert (ply1.getTechRsc("Andy") == 20);
    assert (ply1.getTechLevel("Andy") == 2);
    assert (ply1.upgradeTech("Andy") == false);
  }

}
