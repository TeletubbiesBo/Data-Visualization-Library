
package ui;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.Classifier;
import algorithms.Clusterer;
import classification.RandomClassifier;
import clusterer.KMeansClusterer;
import clusterer.RandomClusterer;
import data.DataSet;
import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;
import javafx.scene.chart.XYChart;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
//import java.lang.String;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /**
     * The application to which this class of actions belongs.
     */
    ApplicationTemplate applicationTemplate;
    int linesread;
    @SuppressWarnings("FieldCanBeLocal")
    private Button scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    private Button displayButton;  // workspace button to display data on the chart
    private TextArea textArea;       // text area for new data input
    private boolean hasNewText;     // whether or not the text area has any new data since last display
    private CheckBox chkEnableTextArea;
    private ToggleButton toggle = new ToggleButton("Done");
    private Label lableDataInfo = new Label();
    private GridPane paneAlgorithmType = new GridPane();
    private Label labelalgorithmType = new Label("Algorithm Type");
    private Button btnClassification = new Button("Classification");
    private Button btnCluster = new Button("Cluster");
    private RadioButton rdoClassificationA = new RadioButton("RandomClassifier");
    //private RadioButton rdoClassificationB = new RadioButton("Algorithm B");
    //private RadioButton rdoClassificationC = new RadioButton("Algorithm C");
    private RadioButton rdoClusteringA = new RadioButton("RandomClusterer");
    private RadioButton rdoClusteringB = new RadioButton("KMeansClusterer");
    //private RadioButton rdoClusteringC = new RadioButton("Algorithm C");
    private Button btnClassificationA = new Button(null, new ImageView(new Image("gui/icons/gearbox.png", 25, 25, true, true)));
    //private Button btnClassificationB = new Button(null, new ImageView(new Image("gui/icons/gearbox.png", 25, 25, true, true)));
    //private Button btnClassificationC = new Button(null, new ImageView(new Image("gui/icons/gearbox.png", 25, 25, true, true)));
    private Button btnClusteringA = new Button(null, new ImageView(new Image("gui/icons/gearbox.png", 25, 25, true, true)));
    private Button btnClusteringB = new Button(null, new ImageView(new Image("gui/icons/gearbox.png", 25, 25, true, true)));
    //private Button btnClusteringC = new Button(null, new ImageView(new Image("gui/icons/gearbox.png", 25, 25, true, true)));
    private Button btnrun = new Button(null, new ImageView((new Image("gui/icons/runicon.png", 25, 25, true, true))));

    private AlgorithmType algorithmType;
    private HashMap<AlgorithmName, AlgorithmSetting> algorithmSettings = new HashMap<AlgorithmName, AlgorithmSetting>();

    private int maxIterval;
    private int upate;
    private boolean toCountious;
    private DataSet dataset = null;
    private RandomClassifier classifier;
    private RandomClusterer randomClusterer;
    private KMeansClusterer kMeansClusterer;


    private int numberofClusterer;

    private Thread threadClassifier;
    private Thread threadrandomClusterer;
    private Thread threadkMeansClusterer;

    public Thread getThreadClassifier() {
        return threadClassifier;
    }

    public Thread getThreadrandomClusterer() {
        return threadrandomClusterer;
    }

    public Thread getThreadkmeanCLusterer(){
        return threadkMeansClusterer;
    }

    private Stage stageConfig = new Stage();
    private TextField txtMaxIter = new TextField();
    private TextField txtInterval = new TextField();
    private TextField txtNumCluster = new TextField();
    private CheckBox chkContinousRun = new CheckBox();

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    public void showToggle(boolean enable) {
        toggle.setVisible(enable);
    }

    public void showlableDataInfo(boolean enable) {
        lableDataInfo.setVisible(enable);
    }

    public void showpaneAlgorithmType(boolean enable) {
        paneAlgorithmType.setVisible(enable);
    }

    public void disablebtnClassification(boolean enable) {
        btnClassification.setDisable(enable);
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        String scrnshoticonPath = String.join(SEPARATOR,
                iconsPath,
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_ICON.name()));
        scrnshotButton = setToolbarButton(scrnshoticonPath,
                manager.getPropertyValue(AppPropertyTypes.SCREENSHOT_TOOLTIP.name()),
                true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> {
            applicationTemplate.getActionComponent().handleLoadRequest();

            lblClustering.setVisible(false);
            rdoClusteringA.setVisible(false);
            rdoClusteringB.setVisible(false);
            //rdoClusteringC.setVisible(false);
            btnClusteringA.setVisible(false);
            btnClusteringB.setVisible(false);
            //btnClusteringC.setVisible(false);

            lblClassification.setVisible(false);
            rdoClassificationA.setVisible(false);
            //rdoClassificationB.setVisible(false);
            //rdoClassificationC.setVisible(false);
            btnClassificationA.setVisible(false);
            //btnClassificationB.setVisible(false);
            //btnClassificationC.setVisible(false);
        });
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());

        scrnshotButton.setOnAction(event -> {
            try {
                ((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (Exception IOException) {
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
        ClearChart();
        getPrimaryWindow().setOnCloseRequest(e -> {
            stageConfig.close();
        });

        AlgorithmName[] algorithmNames = AlgorithmName.values();
        for (int i = 0; i < algorithmNames.length; i++) {
            algorithmSettings.put(algorithmNames[i], new AlgorithmSetting(-1, -1, -1, false));
        }
    }

    public void enableSaveButton(boolean enable) {
        saveButton.setDisable(!enable);
    }

    public void enableScrnShotButton(boolean enable) {
        scrnshotButton.setDisable(!enable);
    }

    @Override
    public void clear() {
        while (textArea.getText().length() > 0)
            textArea.clear();
        chart.getData().clear();
        ClearChart();
        scrnshotButton.setDisable(true);
    }

    public String getCurrentText() {
        return textArea.getText();
    }

    public void setTextArea(String s) {
        textArea.setText(s);
    }

    public void inputTextintolableDataInfo(String context) {
        lableDataInfo.setText(context);

        lableDataInfo.setMinHeight(20 * context.split("\n").length);
    }

    AlgorithmName algorithmNameForConfigWindow;

    private Point2D changTo2D(String s) {
        String[] str = s.trim().split(",");
        return new Point2D(Double.parseDouble(str[0]), Double.parseDouble(str[1]));
    }

    Label lblClustering = new Label("Clustering");
    Label lblClassification = new Label("Classification");


    private Class classAlgorithm;
    private Constructor constructor;


    private Label lblIteration = new Label();

    private void layout() {
        PropertyManager manager = applicationTemplate.manager;
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setTitle(manager.getPropertyValue(AppPropertyTypes.CHART_TITLE.name()));

        VBox leftPanel = new VBox(8);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPadding(new Insets(10));

        VBox.setVgrow(leftPanel, Priority.ALWAYS);
        leftPanel.setMaxSize(windowWidth * 0.29, windowHeight * 0.3);
        leftPanel.setMinSize(windowWidth * 0.29, windowHeight * 0.3);

        Text leftPanelTitle = new Text(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLE.name()));
        String fontname = manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLEFONT.name());
        Double fontsize = Double.parseDouble(manager.getPropertyValue(AppPropertyTypes.LEFT_PANE_TITLESIZE.name()));
        leftPanelTitle.setFont(Font.font(fontname, fontsize));

        textArea = new TextArea();
        textArea.setMinHeight(150);

        HBox processButtonsBox = new HBox();
        displayButton = new Button(manager.getPropertyValue(AppPropertyTypes.DISPLAY_BUTTON_TEXT.name()));
        HBox.setHgrow(processButtonsBox, Priority.ALWAYS);
        chkEnableTextArea = new CheckBox();
        chkEnableTextArea.setOnAction(e -> {
                    textArea.setDisable(chkEnableTextArea.isSelected());
                }
        );
        chkEnableTextArea.setText("Disable TextArea");
        processButtonsBox.getChildren().addAll(displayButton, chkEnableTextArea);

        lblIteration.setText("test...");
        StackPane rightPanel = new StackPane(chart);
        rightPanel.setMaxSize(windowWidth * 0.69, windowHeight * 0.69);
        rightPanel.setMinSize(windowWidth * 0.69, windowHeight * 0.69);
        StackPane.setAlignment(rightPanel, Pos.CENTER);

        workspace = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(workspace, Priority.ALWAYS);

        appPane.getChildren().add(workspace);
        VBox.setVgrow(appPane, Priority.ALWAYS);

        // hw4 Use Case 1
        displayButton.setVisible(false);
        textArea.setVisible(false);
        chkEnableTextArea.setVisible(false);
        leftPanelTitle.setVisible(false);
        showlableDataInfo(false);
        showpaneAlgorithmType(false);
        newButton.setDisable(false);
        toggle.setVisible(false);

        paneAlgorithmType.add(labelalgorithmType, 0, 0);
        paneAlgorithmType.add(btnClassification, 0, 1);
        paneAlgorithmType.add(btnCluster, 0, 2);

        //algorithm
        GridPane gpClassification = new GridPane();
        gpClassification.setVisible(false);
        gpClassification.add(lblClassification, 0, 0);

        gpClassification.add(rdoClassificationA, 0, 1);
        gpClassification.add(btnClassificationA, 1, 1);
        //gpClassification.add(rdoClassificationB, 0, 2);
        //gpClassification.add(btnClassificationB, 1, 2);
        //gpClassification.add(rdoClassificationC, 0, 3);
        //gpClassification.add(btnClassificationC, 1, 3);

        GridPane gpClustering = new GridPane();
        gpClustering.setVisible(false);

        gpClustering.add(lblClustering, 0, 0);
        gpClustering.add(rdoClusteringA, 0, 1);
        gpClustering.add(rdoClusteringB, 0, 2);
        //gpClustering.add(rdoClusteringC, 0, 3);
        gpClustering.add(btnClusteringA, 1, 1);
        gpClustering.add(btnClusteringB, 1, 2);
        //gpClustering.add(btnClusteringC, 1, 3);

        leftPanel.getChildren().addAll(leftPanelTitle, textArea, toggle, processButtonsBox,
                lableDataInfo, paneAlgorithmType, gpClassification, gpClustering, btnrun);

        btnrun.setVisible(false);

        rdoClassificationA.setOnAction(e -> handleClassificationAlgorithmSelection());
        //rdoClassificationB.setOnAction(e -> handleClassificationAlgorithmSelection());
        //rdoClassificationC.setOnAction(e -> handleClassificationAlgorithmSelection());
        rdoClusteringA.setOnAction(e -> {
            handleClusteringAlgorithmSelection();
            algorithmName = manager.getPropertyValue(AppPropertyTypes.RandomClassifier.name());
        });
        rdoClusteringB.setOnAction(e -> {
            handleClusteringAlgorithmSelection();
            algorithmName = manager.getPropertyValue(AppPropertyTypes.KmeanClusterer.name());
        });
        //rdoClusteringC.setOnAction(e -> handleClusteringAlgorithmSelection());

        btnClassificationA.setOnAction(event -> {

            algorithmNameForConfigWindow = AlgorithmName.ClassificationA;
            if (algorithmSettings.get(algorithmNameForConfigWindow).Max >= 0)
                txtMaxIter.setText(algorithmSettings.get(algorithmNameForConfigWindow).Max + "");
            stageConfig.show();

        });
//        btnClassificationB.setOnAction(event -> {
//            algorithmNameForConfigWindow = AlgorithmName.ClassificationB;
//            loadSettingsToConfigWindow();
//        });
//        btnClassificationC.setOnAction(event -> {
//            algorithmNameForConfigWindow = AlgorithmName.ClassificationC;
//            loadSettingsToConfigWindow();
//        });
        btnClusteringA.setOnAction(event -> {
            algorithmNameForConfigWindow = AlgorithmName.ClusteringA;
            loadSettingsToConfigWindow();
        });
        btnClusteringB.setOnAction(event -> {
            algorithmNameForConfigWindow = AlgorithmName.ClusteringB;
            loadSettingsToConfigWindow();
        });
//        btnClusteringC.setOnAction(event -> {
//            algorithmNameForConfigWindow = AlgorithmName.ClusteringC;
//            loadSettingsToConfigWindow();
//        });

        btnClassification.setOnAction(e -> {

            NumOfCluster.setVisible(false);
            txtNumCluster.setVisible(false);

            lblClassification.setVisible(true);
            rdoClassificationA.setVisible(true);
            //rdoClassificationB.setVisible(true);
            //rdoClassificationC.setVisible(true);
            btnClassificationA.setVisible(true);
//            btnClassificationB.setVisible(true);
//            btnClassificationC.setVisible(true);

            gpClassification.setVisible(true);
            paneAlgorithmType.setVisible(false);
            algorithmType = AlgorithmType.Classification;
        });


        btnCluster.setOnAction(event -> {
            //show number of cluster
            NumOfCluster.setVisible(true);
            txtNumCluster.setVisible(true);

            lblClustering.setVisible(true);
            rdoClusteringA.setVisible(true);
            rdoClusteringB.setVisible(true);
            //rdoClusteringC.setVisible(true);
            btnClusteringA.setVisible(true);
            btnClusteringB.setVisible(true);
            //btnClusteringC.setVisible(true);

            gpClustering.setVisible(true);
            paneAlgorithmType.setVisible(false);
            algorithmType = AlgorithmType.Clustering;
        });

        final ToggleGroup groupClassification = new ToggleGroup();
        final ToggleGroup groupClustering = new ToggleGroup();
        rdoClassificationA.setToggleGroup(groupClassification);
        //rdoClassificationB.setToggleGroup(groupClassification);
        //rdoClassificationC.setToggleGroup(groupClassification);

        rdoClusteringA.setToggleGroup(groupClustering);
        rdoClusteringB.setToggleGroup(groupClustering);
        //rdoClusteringC.setToggleGroup(groupClustering);


        toggle.setOnAction(event -> {

            //can edit
            if (toggle.getText() == "Done") {
                //text to dataset
                dataset = new DataSet();
                String[] textStrLines = textArea.getText().split("\n");
                for (int i = 0; i < textStrLines.length; i++) {
                    String[] Strline = textStrLines[i].split("\t");
                    try {
                        dataset.getLabels().put((Strline[0]), Strline[1]);
                        dataset.getLocations().put(Strline[0], changTo2D(Strline[2]));
                    } catch (Exception e) {
                    }
                }

                toggle.setText("Edit");
                textArea.setDisable(true);

                //processdata
                ((AppData) applicationTemplate.getDataComponent()).processDataString(textArea.getText(), "");
                textArea.setVisible(true);
            }
            //cannot edit
            else {
                toggle.setText("Done");
                textArea.setDisable(false);
                lableDataInfo.setVisible(false);
                paneAlgorithmType.setVisible(false);
                gpClassification.setVisible(false);
                gpClustering.setVisible(false);
                btnrun.setVisible(false);
            }
        });

        stageConfig.setOnCloseRequest(event -> {
            try {
                maxIterval = Integer.parseInt(txtMaxIter.getText());
                upate = Integer.parseInt(txtInterval.getText());
                toCountious = chkContinousRun.isSelected();

                if (algorithmType == AlgorithmType.Clustering) {
                    numberofClusterer = Integer.parseInt(txtNumCluster.getText());
                }

                if (maxIterval > 0 && upate > 0 &&
                        (algorithmType == AlgorithmType.Classification || numberofClusterer > 0)) {
                    btnrun.setDisable(false);
                } else {
                    btnrun.setDisable(true);
                }
            } catch (Exception e) {
                btnrun.setDisable(true);
            }
        });

        btnrun.setOnAction(event -> {

            runButtonAction();
        });

        txtMaxIter.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                try {
                    int i = Integer.parseInt(txtMaxIter.getText());
                    algorithmSettings.get(algorithmNameForConfigWindow).Max = i;
                    errorLabel.setText("");
                } catch (Exception e) {
                    //ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                    //dialog.show("Error", "Invaild Information");
                    errorLabel.setText("Invalid number format");
                }
            }
        });

        txtInterval.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                try {
                    int i = Integer.parseInt(txtInterval.getText());
                    algorithmSettings.get(algorithmNameForConfigWindow).Interval = i;
                    errorLabel.setText("");
                } catch (Exception e) {
                    errorLabel.setText("Invalid number format");
                }
            }
        });

        txtNumCluster.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                try {
                    int i = Integer.parseInt(txtInterval.getText());
                    algorithmSettings.get(algorithmNameForConfigWindow).NumClusters = i;
                    errorLabel.setText("");
                } catch (Exception e) {
                    errorLabel.setText("Invalid number format");
                }
            }
        });

        ConfigurationWindows();
    }

    private String algorithmName;

    private void runButtonAction() {
        PropertyManager manager = applicationTemplate.manager;
        try {
            Path path = ((AppActions) (applicationTemplate.getActionComponent())).getLoadFilePath();
            dataset = DataSet.fromTSDFile(path);
        } catch (Exception e) {
        }
        if (dataset != null) {
            if (algorithmType == AlgorithmType.Classification) {

                try {
                    classAlgorithm = Class.forName(manager.getPropertyValue(AppPropertyTypes.RandomClassifier.name()));

                    // Runnable runnable = new RandomClassifier(null, 1,1,true);

                    constructor = classAlgorithm.getConstructor(DataSet.class, int.class, int.class, boolean.class);

                    if (classifier == null || !classifier.isAlgorithInProgress())
                        classifier = (RandomClassifier) constructor.newInstance(dataset, maxIterval, upate, toCountious);

                    classAlgorithm.getMethod(manager.getPropertyValue(AppPropertyTypes.Setlinechart.name()), LineChart.class).invoke(classifier, chart);
                    classAlgorithm.getMethod(manager.getPropertyValue(AppPropertyTypes.SetBtnScrshot.name()), Button.class).invoke(classifier, scrnshotButton);
                    classAlgorithm.getMethod(manager.getPropertyValue(AppPropertyTypes.SetBtnrun.name()), Button.class).invoke(classifier, btnrun);

                    threadClassifier = new Thread(classifier);
                    threadClassifier.setDaemon(true);
                    threadClassifier.start();

//                if (classifier == null || !classifier.isAlgorithInProgress())
//                    classifier = new RandomClassifier(dataset, maxIterval, upate, toCountious);
//
//                classifier.setlinechart(chart);
//                classifier.setBtnScrshot(scrnshotButton);
//                classifier.setBtnrun(btnrun);
//
//                threadClassifier = new Thread(classifier);
//                threadClassifier.setDaemon(true);
//                threadClassifier.start();


//                if (classifier == null || !classifier.isAlgorithInProgress())
//                    classifier = new RandomClassifier(dataset, maxIterval, upate, toCountious);
//                classifier.setlinechart(chart);
//                classifier.setBtnScrshot(scrnshotButton);
//                classifier.run();

                } catch (Exception e) {
                }

            }
            // Clustering
            else {
                if (algorithmName.equals(manager.getPropertyValue(AppPropertyTypes.RandomClassifier.name()))) {
                    try {
                        classAlgorithm = Class.forName(manager.getPropertyValue(AppPropertyTypes.RandomClusterer.name()));
                        constructor = classAlgorithm.getConstructor(DataSet.class, int.class, int.class, int.class, boolean.class);

                        if (randomClusterer == null || !randomClusterer.isAlgorithInProgress())
                            randomClusterer = (RandomClusterer) constructor.newInstance(dataset, maxIterval, upate, numberofClusterer, toCountious);

//                        randomClusterer.setlinechart(chart);
//                        randomClusterer.setBtnrun(btnrun);
//                        randomClusterer.SetBtnScrshot(scrnshotButton);
                        classAlgorithm.getMethod(manager.getPropertyValue(AppPropertyTypes.Setlinechart.name()), LineChart.class).invoke(randomClusterer, chart);
                        classAlgorithm.getMethod(manager.getPropertyValue(AppPropertyTypes.SetBtnScrshot.name()), Button.class).invoke(randomClusterer, scrnshotButton);
                        classAlgorithm.getMethod(manager.getPropertyValue(AppPropertyTypes.SetBtnrun.name()), Button.class).invoke(randomClusterer, btnrun);

                        threadrandomClusterer = new Thread(randomClusterer);
                        threadrandomClusterer.setDaemon(true);
                        threadrandomClusterer.start();


//                    if (randomClusterer == null || !randomClusterer.isAlgorithInProgress())
//                        randomClusterer = new RandomClusterer(dataset, maxIterval, upate, numberofClusterer, toCountious);
//
//                    randomClusterer.setlinechart(chart);
//                    randomClusterer.setBtnrun(btnrun);
//                    randomClusterer.SetBtnScrshot(scrnshotButton);
//
//                    threadrandomClusterer = new Thread(randomClusterer);
//                    threadrandomClusterer.setDaemon(true);
//                    threadrandomClusterer.start();

                    } catch (Exception e) {
                    }

                } else if (algorithmName.equals(manager.getPropertyValue(AppPropertyTypes.KmeanClusterer.name()))) {

                    if (isBtnRunClickFirstTime) {
                        isBtnRunClickFirstTime = false;
                        if (toCountious) {
                            btnrun.setDisable(true);
                            scrnshotButton.setDisable(true);
                        }

                        try {
                            classAlgorithm = Class.forName(manager.getPropertyValue(AppPropertyTypes.KmeanClusterer.name()));
                            constructor = classAlgorithm.getConstructor(DataSet.class, int.class, int.class, int.class, boolean.class);
                            kMeansClusterer = (KMeansClusterer) constructor.newInstance(dataset, maxIterval, upate, numberofClusterer, toCountious);
                            //classAlgorithm.getMethod(manager.getPropertyValue(AppPropertyTypes.addIntervalEventHandler.name(),));
                        } catch (Exception e) {
                        }
                        //kMeansClusterer = new KMeansClusterer(dataset, maxIterval, upate, numberofClusterer, toCountious);

                        kMeansClusterer.addIntervalEventHandler((dataset, iteration) -> {
                            displayDataToChart(dataset);
                            System.out.println(iteration + "");
                        });
                        kMeansClusterer.addFinishedEventHandler(() -> {
                            btnrun.setDisable(false);
                            scrnshotButton.setDisable(false);
                            isBtnRunClickFirstTime = true;
                        });

                        threadkMeansClusterer = new Thread(kMeansClusterer);
                        threadkMeansClusterer.setDaemon(true);
                        threadkMeansClusterer.start();
                    } else {
                        kMeansClusterer.continueAlogrithm();
                    }
                }
            }
        }
    }

    private boolean isBtnRunClickFirstTime = true;

    private void displayDataToChart(DataSet dataset) {

        // 2. make a <label, series> map
        Map<String, XYChart.Series> seriesMap = new HashMap<>();

        // 3. put each points into each series, by its label
        for (String instancename : dataset.getLabels().keySet()) {
            String label = dataset.getLabels().get(instancename);

            if (!seriesMap.containsKey(label)) {
                seriesMap.put(label, new XYChart.Series());
            }

            XYChart.Series series = seriesMap.get(label);

            Point2D point = dataset.getLocations().get(instancename);

            series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
        }

        // 4. put series into chart
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // 1. clear chart
                chart.getData().clear();

                for (XYChart.Series series : seriesMap.values()) {
                    chart.getData().add(series);

                    Node n = series.getNode();
                    n.setStyle(" -fx-stroke: transparent;");
                }

                if (!toCountious) {
                    scrnshotButton.setDisable(false);
                }
            }
        });
    }

    private void loadSettingsToConfigWindow() {
        if (algorithmSettings.get(algorithmNameForConfigWindow).Max >= 0)
            txtMaxIter.setText(algorithmSettings.get(algorithmNameForConfigWindow).Max + "");
        if (algorithmSettings.get(algorithmNameForConfigWindow).Interval >= 0)
            txtInterval.setText(algorithmSettings.get(algorithmNameForConfigWindow).Interval + "");
        if (algorithmSettings.get(algorithmNameForConfigWindow).NumClusters >= 0)
            txtNumCluster.setText(algorithmSettings.get(algorithmNameForConfigWindow).NumClusters + "");

        stageConfig.show();
    }

    GridPane pane = new GridPane();
    Scene scene = new Scene(pane);
    Label errorLabel = new Label();

    Label NumOfCluster = new Label("Number Of Cluster");


    public void ConfigurationWindows() {
        errorLabel.setMinWidth(200);

        stageConfig.setTitle("Algorithm Run stageConfig");

        pane.add(new Label("Max. Iterations"), 0, 0);
        pane.add(txtMaxIter, 1, 0);
        pane.add(new Label("Update Interval"), 0, 1);
        pane.add(txtInterval, 1, 1);

        // if (algorithmType == AlgorithmType.Clustering) {
        pane.add(NumOfCluster, 0, 2);
        pane.add(txtNumCluster, 1, 2);
        // }

//        NumOfCluster.setVisible(false);
//        txtNumCluster.setVisible(false);

        pane.add(new Label("Continous Run?"), 0, 3);
        pane.add(chkContinousRun, 1, 3);

        pane.add(errorLabel, 0, 4);
        stageConfig.setScene(scene);
        // stageConfig.show();
    }

    private void handleClassificationAlgorithmSelection() {
        btnrun.setVisible(true);
        btnrun.setDisable(true);
    }

    private void handleClusteringAlgorithmSelection() {
        btnrun.setVisible(true);
        btnrun.setDisable(true);
    }

    public void showDispalyButton(boolean enable) {
        displayButton.setVisible(enable);
    }

    public void showTextArea(boolean enable) {
        textArea.setVisible(enable);
    }

    private void setWorkspaceActions() {
        setTextAreaActions();
        setDisplayButtonActions();
    }

    public void setLineRead(int i) {
        linesread = i;
    }

    private void setTextAreaActions() {

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.equals(oldValue)) {
                    if (!newValue.isEmpty()) {
                        ((AppActions) applicationTemplate.getActionComponent()).setIsUnsavedProperty(true);
                        if (newValue.charAt(newValue.length() - 1) == '\n')
                            hasNewText = true;
                        newButton.setDisable(false);
                        saveButton.setDisable(false);
                    } else {
                        hasNewText = true;
                        newButton.setDisable(true);
                        saveButton.setDisable(true);
                    }

                    String[] oldlines = oldValue.split("\n");
                    String[] newlines = newValue.split("\n");

                    String s = "";
                    int delectlinenum = oldlines.length - newlines.length;

                    String filecontext = ((AppData) applicationTemplate.getDataComponent()).getLoadFile();
                    String[] filelines = filecontext.split("\n");

                    if (filelines.length > 10) {
                        // linesread = 10;
                        for (int i = 0; i < delectlinenum; i++) {
                            if (linesread < filelines.length) {
                                s = textArea.getText() + filelines[linesread] + "\n";
                                textArea.setText(s);
                                linesread++;
                            } else {
                                break;
                            }
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                System.err.println(newValue);
            }
        });
    }

    public String getTextAreaText() {
        return textArea.getText();
    }

    private void setDisplayButtonActions() {
        displayButton.setOnAction(event -> {
            if (hasNewText) {
                try {
                    chart.getData().clear();
                    AppData dataComponent = (AppData) applicationTemplate.getDataComponent();
                    dataComponent.clear();
                    dataComponent.loadData(textArea.getText());
                    dataComponent.displayData();
                    AverageLine(chart);
                    if (chart.getData().size() > 0) {
                        scrnshotButton.setDisable(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //clear chart
    public void ClearChart() {
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.getYAxis().setVisible(false);
        chart.getXAxis().setVisible(false);
        chart.getStylesheets().add("ui/stylesheet.css");
    }

    //average line
    public void AverageLine(LineChart<Number, Number> chart) {
        double avgY = 0;
        ArrayList<XYChart.Data<Number, Number>> allpoints = new ArrayList<XYChart.Data<Number, Number>>();
        javafx.collections.ObservableList<javafx.scene.chart.XYChart.Series<Number, Number>> series = chart.getData();

        for (int i = 0; i < series.size(); i++) {
            allpoints.addAll(series.get(i).getData());
        }

        for (int i = 0; i < allpoints.size(); i++) {
            avgY += allpoints.get(i).getYValue().doubleValue();
        }
        avgY /= allpoints.size();
        if (allpoints.size() == 0)
            return;

        double min = allpoints.get(0).getXValue().doubleValue();
        double max = allpoints.get(0).getXValue().doubleValue();
        for (int i = 1; i < allpoints.size(); i++) {
            if (min > allpoints.get(i).getXValue().doubleValue()) {
                min = allpoints.get(i).getXValue().doubleValue();
            }
            if (max < allpoints.get(i).getXValue().doubleValue()) {
                max = allpoints.get(i).getXValue().doubleValue();
            }
        }
        //two points of average line
        XYChart.Series averageseries = new XYChart.Series();
        averageseries.getData().add(new XYChart.Data<>(min, avgY));
        averageseries.getData().add(new XYChart.Data<>(max, avgY));
        chart.getData().add(averageseries);
    }
}

enum AlgorithmType {
    Classification,
    Clustering
}

enum AlgorithmName {
    ClassificationA,
    ClassificationB,
    ClassificationC,
    ClusteringA,
    ClusteringB,
    ClusteringC
}

class AlgorithmSetting {
    public int Max = -1;
    public int Interval = -1;
    public int NumClusters = -1;
    public boolean ToContinous;

    public AlgorithmSetting(int max, int interval, int numClusters, boolean toContinous) {
        Max = max;
        Interval = interval;
        NumClusters = numClusters;
        ToContinous = toContinous;
    }
}

