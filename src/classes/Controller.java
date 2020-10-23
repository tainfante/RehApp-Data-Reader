package classes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class Controller {

    @FXML
    private TextField nameText;
    @FXML
    private TextField lastNameText;
    @FXML
    private Label accLabel;
    @FXML
    private Label emgLabel;

    public static ArrayList<Double> accx = new ArrayList<>();
    public static ArrayList<Double> accy = new ArrayList<>();
    public static ArrayList<Double> accz = new ArrayList<>();
    public static ArrayList<Double> gyrx = new ArrayList<>();
    public static ArrayList<Double> gyry = new ArrayList<>();
    public static ArrayList<Double> gyrz = new ArrayList<>();
    public static ArrayList<XYChart.Data<Float, Float>> emg1 = new ArrayList<>();
    public static ArrayList<XYChart.Data<Float, Float>> emg2 = new ArrayList<>();
    public static String name = new String();
    public static String lastname = new String();


    public void onClickEMGButton(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open EMG File");
        File emgFile = fileChooser.showOpenDialog(Main.getPrimaryStage());
        if (emgFile != null) {
            BufferedReader br = new BufferedReader(new FileReader(emgFile));
            String st;
            int k=0;
            while ((st = br.readLine()) != null) {
                if (st.equals("")) ;
                else {
                    String[] strArr = st.split("\\s+");
                    k++;
                    for (int i = 0; i < strArr.length; i++) {
                        float parsedToFloat;
                        parsedToFloat = (float)(Float.parseFloat(strArr[i])*3.3/4095);
                        switch (i) {
                            case 0:
                                emg1.add(new XYChart.Data<>((float)k/1500, parsedToFloat));
                                break;
                            case 1:
                                emg2.add(new XYChart.Data<>((float)k/1500, parsedToFloat));
                                break;
                        }
                    }
                }
            }
            emgLabel.setText("File loaded");
        }
    }

    public void onClickACCButton(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open ACC File");
        File accFile = fileChooser.showOpenDialog(Main.getPrimaryStage());
        if (accFile != null) {
            BufferedReader br = new BufferedReader(new FileReader(accFile));
            String st;
            while ((st = br.readLine()) != null) {
                if (st.equals("")) ;
                else {
                    String[] strArr = st.split("\\s+");
                    for (int i = 0; i < strArr.length; i++) {
                       double parsedToDouble;
                        parsedToDouble = Double.parseDouble(strArr[i]);
                        switch (i) {
                            case 0:
                                accx.add(parsedToDouble);
                                break;
                            case 1:
                                accy.add(parsedToDouble);
                                break;
                            case 2:
                                accz.add(parsedToDouble);
                                break;
                            case 3:
                                gyrx.add(parsedToDouble);
                                break;
                            case 4:
                                gyry.add(parsedToDouble);
                                break;
                            case 5:
                                gyrz.add(parsedToDouble);
                                break;
                        }
                    }
                }
            }
            accLabel.setText("File loaded");
        }
    }

    public void onClickGenerateButton(ActionEvent actionEvent) {

        name=nameText.getText();
        lastname=lastNameText.getText();
        if(name.equals("")||lastname.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wrong data");
            alert.setHeaderText("You haven't submitted name or last name");
            alert.setContentText("Please, set some name");
            alert.showAndWait();
        }

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("main_charts.fxml"));
            Stage stage = new Stage();
            stage.setTitle("RehApp data");
            stage.setScene(new Scene(root, 600, 450));
            stage.setMinHeight(450.0);
            stage.setMinWidth(600.0);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
