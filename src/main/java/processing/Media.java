package processing;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import utils.Interval;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static utils.Interval.parseInterval;

public class Media {
    protected static String tempPath = "ffmpeg";
    private final String ffmpegPath = "ffmpeg.exe";
    private final String ffprobePath = "ffprobe.exe";

    public List<Interval> emptyIntervals = new ArrayList<>();
    public List<String> filters = new ArrayList<>();
    public List<String> fileAudio = new ArrayList<>();

    protected FFmpeg ffmpeg;
    protected FFprobe ffprobe;
    protected String inputPath;
    protected FFmpegProbeResult inputProbe;
    private final FFmpegBuilder builder = new FFmpegBuilder();

    public boolean hasVideo = false;
    public boolean hasAudio = false;
    public float duration;

    public Media(String inputPath) throws IOException {
        ffmpeg = new FFmpeg(tempPath + '/' + ffmpegPath);
        ffprobe = new FFprobe(tempPath + '/' + ffprobePath);

        inputProbe = ffprobe.probe(inputPath);

        this.duration = (float) inputProbe.getFormat().duration;

        List<FFmpegStream.CodecType> codecType = inputProbe.streams.stream().map(e -> e.codec_type).toList();
        if (codecType.contains(FFmpegStream.CodecType.AUDIO))
            hasAudio = true;
        if (codecType.contains(FFmpegStream.CodecType.VIDEO))
            hasVideo = true;

        this.inputPath = inputPath;
    }

    protected static List<Interval> parseEmptyIntervalsFile(String path, String select, float duration) throws IOException {
        File file = new File(tempPath + '/' + path);
        List<Interval> intervals = new ArrayList<>();

        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            int startLineIndex = line.indexOf(select + "_start");
            int endLineIndex = line.indexOf(select + "_end");

            if (startLineIndex > 0) {
                intervals.add(new Interval(parseInterval(line.substring(startLineIndex)), -1));
            } else if (endLineIndex > 0) {
                intervals.get(intervals.size() - 1).end =
                        parseInterval(line.substring(endLineIndex));
            }
        }

        if (intervals.get(intervals.size() - 1).end < 0)
            intervals.get(intervals.size() - 1).end = duration;

        sc.close();
        file.delete();
        return intervals;
    }

    private void startProgress() {
        FFmpegJob job = getExecutor().createJob(builder, new ProgressListener() {

            final double duration_ns = inputProbe.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                double percentage = progress.out_time_ns / duration_ns;
                System.out.println(String.format(
                        "[%.0f%%] Video Progress",
                        percentage * 100
                ));
            }
        });
        job.run();

    }

    private String parseIntervalToFilter() {
        String output = "";
        for (Interval interval : emptyIntervals) {
            output += String.format("between(t,%.2f,%.2f)+", interval.start, interval.end);
        }
        if (output.length() > 0)
            return output.substring(0, output.length() - 1);

        return null;
    }

    private void filterToFile(String type) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("ffmpeg/tempFilterAudio", StandardCharsets.UTF_8);
            String filter = parseIntervalToFilter();

            if (type.contains("audio") || type.contains("full")) {
                writer.println("aselect='" + filter + "',asetpts=N/SR/TB");
                if (fileAudio.size() > 0) {
                    writer.print(", ");
                }
            }
            if (fileAudio.size() > 0) {
                writer.println(String.join(", ", fileAudio));
            }

            writer.close();

            if (hasVideo) {
                writer = new PrintWriter("ffmpeg/tempFilterVideo", StandardCharsets.UTF_8);
                writer.println("select='" + parseIntervalToFilter() + "',setpts=N/FRAME_RATE/TB");
                writer.close();
            }
        } catch (
                IOException err) {
            System.err.println(err.getMessage());
        }

    }

    public void createMedia(String outputPath, String type) {
        filterToFile(type);
        builder.addInput(inputProbe)
                .addOutput(outputPath)
                .addExtraArgs(filters.toArray(new String[filters.size()]));
        startProgress();
    }

    public FFmpegExecutor getExecutor() {
        return new FFmpegExecutor(ffmpeg, ffprobe);
    }

    public void dispose() {
        try {
            File file = new File(tempPath + '/' + "tempFilterAudio");
            file.delete();

            file = new File(tempPath + '/' + "tempFilterVideo");
            file.delete();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
