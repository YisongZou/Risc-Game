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
  // @Test
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
    Message message = new Message("AssignTerritoryGroup", "Green", "Assign", 0, greenPlayer.getGmap());

    Initializer action = initFactory.create(message, gmap);action.action();
  
    assertEquals("Green",gmap.getTerritory("Narnia").getPlayerName());
    assertEquals("Green",gmap.getTerritory("Oz").getPlayerName());
    assertEquals("Green",gmap.getTerritory("Midkemia").getPlayerName());
    assertEquals(null,gmap.getTerritory("Gondor").getPlayerName());
    
    //test for assignunits
    message = new Message("AssignUnits", "Green",
                             "Assign", "Oz", 7, greenPlayer.getGmap());
    action = initFactory.create(message, gmap);
    assertEquals(0,gmap.getTerritory("Oz").getUnit(0).getNumUnits());    
    action.action();
    assertEquals(7,gmap.getTerritory("Oz").getUnit(0).getNumUnits());    
  }
  
  @Test
  public void parserTest() {
    Parser parser = new Parser(1);
    
    ArrayList<String> names = new ArrayList<String>();
    names.add("Green");
    names.add("Blue");
    names.add("Red");
    parser.getMap().init_ply_resource(names);
    
    Message promt = parser.initialPrompt();
    ArrayList<Integer> attackList = new ArrayList<Integer>();
    Message m = new Message("Move", "Green", "Execute", "Midkemia", "Oz", 2, parser.getMap(), 0, attackList);
    Message m3 = new Message("unknown", "unknown", "unknown", "unknown", 7, parser.getMap());
    // execute message
    parser.parseMessage(m);
    parser.parseMessage(m3);

    // assert that this is true
    assertEquals(0, parser.getMap().getTerritory("Midkemia").getUnit(0).getNumUnits());
    assertEquals(2, parser.getMap().getTerritory("Oz").getUnit(0).getNumUnits());

    Message m1 = new Message("AssignTerritoryGroup", "Blue", "Assign", 0, parser.getMap());
    //m1.setGmap(null);
    Message m2 = new Message("AssignUnits", "Green", "Assign", "Oz", 7, parser.getMap());
    assertEquals("Green", parser.getMap().getTerritory("Oz").getPlayerName());
    parser.executeAssignTerritoryGroup(m1);
    parser.parseMessage(m2);
    assertEquals("Blue", parser.getMap().getTerritory("Oz").getPlayerName());
  }

  @Test

  public void serverTest() throws IOException {
    Server server = new Server(new GameMap(1));
    // Server server = new Server(12345);
    ArrayList<String> names = new ArrayList<String>();
    names.add("Green");
    names.add("Blue");
    names.add("Red");
    server.getParser().getMap().init_ply_resource(names);
    ExecutorFactory execFactory = new ExecutorFactory();
    InitializerFactory initFactory = new InitializerFactory();
    //Initialize all territories to have 5 basic units
    Message init1 = new Message("AssignUnits", "Green", "Assign", "Oz", 5, server.getParser().getMap());
    Initializer action1 = initFactory.create(init1, server.getParser().getMap());
    action1.action();
    Message init2 = new Message("AssignUnits", "Green", "Assign", "Narnia", 5, server.getParser().getMap());
    Initializer action2 = initFactory.create(init2, server.getParser().getMap());
    action2.action();
    Message init3 = new Message("AssignUnits", "Green", "Assign", "Midkemia", 5, server.getParser().getMap());
    Initializer action3 = initFactory.create(init3, server.getParser().getMap());
    action3.action();
    Message init4 = new Message("AssignUnits", "Green", "Assign", "Gondor", 5, server.getParser().getMap());
    Initializer action4 = initFactory.create(init4, server.getParser().getMap());
    action4.action();
    Message init5 = new Message("AssignUnits", "Green", "Assign", "Mordor", 5, server.getParser().getMap());
    Initializer action5 = initFactory.create(init5, server.getParser().getMap());
    action5.action();
    Message init6 = new Message("AssignUnits", "Green", "Assign", "Hogwarts", 5, server.getParser().getMap());
    Initializer action6 = initFactory.create(init6, server.getParser().getMap());
    action6.action();
    Message init7 = new Message("AssignUnits", "Green", "Assign", "Elantris", 5, server.getParser().getMap());
    Initializer action7 = initFactory.create(init7, server.getParser().getMap());
    action7.action();
    Message init8 = new Message("AssignUnits", "Green", "Assign", "Scadrial", 5, server.getParser().getMap());
    Initializer action8 = initFactory.create(init8, server.getParser().getMap());
    action8.action();
    Message init9 = new Message("AssignUnits", "Green", "Assign", "Roshar", 5, server.getParser().getMap());
    Initializer action9 = initFactory.create(init9, server.getParser().getMap());
    action9.action();

    ArrayList<Integer> attackList = new ArrayList<Integer>();
    //Currently all territories have 5 basic units 
    Message m = new Message("Move", "Green", "Execute", "Oz", "Midkemia", 5, server.getParser().getMap(), 0,
        attackList);
    //Use 6 lv0, 10 lv1, 10 lv2, 10 lv3 units to attack
    attackList.add(6);
    attackList.add(10);
    attackList.add(10);
    attackList.add(10);
    server.getParser().getMap().getTerritory("Midkemia").addUnit(1, 10);
        server.getParser().getMap().getTerritory("Midkemia").addUnit(2, 10);
        server.getParser().getMap().getTerritory("Midkemia").addUnit(3, 10);
    
    Message m2 = new Message("Attack", "Green", "Execute", "Midkemia", "Elantris", 0, server.getParser().getMap(), 0,
        attackList);

        server.getParser().getMap().getTerritory("Elantris").addUnit(4, 10);
        server.getParser().getMap().getTerritory("Elantris").addUnit(5, 10);
        server.getParser().getMap().getTerritory("Elantris").addUnit(6, 10);
        
    System.out.println(server.getParser().promptGenerator(m));
    Executor action0 = execFactory.create(m, server.getParser().getMap());
    action0.action();

    System.out.println(server.getParser().promptGenerator(m));
    Message m3 = new Message();
    m3.setType("Upgrade_Spy");
    m3.setPlayerName("Green");
    m3.setDestTerritory("Elantris");
    Executor myaction = execFactory.create(m3, server.getParser().getMap());
    myaction.action();
    Message m4 = new Message();
    m4.setType("Move_Spy");
    m4.setSrcTerritory("Elantris");
    m4.setDestTerritory("Midkemia");
    m4.setPlayerName("Green");
    Executor myaction2 = execFactory.create(m4, server.getParser().getMap());
    myaction2.action();
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
    server.getParser().getMap().addTerritory(new Territory("Durham", 0));
    server.getParser().getMap().addTerritory(new Territory("Antarctica", 0));
    // connect these new test territories
    server.getParser().getMap().connectTerritory("Durham", "Narnia");
    server.getParser().getMap().connectTerritory("Durham", "Antarctica");
    // test if able to find unclaimed territories
    unclaimedGroups = server.getParser().getMap().getUnclaimedGroupIds();
    assertEquals(true, unclaimedGroups.contains(0));
    assertEquals(1, unclaimedGroups.size());
    // now test game over functions
    assertEquals(false, server.getParser().getMap().gameOver());
  }

  @Test
  public void parseMessagesTest() {
    Server server = new Server(new GameMap(1));

    ArrayList<String> names = new ArrayList<String>();
    names.add("Green");
    names.add("Blue");
    names.add("Red");
    server.getParser().getMap().init_ply_resource(names);
    
    ArrayList<Message> initialMessages = new ArrayList<Message>();
    ArrayList<Message> messages = new ArrayList<Message>();
    initialMessages.add(new Message("AssignUnits", "Green", "Assign", "Narnia", 7, server.getParser().getMap()));

    initialMessages.add(new Message("AssignUnits", "Blue", "Assign", "Elantris", 6, server.getParser().getMap()));

    initialMessages.add(new Message("AssignUnits", "Red", "Assign", "Mordor", 8, server.getParser().getMap()));
    ArrayList<Integer> attackList1 = new ArrayList<Integer>();
    attackList1.add(1);
    messages.add(new Message("Attack", "Green", "Execute", "Narnia", "Gondor", 1, server.getParser().getMap(),0,attackList1));
    messages.add(new Message("Move", "Green", "Execute", "Midkemia", "Oz", 5, server.getParser().getMap(),0,attackList1));
    
    ArrayList<Integer> attackList2 = new ArrayList<Integer>();
    attackList2.add(8);
    messages.add(new Message("Attack", "Green", "Execute", "Narnia", "Elantris", 8, server.getParser().getMap(),0,attackList2));
    messages.add(new Message("Attack", "Green", "Execute", "Oz", "Elantris", 8, server.getParser().getMap(),0,attackList2));

    messages.add(new Message("Attack", "Green", "Execute", "Gondor", "Mordor", 8, server.getParser().getMap(),0,attackList2));

    messages.add(new Message("Move", "Red", "Execute", "Gondor", "Mordor", 5, server.getParser().getMap(),0,attackList2));

    ArrayList<Integer> attackList3 = new ArrayList<Integer>();
    attackList3.add(6); 
    messages.add(new Message("Attack", "Red", "Execute", "Hogwarts", "Scadrial", 6, server.getParser().getMap(),0,attackList3));

    server.getParser().executeAssignUnit(initialMessages);

    assertEquals(7, server.getParser().getMap().getTerritory("Narnia").getUnit(0).getNumUnits());
    assertEquals(6, server.getParser().getMap().getTerritory("Elantris").getUnit(0).getNumUnits());
    assertEquals(8, server.getParser().getMap().getTerritory("Mordor").getUnit(0).getNumUnits());

    Message m2 = new Message("Attack", "Green", "Execute", "Oz", "Elantris", 13, server.getParser().getMap());

    System.out.println(server.getParser().promptGenerator(m2));

    server.getParser().messageExecutor(messages);

    System.out.println(server.getParser().promptGenerator(m2));

    // assert the results of combat resolutions....
    // assertEquals results of these combats    
  }
}
