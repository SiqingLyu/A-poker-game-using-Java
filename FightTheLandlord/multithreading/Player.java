package FightTheLandlord.multithreading;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Player implements Serializable {
    private boolean gameStart=false;//开始标志
    private String name;//账号
    private int number=0;//玩家在数据库中的序号
    private int roomnumber=0;//房间号
    private int point=0;//自己的叫分记录
    private int pointAll=0;//所有人的叫分记录
    private int score=0;//积分
    private int position=-1;//座位
    private String formerName="";//上家账号
    private String formerRole="";//上家角色
    private int formerPokerNumbers=0;//上家牌数
    private String latterName="";//下家账号
    private String latterRole="";//下家角色
    private int formerScore = 0;
    private int latterScore = 0;
    private int latterPokerNumbers=0;//下家牌数
    private int playerTurn=-1;//谁的回合
    private String role="no";//农民：farmer  地主：landlord
    private long time=0;//时间
    private ArrayList<Integer> cards =new ArrayList<Integer>();//牌
    private ArrayList<Integer> cardsLead =new ArrayList<Integer>();//当前场上的牌
    private ArrayList<Integer> landlordCards=new ArrayList<Integer>();//地主牌（三张）
    private int Multiplying=1;//倍率


    public Player(){

    }


    public void resetIn(){
        this.playerTurn=-1;
        this.time=0;
        this.cards.removeAll(this.cards);
        this.cardsLead.removeAll(this.cardsLead);
        this.role="no";
        this.point=0;
        this.pointAll=0;
        this.Multiplying=1;
        this.formerName="";
        this.formerRole="";
        this.formerPokerNumbers=0;
        this.latterName="";
        this.latterRole="";
        this.latterPokerNumbers=0;
    }

    public void setFormerScore(int formerScore) {
        this.formerScore = formerScore;
    }

    public void setLatterScore(int latterScore) {
        this.latterScore = latterScore;
    }

    public int getFormerScore() {
        return formerScore;
    }

    public int getLatterScore() {
        return latterScore;
    }

    public void setFormerName(String formerName) {
        this.formerName = formerName;
    }

    public void setFormerRole(String formerRole) {
        this.formerRole = formerRole;
    }

    public ArrayList<Integer> getLandlordCards() {
        return landlordCards;
    }

    public void setLandlordCards(ArrayList<Integer> landlordCards) {
        this.landlordCards = landlordCards;
    }

    public void setFormerPokerNumbers(int formerPokerNumbers) {
        this.formerPokerNumbers = formerPokerNumbers;
    }

    public void setLatterName(String latterName) {
        this.latterName = latterName;
    }

    public void setLatterRole(String latterRole) {
        this.latterRole = latterRole;
    }

    public void setLatterPokerNumbers(int latterPokerNumbers) {
        this.latterPokerNumbers = latterPokerNumbers;
    }

    public String getFormerName() {
        return formerName;
    }

    public String getFormerRole() {
        return formerRole;
    }

    public int getFormerPokerNumbers() {
        return formerPokerNumbers;
    }

    public String getLatterName() {
        return latterName;
    }

    public String getLatterRole() {
        return latterRole;
    }

    public int getLatterPokerNumbers() {
        return latterPokerNumbers;
    }

    public int getMultiplying() {
        return Multiplying;
    }

    public void setMultiplying(int multiplying) {
        Multiplying = multiplying;
    }

    public int getPointAll() {
        return pointAll;
    }

    public void setPointAll(int pointAll) {
        this.pointAll = pointAll;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isGameStart() {
        return gameStart;
    }

    public void setGameStart(boolean gameStart) {
        this.gameStart = gameStart;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ArrayList<Integer> getCardsLead() {
        return cardsLead;
    }

    public void setCardsLead(ArrayList<Integer> cardsLead) {
        this.cardsLead = cardsLead;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getScore() {
        return score;
    }

    public int getPosition() {
        return position;
    }

    public int getRoomnumber() {
        return roomnumber;
    }

    public void setRoomnumber(int roomnumber) {
        this.roomnumber = roomnumber;
    }

    public ArrayList<Integer> getCards() {
        return cards;
    }

    public void sortCrads(){
        Comparator comp=new MyComp();//给牌排序
        this.cards.sort(comp);
    }

    public void addCrads(int a){
        this.cards.add(a);
    }

    public void setCards(ArrayList<Integer> cards) {
        this.cards = cards;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "player{" +
                "name=" + name +
                ", score=" + score +
                ", position=" + position +
                ", playerTurn=" + playerTurn +
                ", cards=" + cards +
                '}';
    }
}
