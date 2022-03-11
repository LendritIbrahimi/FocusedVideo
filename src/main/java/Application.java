import processing.Audio;
import processing.Video;
import utils.Interval;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        String input = "ffmpeg/Test.mp4";
        Video video = new Video(input);
        Audio audio = new Audio(input);

        for (Interval interval :
                video.getEmptyIntervals()) {
            System.out.println("Video: " + interval.start + " - " + interval.end);
        }

        for (Interval interval :
                audio.getEmptyIntervals()) {
            System.out.println("Audio: " + interval.start + " - " + interval.end);
        }
    }
}
