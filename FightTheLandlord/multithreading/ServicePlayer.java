package FightTheLandlord.multithreading;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//线程 服务端登录验证后续转为单个用户逻辑处理
public class ServicePlayer implements Runnable {//创建多线程
    private InputStream input = null;
    private OutputStream output = null;
    public int roomnumber=0;//房间号
    private ObjectOutputStream obj = null;
    private ObjectInputStream objin=null;
    private PrintWriter pw = null;
    private Socket s = null;
    private int count = 0;
    public  Player player=new Player();//玩家
    private String user;
    private String passwd;

    public ArrayList<Integer> poker = new ArrayList<Integer>();//玩家手中的牌
    public ArrayList<Integer> pokerOut = new ArrayList<Integer>();//玩家出的牌
    public boolean clientStat=true;//客户端是否存在。true没有关闭，false关闭
    public String State="";
    public String id="";
    public String password="";
    public String table="";
    public String stateRegiste="";
    public String gamePrepareStr="";
    public String point="";
    public boolean pointUpdate=false;
    public boolean pukeUpdate=false;
    public boolean gamePrepare=false;
    public Play objectDapei=new Play();
    Robot r=new Robot();
    public Lock lock=new ReentrantLock();

    SheetInform sheetinform =null;
    TableNumber tableNumber=null;

    public ServicePlayer(Socket s, SheetInform sheetinform,TableNumber tableNumber) throws AWTException {
        this.s = s;
        this.sheetinform= sheetinform;
        this.tableNumber=tableNumber;
    }

    public void setRoomnumber(int roomnumber) {
        this.roomnumber = roomnumber;
    }

    //将从客户端得到的String转换为普通String
    public String changeString(String a){
        String str="";
        for (int i = 1; i < a.length(); i = i + 2) {
            str += a.charAt(i);
        }
        return str;
    }


    public String getPath() throws IOException {
        File directory = new File(".");
        return  directory.getCanonicalPath() +"\\src\\FightTheLandlord\\multithreading\\"+ "sjk.xls";
    }


    //登录验证
    public void log(String id,String password) throws IOException, BiffException {
        String line = null;
        boolean flag = false;//判断标记
        for (int i = 0; i < sheetinform.usrid.size(); i++) {
            String usrid = sheetinform.usrid.get(i);
            String usrpassword = sheetinform.usrpasswd.get(i);
            String usrscore =sheetinform.usrscore.get(i);
            String usrloginstate =sheetinform.usrloginstate.get(i);
            int Score = Integer.valueOf(usrscore).intValue();
            int loginstate = Integer.valueOf(usrloginstate).intValue();


//                  读取数据库(Use.txt)中数据
            if (id.equals( usrid)&&(loginstate==0)) {
                if (password.equals( usrpassword)) {
                    player.setName(usrid);
                    player.setScore(Score);
                    player.setNumber(i);
                    sheetinform.usrloginstate.set(i,"1");
                    flag=true;
                    break;
                }
            }                           //如果数据库和读取用户名和密码相同，则终止
        }
        output = s.getOutputStream();
        obj = new ObjectOutputStream(output);
        //写入目前状态
        obj.writeChars("returnLogin\n");
        if(flag){
            obj.writeChars("ok\n");//成功登陆
        }else if(!flag){
            obj.writeChars("no\n");//密码错误
            //已经登陆
        }
        obj.flush();

    }

    //注册验证
    public void registe(String id,String password) throws IOException, WriteException, BiffException {

        boolean flag = false;//判断标记
        for (int i = 0; i < sheetinform.usrid.size(); i++) {
            String usrid = sheetinform.usrid.get(i);
            String usrpassword = sheetinform.usrpasswd.get(i);
//                            读取数据库(Use.txt)中数据
            if (id.equals( usrid)) {
                flag=true;
                stateRegiste="lose\n";
                System.out.println("存在");
                break;
            }                           //如果数据库和读取用户名和密码相同，则终止
        }
        if(!flag){
//                    obj.writeBytes("不存在\n");
            stateRegiste="success\n";
            System.out.println("不存在" );
           //添加至数组
            sheetinform.usrid.add(id);
            sheetinform.usrpasswd.add(password);
            sheetinform.usrscore.add("0");
            sheetinform.usrloginstate.add("0");
            sheetinform.flag=true;
        }
        output = s.getOutputStream();
        obj = new ObjectOutputStream(output);
        //写入目前状态
        obj.writeChars("returnRegiste\n");
        obj.writeChars(stateRegiste);
        obj.flush();
    }

    public void judgeLogAndRegiste(String State,String id ,String password) throws BiffException, IOException, WriteException {
        System.out.println("id="+id+" password="+password);
        if (State.equals( "login")) {
            log(id,password);//登录验证
        }else if (State.equals( "registe")) {
            registe(id,password);//注册验证
        }
    }

