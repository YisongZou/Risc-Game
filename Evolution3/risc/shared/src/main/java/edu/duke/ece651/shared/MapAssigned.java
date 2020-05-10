package edu.duke.ece651.shared;

public class MapAssigned implements MapState {

	@Override
	public void handle(GameMap myMap) {
    System.out.println("Initializing assigned map with 3 groups");
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
    myMap.connectTerritory("Narnia","Elantris");
    
    myMap.connectTerritory("Midkemia","Oz");
    myMap.connectTerritory("Midkemia","Scadrial");
    myMap.connectTerritory("Midkemia","Elantris");
    
    myMap.connectTerritory("Oz","Gondor");
    myMap.connectTerritory("Mordor","Gondor");
    myMap.connectTerritory("Mordor","Hogwarts");
    myMap.connectTerritory("Mordor","Oz");    
    myMap.connectTerritory("Roshar","Hogwarts");
  
    myMap.connectTerritory("Scadrial","Oz");
    myMap.connectTerritory("Scadrial","Hogwarts");                
    myMap.connectTerritory("Scadrial","Mordor");    
    myMap.connectTerritory("Scadrial","Elantris");
    myMap.connectTerritory("Scadrial","Roshar");    
    myMap.connectTerritory("Roshar","Elantris");      
    
    myMap.setTerritoryGroupPlayer(0,"Green");
    myMap.setTerritoryGroupPlayer(1,"Blue");
    myMap.setTerritoryGroupPlayer(2,"Red");  
		
	}
  
}
