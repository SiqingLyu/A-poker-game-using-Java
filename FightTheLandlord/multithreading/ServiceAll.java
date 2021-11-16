package FightTheLandlord.multithreading;
import java.awt.*;
import java.util.Date;
import java.util.ArrayList;
//线程 服务开始运行时创建 用于判断每个用户所在的房间号是否更改以及控制ServiceThree的创建
class ServiceAll implements Runnable {
    ArrayList <ServicePlayer> ListLog =new ArrayList<ServicePlayer>();//创建用户线程数组
    ArrayList <ServiceThree> ListThree =new ArrayList<ServiceThree>();//创建房间线程数组
    TableNumber tableNumber=new TableNumber();
    public int numberPlayer=-1;
    public boolean flag=true; //用于判断是否需要创建新的房间线程
    //外部调用向用户线程数组中增加元素
    public void addListLog(ServicePlayer a){
        this.ListLog.add(a);
        System.out.println("ServiceAll ListLog's list add");
    }
    public void updateTableNumber(){
        for(ServiceThree serviceThree :ListThree){
            tableNumber.setNumber(serviceThree.roomnumber-1,serviceThree.List.size());
        }
    }

    @Override
    public void run() {
        while(true){

            if(numberPlayer!=ListLog.size()){//当玩家人数改变时执行
                System.out.println("ALL ="+ListLog.size());
                numberPlayer=ListLog.size();
            }
            Robot r = null;
            try {
                r = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
            r.delay(   100   );//50ms延迟

            int sub=0;
            for(int i=0;i<ListLog.size()-sub;i++){
                if(!ListLog.get(i).clientStat){//当玩家退出之后需要删除
                    ListLog.remove(i);
                    i--;
                    sub++;
                    continue;
                }

                updateTableNumber();//更新房间人数

                if(ListLog.get(i).player.getRoomnumber()>0){//当有一个服务端房间不为0时
                    for(int j=0;j<ListThree.size();j++){//判断该房间的服务端存在吗？
                        if(ListThree.get(j).roomnumber==ListLog.get(i).player.getRoomnumber()){
                            flag=false;
                            if(ListThree.get(j).List.size()<3) {
                                ListThree.get(j).addList(ListLog.get(i));
                                ListThree.get(j).display();
                                ListLog.get(i).player.setRoomnumber(-ListLog.get(i).player.getRoomnumber());
                            }
                            break;
                        }
                    }
                    if(flag==true){//房间服务器不存在
                        ServiceThree ser =new ServiceThree(ListLog.get(i));
                        ListLog.get(i).player.setRoomnumber(-ListLog.get(i).player.getRoomnumber());
                        ListThree.add(ser);
                        new Thread (ser).start ();
                    }else{
                        flag=true;
                    }
//                    ListLog.remove(i);
                }//if(ListLog.get(i).roomnumber!=0)
            }//for i
        }//while
    }//run
}

class TableNumber{//用于统计不同房间内的玩家人数
    public ArrayList <Integer> number =new ArrayList<Integer>();
    TableNumber(){
        init();
    }
    public void init(){
        for(int i=0;i<9;i++)
        number.add(0);
    }
    public void setNumber(int indox,int num){
        number.set(indox,num);
    }
    public void display(){
        int t=0;
        for(int i:this.number){
            System.out.print("房间"+t+"人数="+i+"  ");
            t++;
        }
        System.out.println("");
    }
}