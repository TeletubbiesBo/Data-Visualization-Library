package dataprocessors;

import actions.AppActions;
import javafx.application.Application;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.lang.String;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        try {
            //read file
            String content = new String(Files.readAllBytes(dataFilePath));

            //processor
            processDataString(content, dataFilePath.getFileName().toString());
            //data into chart
            //loadData(content);

            displayData();
            //average line
            // ((AppUI) applicationTemplate.getUIComponent()).AverageLine(((AppUI) applicationTemplate.getUIComponent()).getChart());
            ((AppUI) applicationTemplate.getUIComponent()).setLineRead(10);
            ((AppUI) applicationTemplate.getUIComponent()).enableScrnShotButton(false);
        } catch (IOException e) {
        }
    }

    public void processDataString(String content, String datasource) {

        String[] lines = content.split("\n");
        String tenlines = "";
        //Processor data
        loadData(content);

        //if the data is valid
        if (!processor.hadAnError.get()) {
            ((AppUI) applicationTemplate.getUIComponent()).showTextArea(true);
            //show data into textArea
            if (lines.length <= 10) {
                ((AppUI) applicationTemplate.getUIComponent()).setTextArea(content);
            } else {
                // applicationTemplate.getDialog(Dialog.DialogType.ERROR).show("Too many lines",
                //         "Loaded data consists of " + lines.length + " lines. Showing only the first 10 in the text area.");
                for (int k = 0; k < 10; k++) {
                    tenlines += lines[k] + "\n";
                }
                ((AppUI) applicationTemplate.getUIComponent()).setTextArea(tenlines);
            }

            ((AppUI) applicationTemplate.getUIComponent()).showpaneAlgorithmType(true);
            ((AppUI) applicationTemplate.getUIComponent()).showlableDataInfo(true);

            Set<String> labelset = new HashSet<>(processor.getDataLabels().values());
            String[] labels = new HashSet<>(labelset).toArray(new String[labelset.size()]);
            String dataInfo = lines.length + " instances with " + labels.length + " labels loaded from \n" +
                    datasource + ". The labels are:\n";

            for (int i = 0; i < labels.length; i++)
                dataInfo += " - " + labels[i] + "\n";

            ((AppUI) applicationTemplate.getUIComponent()).inputTextintolableDataInfo(dataInfo);

            int numOfnamedLabels = 0;
            for (int i = 0; i < labels.length; i++) {
                if (labels[i] != "") {
                    numOfnamedLabels++;
                }
            }

            ((AppUI) applicationTemplate.getUIComponent()).disablebtnClassification(numOfnamedLabels != 2);

        }
        // invalid
        else {
            ((AppUI) applicationTemplate.getUIComponent()).showlableDataInfo(false);
            ((AppUI) applicationTemplate.getUIComponent()).showpaneAlgorithmType(false);
            ((AppUI) applicationTemplate.getUIComponent()).showTextArea(false);
        }
    }

    public String getLoadFile() {
        try {
            Path a = ((AppActions) (applicationTemplate.getActionComponent())).getLoadFilePath();
            if (a != null) {
                String s = new String(Files.readAllBytes(a));
                return s;
            }
        } catch (IOException e) {
        }
        return "";
    }

    public void loadData(String dataString) {
        try {
            processor.processString(dataString);
        } catch (Exception e) {
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput + e.getMessage());
        }
    }

    @Override
    public void saveData(Path dataFilePath) {
        // NOTE: completing this method was not a part of HW 1. You may have implemented file saving from the
        // confirmation dialog elsewhere in a different way.
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(dataFilePath))) {
            writer.write(((AppUI) applicationTemplate.getUIComponent()).getCurrentText());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
