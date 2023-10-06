package clusterer;

import algorithms.Clusterer;
import data.DataSet;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer {

    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;
    private final int numberOfClusters;
    private final AtomicBoolean tocontinue;



    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters, boolean tocontinue) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.numberOfClusters = numberOfClusters;
        this.tocontinue = new AtomicBoolean(tocontinue);
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

    public int getNumberOfClusters() {
        return numberOfClusters;

    }

    private XYChart.Series[] seriesArray;

    @Override
    public void run() {
        //chart.getData().clear();
        if (tocontinue.get()) {
            Platform.runLater(() -> {
                btnrun.setDisable(true);
                btnScrshot.setDisable(true);
            });

            for (int i = 0; i < maxIterations; i++) {
                Platform.runLater(() -> {
                    runClusterOnce();
                });
                if (i % updateInterval == 0) {
                    Platform.runLater(() -> {
                        chart.getData().clear();
                        for (int a = 0; a < numberOfClusters; a++) {
                            chart.getData().add(seriesArray[a]);
                            Node n = seriesArray[a].getNode();
                            n.setStyle(" -fx-stroke: transparent;");
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
            Platform.runLater(() -> {
                btnrun.setDisable(false);
                btnScrshot.setDisable(false);
            });
        } else {
            clicktimes++;
            if (clicktimes <= maxIterations) {
                isInProgress = true;
                Platform.runLater(() -> {
                    runClusterOnce();
                });
                if (clicktimes % updateInterval == 0) {
                    Platform.runLater(() -> {
                        chart.getData().clear();
                        for (int a = 0; a < numberOfClusters; a++) {
                            chart.getData().add(seriesArray[a]);
                            Node n = seriesArray[a].getNode();
                            n.setStyle(" -fx-stroke: transparent;");
                        }
                        btnScrshot.setDisable(false);
                    });
                }

            } else {
                isInProgress = false;
                btnScrshot.setDisable(true);
            }
        }
    }



    private void runClusterOnce() {
        // Generate the labels
        String[] Ramdomlabels = new String[numberOfClusters];
        seriesArray = new XYChart.Series[numberOfClusters];

        for (int i = 0; i < numberOfClusters; i++) {
            Ramdomlabels[i] = "" + i;

            // Create a new series into chart
            XYChart.Series series = new XYChart.Series<>();
            seriesArray[i] = series;
        }

        String[] instanceNames = dataset.getLabels().keySet().toArray(new String[dataset.getLabels().size()]);

        // for each point:
        for (int i = 0; i < dataset.getLabels().size(); i++) {

            // 1. randomly assign a label from the labels array to a point
            String label = "";
            int randomInderx = new Random().nextInt(numberOfClusters);
            label = Ramdomlabels[randomInderx];
            String instantname = instanceNames[i];
            dataset.updateLabel(instantname, label);

            // 2. put the point into a series
            Point2D point2D = dataset.getLocations().get(instantname);
            seriesArray[randomInderx].getData().add(new XYChart.Data<>(point2D.getX(), point2D.getY()));
        }
    }

}