package FightTheLandlord.multithreading;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

//房间线程 管理对局中的信息
class ServiceThree implements Runnable {
    public int roomnumber=0;//房间标志
    public int playLead;//出牌玩家
    public boolean statGame=false;
    long start=0;
    long end=0;
    Deal dealCards;
    int clearPuker=0;
    int numberPlayer=-1;
    int []pukeNum=new int[3];
    int turn=0;//回合
    int score=0;
    ArrayList <ServicePlayer> List =new ArrayList<ServicePlayer>();//创建用户线程数组，最多三个，三个的时候开始游戏

    //创建ServiceThree时直接添加一个ServiceLog，将房间号命名为ServiceLog的房间号
    public ServiceThree(ServicePlayer a){
        List.add(a);
        roomnumber=a.roomnumber;
    }

    //外部调用向List中增加元素
    public void addList(ServicePlayer a){
        List.add(a);
        System.out.println("ServiceThree's list add");
    }

    //显示用户线程数组中的信息
    public void display(){
         for(int i=0;i<List.size();i++){
             System.out.println(List.get(i).player.getName());
         }
    }

    public void updateCards(){
        pukeNum[0]=List.get(0).player.getCards().size();
        pukeNum[1]=List.get(1).player.getCards().size();
        pukeNum[2]=List.get(2).player.getCards().size();

        List.get(0).player.setFormerPokerNumbers(pukeNum[2]);
        List.get(0).player.setLatterPokerNumbers(pukeNum[1]);
        List.get(1).player.setFormerPokerNumbers(pukeNum[0]);
        List.get(1).player.setLatterPokerNumbers(pukeNum[2]);
        List.get(2).player.setFormerPokerNumbers(pukeNum[1]);
        List.get(2).player.setLatterPokerNumbers(pukeNum[0]);
    }

    public void setTurn(int turn){
        List.get(0).player.setPlayerTurn(turn);
        List.get(1).player.setPlayerTurn(turn);
        List.get(2).player.setPlayerTurn(turn);
    }

    public void accountScore(int a,int b,int c){
        if(List.get(a).player.getCards().size()==0){
            if(List.get(a).player.getRole().equals("landlord")){//a地主赢
                List.get(a).player.setScore(List.get(a).player.getScore()+score*2);
                List.get(b).player.setScore(List.get(b).player.getScore()-score);
                List.get(c).player.setScore(List.get(c).player.getScore()-score);
            }else{//a农民赢
                List.get(a).player.setScore(List.get(a).player.getScore()+score);
                if(List.get(b).player.getRole().equals("landlord")){//c农民
                    List.get(b).player.setScore(List.get(b).player.getScore()-score*2);
                    List.get(c).player.setScore(List.get(c).player.getScore()+score);
                }else{//b农民
                    List.get(b).player.setScore(List.get(b).player.getScore()+score);
                    List.get(c).player.setScore(List.get(c).player.getScore()-score*2);
                }
            }
        }
    }

    public void setPlayerDataUpdate(String str){
        if(str.equals("lock")){
            List.get(0).lock.lock();
            List.get(1).lock.lock();
            List.get(2).lock.lock();
        }else if(str.equals("unlock")){
            List.get(0).lock.unlock();
            List.get(1).lock.unlock();
            List.get(2).lock.unlock();
        }
    }

    public void setPlayPointAll(int pointAll){
        List.get(0).player.setPointAll(pointAll);
        List.get(1).player.setPointAll(pointAll);
        List.get(2).player.setPointAll(pointAll);
    }

    //设置其他玩家角色
    public void setPlayerOtherRole(){
        List.get(0).player.setLatterRole(List.get(1).player.getRole());
        List.get(1).player.setLatterRole(List.get(2).player.getRole());
        List.get(2).player.setLatterRole(List.get(0).player.getRole());
        List.get(0).player.setFormerRole(List.get(2).player.getRole());
        List.get(1).player.setFormerRole(List.get(0).player.getRole());
        List.get(2).player.setFormerRole(List.get(1).player.getRole());
    }

    public void setPlayTime(long time){
        List.get(0).player.setTime(time);
        List.get(1).player.setTime(time);
        List.get(2).player.setTime(time);
    }

    public void setPlayerSheetInFormFlag(){
        List.get(0).sheetinform.flag=true;
        List.get(1).sheetinform.flag=true;
        List.get(2).sheetinform.flag=true;
    }

