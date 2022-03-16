package processing;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import utils.Interval;

import java.io.IOException;
import java.util.List;

public class Audio implements Focusable {
    final String emptyIntervalSelect = "silence";
    final String emptyIntervalFile = "tempAudio";

    private final Media media;

    public Audio(Media media) {
        this.media = media;
    }

    @Override
    public Media getMedia() {
        return media;
    }


    public void voiceFilter() {
        media.fileAudio.add("highpass=f=200");
        media.fileAudio.add("lowpass=f=2000");
        media.fileAudio.add("volume=2.5");
    }

    public void focusFilter() {
        media.filters.addAll(List.of(new String[]{"-filter_script:a", "ffmpeg/tempFilterAudio"}));
    }

    public List<Interval> getEmptyIntervals() throws IOException {
        FFmpegBuilder builder = new FFmpegBuilder();

        builder.addInput(media.inputProbe).setVerbosity(FFmpegBuilder.Verbosity.FATAL)
                .addStdoutOutput()
                .setFormat("null")
                .setAudioFilter("silencedetect=n=0.01:d=0.5,ametadata=mode=print:file="
                        + Media.tempPath + '/' + emptyIntervalFile);

        media.getExecutor().createJob(builder).run();

        return Media.parseEmptyIntervalsFile(emptyIntervalFile, emptyIntervalSelect, media.duration);
    }
}
