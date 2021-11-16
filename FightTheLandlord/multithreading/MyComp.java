package FightTheLandlord.multithreading;

import java.util.Comparator;

public class MyComp implements Comparator<Integer> {
    public int compare(Integer o1, Integer o2) {
        int a = o1%13;
        int b = o2%13;
        int c = o1/13;
        int d = o2/13;
        if (o1>51) {
            a=o1;
        }
        if (o2>51) {
            b=o2;
        }
        if (a<b) {
            return 1;
        }
        else if (a>b) {
            return -1;
        }
        else if (c<d) {
            return 1;
        }
        else if (c>d) {
            return -1;
        }
        else return 0;
    }
}
