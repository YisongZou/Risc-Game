package edu.duke.ece651.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException; 
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import edu.duke.ece651.client.Player;
import edu.duke.ece651.shared.GameMap;
import edu.duke.ece651.shared.Message;
import edu.duke.ece651.shared.Territory;

public class RiscTest {
  @Test
  public void testGame() {
    GameMap gmap = new GameMap(3);
    GameMap gmap_2p = new GameMap(2);
    GameMap gmap_4p = new GameMap(4);
    GameMap gmap_5p = new GameMap(5);
    InitializerFactory initFactory = new InitializerFactory();
    //initialize the map
    //gmap.addTerritory(new Territory("Durham", 0));
    //gmap.addTerritory(new Territory("Wake", 0));
    Player greenPlayer = new Player("Green",gmap); // no input stream
    /*
      String action
      String playername,
      String territoryname,
      Integer unitnum,
      Integer groupid,
      GameMap gamemap
     */
    //test for assignTerritory
    Message message = new Message("AssignTerritoryGroup", "Green",
                                  "Assign", 
                                  0, greenPlayer.getGmap());
    
    Initializer action = initFactory.create(message, gmap);   
    action.action();
    
    assertEquals("Green",gmap.getTerritory("Narnia").getPlayerName());
    assertEquals("Green",gmap.getTerritory("Oz").getPlayerName());
    assertEquals("Green",gmap.getTerritory("Midkemia").getPlayerName());
    assertEquals(null,gmap.getTerritory("Gondor").getPlayerName());
    
    //test for assignunits
    message = new Message("AssignUnits", "Green",
                             "Assign", "Oz", 7, greenPlayer.getGmap());
    action = initFactory.create(message, gmap);
    assertEquals(0,gmap.getTerritory("Oz").getUnit().getNumUnits());    
    action.action();
    assertEquals(7,gmap.getTerritory("Oz").getUnit().getNumUnits());    
  }

  @Test
  public void parserTest(){
    Parser parser = new Parser(3);
    Message promt = parser.initialPrompt();
    Message m = new Message("Move", "Green",
                            "Execute", "Midkemia", "Oz", 
                            2, parser.getMap());
    Message m3 = new Message("unknown", "unknown",
        "unknown", "unknown", 7, parser.getMap());
    // execute message
    parser.parseMessage(m);
    parser.parseMessage(m3);
    
    // assert that this is true
    assertEquals(0,parser.getMap().getTerritory("Midkemia").getUnit().getNumUnits());
    assertEquals(2,parser.getMap().getTerritory("Oz").getUnit().getNumUnits());

    Message m1 = new Message("AssignTerritoryGroup", "Green",
                                  "Assign", 
                                  0, parser.getMap());
    //m1.setGmap(null);
    Message m2 = new Message("AssignUnits", "Green",
                             "Assign", "Oz", 7, parser.getMap());
    assertEquals(null,parser.getMap().getTerritory("Oz").getPlayerName());
    parser.executeAssignTerritoryGroup(m1);
    parser.parseMessage(m2);
    assertEquals("Green",parser.getMap().getTerritory("Oz").getPlayerName());

  }
  

  @Test

  public void serverTest() throws IOException{
    Server server = new Server(new GameMap(1));
    // Server server = new Server(12345);
    ExecutorFactory execFactory = new ExecutorFactory();
    
    // narnia currently has 10, and midkemia has 5
    Message m = new Message("Move", "Green",
                            "Execute", "Midkemia", "Oz", 
                            5, server.getParser().getMap());
    
    Message m2 = new Message("Attack", "Green",
                            "Execute", "Oz", "Elantris",
                             13, server.getParser().getMap());
    
    System.out.println(server.getParser().promptGenerator(m));
    Executor action0 = execFactory.create(m, server.getParser().getMap());
    action0.action();
     
    System.out.println(server.getParser().promptGenerator(m));
    
    //server.getParser().parseMessage(m);
    //System.out.println(server.getParser().promptGenerator(m2));
    //System.out.println(server.getParser().initialPrompt());
    
    m2.setAttackType("sendTroop");
    Executor action = execFactory.create(m2, server.getParser().getMap());
    action.action();

    System.out.println(server.getParser().promptGenerator(m2));
    //System.out.println(server.getParser().initialPrompt());
    
    m2.setAttackType("attack");
    action = execFactory.create(m2, server.getParser().getMap());
    action.action();

    System.out.println(server.getParser().promptGenerator(m2));
    //System.out.println(server.getParser().initialPrompt());    
  }

