package algorithms;

import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;

/**
 * @author Ritwik Banerjee
 */
public abstract class Clusterer implements Algorithm {

    protected final int numberOfClusters;

    protected LineChart chart;
    protected Button btnrun;
    protected Button btnScrshot;
    protected int clicktimes;
    protected boolean isInProgress;

    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    public Clusterer(int k) {
        if (k < 2)
            k = 2;
        else if (k > 4)
            k = 4;
        numberOfClusters = k;
    }

    public boolean isAlgorithInProgress() {
        return isInProgress;
    }

    public void setBtnScrshot(Button btnScrshot) {
        this.btnScrshot = btnScrshot;
    }

    public void setLineChart(LineChart chart) {
        this.chart = chart;
    }

    public void setBtnRun(Button btnrun) {
        this.btnrun = btnrun;
    }

}