package edu.duke.ece651.server;
import java.util.ArrayList;
import java.util.HashMap;

import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;

public class ExecutorFactory {

  private interface ExecutorMaker {
    public Executor make(String playername,
                         String srcterritoryname,
                         String dstterritoryname,
                         Integer unitnum,
                         GameMap gamemap,
                         String attacktype,
                         Integer level,
                         ArrayList<Integer> attackLevels
                         );
  }
  
  private HashMap<String, ExecutorMaker> creation;
  
  public ExecutorFactory() {
    creation = new HashMap<String, ExecutorMaker>();


    creation.put("Research_Cloak", (String playername,
                            String srcterritoryname,
                            String dstterritoryname,
                            Integer unitnum,
                            GameMap gamemap,
                            String attacktype,
                            Integer level,
                            ArrayList<Integer> attackLevels
                            ) -> {
                   return new Research_Cloak(playername,
                                        gamemap
                                     );
                 });

     creation.put("Use_Cloak", (String playername,
                            String srcterritoryname,
                            String dstterritoryname,
                            Integer unitnum,
                            GameMap gamemap,
                            String attacktype,
                            Integer level,
                            ArrayList<Integer> attackLevels
                            ) -> {
                   return new Use_Cloak(playername,
                                     dstterritoryname,
                                     gamemap
                                     );
                 });

    creation.put("Virus", (String playername,
                            String srcterritoryname,
                           String dstterritoryname,
                           Integer unitnum,
                           GameMap gamemap,
                           String attacktype,
                           Integer level,
                           ArrayList<Integer> attackLevels
                           ) -> {
                   return new Virus(playername,
                                    gamemap
                                    );
                 });

    creation.put("Nuclear", (String playername,
                            String srcterritoryname,
                           String dstterritoryname,
                           Integer unitnum,
                           GameMap gamemap,
                           String attacktype,
                           Integer level,
                           ArrayList<Integer> attackLevels
                           ) -> {
                   return new NuclearBomb(playername,
                                    gamemap
                                    );
                 });
    
    
    creation.put("Attack", (String playername,
                            String srcterritoryname,
                            String dstterritoryname,
                            Integer unitnum,
                            GameMap gamemap,
                            String attacktype,
                            Integer level,
                            ArrayList<Integer> attackLevels
                            ) -> {
                   return new Attack(playername,
                                     srcterritoryname,
                                     dstterritoryname,
                                     unitnum,
                                     gamemap,
                                     attacktype,
                                     attackLevels
                                     );
                 });
    
    
    creation.put("Move", (String playername,
                          String srcterritoryname,
                          String dstterritoryname,
                          Integer unitnum,
                          GameMap gamemap,
                          String attacktype,
                          Integer level,
                          ArrayList<Integer> attackLevels
                          ) -> {
                   return new Move(playername,
                                   srcterritoryname,
                                   dstterritoryname,
                                   unitnum,
                                   gamemap,
                                   level);
                 });
    
    creation.put("Move_Spy", (String playername,
                              String srcterritoryname,
                              String dstterritoryname,
                              Integer unitnum,
                              GameMap gamemap,
                              String attacktype,
                              Integer level,
                              ArrayList<Integer> attackLevels
                              ) -> {
                   return new Move_Spy(playername,
                                       srcterritoryname,
                                       dstterritoryname,
                                       gamemap);
                 });
    
    creation.put("Upgrade_Spy", (String playername,
                              String srcterritoryname,
                              String dstterritoryname,
                              Integer unitnum,
                              GameMap gamemap,
                              String attacktype,
                              Integer level,
                              ArrayList<Integer> attackLevels
                              ) -> {
                   return new Upgrade_Spy(playername,
                                          dstterritoryname,
                                          gamemap);
                 });
    
    creation.put("Upgrade_Unit", (String playername,
                                  String srcterritoryname,
                                  String dstterritoryname,
                                  Integer unitnum,
                                  GameMap gamemap,
                                  String attacktype,
                                  Integer level,
                                  ArrayList<Integer> attackLevels
                                  ) -> {
                   return new Upgrade_Unit(playername,
                                      dstterritoryname,
                                      unitnum,
                                      gamemap,
                                      level);
                 });
    
    creation.put("Upgrade_Tech", (String playername,
                                  String srcterritoryname,
                                  String dstterritoryname,
                                  Integer unitnum,
                                  GameMap gamemap,
                                  String attacktype,
                                  Integer level,
                                  ArrayList<Integer> attackLevels
                                  ) -> {
                   return new Upgrade_Tech(playername,
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
    Integer level = m.getLevel();
    ArrayList<Integer> attackLevels = m.getAttackLevels();
    return creation.get(type).make(playername,
                                   srcterritoryname,
                                   dstterritoryname,
                                   unitnum,
                                   gamemap,
                                   attacktype,
                                   level,
                                   attackLevels);
  }
}
