package edu.duke.ece651.shared;

public class Map2p implements MapState {

	@Override
	public void handle(GameMap myMap) {
    System.out.println("Initializing Map2p for 2 players");
    myMap.addTerritory(new Territory("Narnia",0));
    myMap.addTerritory(new Territory("Midkemia",0));
    
    myMap.addTerritory(new Territory("Oz",1));  
    myMap.addTerritory(new Territory("Gondor",1));
    myMap.setNumGroups(2);
    
    myMap.connectTerritory("Narnia","Midkemia");
    myMap.connectTerritory("Narnia","Oz");
    
    myMap.connectTerritory("Midkemia","Gondor");
    myMap.connectTerritory("Oz","Gondor");
	}

}
