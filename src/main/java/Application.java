import com.beust.jcommander.JCommander;
import processing.Audio;
import processing.Media;
import processing.Video;
import utils.Interval;
import utils.Options;

import java.io.IOException;
import java.util.Locale;

public class Application {
    public static void main(String[] args) throws IOException {
        Options options = new Options();

        JCommander.newBuilder()
                .addObject(options)
                .build()
                .parse(args);

        if (options.input == null) {
            System.out.println("Input path not given, consider using -input/-i [PATH]");
            return;
        }
        if (options.output == null) {
            System.out.println("Output path not given, consider using -output/-o [PATH]\"");
            return;
        }

        Media media = new Media(options.input);
        Audio audio = null;
        Video video = null;

        if (media.hasAudio)
            audio = new Audio(media);
        if (media.hasVideo)
            video = new Video(media);

        String type = options.type.toLowerCase(Locale.ROOT);

        if (type.contains("audio") && media.hasAudio) {
            audio.focusFilter();

            media.emptyIntervals = Interval.reverse(audio.getEmptyIntervals(), media.duration);
        } else if (type.contains("video") && media.hasVideo) {
            video.focusFilter();

            media.emptyIntervals = Interval.reverse(video.getEmptyIntervals(), media.duration);
        } else if (type.contains("full") && media.hasAudio && media.hasVideo) {
            audio.focusFilter();
            video.focusFilter();

            media.emptyIntervals = Interval.reverse(
                    Interval.overlap(video.getEmptyIntervals(), audio.getEmptyIntervals()),
                    media.duration);
        } else {
            type = "none";
            audio.focusFilter();
        }

        if (options.voice && media.hasAudio)
            audio.voiceFilter();
        if(options.encode && media.hasVideo)
            video.encodeFilter();

        media.createMedia(options.output, type);
        media.dispose();
    }
}
