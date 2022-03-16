package utils;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Options {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"-input", "-i"}, description = "Input file path")
    public String input;

    @Parameter(names = {"-output", "-o"}, description = "Output file path")
    public String output;

    @Parameter(names = {"-type", "-t"}, description = "Focus type")
    public String type = "full";

    @Parameter(names = {"-voicefilter", "-vf"}, description = "Voice filter")
    public boolean voice = false;

    @Parameter(names = {"-encode", "-en"}, description = "Encode to mp4")
    public boolean encode = false;
}
