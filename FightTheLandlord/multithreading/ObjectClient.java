package FightTheLandlord.multithreading;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.ImageView;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;

import com.sun.corba.se.impl.ior.OldJIDLObjectKeyTemplate;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import com.sun.xml.internal.messaging.saaj.soap.JpegDataContentHandler;
import javafx.scene.layout.StackPane;
import jdk.nashorn.internal.objects.Global;

import java.io.*;
import java.net.Socket;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;


/****↓初始化类↓***/
public class ObjectClient implements ActionListener {

    String password_input;
    boolean flag_login;
    boolean flag_contain;
    String user_table;
    int table_num = 9;

    //GUI界面
    JPanel table_panel;
    JPanel play_panel;
    JPanel poker_panel;
    JLabel choose_label;
    JFrame login_frame;//基本面板
    JPanel login_panel;
    JTextField username_text;
    JPasswordField password_text;
    Button login_button;
    Button register_button;
    JButton[] tables = new JButton[table_num];
    JButton[] players = new JButton[3];
    JButton[] pokers = new JButton[20];
    JButton[] landlordPokers = new JButton[3];
    JButton[] hangUpAndCall = new JButton[5];
    JButton[] pokers_lead = new JButton[20];
    JButton prepare;
    JButton disprepare;
    JButton Back;
    JPanel landlordCards_panel;
    JLabel username_label;
    JLabel gamename_label;
    JLabel password_label;
    JLabel poker_begin;
    JFrame game_frame;
    JPanel game_panel;
    JLabel MyTime = new JLabel();
    JLabel formerPlayerInfo = new JLabel();  //其他玩家牌数
    JLabel latterPlayerInfo = new JLabel();
    JLabel whoIsWinner = new JLabel("我的牌出完啦！");
    JButton letGo = new JButton("出牌");
    JButton cantLetGo = new JButton("不要");                      //不要按钮的ui设计
    JLabel whoIsPlaying = new JLabel("正在出牌");                   //正在出牌、游戏结束等的游戏文本ui设计
    JLabel GameIsOver = new JLabel("游戏结束！");

    int formerPlayerCardnumFlag = 0;
    int latterPlayerCardnumFlag = 0;
    int thisPlayerCardnumFlag = 0;


    Deal f = new Deal();

    private int count = 0;
    Date date = new Date();
    long start = date.getTime();

    public ArrayList<Integer> pokerOut = new ArrayList<Integer>();
    Player player = null;
    int n ;
    ImageIcon[] poke_img;
    ImageIcon[] landlordImg = new ImageIcon[3];
    String[] img_name = f.getPoker();

    private int connectNumber = 0;
    //private  String ip = "192.168.43.228";
    private String ip = "127.0.0.1";
    private int port = 20000;
    public Socket s = new Socket(ip, port);
    private Lock lock = new ReentrantLock();


    private Scanner sc = new Scanner(System.in);//创建写入接口
    private InputStream input = null;//字节流输出
    public OutputStream output = null;//字节流输入
    private ObjectInputStream objin = null;
    private ObjectOutputStream obj = null;
    private String State = "";
    private String returnLoginStr = "";
    private String returnRegisteStr = "";
    private String returnChooseTableStr = "";
    public String strTure = "";
    public String gameStat="";
    TableNumber tableNumber = new TableNumber();
    public boolean deposit=false;//托管标志位
    //    private  boolean gameStart=false;
    public boolean gamePrepare = false;
    Robot r = new Robot();

    //将从客户端得到的String转换为普通String
    public String changeString(String a) {
        String str = "";
        for (int i = 1; i < a.length(); i = i + 2) {
            str += a.charAt(i);
        }
        return str;
    }


    /****↓客户端读取数据↓***/
    public void clientRead() throws IOException, ClassNotFoundException {
        input = s.getInputStream();
        objin = new ObjectInputStream(input);//读取客户端发送的数据
        State = objin.readLine();
        State = changeString(State);
//        System.out.println(State);
//        System.out.println("Stat="+State);
//        System.out.println("");

        if (State.equals("returnLogin")) {
            returnLoginStr = objin.readLine();
            returnLoginStr = changeString(returnLoginStr);
            System.out.println("Changer returnLoginStr=" + returnLoginStr);
        } else if (State.equals("returnRegiste")) {
            returnRegisteStr = objin.readLine();
            returnRegisteStr = changeString(returnRegisteStr);
        } else if (State.equals("sendPlayer")) {
            player = (Player) objin.readObject();
        } else if (State.equals("nextTure")) {
            strTure = "出牌成功";
        } else if (State.equals("againLead")) {
            JOptionPane.showMessageDialog(null, "不能这样出牌 ", "信息提示", JOptionPane.ERROR_MESSAGE);
            strTure = "不能这样出牌" + count;
            count++;
        } else if (State.equals("returnchoosetable")) {
            returnChooseTableStr = objin.readLine();
            returnChooseTableStr = changeString(returnChooseTableStr);
        } else if (State.equals("returnTableNumber")) {
            int t = 0;
            for (int i : tableNumber.number) {
                tableNumber.setNumber(t, objin.readInt());
                t++;
            }
//            tableNumber.display();
        } else if (State.equals("Null")) {

        }
    }


