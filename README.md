# FocusedVideo &middot; [![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/LendritIbrahimi/FocusedVideo/blob/main/LICENSE)

FocusedVideo is a Java video-processing application in development that allows the removal of still-frames and audio silence from a media file.

## Installation

Download the latest release and run it using the command line.

## Usage

In order to run the application, you need to run the following command:

```
java app.java
```

To get a description of available options:

```
Usage: app [options]

Options:
  Required
    -input  <path>                   Input file path
    -output <path>                   Output file path
  Optional
    -type   <type>                   Focus type "audio", "video", "full" (default: "none")
    -voicefilter                     Optional audio filter that intends to remove noise and make voices more audible
    -encode                          Experimental full encode to MP4 1080p
```

## Examples

### Remove audio silence and still frames
```
java app.java -input "input.mp4" -output "output.mp4" -type "full"
```

### Make voice more audible on high background noise recording
```
java app.java -input "input.mp3" -output "output.mp3" -voicefilter
```

### Encode to MP4
```
java app.java -input "input.mov" -output "output.mp4" -encode
```


## License
FocusedVideo is [MIT licensed](./LICENSE).
