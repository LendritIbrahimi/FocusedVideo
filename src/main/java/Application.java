import processing.Audio;
import processing.Focusable;
import processing.Media;
import processing.Video;
import utils.Interval;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        String input = "ffmpeg/Test.mp4";
        Media media = new Media(input);

        Focusable video = new Video(media);
        Focusable audio = new Audio(media);

        media.emptyIntervals = Interval.overlap(video.getEmptyIntervals(), audio.getEmptyIntervals());


    }
}
