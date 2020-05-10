package edu.duke.ece651.shared;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class SpyTest {
  @Test
  public void test_y() {
    Spy myspy = new Spy();
    assertEquals(0, myspy.getNumUnits());
    myspy.setNumUnits(2);
    myspy.addNumUnits(1);
    assertEquals(3, myspy.getNumUnits());
    assertEquals(true, myspy.getspylocation().isEmpty());
    myspy.addSpy("Oz");
    myspy.addSpy("Oz");
    assertEquals(2, myspy.getspylocation().get("Oz"));
    myspy.moveSpy("Oz", "Hogwartz");
    assertEquals(1, myspy.getspylocation().get("Oz"));
    assertEquals(1, myspy.getSpies("Hogwartz"));
    ArrayList<String> my_terr = new ArrayList<String>();
    my_terr.add("Hogwartz");
    my_terr.add("Oz");
    assertEquals(my_terr, myspy.getValidSpyLocation());
    my_terr.remove(1);
    myspy.moveSpy("Oz", "Hogwartz");
    assertEquals(my_terr, myspy.getValidSpyLocation());
    assertEquals(false, myspy.moveSpy("wonderland", "Oz"));
    assertEquals(0, myspy.getSpies("testland"));
  }

}
