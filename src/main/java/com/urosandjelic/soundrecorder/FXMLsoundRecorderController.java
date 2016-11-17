/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.urosandjelic.soundrecorder;

import com.urosandjelic.alert.ErrorAlertGenerator;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * FXML Controller class
 *
 * @author Uros
 */
public class FXMLsoundRecorderController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TableView<Sound> soundsTable;
    @FXML
    private TableColumn<Sound, String> columnSound;
    @FXML
    private TableColumn<Sound, Integer> columnNumber;
    @FXML
    private TableColumn<Sound, Boolean> columnSelect;
    @FXML
    private Button btnRec;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnSaveSelected;
    @FXML
    private Button btnDeleteSelected;

    private final ObservableList<Sound> recList;
    private boolean recordingSound; //set true when sound is being recorded

    public FXMLsoundRecorderController() {
        this.recList = FXCollections.observableArrayList();
        this.recordingSound = false;
    }

    //gets the sound from the selected sound from the table
    public byte[] getSelectedSound() {
        return soundsTable.getSelectionModel().getSelectedItem().getByteArray();
    }

    //true enables only stop button, false enables play and rec
    public void stopButtonEnabled(boolean yes) {
        btnRec.setDisable(yes);
        btnPlay.setDisable(yes);
        btnStop.setDisable(!yes);
        //disable double click on the table
        soundsTable.setDisable(yes); //not a perfect solution
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        SoundRecorder sr = SoundRecorder.getInstance();

        this.btnStop.setDisable(true);
        this.btnPlay.setDisable(true);
        //SET THE TABLE
        //this is the numbering column
        columnNumber.setCellValueFactory(column
                -> new ReadOnlyObjectWrapper<>(soundsTable.getItems().indexOf(
                        column.getValue()) + 1));
        columnSound.setCellValueFactory(new PropertyValueFactory<>("title"));

        //columnSelect has a checkbox for selecting sounds to save or delete
        columnSelect.setCellValueFactory(param -> param.getValue().getIsSelected());
        columnSelect.setCellFactory(CheckBoxTableCell.forTableColumn(columnSelect));

        soundsTable.setItems(recList);

        //BUTTONS START HERE
        btnRec.setOnAction((e) -> {
            Runnable recordSound = () -> {
                sr.recordSound(); //start recording sound
            };
            recordingSound = true; //sound is being recorded
            stopButtonEnabled(true); //set buttons accordingly
            new Thread(recordSound).start();
        }
        );

        btnStop.setOnAction(
                (e) -> {
                    sr.stop(); //stop recording sound
                    stopButtonEnabled(false);
                    if (recordingSound) {
                        recList.add(new Sound(nameField.getText(), sr.getBytes()));
                        soundsTable.getSelectionModel().selectLast();
                        recordingSound = false;
                    }
                }
        );

        //Prepare a Runnable for playing sound (started from 2 places)
        Runnable playSound = () -> {
            sr.stop(); //make sure another sound is not playing
            stopButtonEnabled(true);
            sr.playbackSound(getSelectedSound());
            stopButtonEnabled(false);
        };

        btnPlay.setOnAction(
                (e) -> {
                    new Thread(playSound).start();
                }
        );
        soundsTable.setOnMouseClicked((e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (e.getClickCount() == 2) {
                    new Thread(playSound).start();
                }
            }

        }
        );

        btnSaveSelected.setOnAction(
                (e) -> {
                    if (!recList.isEmpty()) {
                        try {
                            for (int i = 0; i < recList.size(); i++) {
                                if (columnSelect.getCellData(i)) {
                                    System.out.printf("Row %d is checked!\n", i);
                                    String filePath = recList.get(i).getTitle() + ".wav";
                                    sr.saveByteArrayToWAVFile(recList.get(i).getByteArray(), filePath);
                                }
                            }
                        } catch (IOException ioe) {
                            ErrorAlertGenerator.generate(
                                    "Couldn't save byte array to wav", ioe.toString());
                        } catch (UnsupportedAudioFileException uafe) {
                            ErrorAlertGenerator.generate(
                                    "Couldn't save byte array to wav", uafe.toString());
                        }
                    } else {
                        awesomeAlert("There are no recordings!",
                                "Make a recording, then save it.");
                    }
                });

        btnDeleteSelected.setOnAction(
                (e) -> {
                    //
                    if (!recList.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("");
                        alert.setHeaderText("Are you sure you want to delete selected sounds?");
                        alert.setContentText("There is no undo button.");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            for (Iterator<Sound> it = recList.iterator(); it.hasNext();) {
                                Sound next = it.next();
                                if (next.getIsSelected().get()) {
                                    it.remove();
                                }
                            }
                        }
                    } else {
                        awesomeAlert("There are no recordings!",
                                "You can't delete something that doesn't exist...");
                    }
                });

    }

    private void awesomeAlert(String text1, String text2) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText(text1);
        alert.setContentText(text2);
        alert.showAndWait();
    }
}
