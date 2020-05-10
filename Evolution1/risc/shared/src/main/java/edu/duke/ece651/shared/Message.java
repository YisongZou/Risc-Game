package edu.duke.ece651.shared;

import java.util.ArrayList;

public class Message implements java.io.Serializable {
  private String content; // content stores information
  private String type;//AssignTerritory, Move, or Attack or AssignUnits
  private String playerName;
  private String messageType;//Assign or Execute
  private String srcTerritory;
  private String destTerritory;
  private String attackType;
  private GameMap gmap;
  private Integer unitNum;
  private Integer groupId;
  private static final long serialVersionUID = 1L;
  private ArrayList<Integer> validGroupIds;
  //  private ArrayList<Message> assignUnitsMeses; 

  public Message(){
  }
  /*
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    System.out.println("**************************");
    
    System.out.println("**************************");
    }   */
  
  public Message(String content) {
    this.content = content;    
  }

  public Message(String type, String playerName, String messageType,
                 String srcTerritory,String destTerritory,
                 Integer numUnits, GameMap gmap) {
    this.content = "";
    this.type = type;
    this.playerName = playerName;
    this.messageType = messageType;
    this.srcTerritory = srcTerritory;
    this.destTerritory = destTerritory;
    this.gmap = gmap;
    this.unitNum = numUnits;    
  }

  public Message(String type, String playerName, String messageType,
                 String destTerritory,Integer numUnits, GameMap gmap) {
    this.type = type;
    this.playerName = playerName;
    this.messageType = messageType;
    this.destTerritory = destTerritory;
    this.gmap = gmap;
    this.unitNum = numUnits;    
  }  
  

  public Message(String type, String playerName, String messageType,
                 Integer groupId, GameMap gmap) {
    this.type = type;
    this.playerName = playerName;
    this.messageType = messageType;
    this.groupId = groupId;
    this.gmap = gmap;
    this.unitNum = 0;    
    this.groupId = groupId;
  }
  
  public Integer getGroupId() {
    return this.groupId;
  }

  public void setGroupId(Integer groupid) {
    this.groupId = groupid;
  }
  
  public ArrayList<Integer> getValidGroupIds(){
    return this.validGroupIds;
  }

  public void setValidGroupIds(ArrayList<Integer> newValidGroupIds){
    this.validGroupIds = newValidGroupIds;
  }
  
  public String getAttackType(){
    return this.attackType;
  }

  public void setAttackType(String newAttackType){
    this.attackType = newAttackType;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String newCnt) {
    this.content = newCnt;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public String getSrcTerritory() {
    return srcTerritory;
  }

  public void setSrcTerritory(String srcTerritory) {
    this.srcTerritory = srcTerritory;
  }

  public String getMessageType() {
    return this.messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public String getDestTerritory() {
    return destTerritory;
  }

  public void setDestTerritory(String destTerritory) {
    this.destTerritory = destTerritory;
  }

  public Integer getUnitNum() {
    return unitNum;
  }

  public void setUnitNum(Integer unitNum) {
    this.unitNum = unitNum;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public GameMap getGmap() {
    return gmap;
  }

  public void setGmap(GameMap gmap) {
    this.gmap = gmap;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}

