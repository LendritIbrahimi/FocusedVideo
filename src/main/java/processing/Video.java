package processing;

import net.bramp.ffmpeg.builder.FFmpegBuilder;
import utils.Interval;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Video implements Focusable {
    final String emptyIntervalSelect = "freeze";
    final String emptyIntervalFile = "tempVideo";

    private final Media media;

    public Video(Media media) {
        this.media = media;
    }

    public void CreateVideo(String outputPath) {
        FFmpegBuilder builder = new FFmpegBuilder();
        filterToFile();
        builder.addInput(media.inputProbe)
                .addOutput(outputPath)
                .addExtraArgs("-filter_script:v", "ffmpeg/tempFilterVideo","-filter_script:a","ffmpeg/tempFilterAudio");
        media.startProgress(media.inputProbe,media.getExecutor(),builder);


    }
    private void filterToFile(){
        try {
            PrintWriter writer = new PrintWriter("ffmpeg/tempFilterAudio", "UTF-8");
            writer.println("aselect='" + media.parseIntervalToFilter() + "',asetpts=N/SR/TB");
            writer.close();

            writer = new PrintWriter("ffmpeg/tempFilterVideo", "UTF-8");
            writer.println("select='" + media.parseIntervalToFilter() + "',setpts=N/FRAME_RATE/TB");
            writer.close();
        }
        catch(IOException err){
            System.err.println(err.getMessage());
        }
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
