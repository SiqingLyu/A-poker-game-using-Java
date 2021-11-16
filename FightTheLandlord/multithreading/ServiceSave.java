package FightTheLandlord.multithreading;
import jxl.Sheet;
import jxl.Workbook;

import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public  class ServiceSave implements Runnable {

    private Workbook wb;//得到wb（整个excel文件的数据）

    //得到路径
    public String getPath() throws IOException {//得到数据库路径
        File directory = new File(".");
        return  directory.getCanonicalPath() +"\\src\\FightTheLandlord\\multithreading\\"+ "sjk.xls";
    }

    //初始化wb
    public Workbook getwb() throws IOException, BiffException {
        System.out.println(getPath());
        InputStream is = new FileInputStream(getPath());
        return Workbook.getWorkbook(is);
    }

     {
        try {
            wb = getwb();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    //get sheet：0 data
    public Sheet sheet=wb.getSheet(0);

    public SheetInform sheetinform= new SheetInform();

    //保存
    public void save () throws WriteException, IOException {
        WritableWorkbook book = null;
        try {
            book = Workbook.createWorkbook(new File(getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        WritableSheet sheet = book.createSheet("sheet", 0);
        System.out.println("\n******Save sjk******\n");
        sheetinform.flag = false;
        for (int i = 0; i < sheetinform.getlength(); i++) {
            String a = sheetinform.usrid.get(i);
            String b = sheetinform.usrpasswd.get(i);
            String c = sheetinform.usrscore.get(i);
            // String d=SheetInform.usrloginstate.get(i);
            Label label = new Label(0, i, a);
            // 将定义好的单元格添加到工作表中

            sheet.addCell(label);
            label = new Label(1, i, b);
            sheet.addCell(label);
            label = new Label(2, i, c);
            sheet.addCell(label);
        }
        book.write();
        book.close();
    }

    //显示
    public void display(){
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        for (int i = 0; i < sheetinform.getlength(); i++) {
            String usrid = sheetinform.usrid.get(i);
            String usrpassword = sheetinform.usrpasswd.get(i);
            String usrscore = sheetinform.usrscore.get(i);
            String usrloginstate = sheetinform.usrloginstate.get(i);
            System.out.println(usrid +" "+ usrpassword + " " +usrscore + " "+ usrloginstate);
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    //定时器任务
    public class Task extends TimerTask {
        public void run() {
            try {
                if(sheetinform.flag){
                    System.out.println("saveing.....");
                    display();
                    save();
                    System.out.println("save over");
                }else{
                    System.out.println("sheetinform isn't change");
                }
            } catch (WriteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override//实际运行函数
    public void run() {
        sheetinform.init(sheet);//初始化sheetinform
        Timer timer = new Timer();
        timer.schedule(new Task(), 120 * 1000,120 * 1000);
    }
}

class SheetInform{
    public boolean flag=true;//用于判断
    public ArrayList<String> usrid=new ArrayList<String>();//玩家id
    public ArrayList<String> usrpasswd=new ArrayList<String>();//玩家密码
    public ArrayList<String> usrscore=new ArrayList<String>();//玩家积分
    public ArrayList<String> usrloginstate=new ArrayList<String>();//玩家登陆标志位

    public int  getlength(){
        return usrid.size();
    }

    public void init(Sheet sheet){
        for(int i=0;i<sheet.getRows();i++){
            usrid.add(sheet.getRow(i)[0].getContents());
            usrpasswd.add(sheet.getRow(i)[1].getContents());
            usrscore.add(sheet.getRow(i)[2].getContents());
            usrloginstate.add("0");
        }
    }
}


