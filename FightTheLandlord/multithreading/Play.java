package FightTheLandlord.multithreading;
import java.util.*;
public class Play {
    public String[] nums = {"3","4","5","6","7","8","9","10","J","Q","K","A","2"};
    public String[] colors = {"方块","草花","红桃","黑桃"};
    public String[] poker = new String[54];
    public Comparator comp=new MyComp();

    Play(){

    }

    public int point(Integer a){
        if (a<52) return a.intValue()%13+1;
        else return a.intValue();
    }
    public int shunzi(ArrayList <Integer> a,int b,int c){
        for(int i=b;i<c-1;i++)
            if (point(a.get(i))-point(a.get(i+1))!=1||point(a.get(i))>12) return 0;
        return point(a.get(b));
    }
    public int liandui(ArrayList <Integer> a,int b,int c){
        for(int i=b;i<c-2;i+=2){
            if (point(a.get(i))>12) return 0;
            if (point(a.get(i))-point(a.get(i+2))!=1||point(a.get(i+1))-point(a.get(i+3))!=1||point(a.get(i))!=point(a.get(i+1)))
                return 0;
        }
        return point(a.get(b));
    }
    public int sanshun(ArrayList <Integer> a,int b,int c){
        for(int i=b;i<c-3;i+=3){
            if (point(a.get(i))>12) return 0;
            if (point(a.get(i))-point(a.get(i+3))!=1||point(a.get(i))!=point(a.get(i+1))||point(a.get(i+1))!=point(a.get(i+2)))
                return 0;
        }
        if (point(a.get(c-3))!=point(a.get(c-2))||point(a.get(c-2))!=point(a.get(c-1))) return 0;
        return point(a.get(b));
    }
    public int bomb(ArrayList <Integer> a,int b){
        if (point(a.get(b))==point(a.get(b+1))&&point(a.get(b+1))==point(a.get(b+2))&&point(a.get(b+2))==point(a.get(b+3)))
            return point(a.get(b));
        return 0;
    }
    public int feiji(ArrayList <Integer> a,int b){
        ArrayList <Integer> c=new ArrayList <Integer>();
        int i=0;
        while(i<b-2) {
            if (point(a.get(i))==point(a.get(i+1))&&point(a.get(i+1))==point(a.get(i+2))) {
                c.add(a.get(i));
            }
            while (i<b-1&&point(a.get(i))==point(a.get(i+1))) i++;
            i++;
        }
        c.sort(comp);
        int k=c.size();
        if (k<b/4) return 0;
        if (k==b/4&&shunzi(c,0,k)>0) return shunzi(c,0,k);
        if (k>b/4)
            for(i=0;i<=k-b/4;i++)
                if (shunzi(c,i,i+b/4)>0) return shunzi(c,i,i+b/4);
        return 0;
    }
    public int feiji2(ArrayList <Integer> a,int b){
        int k=0;
        int flag=0;
        for(int i=0;i<=b/5*2;i+=2)
            if (sanshun(a,i,i+b/5*3)>0) {k=i;flag=1;break;}
        if (flag==0) return 0;
        for(int i=0;i<k;i+=2) if (point(a.get(i))!=point(a.get(i+1))) return 0;
        for(int j=k+b/5*3;j<b;j+=2) if (point(a.get(j))!=point(a.get(j+1))) return 0;
        return sanshun(a,k,k+b/5*3);
    }
    public int judge(ArrayList <Integer> a){
        int len=a.size();
        if (len==1) return 1;//单牌
        else if (len==2&&point(a.get(0))==point(a.get(1))) return 2;//对子
        else if (len==3&&point(a.get(0))==point(a.get(1))&&point(a.get(1))==point(a.get(2))) return 3;//三个
        else if (len==4&&bomb(a,0)>0) return 13;//炸弹
        else if (len==4&&point(a.get(0))==point(a.get(1))&&point(a.get(1))==point(a.get(2))&&point(a.get(2))!=point(a.get(3)))
            return 4;//3+1
        else if (len==4&&point(a.get(0))!=point(a.get(1))&&point(a.get(1))==point(a.get(2))&&point(a.get(2))==point(a.get(3)))
            return 4;//3+1
        else if (len==5&&point(a.get(0))==point(a.get(1))&&point(a.get(1))==point(a.get(2))&&point(a.get(3))==point(a.get(4)))
            return 5;//3+2
        else if (len==5&&point(a.get(2))==point(a.get(3))&&point(a.get(3))==point(a.get(4))&&point(a.get(0))==point(a.get(1)))
            return 5;//3+2
        else if (len>=5&&len<=12&&shunzi(a,0,len)>0) return 6;//顺子
        else if (len%2==0&&len>=6&&len<=20&&liandui(a,0,len)>0) return 7;//连对
        else if (len%3==0&&len>=6&&len<=18&&sanshun(a,0,len)>0) return 8;//三顺
        else if (len==6&&(bomb(a,0)>0||bomb(a,1)>0||bomb(a,2)>0)) return 9;//四带二
        else if (len==8&&bomb(a,0)>0&&point(a.get(4))==point(a.get(5))&&point(a.get(6))==point(a.get(7))) return 10;//四带2对
        else if (len==8&&bomb(a,2)>0&&point(a.get(0))==point(a.get(1))&&point(a.get(6))==point(a.get(7))) return 10;//四带2对
        else if (len==8&&bomb(a,4)>0&&point(a.get(0))==point(a.get(1))&&point(a.get(2))==point(a.get(3))) return 10;//四带2对
        else if (len==2&&point(a.get(0))+point(a.get(1))==105) return 14;//王炸
        if (len%4==0&&len>=8&&len<=20&&feiji(a,len)>0) return 11;//飞机
        if (len%5==0&&len>=10&&len<=20&&feiji2(a,len)>0) return 12;//飞机带对
        return 0;//不合规则
    }
    public int fight(ArrayList <Integer> a,ArrayList <Integer> b){
        int len1=a.size();
        int len2=b.size();
        a.sort(comp);
        b.sort(comp);
        int i,j;
        System.out.println("len1="+len1+" len2="+len2+" a="+a.toString()+" b="+b.toString());
        if (len1==0&&judge(b)!=0){
            return 1;
        }else if(len1==0&&judge(b)==0){
            return 0;
        }
        if (len1==len2&&judge(a)==judge(b)){
            switch (judge(a)){
                case 1: case 2: case 3:
                    if (point(a.get(0))<point(b.get(0)))
                        return 1;
                    else
                        return 0;
                case 4: case 5:
                    for(i=0;i<=len1-3;i++)
                        if (sanshun(a,i,i+3)>0)
                            break;
                    for(j=0;j<=len2-3;j++)
                        if (sanshun(b,j,j+3)>0)
                            break;
                    if (sanshun(a,i,i+3)<sanshun(b,j,j+3))
                        return 1;
                    else
                        return 0;
                case 6:
                    if (shunzi(a,0,len1)<shunzi(b,0,len2))
                        return 1;
                    else
                        return 0;
                case 7:
                    if (liandui(a,0,len1)<liandui(b,0,len2))
                        return 1;
                    else
                        return 0;
                case 8:
                    if (sanshun(a,0,len1)<sanshun(b,0,len2))
                        return 1;
                    else
                        return 0;
                case 9:
                    for(i=0;i<=2;i++)
                        if (bomb(a,i)>0)
                            break;
                    for(j=0;j<=2;j++)
                       if (bomb(b,j)>0)
                           break;
                   if (bomb(a,i)<bomb(b,j))
                       return 1;
                   else
                       return 0;
                case 10:
                    for(i=0;i<=4;i+=2)
                        if (bomb(a,i)>0)
                            break;
                    for(j=0;j<=4;j+=2)
                        if (bomb(b,j)>0)
                            break;
                    if (bomb(a,i)<bomb(b,j))
                        return 1;
                    else
                        return 0;
                case 11:
                    if (feiji(a,len1)<feiji(b,len2))
                        return 1;
                    else
                        return 0;
                case 12:
                    if (feiji2(a,len1)<feiji2(b,len2))
                        return 1;
                    else
                        return 0;
                case 13:
                    if (bomb(a,0)<bomb(b,0))
                        return 1;
                    else
                        return 0;
                default:return 0;
            }
        }
        else if (len1==12&&len2==12&&judge(a)==11&&judge(b)==8&&feiji(a,12)<sanshun(b,0,12)) return 1;
        else if (len1==8&&len2==8&&judge(a)==11&&judge(b)==10&&feiji(a,8)<point(b.get(0))) return 1;
        else if (judge(b)>=13) return 1;
        else return 0;
    }
    public int pukerScore(int curScore,ArrayList <Integer> leadPuker){
        if(judge(leadPuker)>=13) return curScore*2;
        else return curScore;
    }
    public ArrayList <Integer> init(String s){
        ArrayList <Integer> a =new ArrayList<Integer>();
        int c=0;
        for(int i=0;i<s.length();i++){
            if (s.charAt(i)==' ')
            {
                while (i<s.length()-1&&s.charAt(i+1)==' ') i++;
                a.add(c);
                c=0;
            }
            else
            {
                c=c*10+(int)s.charAt(i)-48;
                if (i==s.length()-1) a.add(c);
            }
        }
        a.sort(comp);
        for(int i=0;i<a.size();i++)
            System.out.print(poker[a.get(i)]+" ");
        System.out.println();
        return a;
    }

