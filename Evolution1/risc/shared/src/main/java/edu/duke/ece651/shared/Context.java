package edu.duke.ece651.shared;

public class Context {
  private MapState mapstate;

  public void setMap(MapState s,GameMap m) {
    mapstate = s;
    mapstate.handle(m);
  }
}
