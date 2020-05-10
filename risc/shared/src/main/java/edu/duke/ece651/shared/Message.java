package edu.duke.ece651.shared;

import java.net.Socket;
import java.util.ArrayList;

public class Message implements java.io.Serializable {
  private String content; // content stores information
  private String type;//AssignTerritory, Move, or Attack or AssignUnits or Upgrade_Unit or Upgrade_Tech or Research_Cloak or Use_Cloak
  private String playerName;
  private String messageType;//Assign or Execute
  private String srcTerritory;
  private String destTerritory;
  private String attackType;
  private GameMap gmap;
  private Integer unitNum;
  private Integer groupId;
  private Integer level;
  private ArrayList<Integer> attackLevels; 
  private static final long serialVersionUID = 1L;
  private ArrayList<Integer> validGroupIds;
  
  private String password;
  private Integer gameCapacity;
  private Integer curPlyNum;
  private String gameServerIP;
  private int gameServerPort;

  public Message(){
    this.attackLevels = new ArrayList<Integer>();
    this.validGroupIds = new ArrayList<Integer>();
  }
  
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

  public void setCurPlyNum(int num) { 
    this.curPlyNum = num;
  }

  public Integer getCurPlyNum() {
    return this.curPlyNum;
  }  

  public void setGameServerPort(int port) {
    this.gameServerPort = port;
  }

  public Integer getGameServerPort() {
    return this.gameServerPort;
  }

  public String getGameServerIP() {
    return this.gameServerIP;
  }

  public void setGameServerIP(String ip) {
    this.gameServerIP = ip;
  }  
  
  public void setGameCapacity(int num) {
    this.gameCapacity = num;
  }

  public Integer getGameCapacity() {
    return this.gameCapacity;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  //constructor for upgrade
  public Message(String type, String playerName, String messageType,
                 String destTerritory, Integer level, GameMap gmap,Integer UnitNum) {
    this.type = type;
    this.playerName = playerName;
    this.messageType = messageType;
    this.destTerritory = destTerritory;
    this.level = level;
    this.unitNum = UnitNum;
    this.gmap = gmap;
  }

   //constructor for evolusion 2 attack and move
public Message(String type, String playerName, String messageType,
                 String srcTerritory,String destTerritory,
               Integer numUnits, GameMap gmap, int lv, ArrayList<Integer> atkLevels) {
    this.content = "";
    this.type = type;
    this.playerName = playerName;
    this.messageType = messageType;
    this.srcTerritory = srcTerritory;
    this.destTerritory = destTerritory;
    this.gmap = gmap;
    this.unitNum = numUnits;
    this.level = lv;
    this.attackLevels = atkLevels;
  }

  public void setAttackLevels(ArrayList<Integer> inputArrayList ) {
    for (Integer i : inputArrayList) {
      this.attackLevels.add(i);
    }
  }

  public ArrayList<Integer> getAttackLevels() {
    return this.attackLevels;
  }

  public Integer getLevel() {
    return this.level;
  }

  public void setLevel(Integer level) {
    this.level = level;
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
