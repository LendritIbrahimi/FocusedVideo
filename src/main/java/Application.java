import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        Media media = new Media("ffmpeg/Test.mp4");
        media.getAudioSilence();

    }
}
