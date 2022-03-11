package processing;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import utils.Interval;

import java.io.IOException;
import java.util.List;

public class Video extends Media {
    final String emptyIntervalSelect = "freeze";
    final String emptyIntervalFile = "tempVideo";

    public Video(String inputPath) throws IOException {
        super(inputPath);
    }

    public List<Interval> getEmptyIntervals() throws IOException {
        FFmpegProbeResult inputProbe = ffprobe.probe(this.inputPath);

        builder.addInput(inputProbe).setVerbosity(FFmpegBuilder.Verbosity.FATAL)
                .addStdoutOutput()
                .addExtraArgs("-map", "0:v:0")
                .setFormat("null")
                .setVideoFilter("freezedetect=n=-60dB:d=0.5,metadata=mode=print:file="
                        + tempPath + '/' + emptyIntervalFile);

        executor.createJob(builder).run();

        return parseEmptyIntervalsFile(emptyIntervalFile, emptyIntervalSelect, duration);
    }
}
