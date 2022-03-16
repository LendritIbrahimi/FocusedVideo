package processing;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import utils.Interval;

import java.io.IOException;
import java.util.List;

public class Video implements Focusable {
    final String emptyIntervalSelect = "freeze";
    final String emptyIntervalFile = "tempVideo";

    private final Media media;

    public Video(Media media) {
        this.media = media;
    }

    @Override
    public Media getMedia() {
        return media;
    }

    public void encodeFilter() {
        media.filters.addAll(List.of("-preset", "slow",
                "-codec:a", "libvorbis",
                "-b:a", "128k",
                "-codec:v", "libx264",
                "-pix_fmt", "yuv420p",
                "-b:v", "4500k",
                "-minrate", "4500k",
                "-maxrate", "9000k",
                "-bufsize", "9000k"));
    }

    public void focusFilter() {
        media.filters.addAll(List.of(new String[]{"-filter_script:v", "ffmpeg/tempFilterVideo"}));
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
