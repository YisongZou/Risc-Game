package edu.duke.ece651.shared;

public class Map5p implements MapState {

	@Override
	public void handle(GameMap myMap) {
      System.out.println("Initializing Map5p for 5 players");

      myMap.addTerritory(new Territory("Narnia",0));
      myMap.addTerritory(new Territory("Midkemia",0));
      
      myMap.addTerritory(new Territory("Oz",1));  
      myMap.addTerritory(new Territory("Gondor",1));
      
      myMap.addTerritory(new Territory("Mordor",2));
      myMap.addTerritory(new Territory("Hogwarts",2));
      
      myMap.addTerritory(new Territory("Roshar",3));
      myMap.addTerritory(new Territory("Elantris",3));
      
      myMap.addTerritory(new Territory("Hoth",4));
      myMap.addTerritory(new Territory("Scadrial",4));
      myMap.setNumGroups(5);
      
      myMap.connectTerritory("Narnia", "Midkemia");
      myMap.connectTerritory("Narnia", "Elantris");
      
      myMap.connectTerritory("Midkemia", "Roshar");
      myMap.connectTerritory("Midkemia", "Oz");
      
      myMap.connectTerritory("Oz", "Gondor");
      myMap.connectTerritory("Oz", "Hogwarts");
      
      myMap.connectTerritory("Gondor", "Mordor");
      
      myMap.connectTerritory("Mordor", "Hogwarts");
    
      myMap.connectTerritory("Hogwarts", "Roshar");
      
      myMap.connectTerritory("Roshar", "Elantris");
      myMap.connectTerritory("Roshar", "Hoth");
      
      myMap.connectTerritory("Hoth", "Hogwarts");
      myMap.connectTerritory("Hoth", "Scadrial");
     
      myMap.connectTerritory("Scadrial", "Mordor");
      myMap.connectTerritory("Elantris", "Scadrial");
		
	}

}
