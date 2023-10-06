package actions;

import classification.RandomClassifier;
import data.DataSet;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;

import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;


import static vilij.settings.PropertyTypes.SAVE_WORK_TITLE;
import static vilij.templates.UITemplate.SEPARATOR;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate applicationTemplate;

    /**
     * Path to the data file currently active.
     */
    Path dataFilePath, loadFilePath;

    public Path getLoadFilePath() {
        return loadFilePath;
    }

    /**
     * The boolean property marking whether or not there are any unsaved changes.
     */
    SimpleBooleanProperty isUnsaved;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        this.isUnsaved = new SimpleBooleanProperty(false);
    }

    public void setIsUnsavedProperty(boolean property) {
        isUnsaved.set(property);
    }

    @Override
    public void handleNewRequest() {
        try {
            if (!isUnsaved.get() || promptToSave()) {
                applicationTemplate.getDataComponent().clear();
                applicationTemplate.getUIComponent().clear();
                isUnsaved.set(false);
                dataFilePath = null;
                ((AppUI) applicationTemplate.getUIComponent()).showTextArea(true);
                ((AppUI) applicationTemplate.getUIComponent()).showToggle(true);
            }
        } catch (IOException e) {
            errorHandlingHelper();
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
        try {
            if (dataFilePath == null) {
                //((AppData)(applicationTemplate.getDataComponent())).loadData(((AppUI) (applicationTemplate.getUIComponent())).getTextAreaText());
                new TSDProcessor().processString(((AppUI) (applicationTemplate.getUIComponent())).getTextAreaText());
                if (promptToSave() && (isUnsaved.get() == false)) {
                    ((AppUI) (applicationTemplate.getUIComponent())).enableSaveButton(false);
                }
            } else {
                save();
            }

        } catch (Exception e) {
            ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            PropertyManager manager = applicationTemplate.manager;
            String errTitle = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name());
            String errMsg = manager.getPropertyValue(PropertyTypes.LOAD_ERROR_MSG.name());
            String errInput = manager.getPropertyValue(AppPropertyTypes.TEXT_AREA.name());
            dialog.show(errTitle, errMsg + errInput + e.getMessage());

            errorHandlingHelper();
        }
    }


    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1

        //clear   before load data
        applicationTemplate.getUIComponent().clear();
        applicationTemplate.getDataComponent().clear();

        //load data
        PropertyManager manager = applicationTemplate.manager;

        FileChooser fileChooser = new FileChooser();
        String dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
        URL dataDirURL = getClass().getResource(dataDirPath);
        fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
        fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

        String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
        String extension = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
        ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                String.format("*.%s", extension));

        fileChooser.getExtensionFilters().add(extFilter);
        File selected = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        //obtain the file name
        if (selected == null)
            return;
        loadFilePath = selected.toPath();
        //open the file
        applicationTemplate.getDataComponent().loadData(selected.toPath());
        ((AppUI) applicationTemplate.getUIComponent()).enableSaveButton(false);
    }

    private void promptDialogIsProgress() {
        Stage stage = new Stage();
        stage.setTitle("Do you want to exit?");

        Button btnYes = new Button("Yes");
        Button btnNo = new Button("No");

        HBox buttonBox = new HBox(120);
        buttonBox.getChildren().addAll(btnYes, btnNo);

        Scene scene = new Scene(buttonBox);
        stage.setScene(scene);
        stage.show();

        btnYes.setOnAction(event -> {
            System.exit(0);
            stage.close();
        });
        btnNo.setOnAction(event -> stage.close());
    }

    @Override
    public void handleExitRequest() {
        //Exception1: Unsaved data: a) NO:exit any way. b)Cancel:return to application. c)Yes:save data and then exit
        //Exception2: Algorithm is running: a)Yes:terminate the algorithm right away and close
        //                                  b)No:return to application

        Thread threadClassifier = ((AppUI) applicationTemplate.getUIComponent()).getThreadClassifier();
        Thread threadRandomClusterer = ((AppUI) applicationTemplate.getUIComponent()).getThreadrandomClusterer();
        Thread theadKmeanCluster = ((AppUI)applicationTemplate.getUIComponent()).getThreadkmeanCLusterer();

        try {
            if (threadClassifier == null && threadRandomClusterer == null && theadKmeanCluster == null) {
                try {
                    if (!isUnsaved.get() || promptToSave()) {
                        System.exit(0);
                    }
                } catch (IOException e) {
                    errorHandlingHelper();
                }
            } else if (threadClassifier != null) {
                try {
                    if (threadClassifier.isAlive()) {
                        promptDialogIsProgress();
                    } else if (!isUnsaved.get() || promptToSave()) {
                        System.exit(0);
                    }
                } catch (IOException a) {
                    errorHandlingHelper();
                }
            } else if (threadRandomClusterer != null) {
                try {
                    if (threadRandomClusterer.isAlive()) {
                        promptDialogIsProgress();
                    } else if (!isUnsaved.get() || promptToSave()) {
                        System.exit(0);
                    }
                } catch (IOException a) {
                    errorHandlingHelper();
                }
            }else if(theadKmeanCluster != null){
                try {
                    if (theadKmeanCluster.isAlive()) {
                        promptDialogIsProgress();
                    } else if (!isUnsaved.get() || promptToSave()) {
                        System.exit(0);
                    }
                } catch (IOException a) {
                    errorHandlingHelper();
                }
            }
        }catch (Exception q){
            System.exit(0);
        }
//        try {
//            if(threadClassifier.isAlive()){
//                promptDialogIsProgress();
//            }
//            else if ( !isUnsaved.get() || promptToSave()) {
//                System.exit(0);
//            }
//        } catch (Exception e) {
//            try {
//                if(threadRandomClusterer.isAlive()){
//                    promptDialogIsProgress();
//                }
//                else if (!isUnsaved.get() || promptToSave()) {
//                    System.exit(0);
//                }
//            } catch (IOException w) {
//                errorHandlingHelper();
//            }
//        }


//        try {
//            if (!isUnsaved.get() || promptToSave()) {
//                System.exit(0);
//            }
//        } catch (IOException e) {
//            errorHandlingHelper();
//        }
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;
        FileChooser fileChooser = new FileChooser();
        String dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
        URL dataDirURL = getClass().getResource(dataDirPath);
        fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
        fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

        String description = manager.getPropertyValue(AppPropertyTypes.IMG_FILE_EXT_DESC.name());
        String extension = manager.getPropertyValue(AppPropertyTypes.IMG_FILE_EXT.name());
        ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                String.format("*.%s", extension));

        fileChooser.getExtensionFilters().add(extFilter);
        File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

        SnapshotParameters snapshot = new SnapshotParameters();
        WritableImage img = ((AppUI) (applicationTemplate.getUIComponent())).getChart().snapshot(snapshot, null);
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", selected);

    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        PropertyManager manager = applicationTemplate.manager;
        ConfirmationDialog dialog = ConfirmationDialog.getDialog();
        dialog.show(manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK_TITLE.name()),
                manager.getPropertyValue(AppPropertyTypes.SAVE_UNSAVED_WORK.name()));

        if (dialog.getSelectedOption() == null) return false; // if user closes dialog using the window's close button

        if (dialog.getSelectedOption().equals(ConfirmationDialog.Option.YES)) {

            //prevent invaild data save
            String s = ((AppUI) applicationTemplate.getUIComponent()).getCurrentText();
            ((AppData) applicationTemplate.getDataComponent()).loadData(s);

            if (dataFilePath == null) {
                FileChooser fileChooser = new FileChooser();
                String dataDirPath = SEPARATOR + manager.getPropertyValue(AppPropertyTypes.DATA_RESOURCE_PATH.name());
                URL dataDirURL = getClass().getResource(dataDirPath);

                if (dataDirURL == null)
                    throw new FileNotFoundException(manager.getPropertyValue(AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND.name()));

                fileChooser.setInitialDirectory(new File(dataDirURL.getFile()));
                fileChooser.setTitle(manager.getPropertyValue(SAVE_WORK_TITLE.name()));

                String description = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name());
                String extension = manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name());
                ExtensionFilter extFilter = new ExtensionFilter(String.format("%s (.*%s)", description, extension),
                        String.format("*.%s", extension));

                fileChooser.getExtensionFilters().add(extFilter);
                File selected = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if (selected != null) {
                    dataFilePath = selected.toPath();
                    save();
                } else return false; // if user presses escape after initially selecting 'yes'
            } else
                save();
        }
        return !dialog.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL);
    }

    private void save() throws IOException {
        applicationTemplate.getDataComponent().saveData(dataFilePath);
        isUnsaved.set(false);
    }

    private void errorHandlingHelper() {
        ErrorDialog dialog = (ErrorDialog) applicationTemplate.getDialog(Dialog.DialogType.ERROR);
        PropertyManager manager = applicationTemplate.manager;
        String errTitle = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_TITLE.name());
        String errMsg = manager.getPropertyValue(PropertyTypes.SAVE_ERROR_MSG.name());
        String errInput = manager.getPropertyValue(AppPropertyTypes.SPECIFIED_FILE.name());
        dialog.show(errTitle, errMsg + errInput);
    }
}
