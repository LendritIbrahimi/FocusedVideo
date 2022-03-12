package processing;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import utils.Interval;

import java.io.IOException;
import java.util.List;

public class Audio implements Focusable {
    final String emptyIntervalSelect = "silence";
    final String emptyIntervalFile = "tempAudio";

    private final Media media;

    public Audio(Media media){
        this.media = media;
    }

    public List<Interval> getEmptyIntervals() throws IOException {
        FFmpegBuilder builder = new FFmpegBuilder();

        builder.addInput(media.inputProbe).setVerbosity(FFmpegBuilder.Verbosity.FATAL)
                .addStdoutOutput()
                .setFormat("null")
                .setAudioFilter("silencedetect=n=0.005:d=0.5,ametadata=mode=print:file="
                        + Media.tempPath + '/' + emptyIntervalFile);

        media.getExecutor().createJob(builder).run();

        return Media.parseEmptyIntervalsFile(emptyIntervalFile, emptyIntervalSelect, media.duration);
    }
}
