package FightTheLandlord.multithreading;
import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.InputStream;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.util.Date;


import jxl.write.Number;
import jxl.write.DateTime;
import jxl.write.biff.RowsExceededException;
///////////////////////////////////////////////
public class ObjectServer {
    public ObjectServer() throws IOException, BiffException {
    }
    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        Socket s = null;
        ServiceAll All = new ServiceAll();//全体玩家服务线程
        new Thread (All).start ();
        ServiceSave save =new ServiceSave();//保存数据线程
        new Thread(save).start();
        try {
            server = new ServerSocket (20000);
            System.out.println ("监听用户连接......");
            while (true) {//客户端的端口正确才能接下去访问，否则要么等待要么报错
                s = server.accept ();//监听,阻塞连接
                System.out.println ("已监听到客户连接到[远程主机" + s.getRemoteSocketAddress ()
                        + ":端口" +
                        s.getPort () + "]");
                ServicePlayer Ser= new ServicePlayer (s,save.sheetinform,All.tableNumber);
                All.addListLog(Ser);
                new Thread (Ser).start ();
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}

