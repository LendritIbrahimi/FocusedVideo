package processing;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import utils.Interval;

import java.io.IOException;
import java.util.List;

public class Video implements Focusable{
    final String emptyIntervalSelect = "freeze";
    final String emptyIntervalFile = "tempVideo";

    private final Media media;
    public Video(Media media){
        this.media = media;
    }
    public void CreateVideo(String outputPath){

    }

    public List<Interval> getEmptyIntervals() throws IOException {
        FFmpegBuilder builder = new FFmpegBuilder();
        builder.addInput(media.inputProbe).setVerbosity(FFmpegBuilder.Verbosity.FATAL)
                .addStdoutOutput()
                .addExtraArgs("-map", "0:v:0")
                .setFormat("null")
                .setVideoFilter("freezedetect=n=-60dB:d=0.5,metadata=mode=print:file="
                        + Media.tempPath + '/' + emptyIntervalFile);

        media.getExecutor().createJob(builder).run();

        return Media.parseEmptyIntervalsFile(emptyIntervalFile, emptyIntervalSelect, media.duration);
    }
}