    /****↓动作监听函数，负责对游戏UI中的按钮和点击事件进行反应↓***/
    public void actionPerformed(ActionEvent a) {
        try {//开始访问服务器
            lock.lock();

            /////////////////////////////////////输出到服务端output和obj↓
            Object x = a.getSource();
            ///////////////////////逻辑处理↓
            if (a.getActionCommand() == "登录") {
                if (connectNumber < 3) {
                    System.out.println("请输入你的账户和密码：");
                    username_input = username_text.getText();
                    password_input = String.valueOf(password_text.getPassword());
                    username_input = username_input + "\n";
                    password_input = password_input + "\n";
                    System.out.println(username_input + password_input);
                    System.out.println("正在登陆...");
                    output = s.getOutputStream();
                    obj = new ObjectOutputStream(output);
                    obj.writeChars("login\n");
                    obj.writeChars(username_input);//写入账户
                    obj.writeChars(password_input);//写入密码
                    obj.flush();//发送obj


                    ////////////////////从服务端接收的数据
                    clientRead();
                    System.out.println("returnLoginStr=" + returnLoginStr);
                    if (returnLoginStr.equals("ok")) {
                        System.out.println("登陆成功");
                        output = s.getOutputStream();
                        obj = new ObjectOutputStream(output);
                        obj.writeChars("getPlayer\n");
                        obj.flush();//发送obj

                        clientRead();//读

                        game_frame.setVisible(true);
                        login_frame.setVisible(false);
                    } else {
                        connectNumber++;
                        System.out.println("密码错误");
                        JOptionPane.showMessageDialog(null, "密码错误，还有 " + (3 - connectNumber) + "次机会", "信息提示", JOptionPane.ERROR_MESSAGE);
                        if (connectNumber != 0 && connectNumber < 3) {
                            System.out.println("还有" + (3 - connectNumber) + "次机会");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "账号密码错误超过3次，无法连接服务器", "信息提示", JOptionPane.ERROR_MESSAGE);
                    System.out.println("账号密码错误超过3次，无法连接服务器");
                }
            }

            if (a.getActionCommand() == "注册") {
                username_input = username_text.getText();
                password_input = String.valueOf(password_text.getPassword());
                username_input = username_input + "\n";
                password_input = password_input + "\n";
//                                System.out.println(username_input+password_input);
                System.out.println("正在注册。。。");
                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("registe\n");
                obj.writeChars(username_input);//写入账户
                obj.writeChars(password_input);//写入密码
                obj.flush();//发送obj

                clientRead();//读

                System.out.println("Stat=" + State);
                System.out.println(returnRegisteStr);
            }

            if (Arrays.asList(tables).contains(x)) {
                for (int i = 0; i < 9; i++) {
                    if (tables[i] == x) {
                        int n = i + 1;
                        System.out.println("你已经选择" + String.valueOf(n) + "号桌");
                        user_table = String.valueOf(n) + "\n";
                        //                           obj = new ObjectOutputStream(output);
                        output = s.getOutputStream();
                        obj = new ObjectOutputStream(output);
                        obj.writeChars("choosetable\n");
                        obj.writeChars(user_table);//写入账户
                        obj.flush();//发送obj

                        clientRead();
                        if (returnChooseTableStr.equals("ok")) {
                            table_panel.setVisible(false);
                            choose_label.setVisible(false);

                            Back.setVisible(true);
                            prepare.setVisible(true);
                            poker_begin.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "房间人数已满", "信息提示", JOptionPane.ERROR_MESSAGE);
                            System.out.println("人员已满");
                        }
                    }
                }
            }

            if (a.getActionCommand() == "准备") {
                System.out.println("准备");
                gamePrepare = true;
                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("setGamePrepare\n");
                obj.writeChars("true\n");
                obj.flush();//发送obj

                prepare.setVisible(false);
                disprepare.setVisible(true);

            }
            if (a.getActionCommand() == "取消准备") {
                System.out.println("取消准备");
                gamePrepare = false;
                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("setGamePrepare\n");
                obj.writeChars("false\n");
                obj.flush();//发送obj

                disprepare.setVisible(false);
                prepare.setVisible(true);
            }

            if (Arrays.asList(pokers).contains(x)) {
                for (int i = 0; i < player.getCards().size(); i++) {
                    if (pokers[i] == x && !pokerOut.contains(player.getCards().get(i))) {
                        pokerOut.add(player.getCards().get(i));
                        pokers[i].setBounds((1500 - 160 - ((player.getCards().size() - 1) * (1500 - 160) / 19)) / 2 + i * ((1500 - 160) / 20), 0, 160, 220);

                    } else if (pokers[i] == x && pokerOut.contains(player.getCards().get(i))) {
                        pokerOut.remove(player.getCards().get(i));
                        pokers[i].setBounds((1500 - 160 - ((player.getCards().size() - 1) * (1500 - 160) / 19)) / 2 + i * ((1500 - 160) / 20), 30, 160, 220);

                    }
                }
            }
            if (a.getActionCommand() == "不要") {
                System.out.println("不要");
                date = new Date();
                start = date.getTime();
                String[] allPokers = f.getPoker();    //查看打出的元素
                for (int i = 0; i < pokerOut.size(); i++)
                    System.out.print(allPokers[pokerOut.get(i)] + "   ");
//                       发送pokerOut里的元素给服务器，服务器从player的cards中删除这些元素
                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("noLead\n");
                obj.flush();//发送obj
            }

            if (a.getActionCommand() == "出牌") {
                System.out.println("pukerOut=" + pokerOut);
                date = new Date();
                start = date.getTime();
                String[] allPokers = f.getPoker();    //查看打出的元素
                for (int i = 0; i < pokerOut.size(); i++)
                    System.out.print(allPokers[pokerOut.get(i)] + "   ");
//                       发送pokerOut里的元素给服务器，服务器从player的cards中删除这些元素
                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("lead\n");
                obj.writeObject(pokerOut);
                obj.flush();//发送obj

                clientRead();
                System.out.println(strTure);
            }

            if (a.getActionCommand() == "返回选座") {

                poker_panel.removeAll();
                play_panel.removeAll();                  //清空屏幕

                poker_panel.repaint();
                poker_panel.validate();
                play_panel.repaint();
                play_panel.validate();

                table_panel.setVisible(true);
                choose_label.setVisible(true);
                prepare.setVisible(false);
                Back.setVisible(false);
                disprepare.setVisible(false);
                poker_begin.setVisible(false);
                gamePrepare = false;

                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("choosetable\n");
                obj.writeChars("0\n");//写入账户
                obj.flush();//发送obj

                System.out.println("返回选座");
                //gameStop=true;
            }
            if (a.getActionCommand() == "不叫") {
                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("setPoint\n");
                obj.writeChars("-1\n");//写入分数
                obj.flush();//发送obj
                System.out.println("不叫");
            }
            if (a.getActionCommand() == "1分") {
                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("setPoint\n");
                obj.writeChars("1\n");//写入分数
                obj.flush();//发送obj
                System.out.println("1分");
            }
            if (a.getActionCommand() == "2分") {
                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("setPoint\n");
                obj.writeChars("2\n");//写入分数
                obj.flush();//发送obj
                System.out.println("2分");
            }
            if (a.getActionCommand() == "3分") {
                output = s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("setPoint\n");
                obj.writeChars("3\n");//写入分数
                obj.flush();//发送obj
                System.out.println("3分");
            }
            if (a.getActionCommand() == "托管") {
                if(deposit){
                    deposit=false;
                }else{
                    deposit=true;
                }
                System.out.println("托管");
            }
            lock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /****↓登录以及不变的UI内容设计↓***/
    public ObjectClient() throws IOException, AWTException {
        loginUInit();
        gameUInit();
    }

    public void loginUInit() {
        login_frame = new JFrame();//基本面板
        login_frame.addWindowListener(new MyWindowListener());
        login_panel = new JPanel();
        login_frame.add(login_panel);

        login_frame.setBounds(256, 256, 1200, 700);

        Color c_background = new Color(255, 255, 205);//颜色配置
        login_panel.setBackground(c_background);
        login_panel.setSize(2000, 1000);
        //label设计
        gamename_label = new JLabel("欢 乐 斗 地 主");
        gamename_label.setFont(new Font("Serif", Font.BOLD, 50));

        username_label = new JLabel("用户名：");
        username_label.setFont(new Font("Serif", Font.BOLD, 20));

        password_label = new JLabel("密码：");
        password_label.setFont(new Font("Serif", Font.BOLD, 20));

        username_label.setBounds(300, 200, 300, 50);
        password_label.setBounds(300, 350, 300, 50);
        gamename_label.setBounds(480, 50, 600, 100);

        //button设计
        JButton login_button = new JButton("登录");
        login_button.setFont(new Font("Serif", Font.BOLD, 20));
        JButton register_button = new JButton("注册");
        register_button.setFont(new Font("Serif", Font.BOLD, 20));

        login_button.setVisible(true);
        login_button.addActionListener(this);
        login_button.setBounds(450, 500, 120, 60);

        register_button.setVisible(true);
        register_button.addActionListener(this);
        register_button.setBounds(700, 500, 120, 60);

        //text设计
        username_text = new JTextField(0);
        username_text.setFont(new Font("Serif", Font.BOLD, 20));

        username_text.setBounds(400, 200, 500, 40);

        password_text = new JPasswordField(0);
        password_text.setFont(new Font("Serif", Font.BOLD, 20));

        password_text.setBounds(400, 350, 500, 40);

        //panel设计
        login_panel.add(username_text);
        login_panel.add(password_text);
        login_panel.add(username_label);
        login_panel.add(gamename_label);
        login_panel.add(password_label);
        login_panel.add(login_button);
        login_panel.add(register_button);
        login_panel.setLayout(null);


        //frame设计
        login_frame.setLayout(null);
        login_frame.setTitle("欢乐斗地主");
        login_frame.setResizable(true);
        login_frame.setVisible(true);
        login_frame.validate();
        login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
/****************************************↑游戏登录注册面板↑*****************************************/

    }

    public void gameUInit() {
        game_frame = new JFrame();//基本面板
        game_frame.addWindowListener(new MyWindowListener());

        gameFrameUInit();

        game_frame.setBounds(0, 0, 1920, 1800);
        game_frame.setLayout(null);
        game_frame.setVisible(false);
        game_frame.setResizable(true);      //设置用户可调节窗口
        game_frame.validate();              //是重新调整大小，前提是已经设置大小（已经调用了setSize()），且大小发生变化。如最大最小化窗口。
        game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
/***********************************↑打牌界面↑（还有下面的change_poker也包含）***************************************************/
    }

    public void gameFrameUInit() {
        login_frame.setTitle("欢乐斗地主");
        /**INIT**/
        HandUpAndCallUInit();
        TableUInit();
        preparePanelUInit();
        playPanelUInit();
        pokerPanelUInit();
        gamePanelUInit();
        landLordCardsPanelUInit();
        gameFrameAddPanel();
    }

    public void gameFrameAddPanel() {
        game_frame.add(Back);
        game_frame.add(landlordCards_panel);
        game_frame.add(poker_panel);
        game_frame.add(play_panel);
        game_frame.add(poker_begin);
        game_frame.add(prepare);
        game_frame.add(disprepare);
        game_frame.add(choose_label);
        game_frame.add(table_panel);                      //先加入的在页面上方
        game_frame.add(game_panel);

    }

    public void gamePanelUInit() {
        game_panel = new GameBgLoad("../img/game_bg.png");
        game_panel.setSize(2000, 1800);
        game_panel.setLayout(null);             //设置布局
    }

    public void playPanelUInit() {
        poker_panel = new JPanel();
        play_panel = new JPanel();
        play_panel.setLayout(null);
        play_panel.setBackground(null);
        play_panel.setOpaque(false);
        play_panel.setVisible(false);
        play_panel.setBounds(0, 0, 1900, 1800);
    }

    public void pokerPanelUInit() {
        poker_panel.setLayout(null);
        poker_panel.setBackground(null);
        poker_panel.setOpaque(false);
        poker_panel.setVisible(false);
        poker_panel.setBounds(300, 800, 1500, 300);
    }

    public void landLordCardsPanelUInit() {
        landlordCards_panel = new JPanel();
        landlordCards_panel.setLayout(null);
        landlordCards_panel.setBackground(null);
        landlordCards_panel.setOpaque(false);
        landlordCards_panel.setVisible(false);
        landlordCards_panel.setBounds(300, 50, 1500, 300);
    }

    public void preparePanelUInit() {
        choose_label = new JLabel("请选择你的牌桌");
        prepare = new JButton("准备");
        disprepare = new JButton("取消准备");
        Back = new JButton("返回选座");                     //返回按钮的ui设计
        poker_begin = new JLabel("等待其他玩家。。。");
        poker_begin.setFont(new Font("Serif", Font.BOLD, 70));
        poker_begin.setVisible(false);
        poker_begin.setBounds(600, 300, 1500, 300);
        poker_begin.setForeground(Color.WHITE);

        choose_label.setBounds(570, 80, 1500, 300);
        choose_label.setForeground(Color.WHITE);
        choose_label.setFont(new Font("Serif", Font.BOLD, 100));
        disprepare.setBounds(700, 800, 400, 100);
        disprepare.setVisible(false);
        disprepare.setForeground(Color.black);
        disprepare.setBackground(Color.YELLOW);
        disprepare.addActionListener(this);
        disprepare.setFont(new Font("Serif", Font.BOLD, 80));
        prepare.setBounds(700, 800, 400, 100);
        prepare.setVisible(false);
        prepare.setForeground(Color.WHITE);
        prepare.setBackground(Color.orange);
        prepare.setFont(new Font("Serif", Font.BOLD, 80));
        prepare.addActionListener(this);

        Back.setBounds(700, 600, 400, 100);
        Back.setVisible(false);
        Back.setForeground(Color.WHITE);
        Back.setBackground(Color.orange);
        Back.setFont(new Font("Serif", Font.BOLD, 70));
        Back.addActionListener(this);
    }

    public void HandUpAndCallUInit() {
        hangUpAndCall[0] = new JButton("不叫");
        hangUpAndCall[1] = new JButton("1分");
        hangUpAndCall[2] = new JButton("2分");
        hangUpAndCall[3] = new JButton("3分");
        hangUpAndCall[4] = new JButton("托管");

        for (int i = 0; i < 4; i++) {
            hangUpAndCall[i].setBounds(550 + 200 * i, 600, 150, 60);
            hangUpAndCall[i].setFont(new Font("Serif", Font.BOLD, 30));
            hangUpAndCall[i].setVisible(true);
            hangUpAndCall[i].setForeground(Color.WHITE);
            hangUpAndCall[i].setBackground(Color.pink);
            hangUpAndCall[i].addActionListener(this);
        }
        hangUpAndCall[4].setBounds(1400, 700, 100, 60);
        hangUpAndCall[4].setFont(new Font("Serif", Font.BOLD, 30));
        hangUpAndCall[4].setVisible(true);
        hangUpAndCall[4].setForeground(Color.WHITE);
        hangUpAndCall[4].setBackground(Color.orange);
        hangUpAndCall[4].addActionListener(this);
    }

    public void TableUInit() {
        table_panel = new JPanel();
        String table_image_url = "../img/table.png";

        for (int i = 0; i < 9; i++) {
            String table_name = String.valueOf(i + 1) + "号桌";
            tables[i] = new JButton(table_name + ":" + tableNumber.number.get(i) + "人");

            ImageIcon img = new ImageIcon(ObjectClient.class.getResource(String.valueOf(table_image_url)));
//                为把它缩小点，先要取出这个Icon的image ,然后缩放到合适的大小
            Image smallImage = img.getImage().getScaledInstance(150, 150, Image.SCALE_FAST);
//                再由修改后的Image来生成合适的Icon
            img = new ImageIcon(smallImage);
            tables[i].setIcon(img);
            tables[i].setContentAreaFilled(false);   //不绘制边框
            tables[i].setFont(new Font("Serif", Font.BOLD, 20));
            tables[i].setVisible(true);
            tables[i].addActionListener(this);
            tables[i].setSize(img.getIconWidth() + 50, img.getIconHeight());
            tables[i].setForeground(Color.WHITE);

        }
        table_panel.setLayout(new GridLayout(3, 3, 250, 100));
        table_panel.setBackground(null);
        table_panel.setOpaque(false);

        for (int i = 0; i < 9; i++) {
            table_panel.add(tables[i]);
        }

        table_panel.setBounds(200, 400, 1500, 500);
    }

    public void changeTablePlayerNumber() {
        for (int i = 0; i < 9; i++) {
            String table_name = String.valueOf(i + 1) + "号桌";
            tables[i].setText(table_name + ":" + tableNumber.number.get(i) + "人");
        }
    }

    /****以下是打牌界面相关UI函数****/

    public void changeTime() {
        long time = player.getTime() / 1000;
        long timeLimit = 30;
        long timeRest = timeLimit - time;
        timeRest = timeRest>0?timeRest:0;
            MyTime.setVisible(true);
            MyTime.setText("出牌时间：" + String.valueOf(timeRest) + "秒");

    }

    public void timeUInit() {
        if (player.getPosition() == player.getPlayerTurn()){
            MyTime.setBounds(1600, 650, 400, 100);
        }
        else if((player.getPosition() + 1) % 3 == player.getPlayerTurn()){
            MyTime.setBounds(1500,100,400,100);
        }else{
            MyTime.setBounds(100,100,400,100);
        }
        MyTime.setFont(new Font("Serif", Font.PLAIN, 30));
        MyTime.setForeground(Color.WHITE);

    }

    public void ClearScreen(){
        poker_panel.removeAll();
        play_panel.removeAll();                  //清空屏幕

        poker_panel.repaint();
        poker_panel.validate();
        play_panel.repaint();
        play_panel.validate();
        pokerOut.removeAll(pokerOut);
    }

    public void RepaintScreen(){
        poker_panel.repaint();
        poker_panel.validate();
        play_panel.repaint();
        play_panel.validate();
    }

    public void UIoflandLordPoke(){
        if(!player.getRole().equals("no")){
            for(int i = 0 ; i < 3 ; i ++){
                ImageIcon img1 = new ImageIcon(ObjectClient.class.getResource(String.valueOf("../img/" + img_name[player.getLandlordCards().get(i)] + ".png")));
//                为把它缩小点，先要取出这个Icon的image ,然后缩放到合适的大小
                Image smallImage1 = img1.getImage().getScaledInstance(100, 150, Image.SCALE_FAST);
//                再由修改后的Image来生成合适的Icon
//            System.out.println(n+"   "+i+"    "+img_name[player.getCards().get(i)]);
                landlordImg[i] = new ImageIcon(smallImage1);
                landlordPokers[i] = new JButton();
                landlordPokers[i].setIcon(landlordImg[i]);
                landlordPokers[i].setContentAreaFilled(false);
                landlordPokers[i].setVisible(true);
                landlordPokers[i].addActionListener(this);
                landlordPokers[i].setSize(landlordImg[0].getIconWidth(), landlordImg[0].getIconHeight());
                landlordPokers[i].setForeground(Color.BLACK);
                landlordPokers[i].setBounds( 800 + i * ((1500 - 160) / 15), 30, 120, 180);
            }
            for (int i = 2; i >= 0; i--) {
                play_panel.add(landlordPokers[i]);
            }
        }
    }

    public void UIofPlayer(){

        String player_icon_url = "../img/user.png";
        String landlordIconUrl = "../img/landlord.png";
        String farmerIconUrl = "../img/farmer.png";
        String[] player_names = new String[3];                 //玩家姓名
        ImageIcon[] player_icon = new ImageIcon[3];            //玩家头像
        JLabel MyInfo = new JLabel("我的积分：" + player.getScore());
        JLabel FormerPlayerInfo = new JLabel("上家牌数： " + player.getFormerPokerNumbers() + "    上家积分：" + player.getFormerScore());       //其他玩家牌数
        JLabel LatterPlayerInfo = new JLabel("下家牌数： " + player.getLatterPokerNumbers() + "    上家积分：" + player.getLatterScore());
        System.out.println("上家牌数： " + player.getFormerPokerNumbers());
        System.out.println("下家牌数： " + player.getLatterPokerNumbers());
        MyInfo.setForeground(Color.WHITE);
        MyInfo.setFont(new Font("Serif", Font.BOLD, 20));
        FormerPlayerInfo.setForeground(Color.WHITE);
        FormerPlayerInfo.setFont(new Font("Serif", Font.BOLD, 20));
        LatterPlayerInfo.setForeground(Color.WHITE);
        LatterPlayerInfo.setFont(new Font("Serif", Font.BOLD, 20));

//        for(int i = 0 ; i < 3 ; i ++){
//            player_names[  ] = player.getName();
//        }
        player_names[0] = player.getName();
        player_names[1] = player.getLatterName();
        player_names[2] = player.getFormerName();

        ImageIcon[] img = new ImageIcon[3];
        img[0] = new ImageIcon(ObjectClient.class.getResource(String.valueOf(player_icon_url)));
//            System.out.println(Arrays.toString(player_names));
        if (player.getRole().contains("landlord")) {
            img[0] = new ImageIcon(ObjectClient.class.getResource(String.valueOf(landlordIconUrl)));
        }
        if (player.getRole().contains("farmer")) {
            img[0] = new ImageIcon(ObjectClient.class.getResource(String.valueOf(farmerIconUrl)));
        }
        img[1] = new ImageIcon(ObjectClient.class.getResource(String.valueOf(player_icon_url)));
//            System.out.println(Arrays.toString(player_names));
        if (player.getLatterRole().contains("landlord")) {
            img[1] = new ImageIcon(ObjectClient.class.getResource(String.valueOf(landlordIconUrl)));
        }
        if (player.getLatterRole().contains("farmer")) {
            img[1] = new ImageIcon(ObjectClient.class.getResource(String.valueOf(farmerIconUrl)));
        }
        img[2] = new ImageIcon(ObjectClient.class.getResource(String.valueOf(player_icon_url)));
//            System.out.println(Arrays.toString(player_names));
        if (player.getFormerRole().contains("landlord")) {
            img[2] = new ImageIcon(ObjectClient.class.getResource(String.valueOf(landlordIconUrl)));
        }
        if (player.getFormerRole().contains("farmer")) {
            img[2] = new ImageIcon(ObjectClient.class.getResource(String.valueOf(farmerIconUrl)));
        }


        for (int i = 0; i < 3; i++) {                                                                    //将玩家姓名和头像加入panel
            Image smallImage = img[i].getImage().getScaledInstance(80, 80, Image.SCALE_FAST);
//                再由修改后的Image来生成合适的Icon
            player_icon[i] = new ImageIcon(smallImage);
            players[i] = new JButton(player_names[i]);
            players[i].setIcon(player_icon[i]);
            players[i].setContentAreaFilled(false);   //不绘制边框
            players[i].setFont(new Font("Serif", Font.BOLD, 30));
            players[i].setVisible(true);
            players[i].addActionListener(this);
            players[i].setSize(player_icon[i].getIconWidth() + 50, player_icon[i].getIconHeight());
            players[i].setForeground(Color.BLACK);
            play_panel.add(players[i]);

        }

        play_panel.add(MyInfo);
        play_panel.add(FormerPlayerInfo);             //加入玩家牌数
        play_panel.add(LatterPlayerInfo);
        MyInfo.setBounds(100,700,400,100);
        FormerPlayerInfo.setBounds(100, 300, 400, 100);
        LatterPlayerInfo.setBounds(1500, 300, 400, 100);
        players[2].setBounds(100, 200, 200, 100);
        players[1].setBounds(1500, 200, 200, 100);
        players[0].setBounds(100, 800, 200, 100);

        play_panel.add(MyTime);
    }

    public void UIofPlayMess(){
        letGo = new JButton("出牌");                          //出牌按钮的ui设计
        letGo.setBounds(600, 700, 200, 50);
        letGo.setForeground(Color.WHITE);
        letGo.setBackground(Color.orange);
        letGo.setFont(new Font("Serif", Font.BOLD, 50));
        letGo.addActionListener(this);


        cantLetGo = new JButton("不要");                      //不要按钮的ui设计
        cantLetGo.setBounds(900, 700, 200, 50);
        cantLetGo.setForeground(Color.WHITE);
        cantLetGo.setBackground(Color.orange);
        cantLetGo.setFont(new Font("Serif", Font.PLAIN, 50));
        cantLetGo.addActionListener(this);


        whoIsPlaying = new JLabel("正在出牌");                   //正在出牌、游戏结束等的游戏文本ui设计
        whoIsPlaying.setForeground(Color.WHITE);
        whoIsPlaying.setBackground(Color.GRAY);
        whoIsPlaying.setFont(new Font("Serif", Font.BOLD, 30));

        GameIsOver = new JLabel("游戏结束！");
        GameIsOver.setBounds(800, 300, 1500, 300);
        GameIsOver.setForeground(Color.YELLOW);
        GameIsOver.setBackground(Color.BLUE);
        GameIsOver.setFont(new Font("Serif", Font.BOLD, 60));

        whoIsWinner = new JLabel("我的牌出完啦！");
        whoIsWinner.setForeground(Color.WHITE);
        whoIsWinner.setBackground(Color.BLUE);
        whoIsWinner.setFont(new Font("Serif", Font.PLAIN, 50));
    }

    public void UIofGameOver() {
        if (player.getCards().size() == 0) {
            //当前玩家胜利
            whoIsWinner.setText("胜利！");
            whoIsWinner.setBounds(200, 700, 400, 100);
            JOptionPane.showMessageDialog(null, " 胜  利 ！ ", "信息提示", JOptionPane.INFORMATION_MESSAGE);

        } else if (player.getLatterPokerNumbers() == 0) {
            //下家胜利
            if (player.getLatterRole().equals(player.getRole())) {
                JOptionPane.showMessageDialog(null, " 胜  利 ！ ", "信息提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, " 失   败 ！ ", "信息提示", JOptionPane.INFORMATION_MESSAGE);
            }
            whoIsWinner.setBounds(1200, 500, 400, 100);
        } else if (player.getFormerPokerNumbers() == 0) {
            //上家胜利
            if (player.getFormerRole().equals(player.getRole())) {
                JOptionPane.showMessageDialog(null, " 胜  利 ！ ", "信息提示", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, " 失   败 ！ ", "信息提示", JOptionPane.INFORMATION_MESSAGE);
            }
            whoIsWinner.setBounds(200, 500, 400, 100);
        }
        gamePrepare = false;
        prepare.setVisible(true);
        Back.setVisible(true);
        poker_panel.setVisible(false);
        play_panel.add(whoIsWinner);

    }

    public void UIofGameStillOn(){
        play_panel.add(hangUpAndCall[4]);
        if (player.getPosition() == player.getPlayerTurn()) {//如果当前是玩家回合，显示打牌按钮
            System.out.println("pos="+player.getPosition()+" turn="+player.getPlayerTurn());
//            if(player.getCards().size() >= 17){
            System.out.println("role=" + player.getRole());
            if (player.getRole().equals("no")) {
                play_panel.add(hangUpAndCall[0]);
                for (int i = player.getPointAll() + 1; i < 4; i++)
                    play_panel.add(hangUpAndCall[i]);
                gameStat="";
            } else {
                gameStat="play";
                if (player.getCardsLead().size() != 0) {
                    letGo.setBounds(600, 700, 200, 50);
                    play_panel.add(cantLetGo);
                } else {
                    letGo.setBounds(900, 700, 200, 50);
                }
                play_panel.add(letGo);
            }

        } else if ((player.getPosition() + 1) % 3 == player.getPlayerTurn()) {
            whoIsPlaying.setBounds(1500, 50, 200, 50);
            play_panel.add(whoIsPlaying);
        } else {
            whoIsPlaying.setBounds(100, 50, 200, 50);
            play_panel.add(whoIsPlaying);
        }
    }

    public void UIofPokerCards(){
        n = player.getCards().size();
        poke_img  = new ImageIcon[n];
        for (int i = 0; i < n; i++) {                     //显示玩家的牌
            ImageIcon img1 = new ImageIcon(ObjectClient.class.getResource(String.valueOf("../img/" + img_name[player.getCards().get(i)] + ".png")));
//                为把它缩小点，先要取出这个Icon的image ,然后缩放到合适的大小
            Image smallImage1 = img1.getImage().getScaledInstance(150, 200, Image.SCALE_FAST);
//                再由修改后的Image来生成合适的Icon
//            System.out.println(n+"   "+i+"    "+img_name[player.getCards().get(i)]);
            poke_img[i] = new ImageIcon(smallImage1);
            pokers[i] = new JButton();
            pokers[i].setIcon(poke_img[i]);
            pokers[i].setContentAreaFilled(false);
            pokers[i].setVisible(true);
            pokers[i].addActionListener(this);
            pokers[i].setSize(poke_img[0].getIconWidth(), poke_img[0].getIconHeight());
            pokers[i].setForeground(Color.BLACK);
            pokers[i].setBounds((1500 - 160 - ((n - 1) * (1500 - 160) / 19)) / 2 + i * ((1500 - 160) / 20), 30, 160, 220);
        }
        for (int i = n - 1; i >= 0; i--) {
            poker_panel.add(pokers[i]);
        }

        //显示桌面上的牌
        int pokerPosition = 0;
        if (player.getPosition() == player.getPlayerTurn()) {
            pokerPosition = 0;
        }
        if ((player.getPosition() + 1) % 3 == player.getPlayerTurn()) {
            pokerPosition = 1;
        }
        if ((player.getPosition() + 2) % 3 == player.getPlayerTurn()) {
            pokerPosition = 2;
        }

        if (player.getPlayerTurn() == (player.getPosition() + 1) % 3 && thisPlayerCardnumFlag == player.getCards().size()) {
            pokerPosition = 0;
        }
        thisPlayerCardnumFlag = player.getCards().size();
        if (player.getPlayerTurn() != player.getPosition() + 1) {

            /**其他玩家显示“不要”**/
            JLabel formerCantLetGo = new JLabel("不要");
            formerCantLetGo.setForeground(Color.WHITE);
            formerCantLetGo.setBackground(Color.BLUE);
            formerCantLetGo.setVisible(true);
            formerCantLetGo.setFont(new Font("Serif", Font.PLAIN, 50));
            if (player.getPlayerTurn() == player.getPosition() && formerPlayerCardnumFlag == player.getFormerPokerNumbers()) {
                formerCantLetGo.setBounds(100, 500, 400, 100);
                play_panel.add(formerCantLetGo);
                pokerPosition = 2;

            }
            JLabel latterCantLetGo = new JLabel("不要");
            latterCantLetGo.setForeground(Color.WHITE);
            latterCantLetGo.setBackground(Color.BLUE);
            latterCantLetGo.setVisible(true);
            latterCantLetGo.setFont(new Font("Serif", Font.PLAIN, 50));
            if (player.getPlayerTurn() == (player.getPosition() + 2) % 3 && latterPlayerCardnumFlag == player.getLatterPokerNumbers()) {
                latterCantLetGo.setBounds(1500, 500, 400, 100);
                play_panel.add(latterCantLetGo);
                pokerPosition = 1;

            }

            formerPlayerCardnumFlag = player.getFormerPokerNumbers();
            latterPlayerCardnumFlag = player.getLatterPokerNumbers();

        }

        int m = player.getCardsLead().size();
        ImageIcon[] pokeOut_img = new ImageIcon[m];

        for (int i = 0; i < m; i++) {
            ImageIcon img1 = new ImageIcon(ObjectClient.class.getResource(String.valueOf("../img/" + img_name[player.getCardsLead().get(i)] + ".png")));
//                为把它缩小点，先要取出这个Icon的image ,然后缩放到合适的大小
            Image smallImage1 = img1.getImage().getScaledInstance(100, 150, Image.SCALE_FAST);
//                再由修改后的Image来生成合适的Icon

            pokeOut_img[i] = new ImageIcon(smallImage1);
            pokers_lead[i] = new JButton();
            pokers_lead[i].setIcon(pokeOut_img[i]);
            pokers_lead[i].setContentAreaFilled(false);
            pokers_lead[i].setVisible(true);
            pokers_lead[i].addActionListener(this);
            pokers_lead[i].setSize(pokeOut_img[0].getIconWidth(), pokeOut_img[0].getIconHeight());
            pokers_lead[i].setForeground(Color.BLACK);
            pokers_lead[i].setBounds(pokerPosition * ((1500 - 160 - ((m - 1) * (1500 - 160) / 19)) / 2 + 200) + i * ((1500 - 160) / 20), 400, 120, 180);
        }
        for (int i = m - 1; i >= 0; i--) {
            play_panel.add(pokers_lead[i]);
        }
    }

        /****↓需要随着打牌回合改变的UI内容设计↓***/
    public void change_poker() {  //随着打牌进行的UI变化
        ClearScreen();
        timeUInit();
        UIoflandLordPoke();
        UIofPlayer();
        UIofPlayMess();
        /***         当某玩家手牌为零则表明游戏结束，由于一开始服务器未分发牌时，玩家牌数也是0，所以还要判断一下场上扑克数不为零         ***/
        if (!player.isGameStart()) {
            UIofGameOver();
        }
        /***     游戏未结束，则显示各种按钮和游戏文本      ***/
        else {
            UIofGameStillOn();
        }
        UIofPokerCards();
        RepaintScreen();
    }

    class MyWindowListener extends WindowAdapter {
        public void sendOver() throws IOException {
            output = s.getOutputStream();
            obj = new ObjectOutputStream(output);
            obj.writeChars("clientOver\n");
            obj.flush();//发送obj
        }

            @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            System.out.println("window is closed !");
            try {
                lock.lock();
                sendOver();
//                lock.unlock();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /****↓主函数，进行打牌循环、界面切换、与服务器的交互↓****/
    public static void main(String[] args) throws IOException, ClassNotFoundException, AWTException {
        ObjectClient client = new ObjectClient();
        Date date = new Date();
        long start = date.getTime();
        long end = date.getTime();
        OutputStream output = null;//字节流输入
        int playerTure = 0;
        Deal dealCards = new Deal();
        Robot r = new Robot();
        Play play=new Play();
        int flagTurn = -1;//客户端当前回合
        ObjectOutputStream obj = null;
        boolean pointFlag = true;
        boolean havePutCard=false;//标志本回合是否出过牌
        while (true) {
            r.delay(30);
            if(client.returnLoginStr.equals("ok")){
                client.lock.lock();
                output = client.s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("getTableNumber\n");
                obj.flush();//发送obj
                client.clientRead();
                client.lock.unlock();
                client.changeTablePlayerNumber();
            }
            while (client.gamePrepare) {//进入准备状态
                r.delay(30);
                client.lock.lock();
                output = client.s.getOutputStream();
                obj = new ObjectOutputStream(output);
                obj.writeChars("getPlayer\n");
                obj.flush();//发送obj  用于获取player

                client.clientRead();//读取是否开始游戏

                flagTurn = -1;//客户端当前回合
                if (client.player.isGameStart()) {
                    client.poker_begin.setVisible(false);
                    client.play_panel.setVisible(true);
                    client.poker_panel.setVisible(true);
                    client.disprepare.setVisible(false);
                    client.Back.setVisible(false);
                    pointFlag = true;
                }
                client.lock.unlock();

                date = new Date();
                end = date.getTime();
                client.gameStat="";//用于区分叫分和打牌
                client.deposit=false;//初始化托管按钮
                havePutCard=false;//初始化出牌记录
                while (client.player.isGameStart()) {//开始游戏
                    r.delay(15);
                    client.lock.lock();
                    output = client.s.getOutputStream();
                    obj = new ObjectOutputStream(output);
                    obj.writeChars("getPlayer\n");
                    obj.flush();//发送obj  用于获取player

                    client.clientRead();//得到player，存在client.player中
                    client.changeTime();

                    //超时出牌
                    if(havePutCard==false){
                        if((client.deposit &&client.player.getPosition()==client.player.getPlayerTurn()&&client.player.getTime()>1*1000)
                                ||(client.player.getPosition()==client.player.getPlayerTurn() &&client.player.getTime()>30*1000)){//超时出牌
                            havePutCard=true;
                            if(client.gameStat.equals("")){//叫分
                                output = client.s.getOutputStream();
                                obj = new ObjectOutputStream(output);
                                obj.writeChars("setPoint\n");
                                obj.writeChars("-1\n");//写入分数
                                obj.flush();//发送obj
                                System.out.println("不叫");
                            }else{//打牌
//                            client.pokerOut=play.AIPlay(client.player.getCards(),client.player.getCardsLead());
                                if(client.player.getCardsLead().size()!=0){
                                    output = client.s.getOutputStream();
                                    obj = new ObjectOutputStream(output);
                                    obj.writeChars("noLead\n");
                                    obj.flush();//发送obj
                                }else{
                                    client.pokerOut.removeAll(client.pokerOut);
                                    client.pokerOut.add(client.player.getCards().get(client.player.getCards().size()-1));
                                    output = client.s.getOutputStream();
                                    obj = new ObjectOutputStream(output);
                                    obj.writeChars("lead\n");
                                    obj.writeObject(client.pokerOut);
                                    obj.flush();//发送obj

                                    client.clientRead();
                                    System.out.println(client.strTure);
                                }
                            }
                        }
                    }

                    if (client.player.getPlayerTurn() != flagTurn || (client.player.getPointAll() == 3 && pointFlag)) {//检测服务端发送过来的player的回合是否等于客户端回合
                        havePutCard=false;
                        if (client.player.getPointAll() == 3) {
                            pointFlag = false;
                        }
                        if (!client.player.getRole().equals("no")) {
                            System.out.println("role=" + client.player.getRole());
                        }
                        System.out.println("turn=" + client.player.getPlayerTurn()+" pos="+client.player.getPosition());
                        date = new Date();
                        start = date.getTime();
                        System.out.println("wiat server client times=" + (start - client.start) + "ms");
                        client.change_poker();
                        flagTurn = client.player.getPlayerTurn();//更新客户端回合（首先服务端更新）
                        date = new Date();
                        end = date.getTime();
                        System.out.println("wait deal client times=" + (end - start) + "ms");
                        System.out.println("Multiplying=" + client.player.getMultiplying());
                        System.out.println("score="+client.player.getScore());
                    }
                    client.lock.unlock();
                }//gameStart
//                System.out.println("play over");
            }//gamePrepare
        }//true
    }
}
