package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    public static class DuplicateDataNameException extends Exception {

        public DuplicateDataNameException(String name) {
            super(String.format("Duplicated name '%s'.", name));
        }
    }

    private Map<String, String> dataLabels;
    private Map<String, Point2D> dataPoints;

    public Map<String, String>  getDataLabels(){
        return dataLabels;
    }
    public Map<String, Point2D> getDataPoints(){
        return dataPoints;
    }

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    private int lineNum;
    private ArrayList<String> names;


    public AtomicBoolean hadAnError = new AtomicBoolean(false);

    public void processString(String tsdString) throws Exception {
        lineNum = 1;
        names = new ArrayList<String>();
//        AtomicBoolean hadAnError = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {
                        String name = checkedname(list.get(0));
                        String label = list.get(1);
                        String[] pair = list.get(2).split(",");
                        Point2D point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                        dataLabels.put(name, label);
                        dataPoints.put(name, point);
                        lineNum++;
                        hadAnError.set(false);

                    } catch (Exception e) {
                        errorMessage.setLength(0);
                        errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                        errorMessage.append(", on line #: ").append(lineNum);
                        hadAnError.set(true);
                    }
                });

        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */

    void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            chart.getData().add(series);

            //set series color
            series.getNode().setStyle(" -fx-stroke: transparent;");

            for (XYChart.Data<Number, Number> d : series.getData()) {
                Tooltip.install(d.getNode(), new Tooltip(findName(d)));
            }
        }
    }

    private String findName(XYChart.Data<Number, Number> d) {
        String name = "";
        String[] keys = dataPoints.keySet().toArray(new String[dataPoints.size()]);
        //dataPoints.keySet().toArray(keys);
        for (int i = 0; i < dataPoints.size(); i++) {
            Point2D point = dataPoints.get(keys[i]);
            if (point.getX() == d.getXValue().doubleValue() && point.getY() == d.getYValue().doubleValue()) {
                name = keys[i];
                break;
            }
        }
        return name;
    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException, DuplicateDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        if (names.contains(name)) {
            throw new DuplicateDataNameException(name);
        }
        names.add(name);
        return name;
    }
}
