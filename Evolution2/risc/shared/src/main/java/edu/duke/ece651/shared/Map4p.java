package edu.duke.ece651.shared;

public class Map4p implements MapState {

	@Override
	public void handle(GameMap myMap) {
      System.out.println("Initializing Map4p for 4 players");
      
      myMap.addTerritory(new Territory("Narnia",0));
      myMap.addTerritory(new Territory("Midkemia",0));
      
      myMap.addTerritory(new Territory("Oz",1));  
      myMap.addTerritory(new Territory("Gondor",1));
      
      myMap.addTerritory(new Territory("Mordor",2));
      myMap.addTerritory(new Territory("Hogwarts",2));
      
      myMap.addTerritory(new Territory("Roshar",3));
      myMap.addTerritory(new Territory("Elantris",3));
      myMap.setNumGroups(4);
    
      myMap.connectTerritory("Narnia", "Midkemia");
      myMap.connectTerritory("Narnia", "Elantris");

      myMap.connectTerritory("Midkemia", "Roshar");
      myMap.connectTerritory("Midkemia", "Oz");
    
      myMap.connectTerritory("Oz", "Gondor");
      myMap.connectTerritory("Oz", "Hogwarts");
      
      myMap.connectTerritory("Gondor", "Mordor");

      myMap.connectTerritory("Mordor", "Hogwarts");
      myMap.connectTerritory("Mordor", "Elantris");
      
      myMap.connectTerritory("Hogwarts", "Roshar");
      
      myMap.connectTerritory("Roshar", "Elantris");
	}
  
}
