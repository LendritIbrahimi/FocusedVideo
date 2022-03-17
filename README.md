# FocusedVideo &middot; [![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/LendritIbrahimi/FocusedVideo/blob/main/LICENSE)

FocusedVideo is a Java video-processing application that allows the removal of still-frames and audio silence from a media file.


## Usage

The application can be run from the command line:

```
Usage: java -jar focusedvideo.jar [options]
```

#### Application Options
| Option        | Value         |  Default      | Required   | Description  |
| :-----------: |:-------------:| :--------:    |:----------:|:-------------|
| input, i    | Absulute file path |               | &check;    | The input path media file. This is the file you want to focus.              |
| output, o        | Absulute file path |               | &check;    | The output path to the media file, this is where you want to output your processed file.             |
| type, t          | AUDIO, VIDEO or FULL | NONE  |            | Tells the application what type of focusing it will be doing. AUDIO removes the silences found, VIDEO removes the freeze-frames found and FULL removes both of them.              |
| voicefilter, vf   | TRUE, FALSE         | FALSE              |            | Reduces the background audio noise so that the voice inside the audio will be more clear and audible. This won't make an already clear voice better.             |
| encode, e       | TRUE, FALSE         | FALSE              |            | Encodes the output file to MP4 1080p. this is useful when the media file isn't being focused properly and should be done in a two-pass form. First encode the input file, then focus the output file.             |

## Examples

#### Remove silence and freeze-frames
```
java -jar focusedvideo.jar -input "input.mp4" -output "output.mp4" -type "full"
```

#### Make voice more audible on high background noise recording
```
java -jar focusedvideo.jar -input "input.mp3" -output "output.mp3" -voicefilter
```

#### Encode to MP4 and remove freeze-frames
```
java -jar focusedvideo.jar -input "input.mov" -output "output.mp4" -encode

java -jar focusedvideo.jar -input "output.mp4" -output "output1.mp4" -type VIDEO
```

## Contributing

Contributions are welcomed!

To start contributing download the source code and add [ffmpeg.exe and ffprobe.exe](https://www.ffmpeg.org/download.html) to a new folder named "ffmpeg/" in the root directory of the source code.

## License
FocusedVideo is [MIT licensed](./LICENSE).
