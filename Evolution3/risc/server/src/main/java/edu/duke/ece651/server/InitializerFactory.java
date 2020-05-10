package edu.duke.ece651.server;
import edu.duke.ece651.shared.*;
  
import java.util.HashMap;

public class InitializerFactory {

  private interface InitializerMaker {
    public Initializer make(String playername,
                            String territoryname,
                            Integer unitnum,
                            Integer groupid,
                            GameMap gamemap);
  }
  
  private HashMap<String, InitializerMaker> creation;

  public InitializerFactory() {
    creation = new HashMap<String, InitializerMaker>();
    
    creation.put("AssignTerritoryGroup", (String playername,
                                     String territoryname,
                                     Integer unitnum,
                                     Integer groupid,
                                     GameMap gamemap) -> {
                   return new AssignTerritory(playername,
                                              groupid,
                                              gamemap);
                 });
    
    creation.put("AssignUnits", (String playername,
                                 String territoryname,
                                 Integer unitnum,
                                 Integer groupid,
                                 GameMap gamemap) -> {
                   return new AssignUnits(playername,
                                          territoryname,
                                          unitnum,
                                          gamemap);
                 });

  }
  
  public Initializer create(Message m, GameMap gmap) {
    String playerName = m.getPlayerName();
    String territoryName = m.getDestTerritory();
    int unitNum = m.getUnitNum();
    //  GameMap gmap = m.getGmap();
    String type = m.getType();
    Integer groupid = m.getGroupId();
    return creation.get(type).make(playerName,
                                   territoryName,
                                   unitNum,
                                   groupid,
                                   gmap);
  }
  
}
