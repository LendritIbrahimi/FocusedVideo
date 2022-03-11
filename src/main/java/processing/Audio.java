package processing;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import utils.Interval;

import java.io.IOException;
import java.util.List;

public class Audio extends Media {
    final String emptyIntervalSelect = "silence";
    final String emptyIntervalFile = "tempAudio";

    public Audio(String inputPath) throws IOException {
        super(inputPath);
    }

    public List<Interval> getEmptyIntervals() throws IOException {
        FFmpegProbeResult inputProbe = ffprobe.probe(this.inputPath);

        builder.addInput(inputProbe).setVerbosity(FFmpegBuilder.Verbosity.FATAL)
                .addStdoutOutput()
                .setFormat("null")
                .setAudioFilter("silencedetect=n=0.005:d=0.5,ametadata=mode=print:file="
                        + tempPath + '/' + emptyIntervalFile);

        executor.createJob(builder).run();

        return parseEmptyIntervalsFile(emptyIntervalFile, emptyIntervalSelect, duration);
    }
}