  @Test
  public void serverInitTest() throws IOException {  
    Server server = new Server(new GameMap(1));
    //Server server = new Server(6666);
    ArrayList<Integer> unclaimedGroups = server.getParser().getMap().getUnclaimedGroupIds();
    // assert that all territories are claimed
    assertEquals(0, unclaimedGroups.size());
    // assert that new territories can be added
    server.getParser().getMap().addTerritory(new Territory("Durham",0)); 
    server.getParser().getMap().addTerritory(new Territory ("Antarctica",0));
    // connect these new test territories
    server.getParser().getMap().connectTerritory("Durham", "Narnia");
    server.getParser().getMap().connectTerritory("Durham","Antarctica");
    // test if able to find unclaimed territories
    unclaimedGroups =  server.getParser().getMap().getUnclaimedGroupIds();
    assertEquals(true, unclaimedGroups.contains(0));
    assertEquals(1,unclaimedGroups.size());
    // now test game over functions
    assertEquals(false, server.getParser().getMap().gameOver());
  }

  @Test
  public void parseMessagesTest(){
    Server server = new Server(new GameMap(1));
    
    ArrayList<Message> initialMessages = new ArrayList<Message>();
    ArrayList<Message> messages = new ArrayList<Message>();
    initialMessages.add(new Message("AssignUnits", "Green",
                             "Assign", "Narnia", 7, server.getParser().getMap()));
        
    initialMessages.add(new Message("AssignUnits", "Blue",
                             "Assign", "Elantris", 6, server.getParser().getMap()));

    initialMessages.add(new Message("AssignUnits", "Red",
                             "Assign", "Mordor", 8, server.getParser().getMap()));

    messages.add(new Message("Attack", "Green",
                            "Execute", "Narnia", "Gondor",
                             1, server.getParser().getMap()));
    messages.add(new Message("Move", "Green",
                            "Execute", "Midkemia", "Oz", 
                             5, server.getParser().getMap()));
    
    messages.add(new Message("Attack", "Green",
                            "Execute", "Narnia", "Elantris",
                             8, server.getParser().getMap()));
    messages.add(new Message("Attack", "Green",
                            "Execute", "Oz", "Elantris",
                             8, server.getParser().getMap()));
    
    messages.add(new Message("Attack", "Green",
                            "Execute", "Gondor", "Mordor",
                             8, server.getParser().getMap()));
    
    messages.add(new Message("Move", "Red",
                            "Execute", "Gondor", "Mordor", 
                             5, server.getParser().getMap()));
    
    messages.add(new Message("Attack", "Red",
                            "Execute", "Hogwarts", "Scadrial",
                             6, server.getParser().getMap()));

    server.getParser().executeAssignUnit(initialMessages);
    
    assertEquals(7,server.getParser().getMap().getTerritory("Narnia").getUnit().getNumUnits());
    assertEquals(6,server.getParser().getMap().getTerritory("Elantris").getUnit().getNumUnits());
    assertEquals(8,server.getParser().getMap().getTerritory("Mordor").getUnit().getNumUnits());

    Message m2 = new Message("Attack", "Green",
                            "Execute", "Oz", "Elantris",
                             13, server.getParser().getMap());
    
    System.out.println(server.getParser().promptGenerator(m2));
    
    server.getParser().messageExecutor(messages);

    System.out.println(server.getParser().promptGenerator(m2));
    
    // assert the results of combat resolutions....
    // assertEquals results of these combats    
  }
}
