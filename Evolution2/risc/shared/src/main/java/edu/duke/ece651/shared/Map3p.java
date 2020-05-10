package edu.duke.ece651.shared;

public class Map3p implements MapState {

	@Override
	public void handle(GameMap myMap) {
    System.out.println("Initializing Map3p for 3 players");
    myMap.addTerritory(new Territory("Narnia",0));
    myMap.addTerritory(new Territory("Midkemia",0));
    myMap.addTerritory(new Territory("Oz",0));
    
    myMap.addTerritory(new Territory("Elantris",1));
    myMap.addTerritory(new Territory("Roshar",1));
    myMap.addTerritory(new Territory("Scadrial",1));
    
    myMap.addTerritory(new Territory("Gondor",2));
    myMap.addTerritory(new Territory("Mordor",2));
    myMap.addTerritory(new Territory("Hogwarts",2));
    myMap.setNumGroups(3);
    
    myMap.connectTerritory("Narnia","Midkemia");
    
    myMap.connectTerritory("Midkemia","Oz");
    myMap.connectTerritory("Midkemia","Scadrial");
    
    myMap.connectTerritory("Oz","Gondor");
    myMap.connectTerritory("Mordor","Hogwarts");
    myMap.connectTerritory("Mordor","Oz");
    myMap.connectTerritory("Mordor","Midkemia");        
    
    myMap.connectTerritory("Scadrial","Hogwarts");                
    myMap.connectTerritory("Scadrial","Mordor");    
    myMap.connectTerritory("Scadrial","Elantris");
    myMap.connectTerritory("Scadrial","Roshar");
    
    myMap.connectTerritory("Roshar","Elantris");
    myMap.connectTerritory("Roshar","Hogwarts");    
    
			
	}
  
}
