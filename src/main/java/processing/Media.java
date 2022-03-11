package processing;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import utils.Interval;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static utils.Interval.parseInterval;

public abstract class Media {
    protected static String tempPath = "ffmpeg";
    protected FFmpeg ffmpeg;
    protected FFprobe ffprobe;
    protected FFmpegExecutor executor;
    protected FFmpegBuilder builder = new FFmpegBuilder();
    protected String inputPath;
    protected float duration;
    private final String ffmpegPath = "ffmpeg.exe";
    private final String ffprobePath = "ffprobe.exe";

    public Media(String inputPath) throws IOException {
        ffmpeg = new FFmpeg(tempPath + '/' + ffmpegPath);
        ffprobe = new FFprobe(tempPath + '/' + ffprobePath);
        executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegProbeResult probeResult = ffprobe.probe(inputPath);

        this.duration = (float) probeResult.getFormat().duration;
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

    public abstract List<Interval> getEmptyIntervals() throws IOException;

    protected void startProgress(FFmpegProbeResult inputProbe, FFmpegExecutor executor) {
        FFmpegJob job = executor.createJob(builder, new ProgressListener() {

            final double duration_ns = inputProbe.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                double percentage = progress.out_time_ns / duration_ns;
               System.out.println(String.format(
                       "[%.0f%%] Video Time:%s",
                        percentage * 100,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS)
                ));
            }
        });
        job.run();

    }

}