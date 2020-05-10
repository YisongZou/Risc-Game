package edu.duke.ece651.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Spy implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
  private Integer numUnits;
  private HashMap<String, Integer> spylocation;
  
  public Spy() {
    numUnits = 0;
    spylocation = new HashMap<String, Integer>();
  }

  public Integer getNumUnits(){
    return numUnits;
  }

  public void setNumUnits(Integer num) {
    numUnits = num;
  }
  
  public void addNumUnits(Integer num) {
    numUnits += num;
  }
  
  public HashMap<String, Integer> getspylocation() {
    return spylocation;
  }

  //get all the territory that has > 0 spies in it
  public ArrayList<String> getValidSpyLocation() {
    ArrayList<String> ans = new ArrayList<String>();
    for(Map.Entry<String, Integer> entry : spylocation.entrySet()){
      if (entry.getValue() != 0) {
        ans.add(entry.getKey());
      }
    }
    return ans;
  }
  
  //move 1 unit from src terr, add 1 unit to dst terr
  public boolean moveSpy(String src_terr, String dst_terr) {
    if (spylocation.get(src_terr) == null) {
      return false;
    }
    Integer src_spy_num = spylocation.get(src_terr);
    src_spy_num = src_spy_num - 1;
    spylocation.put(src_terr, src_spy_num);
    return addSpy(dst_terr);
  }
  
  //add 1 spy to territory
  public boolean addSpy(String src_terr) {
    if (spylocation.get(src_terr) == null) {
      spylocation.put(src_terr, 1);
      return true;
    }
    Integer src_spy_num = spylocation.get(src_terr);
    src_spy_num++;
    spylocation.put(src_terr, src_spy_num);
    return true;
  }

  //return the number of spies we have in this territory
  public Integer getSpies(String territoryname) {
    if (spylocation.get(territoryname) == null) {
      return 0;
    }
    return spylocation.get(territoryname);
  }
}
