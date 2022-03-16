package processing;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import utils.Interval;

import java.io.IOException;
import java.util.List;

public interface Focusable {
    String emptyIntervalSelect = "silence";
    String emptyIntervalFile = "tempAudio";

    Media media = null;

    Media getMedia();

    List<Interval> getEmptyIntervals() throws IOException;

}
