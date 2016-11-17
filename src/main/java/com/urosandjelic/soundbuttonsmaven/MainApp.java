package com.urosandjelic.soundbuttonsmaven;

import com.sun.javafx.stage.StageHelper;
import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("Sound Buttons");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(
                (e) -> {
                    //If Sound recorder is open (visible) alert the user
                    if (StageHelper.getStages().size() > 1) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Sound Recorder is open!");
                        alert.setHeaderText("Unsaved recordings will be lost!");
                        alert.setContentText("Are you shure you want to exit?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            System.exit(0);
                        } else {
                            e.consume();
                        }
                    }
                }
        );
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
