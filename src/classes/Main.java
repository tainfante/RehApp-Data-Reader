package classes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage pStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main_window.fxml"));
        primaryStage.setTitle("RehApp Data Reader");
        primaryStage.setScene(new Scene(root, 320, 325));
        primaryStage.setMinHeight(335.0);
        primaryStage.setMinWidth(320.0);
        primaryStage.show();

        pStage=primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return pStage;
    }
}
