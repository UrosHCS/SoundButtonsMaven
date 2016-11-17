/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.urosandjelic.soundrecorder;

import java.io.Serializable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Uros
 */
public class Sound implements Serializable {

    private byte[] soundData;
    private StringProperty title;
    //I don't like this but I've spent too much time trying to do it differently.
    private BooleanProperty isSelected;

    public BooleanProperty getIsSelected() {
        return isSelected;
    }

    public Sound(String title, byte[] soundData) {

        //gotta initialize the boolean
        isSelected = new SimpleBooleanProperty(false);
        //setTitle
        this.title = new SimpleStringProperty();
        this.title.set(title);
        //set soundData
        this.soundData = soundData;
    }

    public byte[] getByteArray() {
        return soundData;
    }

    public String getTitle() {
        return title.get();
    }
}