    public ArrayList <Integer> init(ArrayList <Integer> a){
        a.sort(comp);
        return a;
    }

    public void play(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 13; j++){
                poker[i*13+j] = colors[i]+nums[j];
            }
        }
        poker[52] = "小王";
        poker[53] = "大王";
        String s,s1;
        Scanner reader = new Scanner(System.in);
        s = reader.nextLine();
        ArrayList <Integer> a=init(s);
        System.out.println(judge(a));
        s1 = reader.nextLine();
        ArrayList <Integer> b=init(s1);
        System.out.println(judge(b));
        if (fight(a,b)==1) System.out.println("OK");
        else System.out.println("illegal");
    }

    public ArrayList <Integer> AIPlay(ArrayList <Integer> a,ArrayList <Integer> b)
    //a是手牌,b是桌上的牌
    {
        ArrayList <Integer> c =new ArrayList<Integer>();
        if (b.size()==0) {c.add(a.get(a.size()-1));return c;}
        else if (judge(b)==1) {
            int k=a.size()-1;
            if (k==0&&fight(b,a)==1) c.add(a.get(0));
            else if (k!=0)
            {
                while(k>0&&point(a.get(k))<=point(b.get(0))) k--;
                if (point(a.get(0))<=point(b.get(0))) return c;
                else c.add(a.get(k));
            }
        }
        return c;
    }

    public static void main(String[] args) {
        Play test =new Play();
        test.play();
    }
}
