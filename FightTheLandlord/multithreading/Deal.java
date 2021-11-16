package FightTheLandlord.multithreading;
import java.util.ArrayList;
import java.util.Comparator;

public class Deal {
    public int count=54;
    public String[] poker=null;
    public ArrayList <Integer> ary1 =new ArrayList<Integer>();
    public ArrayList <Integer> ary2 =new ArrayList<Integer>();
    public ArrayList <Integer> ary3 =new ArrayList<Integer>();
    public ArrayList <Integer> ary4 =new ArrayList<Integer>();
    public ArrayList<Integer> getAry1() {
        return ary1;
    }
    public ArrayList<Integer> getAry2() {
        return ary2;
    }
    public ArrayList<Integer> getAry3() {
        return ary3;
    }
    public ArrayList<Integer> getAry4() {
        return ary4;
    }

    public String[] getPoker() {
        return poker;
    }

    Deal(){
        poker=initpuker();//初始化poker
        done();//进行发牌，码牌
    }

    public void display(ArrayList<Integer> puke){
        int i;
        for(i=0;i<puke.size();i++)
            System.out.print(poker[puke.get(i)] + " ");
        System.out.print("\n");
    }

    public String[] initpuker(){
        String[] nums = {"3","4","5","6","7","8","9","10","J","Q","K","A","2"};
        String[] colors = {"方块","草花","红桃","黑桃"};
        String[] poker = new String[54];
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 13; j++){
                poker[i*13+j] = colors[i]+nums[j];
            }
        }
        poker[52] = "小王";
        poker[53] = "大王";
        return poker;
    }

    public void done(){
        ArrayList<Integer> temp = new ArrayList<Integer>();
        ArrayList<Integer> list = new ArrayList<Integer>();
        int it;
        for (int i = 0; i < count; i++) {
            temp.add(i);
        }
        int index = 0;
        while (list.size()<count)
        {
            it = temp.get((int) (Math.random() * count));
            do {
                it=(++it)%54;
            } while(list.contains(it));
            list.add(it);
        }
        // /输出数组的元素
        /*System.out.print("数组的元素:");
        for(int i=0;i<ary.length;i++)
            System.out.print(ary[i] + " ");*/
        for(int i = 0,j = 0; i < list.size()-3; i+=3,j++) {
            ary1.add(list.get(i));
            ary2.add(list.get(i+1));
            ary3.add(list.get(i+2));
        }
        ary4.add(list.get(51));
        ary4.add(list.get(52));
        ary4.add(list.get(53));
        Comparator comp=new MyComp();
        ary1.sort(comp);
        ary2.sort(comp);
        ary3.sort(comp);
        ary4.sort(comp);
//        display(ary1);
//        display(ary2);
//        display(ary3);
//        display(ary4);
//        landlord=(int) (Math.random() * (count-3));
//        System.out.println(poker[ary[landlord]]);
//        System.out.println(landlord%3+1 + "优先叫地主");
    }

    public static void main(String[] args) {
        Deal test =new Deal();
    }
}
