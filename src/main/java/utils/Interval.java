package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Interval implements Comparable<Interval>{
    public float start;
    public float end;

    public Interval(float start, float end){
        this.start = start;
        this.end = end;
    }

    @Override
    public int compareTo(Interval interval) {
        if (start < interval.end) {
            return -1;
        } else if (start == end) {
            return 0;
        } else {
            return 1;
        }
    }

    /*public static List<Interval> overlap(List<Interval> a,List<Interval> b) {
        Collections.sort(intervals);
        List<Interval> overlappingInterval = new ArrayList<>();

        for (int i = 0; i < intervals.size()-1; i++) { //n
            if (intervals.get(i).end > intervals.get(i+1).start) {
                overlappingInterval.add(intervals.get(i));
                overlappingInterval.add(intervals.get(i+1));
            }
        }
        return overlappingInterval;
    }*/

    public static float parseInterval(String str){
        return Float.parseFloat(str.replaceAll("[^\\d.]", ""));
    }
}
