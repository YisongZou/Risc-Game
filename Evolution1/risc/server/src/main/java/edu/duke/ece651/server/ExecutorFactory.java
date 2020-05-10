package edu.duke.ece651.server;
import edu.duke.ece651.shared.*;

import java.util.HashMap;

public class ExecutorFactory {

  private interface ExecutorMaker {
    public Executor make(String playername,
                         String srcterritoryname,
                         String dstterritoryname,
                         Integer unitnum,
                         GameMap gamemap,
                         String attacktype);
  }
  
  private HashMap<String, ExecutorMaker> creation;
  
  public ExecutorFactory() {
    creation = new HashMap<String, ExecutorMaker>();

    creation.put("Attack", (String playername,
                            String srcterritoryname,
                            String dstterritoryname,
                            Integer unitnum,
                            GameMap gamemap,
                            String attacktype) -> {
                   return new Attack(playername,
                         srcterritoryname,
                         dstterritoryname,
                         unitnum,
                         gamemap,
                         attacktype);
                 });
    
    
    creation.put("Move", (String playername,
                            String srcterritoryname,
                            String dstterritoryname,
                            Integer unitnum,
                          GameMap gamemap,
                          String attacktype) -> {
                   return new Move(playername,
                                     srcterritoryname,
                                     dstterritoryname,
                                     unitnum,
                                     gamemap);
                 });

  }


  public Executor create(Message m, GameMap gamemap) {  
    String type = m.getType();
    String playername = m.getPlayerName();
    String srcterritoryname = m.getSrcTerritory();
    String dstterritoryname = m.getDestTerritory();
    Integer unitnum = m.getUnitNum();
    //    GameMap gamemap = m.getGmap();
    String attacktype = m.getAttackType();
    return creation.get(type).make(playername,
                                   srcterritoryname,
                                   dstterritoryname,
                                   unitnum,
                                   gamemap,
                                   attacktype);
  }
}
