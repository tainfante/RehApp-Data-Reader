package classes;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import jfxutils.chart.ChartPanManager;
import jfxutils.chart.JFXChartUtil;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class Acc implements Initializable{

    @FXML
    private LineChart accChart;
    @FXML
    private LineChart gyrChart;
    @FXML
    NumberAxis accyAxis = new NumberAxis();
    @FXML
    NumberAxis accxAxis = new NumberAxis();
    @FXML
    NumberAxis gyryAxis = new NumberAxis();
    @FXML
    NumberAxis gyrxAxis = new NumberAxis();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accChart.setTitle("Accelerometer data");
        gyrChart.setTitle("Gyroscope data");
        accChart.setCreateSymbols(false);
        gyrChart.setCreateSymbols(false);
        XYChart.Series<Double, Double> accxSeries = new XYChart.Series<>();
        XYChart.Series<Double, Double> accySeries = new XYChart.Series<>();
        XYChart.Series<Double, Double> acczSeries = new XYChart.Series<>();
        XYChart.Series<Double, Double> gyrxSeries = new XYChart.Series<>();
        XYChart.Series<Double, Double> gyrySeries = new XYChart.Series<>();
        XYChart.Series<Double, Double> gyrzSeries = new XYChart.Series<>();
        accxSeries.setName("Accelerometer X");
        accySeries.setName("Accelerometer Y");
        acczSeries.setName("Accelerometer Z");
        gyrxSeries.setName("Gyroscope X");
        gyrySeries.setName("Gyroscope Y");
        gyrzSeries.setName("Gyroscope Z");
        accChart.setLegendVisible(true);
        gyrChart.setLegendVisible(true);
        accyAxis.setAutoRanging(false);
        gyryAxis.setAutoRanging(false);
        accxAxis.setLowerBound(0);
        accyAxis.setLowerBound(-2.0);
        accyAxis.setUpperBound(2.0);
        accyAxis.setTickUnit(1.0);
        accyAxis.setLabel("acceleration [g]");
        accxAxis.setLabel("time [s]");
        gyryAxis.setLowerBound(-245.0);
        gyryAxis.setUpperBound(245.0);
        gyryAxis.setLabel("angular velocity [dps]");
        gyrxAxis.setLabel("time [s]");

        Thread plottingThread = new Thread(() -> {

            for(int i=0; i<Controller.accx.size(); i++){
                accxSeries.getData().add(new XYChart.Data<>((double)(i+1)/26, Controller.accx.get(i)*2/32768));
                accySeries.getData().add(new XYChart.Data<>((double)(i+1)/26, Controller.accy.get(i)*2/32768));
                acczSeries.getData().add(new XYChart.Data<>((double)(i+1)/26, Controller.accz.get(i)*2/32768));
                gyrxSeries.getData().add(new XYChart.Data<>((double)(i+1)/26, Controller.gyrx.get(i)*245/32768));
                gyrySeries.getData().add(new XYChart.Data<>((double)(i+1)/26, Controller.gyry.get(i)*245/32768));
                gyrzSeries.getData().add(new XYChart.Data<>((double)(i+1)/26, Controller.gyrz.get(i)*245/32768));

                System.out.println(i);
            }

        });
        plottingThread.setDaemon(true);
        plottingThread.setName("Loading  acc data thread");
        plottingThread.start();

        accChart.getData().addAll(accxSeries, accySeries, acczSeries);
        gyrChart.getData().addAll(gyrxSeries, gyrySeries, gyrzSeries);

        ChartPanManager panner = new ChartPanManager(accChart);
        panner.setMouseFilter(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY);
            else {
                mouseEvent.consume();
            }
        });
        panner.start();
        JFXChartUtil.setupZooming(accChart, mouseEvent -> {
            if (mouseEvent.getButton() != MouseButton.SECONDARY)
                mouseEvent.consume();
        });

        ChartPanManager pannergyr = new ChartPanManager(gyrChart);
        pannergyr.setMouseFilter(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY);
            else {
                mouseEvent.consume();
            }
        });
        pannergyr.start();
        JFXChartUtil.setupZooming(gyrChart, mouseEvent -> {
            if (mouseEvent.getButton() != MouseButton.SECONDARY)
                mouseEvent.consume();
        });
    }
}
