/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.urosandjelic.soundrecorder;

import com.urosandjelic.alert.ErrorAlertGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Uros
 */
public class SoundRecorder {

    private AudioFormat format;

    private TargetDataLine targetLine;
    private SourceDataLine sourceLine;

    private DataLine.Info targetInfo;
    private DataLine.Info sourceInfo;

    private ByteArrayOutputStream out;
    //a flag for while loop threads
    private boolean flag;

    //singleton stuff
    private static SoundRecorder instance;

    public static SoundRecorder getInstance() {
        if (instance == null) {
            instance = new SoundRecorder();
        }
        return instance;
    }

    private SoundRecorder() {
        this.format = new AudioFormat(44100, 16, 2, true, true);

        this.sourceInfo = new DataLine.Info(SourceDataLine.class, format);
        this.targetInfo = new DataLine.Info(TargetDataLine.class, format);

        try {
            targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
        } catch (LineUnavailableException lue) {
            ErrorAlertGenerator.generate(
                    "Microphone or speaker line unavailable!", lue.toString());
        }
        out = new ByteArrayOutputStream();
        this.flag = false;

        if (!AudioSystem.isLineSupported(targetInfo)) {
            ErrorAlertGenerator.generate(
                    "Microphone line not supported!", "Sorry...");
        }
        if (!AudioSystem.isLineSupported(sourceInfo)) {
            ErrorAlertGenerator.generate(
                    "Speakers line not supported!", "Sorry...");
        }
    } //end constructor

    public byte[] getBytes() {
        return out.toByteArray();
    }

    public void stop() {
        flag = false;
    }

    public void recordSound() {

        flag = true;
        out.reset(); //important for now
        try {
            targetLine.open(format);
            targetLine.start();

            int n;
            byte[] data = new byte[targetLine.getBufferSize() / 5];
            while (flag) {
                n = targetLine.read(data, 0, data.length);
                out.write(data, 0, n);
            }
            //out.flush(); //maybe this can help...
            //targetLine.drain(); //look into the drain hang problem
            targetLine.stop();
            targetLine.close();
        } catch (LineUnavailableException ex) {
            ErrorAlertGenerator.generate(
                    "Microphone line unavailable!", ex.toString());
        }
    }

    public void playbackSound(byte[] recData) {

        flag = true;
        int CHUNK_SIZE = 1024; //256 je premalo, secka playback//256 je premalo, secka playback
        try {
            sourceLine.open(format, 8820);
            sourceLine.start();
            int currentRead = 0; //Sums all of the CHUNK_SIZEs
            int totalToRead = recData.length;
            // The "+ CHUNK_SIZE" is so that the ArrayOutOfBoundsException
            //doesn't get thrown
            while (flag && currentRead + CHUNK_SIZE < totalToRead) {
                sourceLine.write(recData, currentRead, CHUNK_SIZE);
                currentRead += CHUNK_SIZE;
            }
            sourceLine.drain();
            sourceLine.stop();
            sourceLine.close();

        } catch (LineUnavailableException ex) {
            ErrorAlertGenerator.generate(
                    "Speakers line unavailable!", ex.toString());
        }
    }

    public void saveByteArrayToWAVFile(byte[] b, File path) throws IOException {

        AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(b), format, b.length);

        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, path);
    }
}