    @Override
    public void run() {
        while(true){
            Robot r = null;
            try {
                r = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
            r.delay(20);//延迟50ms

            if(numberPlayer!=List.size()){//当玩家人数改变时执行
                System.out.println("Three "+roomnumber+"="+List.size());
                numberPlayer=List.size();
            }

            int sub=0;//由于删除List中的元素会导致个数变化，所以需要修正项
            for(int i=0;i<List.size();i++){
                if(List.get(i-sub).roomnumber==0||!List.get(i-sub).clientStat){
                    List.remove(i-sub);
                    sub++;
                }
            }

            if(List.size()==3){//桌子上有三人
                int pos=0;//从随机位置开始分配
                playLead=(int) (Math.random() * 3);//第playLead进入房间的玩家先叫地主
                for(int i=0;i<List.size();i++){
                    List.get((playLead+i)%3).player.setPosition(pos);
                    pos++;
                }

                statGame=false;
                turn=0;
                if(List.get(0).gamePrepare && List.get(1).gamePrepare && List.get(2).gamePrepare){//全部准备
                    setPlayerDataUpdate("lock");
                    List.get(0).player.setFormerScore(List.get(2).player.getScore());
                    List.get(1).player.setFormerScore(List.get(0).player.getScore());
                    List.get(2).player.setFormerScore(List.get(1).player.getScore());

                    List.get(0).player.setLatterScore(List.get(1).player.getScore());
                    List.get(1).player.setLatterScore(List.get(2).player.getScore());
                    List.get(2).player.setLatterScore(List.get(0).player.getScore());
                    List.get(0).player.resetIn();
                    List.get(1).player.resetIn();
                    List.get(2).player.resetIn();
                    System.out.println("game is start");
                    statGame=true;
                    List.get(0).player.setGameStart(true);
                    List.get(1).player.setGameStart(true);
                    List.get(2).player.setGameStart(true);
                    dealCards=new Deal();//发牌
                    List.get(0).player.setCards(dealCards.getAry1());
                    List.get(1).player.setCards(dealCards.getAry2());
                    List.get(2).player.setCards(dealCards.getAry3());
                    setTurn(turn);
                    List.get(0).player.setFormerName(List.get(2).player.getName());
                    List.get(1).player.setFormerName(List.get(0).player.getName());
                    List.get(2).player.setFormerName(List.get(1).player.getName());
                    List.get(0).player.setLatterName(List.get(1).player.getName());
                    List.get(1).player.setLatterName(List.get(2).player.getName());
                    List.get(2).player.setLatterName(List.get(0).player.getName());
                    updateCards();
                    setPlayerDataUpdate("unlock");
                    Date date = new Date();
                    start=date.getTime();
//                    date = new Date();
//                    end=date.getTime();

                    int countNo=0;
                    while(statGame){//叫分逻辑
                        date = new Date();
                        end=date.getTime();
                        setPlayTime(end-start);

                        setPlayerDataUpdate("lock");
                        if(countNo==2){//两人不要
                            System.out.println("countNo=2");
                            if(List.get(playLead).player.getPoint()==0){
                                setPlayPointAll(1);
                                List.get(playLead).player.setRole("landlord");
                                List.get((playLead+1)%3).player.setRole("farmer");
                                List.get((playLead+2)%3).player.setRole("farmer");
                            }
                            else{
                                setPlayPointAll(List.get(playLead).player.getPoint());
                                List.get(playLead).player.setRole("landlord");
                                List.get((playLead+1)%3).player.setRole("farmer");
                                List.get((playLead+2)%3).player.setRole("farmer");
                            }
                            List.get(0).player.setLandlordCards(dealCards.getAry4());
                            List.get(1).player.setLandlordCards(dealCards.getAry4());
                            List.get(2).player.setLandlordCards(dealCards.getAry4());
                            List.get(playLead).player.addCrads(dealCards.getAry4().get(0));//地主牌
                            List.get(playLead).player.addCrads(dealCards.getAry4().get(1));
                            List.get(playLead).player.addCrads(dealCards.getAry4().get(2));
                            List.get(playLead).player.sortCrads();//将地主牌重新排序
                            setPlayerOtherRole();
                            setPlayerDataUpdate("unlock");
                            break;
                        }

                        if(List.get(playLead).pointUpdate==true){
                            System.out.println("pointUpdate "+"point="+List.get(playLead).player.getPoint());
                            List.get(playLead).pointUpdate=false;
                            if(List.get(playLead).player.getPoint()==-1){
                                countNo++;
                            }else if(List.get(playLead).player.getPoint()==1){
                                countNo=0;
                                setPlayPointAll(1);
                            }else if(List.get(playLead).player.getPoint()==2){
                                countNo=0;
                                setPlayPointAll(2);
                            }else if(List.get(playLead).player.getPoint()==3){
                                setPlayPointAll(3);
                                List.get(playLead).player.setRole("landlord");
                                List.get((playLead+1)%3).player.setRole("farmer");
                                List.get((playLead+2)%3).player.setRole("farmer");
                                List.get(0).player.setLandlordCards(dealCards.getAry4());
                                List.get(1).player.setLandlordCards(dealCards.getAry4());
                                List.get(2).player.setLandlordCards(dealCards.getAry4());
                                List.get(playLead).player.addCrads(dealCards.getAry4().get(0));//地主牌
                                List.get(playLead).player.addCrads(dealCards.getAry4().get(1));
                                List.get(playLead).player.addCrads(dealCards.getAry4().get(2));
                                List.get(playLead).player.sortCrads();//将地主牌重新排序
                                setPlayerOtherRole();
                                setPlayerDataUpdate("unlock");
                                break;
                            }
                            do{
                                turn=(turn+1)%3;
                                playLead=(playLead+1)%3;
                                countNo++;//第一次加1是多余的
                            }while(List.get(playLead).player.getPoint()==-1);
                            countNo--;//将第一次加1去除
                            setTurn(turn);
                            date = new Date();
                            start=date.getTime();
                        }
                        setPlayerDataUpdate("unlock");
                    }//叫分结束

                    System.out.println("叫分结束");
                    updateCards();

                    pos=0;
                    for(int i=0;i<List.size();i++){
                        List.get((playLead+i)%3).player.setPosition(pos);
                        pos++;
                    }
                    turn=0;
                    setTurn(turn);
                    date = new Date();
                    start=date.getTime();
                    while(statGame){//打牌逻辑
                        r.delay(3);//延迟3ms
                        date = new Date();
                        end=date.getTime();
                        setPlayTime(end-start);
                        if(List.get(playLead).pukeUpdate==true){
                            setPlayerDataUpdate("lock");
                            System.out.println("0,1,2 update true");
                            System.out.println("回合pos="+ List.get(playLead).player.getPosition());
                            List.get(playLead).pukeUpdate=false;
                            List.get(0).player.setMultiplying(List.get(playLead).player.getMultiplying());
                            List.get(1).player.setMultiplying(List.get(playLead).player.getMultiplying());
                            List.get(2).player.setMultiplying(List.get(playLead).player.getMultiplying());
                            if(List.get(playLead).pokerOut.size()==0){
                                clearPuker++;
                                if(clearPuker==2){
                                    List.get(0).player.setCardsLead(List.get(playLead).pokerOut);
                                    List.get(1).player.setCardsLead(List.get(playLead).pokerOut);
                                    List.get(2).player.setCardsLead(List.get(playLead).pokerOut);
                                }
                            }else{
                                List.get(0).player.setCardsLead(List.get(playLead).pokerOut);
                                List.get(1).player.setCardsLead(List.get(playLead).pokerOut);
                                List.get(2).player.setCardsLead(List.get(playLead).pokerOut);
                                updateCards();
                                clearPuker=0;
                            }
                            playLead=(playLead+1)%3;
                            turn=(turn+1)%3;
                            setTurn(turn);
                            date = new Date();
                            start=date.getTime();
//                            System.out.println("回合更新="+ playLead);
//                            System.out.println("回合pos="+ List.get(playLead).player.getPosition());

                            //更新积分
                            if(List.get(0).player.getCards().size()==0||List.get(1).player.getCards().size()==0||List.get(2).player.getCards().size()==0){
                                score=List.get(0).player.getPointAll()*List.get(0).player.getMultiplying();
                                accountScore(0,1,2);
                                accountScore(1,0,2);
                                accountScore(2,0,1);
                                List.get(0).player.setFormerScore(List.get(2).player.getScore());
                                List.get(1).player.setFormerScore(List.get(0).player.getScore());
                                List.get(2).player.setFormerScore(List.get(1).player.getScore());

                                List.get(0).player.setLatterScore(List.get(1).player.getScore());
                                List.get(1).player.setLatterScore(List.get(2).player.getScore());
                                List.get(2).player.setLatterScore(List.get(0).player.getScore());
                                setPlayerSheetInFormFlag();//将保存位置为真
                                System.out.println("Score="+List.get(0).player.getScore());
                            }
                            setPlayerDataUpdate("unlock");
                            System.out.println("0,1,2 update false");
                        }
//                        date = new Date();
//                        end=date.getTime();
                        //检查游戏是否结束
                        if(List.get(0).player.getCards().size()==0||List.get(1).player.getCards().size()==0||List.get(2).player.getCards().size()==0){
                            setPlayerDataUpdate("lock");
                            System.out.println("game over!");
                            List.get(0).gamePrepare=false;
                            List.get(1).gamePrepare=false;
                            List.get(2).gamePrepare=false;
                            List.get(0).player.setGameStart(false);
                            List.get(1).player.setGameStart(false);
                            List.get(2).player.setGameStart(false);
                            System.out.println("prepare="+List.get(0).gamePrepare);
                            statGame=false;
                            setPlayerDataUpdate("unlock");
                            break;
                        }
                    }//while gamestart
                }//if gamePrepare
            }//if size==3
        }
    }
}
