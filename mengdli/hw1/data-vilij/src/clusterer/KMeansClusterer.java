package clusterer;

import algorithms.Clusterer;
import data.DataSet;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */


public class KMeansClusterer extends Clusterer {
    public interface IntervalEventHandler {
        void handleInterval(DataSet dataSet, int iteration);
    }

    public interface FinishedEventHandler{
        void handledFinishedEvents();
    }

    //public interface IterationEventHandler{
    //    void handledIterationEvents();
    //}

    private int iteration;
    private ArrayList<IntervalEventHandler> intervalEventHandlers = new ArrayList<>();
    public void addIntervalEventHandler(IntervalEventHandler handler) {
        intervalEventHandlers.add(handler);
    }


    private ArrayList<FinishedEventHandler> finishedEventHandlers = new ArrayList<>();
    public void addFinishedEventHandler(FinishedEventHandler finishedEventHandler){
        finishedEventHandlers.add(finishedEventHandler);
    }
    public void sendFinishedEvent(){
        for(FinishedEventHandler finishedEventHandler : finishedEventHandlers){
            finishedEventHandler.handledFinishedEvents();
        }
    }

    public void continueAlogrithm(){
       synchronized (this){
           notifyAll();
       }
    }

    private void generateintervalEvent() {
        try {
            synchronized (this){
                wait(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (IntervalEventHandler handler : intervalEventHandlers) {
           // Platform.runLater(() -> {
                handler.handleInterval(dataset, iteration);
            //});
        }
    }

//    private void generateIterationEvent() {
//        try {
//            synchronized (this){
//                wait();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        for (IntervalEventHandler handler : iteraionEventHandlers) {
//            // Platform.runLater(() -> {
//            handler.handleInterval(dataset, iteration);
//            //});
//        }
//    }

    private DataSet dataset;
    private List<Point2D> centroids;

    private final int maxIterations;
    private final int updateInterval;
    private final AtomicBoolean tocontinue;
    private boolean toContinueBool;

    private LineChart chart;
    private XYChart.Series series;

    public KMeansClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters, boolean tocontinue) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.toContinueBool = tocontinue;
    }

    public void setTocontinue(boolean tocontinue) {
        this.tocontinue.set(tocontinue);
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


    @Override
    public void run() {
        initializeCentroids();
         iteration = 0;
        while (iteration++ < maxIterations ) {
            assignLabels();
            recomputeCentroids();

            // Send event to update chart
            if (iteration % updateInterval == 0) {
                generateintervalEvent();
            }

            if(!toContinueBool){
                try {
                    synchronized (this){
                        wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        sendFinishedEvent();
    }


    private void initializeCentroids() {
        Set<String> chosen = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataset.getLabels().keySet());
        Random r = new Random();
        while (chosen.size() < numberOfClusters) {
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i)))
                //++i;
                i = (++i % instanceNames.size());
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataset.getLocations().get(name)).collect(Collectors.toList());
        // tocontinue.set(true);
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            double minDistance = Double.MAX_VALUE;
            int minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataset.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {
        tocontinue.set(false);
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataset.getLabels()
                    .entrySet()
                    .stream()
                    .filter(entry -> i == Integer.parseInt(entry.getValue()))
                    .map(entry -> dataset.getLocations().get(entry.getKey()))
                    .reduce(new Point2D(0, 0), (p, q) -> {
                        clusterSize.incrementAndGet();
                        return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                    });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                tocontinue.set(true);
            }
        });
    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

}