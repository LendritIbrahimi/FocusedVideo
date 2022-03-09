import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Media {
    private FFmpeg ffmpeg;
    private String ffmpegPath = "ffmpeg/ffmpeg.exe";

    private FFprobe ffprobe;
    private String ffprobePath = "ffmpeg/ffprobe.exe";

    private FFmpegExecutor executor;
    private FFmpegBuilder builder = new FFmpegBuilder();

    private String inputPath = "";
    private String tempPath = "ffmpeg";

    public Media(String inputPath) throws IOException {
        ffmpeg = new FFmpeg(ffmpegPath);
        ffprobe = new FFprobe(ffprobePath);
        executor = new FFmpegExecutor(ffmpeg, ffprobe);

        this.inputPath = inputPath;
    }

    public String getAudioSilence() throws IOException{
        FFmpegProbeResult inputProbe = ffprobe.probe(this.inputPath);

        builder.addInput(inputProbe).setVerbosity(FFmpegBuilder.Verbosity.INFO)
                .addStdoutOutput()
                .setFormat("null")
                .setAudioFilter("silencedetect=n=0.005:d=0.5,ametadata=mode=print:file="+tempPath+"/temp");

        executor.createJob(builder).run();

        return parseAudioSilence();
    }

    private String parseAudioSilence(){
        File file = new File("temp");

        return "";
    }

    private void StartProgress(FFmpegProbeResult inputProbe, FFmpegExecutor executor) {
        FFmpegJob job = executor.createJob(builder, new ProgressListener() {
            // Using the FFmpegProbeResult determine the duration of the input
            final double duration_ns = inputProbe.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {
                double percentage = progress.out_time_ns / duration_ns;
                FFmpegStream f =  inputProbe.getStreams().get(0);
                System.out.println(f.toString());
                // Print out interesting information about the progress
               /* System.out.println(String.format(
                       "[%.0f%%] Video Time:%s",
                        percentage * 100,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS)
                ));*/
            }
        });
        job.run();

    }
}
