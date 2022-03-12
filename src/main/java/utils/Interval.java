package utils;

import java.util.ArrayList;
import java.util.List;

public class Interval {
    public float start;
    public float end;

    public Interval(float start, float end) {
        this.start = start;
        this.end = end;
    }

    // the interval sets are sorted by default and don't overlap inside the same set
    public static List<Interval> overlap(List<Interval> firstSet, List<Interval> secondSet) {
        List<Interval> overlappingInterval = new ArrayList<>();

        int skipIndex = 0;
        for (Interval a : firstSet) {
            boolean hasOverlapped = false;
            for (int i = skipIndex; i < secondSet.size() - 1; i++) {
                Interval b = secondSet.get(i);

                if (a.compareOverlap(b) == 1) {
                    float start = Math.max(a.start, b.start);
                    float end = Math.min(a.end, b.end);

                    Interval interval = new Interval(start, end);
                    overlappingInterval.add(interval);

                    skipIndex = i;
                    hasOverlapped = true;
                } else if (hasOverlapped) {
                    break;
                }
            }
        }
        return overlappingInterval;
    }

    public static float parseInterval(String str) {
        return Float.parseFloat(str.replaceAll("[^\\d.]", ""));
    }

    public int compareOverlap(Interval interval) {
        if (start > interval.end || end < interval.start) {
            return -1;
        } else if (start == interval.start && end == interval.end) {
            return 0;
        } else {
            return 1;
        }
    }
}
