import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Media {
    private FFmpeg ffmpeg;
    private String ffmpegPath = "path";

    private FFprobe ffprobe;
    private String ffprobePath = "path";

    private FFmpegExecutor executor;
    private FFmpegBuilder builder = new FFmpegBuilder();

    public Media(String inputPath, String outputPath) throws IOException {
        ffmpeg = new FFmpeg(ffmpegPath);
        ffprobe = new FFprobe(ffprobePath);
        executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegProbeResult inputProbe = ffprobe.probe(inputPath);

        builder.addInput(inputProbe)
                // Video
                .overrideOutputFiles(true)
                .addOutput(outputPath)   // Filename for the destination
                .setFormat("mp4")        // Format is inferred from filename, or can be set
                .setVideoCodec("libx264")     // Video using x264

                // Audio
                .setAudioCodec("aac")        // using the aac codec
                .setAudioSampleRate(48_000)  // at 48KHz
                .setAudioBitRate(32768)      // at 32 kbit/s

                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
                .done();

        StartProgress(inputProbe, executor);
    }

    private void StartProgress(FFmpegProbeResult inputProbe, FFmpegExecutor executor) {
        FFmpegJob job = executor.createJob(builder, new ProgressListener() {
            // Using the FFmpegProbeResult determine the duration of the input
            final double duration_ns = inputProbe.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                double percentage = progress.out_time_ns / duration_ns;

                // Print out interesting information about the progress
                System.out.println(String.format(
                        "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
                        percentage * 100,
                        progress.status,
                        progress.frame,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                        progress.fps.doubleValue(),
                        progress.speed
                ));
            }
        });
        job.run();
    }

    public String GetVideoInformation() {
        return "";
    }

    public String GetAudioInformation() {
        return "";
    }

}
