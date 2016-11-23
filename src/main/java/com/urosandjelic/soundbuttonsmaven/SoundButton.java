package com.urosandjelic.soundbuttonsmaven;

import java.io.File;
import java.net.MalformedURLException;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author uroshcs@gmail.com
 */
public class SoundButton extends Button {

    private final File wavFile;
    private final AudioClip audioClip;
    private static final Font SOUND_BUTTON_FONT = new Font("Arial", 48);
    //private final String buttonName;

    public SoundButton(File wavFile) throws MalformedURLException {
        super(wavFile.getName().split("[.]")[0]);
        this.wavFile = wavFile;
        audioClip = new AudioClip(wavFile.toURI().toURL().toString());

        setPrefHeight(80);
        setMinHeight(80);
        setMaxHeight(80);
        setFont(SOUND_BUTTON_FONT);
        
        //Set up how the button width will behave
        Text text = new Text(this.getText());
        text.setFont(SOUND_BUTTON_FONT);
        double textWidth = text.getLayoutBounds().getWidth() + 20;
        int width = (int) (textWidth - (textWidth % 120) + 110);
        setPrefWidth(width);
        setMinWidth(width);
        setMaxWidth(width);
        
        setPadding(new Insets(2, 2, 2, 2));
    }

    public File getWavFile() {
        return wavFile;
    }

    public AudioClip getAudioClip() {
        return audioClip;
    }

    public void playWAVFile() {
        audioClip.play();
    }

    public void stopWAVFile() {
        audioClip.stop();
    }

}
