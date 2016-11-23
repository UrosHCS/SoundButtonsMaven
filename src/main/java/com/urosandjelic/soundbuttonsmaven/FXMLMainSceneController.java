package com.urosandjelic.soundbuttonsmaven;

import com.urosandjelic.alert.ErrorAlertGenerator;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Uros
 */
public class FXMLMainSceneController implements Initializable {

    @FXML
    private MenuItem openSoundRecorder;
    @FXML
    private MenuItem openFiles;
    @FXML
    private MenuItem exit;
    @FXML
    private CheckBox stopCurrentBeforeNext;
    @FXML
    private MenuItem deleteAllSounds;
    @FXML
    private MenuItem tutorial;
    @FXML
    private MenuItem about;
    @FXML
    private FlowPane flowPane;

    private SoundButton soundButton;

    //Cause we need only one sound recorder stage
    public static Stage soundRecorderStage;
    private static Stage aboutStage;

    public void startSoundRecorder() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FXMLsoundRecorder.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            //Cause we need only one sound recorder stage:
            if (soundRecorderStage == null) {
                soundRecorderStage = new Stage();
                Scene scene = new Scene(root1);
                scene.getStylesheets().add("/styles/Styles.css");
                soundRecorderStage.setScene(scene);
                soundRecorderStage.show();
            } else {
                soundRecorderStage.show();
            }
        } catch (IllegalStateException | IOException e) {
            ErrorAlertGenerator.generate("Couldn't load Sound recorder.", e.toString());

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        openSoundRecorder.setOnAction((event) -> {
            startSoundRecorder();
        });

        openFiles.setOnAction((event) -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open some sounds.");
            fileChooser.setSelectedExtensionFilter(
                    new FileChooser.ExtensionFilter("WAV files (*.wav)", "*.wav"));

            List<File> listOfFiles = fileChooser.showOpenMultipleDialog(
                    flowPane.getScene().getWindow());
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    try {
                        SoundButton sbtn = new SoundButton(file);
                        flowPane.getChildren().add(sbtn);
                        sbtn.setOnAction((e) -> {
                            //This controler keeps a reference of the last clicked sound button.
                            //If stopCurrentBeforeNext is selected that sound button is stopped
                            //before the next one is played.
                            if (soundButton != null && stopCurrentBeforeNext.isSelected()) {
                                this.soundButton.stopWAVFile();
                            }
                            this.soundButton = sbtn;
                            sbtn.playWAVFile();
                        });
                    } catch (MalformedURLException ex) {
                        ErrorAlertGenerator.generate("Some problem with URL", ex.toString());
                    }
                }
            }
        });

        exit.setOnAction((event) -> {
            System.exit(0);
        });

        deleteAllSounds.setOnAction((e) -> {
            flowPane.getChildren().clear();
        });

        tutorial.setOnAction((e) -> {
            ErrorAlertGenerator.generate("Tutorial is not implemented yet.", "Sorry...");
        });

        about.setOnAction((e) -> {
            if (aboutStage == null) {
                VBox vBox = new VBox(10);
                vBox.setPadding(new Insets(20));
                vBox.getChildren().add(new Label("This app was made by Uroš Anđelić"));
                vBox.getChildren().add(new Label("Repository:"));
                TextField link = new TextField("https://github.com/UrosHCS/SoundButtonsMaven");
                link.setEditable(false);
                vBox.getChildren().add(link);
                Button btnOK = new Button("OK");
                btnOK.setOnAction((event) -> {
                    aboutStage.close();
                });
                vBox.getChildren().add(btnOK);

                aboutStage = new Stage();
                aboutStage.setScene(new Scene(vBox));
                aboutStage.show();
            } else {
                aboutStage.show();
            }
        });
    }

}
