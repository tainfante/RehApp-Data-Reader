package classes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import jfxutils.chart.ChartPanManager;
import jfxutils.chart.JFXChartUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import static java.lang.Math.*;

public class Emg implements Initializable {

    @FXML
    private LineChart emgChart;
    @FXML
    private LineChart fftChart;
    @FXML
    private StackPane pane;
    @FXML
    private NumberAxis emgxAxis;
    @FXML
    private NumberAxis emgyAxis;
    @FXML
    private NumberAxis fftxAxis;
    @FXML
    private NumberAxis fftyAxis;
    @FXML
    private ScrollBar scroll;
    XYChart.Series<Float, Float> rawSeries = new XYChart.Series<>();
    XYChart.Series<Float, Float> filteredSeries = new XYChart.Series<>();
    ArrayList<XYChart.Data<Double, Double>> fft = new ArrayList<>();
    XYChart.Series<Double, Double> fftSeries = new XYChart.Series<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emgChart.setCreateSymbols(false);
        fftChart.setCreateSymbols(false);
        emgChart.setTitle("Electromyography data");
        fftChart.setTitle("FFT");
        emgChart.setLegendVisible(true);
        fftChart.setLegendVisible(false);
        fftxAxis.setLabel("Frequency [Hz]");
        emgxAxis.setAutoRanging(false);
        emgyAxis.setAutoRanging(false);
        emgyAxis.setLowerBound(0);
        emgyAxis.setUpperBound(3.4);
        emgxAxis.setTickUnit(1);
        emgyAxis.setLabel("Voltage [V]");
        emgxAxis.setLabel("Time [s]");
   
        if(Controller.emg1.size()<10000){
            emgxAxis.setUpperBound(Controller.emg1.size()/1500);
            scroll.setVisible(false);

        }else{
            emgxAxis.setUpperBound(10000/1500);
            scroll.setMin(0);
            scroll.setMax(Math.ceil(Controller.emg1.size()/10000.0));
            scroll.setValue(0);
        }


        rawSeries.setName("Raw EMG data");
        filteredSeries.setName("Filtered EMG data");

        for (int i = 0; i < emgxAxis.getUpperBound()*1500; i++) {
            rawSeries.getData().add(Controller.emg1.get(i));
            filteredSeries.getData().add(Controller.emg2.get(i));
        }

        emgChart.getData().addAll(rawSeries,filteredSeries);

        double[] fftBuffer=getFFTbuffer(0);
        performFFT(fftBuffer);
        for (int i=0; i<fft.size(); i++){
            fftSeries.getData().add(fft.get(i));
        }
        fftChart.getData().add(fftSeries);

        ChartPanManager panner = new ChartPanManager(emgChart);
        panner.setMouseFilter(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) ;
            else {
                mouseEvent.consume();
            }
        });
        panner.start();
        JFXChartUtil.setupZooming(emgChart, mouseEvent -> {
            if (mouseEvent.getButton() != MouseButton.SECONDARY)
                mouseEvent.consume();
        });

    }

    public void oNMouseClicked(MouseEvent mouseEvent) {
        int position = (int) scroll.getValue();
        rawSeries.getData().clear();
        filteredSeries.getData().clear();
        emgxAxis.setLowerBound((float)10000*position/1500);
        emgxAxis.setUpperBound((float)10000*(position+1)/1500);
        for (int i = 10000*position; i < 10000*(position+1); i++) {
            rawSeries.getData().add(Controller.emg1.get(i));
            filteredSeries.getData().add(Controller.emg2.get(i));
        }
        double[] fftBuffer=getFFTbuffer(position);
        performFFT(fftBuffer);
        for (int i=0; i<fft.size(); i++){
            fftSeries.getData().add(fft.get(i));
        }

    }

    public double[] getFFTbuffer(int position){
        double[] buffer = new double[1024];
        int max = (int)(rawSeries.getData().stream().max(Comparator.comparing(XYChart.Data::getYValue)).get().getXValue()*1500);
        int start = max-512;
        for(int i = 0; i<1024; i++){
            buffer[i]=(double)Controller.emg1.get(start+i).getYValue();
        }
        return buffer;
    }

    public void performFFT(double[]input){
        fft.clear();
        Complex[] cinput = new Complex[input.length];
        for (int i = 0; i < input.length; i++)
            cinput[i] = new Complex(input[i], 0.0);

        Complex[] coutput = fft(cinput);

        double[] magnitudes = new double[input.length];
        for(int i=0; i<input.length; i++){
            magnitudes[i] = sqrt(pow(coutput[i].re(), 2)+pow(coutput[i].im(), 2))*2/input.length;
        }
        double[] freq = new double[511];
        for (int i=0; i<freq.length; i++){
            freq[i]= (i+1)*1500/input.length;
            fft.add(new XYChart.Data<>(freq[i], magnitudes[i+1]));
        }
    }

    public static Complex[] fft(Complex[] x) {
        int n = x.length;

        // base case
        if (n == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // compute FFT of even terms
        Complex[] even = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] evenFFT = fft(even);

        // compute FFT of odd terms
        Complex[] odd  = even;  // reuse the array (to avoid n log n space)
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] oddFFT = fft(odd);

        // combine
        Complex[] y = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = evenFFT[k].plus (wk.times(oddFFT[k]));
            y[k + n/2] = evenFFT[k].minus(wk.times(oddFFT[k]));
        }
        return y;
    }

}