    public void serviceRead() throws IOException, ClassNotFoundException {
        input = s.getInputStream();
        objin = new ObjectInputStream(input);//读取客户端发送的数据
        State=objin.readLine();
        State=changeString(State);
//        System.out.println("read data="+State);
        if (State.equals("login")){
            id=objin.readLine();
            id=changeString(id);
            password=objin.readLine();
            password=changeString(password);
        }else if(State.equals("registe")){
            id=objin.readLine();
            id=changeString(id);
            password=objin.readLine();
            password=changeString(password);
        }else if(State.equals("choosetable")){
//            table=objin.readLine();
            table=changeString(objin.readLine());
        }else if(State.equals("setGamePrepare")){
            gamePrepareStr=changeString(objin.readLine());
        }else if(State.equals("lead")){
            pokerOut=(ArrayList<Integer>)objin.readObject();
        }else if(State.equals("clientOver")){
            System.out.println("clientOver");
        }else if(State.equals("setPoint")){
            point=changeString(objin.readLine());
        }else if(State.equals("getTableNumber")){

        }else if(State.equals("null")){

        }
    }

    public void run() {//运行线程

        try {
            while (clientStat) {
                r.delay(20);
                serviceRead();//从客户端读取数据（单次全部数据）
                lock.lock();
                switch (State) {
                    case "login"://登录
                        //读取账户密码
                        judgeLogAndRegiste(State,id,password);
                        break;
                    case "registe"://注册
                        judgeLogAndRegiste(State,id,password);
                        break;
                    case "choosetable"://选择桌子
                        roomnumber=Integer.parseInt(table);
//                        System.out.println("roomnumber="+roomnumber);
                        if(roomnumber==0){
                            player.setRoomnumber(roomnumber);
                            output = s.getOutputStream();
                            obj = new ObjectOutputStream(output);
                            obj.writeChars("returnchoosetable\n");
                            obj.writeChars("ok\n");
                            obj.flush();
                        }
                        else if(tableNumber.number.get(roomnumber-1)<3){
                            tableNumber.number.set(roomnumber-1,tableNumber.number.get(roomnumber-1)+1);
                            player.setRoomnumber(roomnumber);
                            output = s.getOutputStream();
                            obj = new ObjectOutputStream(output);
                            obj.writeChars("returnchoosetable\n");
                            obj.writeChars("ok\n");
                            obj.flush();
                        }
                        else{
                            output = s.getOutputStream();
                            obj = new ObjectOutputStream(output);
                            obj.writeChars("returnchoosetable\n");
                            obj.writeChars("no\n");
                            obj.flush();
                        }
//                        System.out.println(table);
                        break;
                    case "getPlayer"://得到player信息
                        output = s.getOutputStream();
                        obj = new ObjectOutputStream(output);
                        obj.writeChars("sendPlayer\n");
                        obj.writeObject(player);
                        obj.flush();
                        break;
                    case "setGamePrepare"://改变准备状态
                        if(gamePrepareStr.equals("false")){
                            gamePrepare=false;
                        }else if(gamePrepareStr.equals("true")){
                            gamePrepare=true;
                        }
                        break;
                    case "lead":
                        poker=player.getCards();
                        output = s.getOutputStream();
                        obj = new ObjectOutputStream(output);

                        //添加判断条件，判断玩家出牌是否合法
                        System.out.println("pukerOut="+pokerOut);
                        if(objectDapei.fight(player.getCardsLead(),pokerOut)==1){
                            System.out.println("出牌成功");
                            player.setMultiplying(objectDapei.pukerScore(player.getMultiplying(),pokerOut));
                            pukeUpdate=true;
                            System.out.println(player.getPosition()+"update true");

                            System.out.println("puker="+poker);
                            for(int i=0;i<pokerOut.size();i++){
                                System.out.println("pokerOut.get(i)="+pokerOut.get(i));
                                poker.remove(pokerOut.get(i));
                            }
                            System.out.println("puker="+poker);
                            player.setCards(poker);
                            System.out.println(player.getPosition()+"update false");

                            obj.writeChars("nextTure\n");
                            obj.flush();
                            count=0;
                        }else {
                            System.out.println("出牌失败"+count);
                            count++;
                            obj.writeChars("againLead\n");
                            obj.flush();
                        }

                        break;
                    case "noLead":
                        pukeUpdate=true;

                        pokerOut.removeAll(pokerOut);
                        output = s.getOutputStream();
                        obj = new ObjectOutputStream(output);
                        obj.writeChars("nextTure\n");
                        obj.flush();

                        break;
                    case "clientOver":
                        clientStat=false;
                        sheetinform.usrscore.set(player.getNumber(),""+player.getScore());
                        sheetinform.usrloginstate.set(player.getNumber(),"0");
                        s.close();
                        break;
                    case "setPoint":
                        System.out.println("setPoint "+point);
                        pointUpdate=true;
                        if(point.equals("-1")) {
                            player.setPoint(-1);
                        }else if(point.equals("1")) {
                            player.setPoint(1);
                        }else if(point.equals("2")) {
                            player.setPoint(2);
                        }else if(point.equals("3")) {
                            player.setPoint(3);
                        }
                        break;
                    case "getTableNumber":
                        output = s.getOutputStream();
                        obj = new ObjectOutputStream(output);
                        obj.writeChars("returnTableNumber\n");
                        for( int i:tableNumber.number){
                            obj.writeInt(i);
                        }
                        obj.flush();
                        break;
                    case "Null":
                        break;
                }
                lock.unlock();
                //////////////////////////
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        } catch (BiffException e) {
            System.out.println("BiffException");
            e.printStackTrace();
        } catch (RowsExceededException e) {
            System.out.println("RowsExceededException");
            e.printStackTrace();
        } catch (WriteException e) {
            System.out.println("WriteException");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
            e.printStackTrace();
        }
    }
}
