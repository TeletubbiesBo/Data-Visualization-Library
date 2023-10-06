
package classification;

import algorithms.Classifier;
import data.DataSet;
import javafx.application.Platform;
import javafx.scene.PointLight;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import ui.AppUI;
import javafx.geometry.Point2D;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;
    private boolean isInProgress = false;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

    private LineChart chart;
    private Button btnrun;
    private Button btnScrshot;

    private int clicktimes;

    public void setBtnScrshot(Button btnScrshot) {
        this.btnScrshot = btnScrshot;
    }

    public int getclicktimes() {
        return clicktimes;
    }

    public boolean isAlgorithInProgress() {
        return isInProgress;
    }

    public void setLineChart(LineChart chart) {
        this.chart = chart;
        chart.getData();
    }
    
    public void setBtnRun(Button btnrun) {
        this.btnrun = btnrun;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
    }

    XYChart.Series series = null;

    @Override
    public void run() {
        //Platform.runLater(()->);
        for (int i = 0; i < chart.getData().size(); i++) {
            if (((XYChart.Series) (chart.getData().get(i))).getName() == "algorithmseries") {
                series = (XYChart.Series) (chart.getData().get(i));
                break;
            }
        }
        if (series == null) {
            series = new XYChart.Series();
            series.setName("algorithmseries");
            Platform.runLater(() ->
                    chart.getData().add(series));
        }

        if (tocontinue()) {
            Platform.runLater(()->btnrun.setDisable(true));

            for (int i = 1; i <= maxIterations && tocontinue(); i++) {
                int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
                int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
                int constant = new Double(RAND.nextDouble() * 100).intValue();

                // this is the real output of the classifier
                output = Arrays.asList(xCoefficient, yCoefficient, constant);

                // everything below is just for internal viewing of how the output is changing
                // in the final project, such changes will be dynamically visible in the UI
                if (i % updateInterval == 0) {
                    System.out.printf("Iteration number %d: ", i);
                    flush();
                    drawline();
                    try {
                        //Platform.runLater(this);
                        Thread.sleep(400);
                    } catch (InterruptedException e) {}
                }
                if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                    System.out.printf("Iteration number %d: ", i);
                    flush();
                    break;
                }
            }
            Platform.runLater(()->btnrun.setDisable(false));
            //btnrun.setDisable(true);
            btnScrshot.setDisable(false);
        } else {
            // not continue
            clicktimes++;
            if (clicktimes <= maxIterations) {
                isInProgress = true;
                int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
                int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
                int constant = new Double(RAND.nextDouble() * 100).intValue();
                output = Arrays.asList(xCoefficient, yCoefficient, constant);
                if (clicktimes % updateInterval == 0) {
                    flush();
                    drawline();
                }
                btnScrshot.setDisable(false);
            } else {
                isInProgress = false;
                btnScrshot.setDisable(true);
            }
        }
    }

    private void drawline() {
        //a*x + b*y + c = 0;
        double a = output.get(0);
        double b = output.get(1);
        double c = output.get(2);
        double ymin = (-c - a * min()) / b;
        double ymax = (-c - a * max()) / b;

        Platform.runLater(() -> {
            series.getData().clear();
            series.getData().add(new XYChart.Data<>(min(), ymin));
            series.getData().add(new XYChart.Data<>(max(), ymax));
        });
    }

    private double max() {
        //dataset.getLocations().keySet();
        double max = Double.MIN_VALUE;
        Point2D[] points = dataset.getLocations().values().toArray(new Point2D[dataset.getLocations().values().size()]);
        if (points.length > 0) {
            max = points[0].getX();
            for (int i = 0; i < points.length; i++) {
                if (max < points[i].getX()) {
                    max = points[i].getX();
                }
            }
        }
        return max;
    }

    private double min() {
        double min = Double.MAX_VALUE;
        Point2D[] points = dataset.getLocations().values().toArray(new Point2D[dataset.getLocations().values().size()]);
        if (points.length > 0) {
            min = points[0].getX();
            for (int i = 0; i < points.length; i++) {
                if (min > points[i].getX()) {
                    min = points[i].getX();
                }
            }
        }
        return min;
    }

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /**
     * A placeholder main method to just make sure this code runs smoothly
     */
//    public static void main(String... args) throws IOException {
//        DataSet dataset = DataSet.fromTSDFile(Paths.get("/Users/limengdong/Desktop/h2222/mengdli/hw1/data-vilij/resources/data/sample-data.tsd"));//"/path/to/some-data.tsd"));
//        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
//        classifier.run(); // no multithreading yet
//    }
}
