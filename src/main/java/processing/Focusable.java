package processing;

import utils.Interval;

import java.io.IOException;
import java.util.List;

public interface Focusable {
    final String emptyIntervalSelect = "silence";
    final String emptyIntervalFile = "tempAudio";

    final Media media = null;

    public List<Interval> getEmptyIntervals() throws IOException;

}
