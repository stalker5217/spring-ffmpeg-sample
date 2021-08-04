package com.stalker5217.ffmpegsample.audio;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/audio")
public class AudioController {
    @Value("${ffmpeg.path.ffprobe}")
    private String ffProbePath;

    @PostMapping(path = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FFmpegFormat> audioInformation(@RequestParam MultipartFile multipartFile) {
        log.info("[GET AUDIO INFORMATION] START");

        File file = null;
        try {
            file = File.createTempFile("temp_" , "");
            multipartFile.transferTo(file);

            FFprobe ffProbe = new FFprobe(ffProbePath);
            FFmpegProbeResult ffmpegProbeResult = ffProbe.probe(file.getPath());
            FFmpegFormat ffmpegFormat = ffmpegProbeResult.getFormat();

            return ResponseEntity.ok(ffmpegFormat);
        } catch (IOException e) {
            log.info("[GET AUDIO INFORMATION] ERROR");
            log.error(e.getMessage());

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if(file != null) file.delete();
        }

        log.info("[GET AUDIO INFORMATION] END");

        return ResponseEntity.ok().build();
    }
}