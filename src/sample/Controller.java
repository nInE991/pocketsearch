package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class Controller {
    private List list;
    private Boolean min = true;
    int cond = 0;
    @FXML
    private TextField functionForm;
    @FXML
    private TextField x0Form;
    @FXML
    private TextField h0Form;
    @FXML
    private TextField rForm;
    @FXML
    private TextField toleranceForm;
    @FXML
    private TextField iterationForm;
    @FXML
    private TextField timeForm;
    @FXML
    private TextField resultXForm;
    @FXML
    private TextField resultFunctionXForm;
    @FXML
    private TextField resultAbsForm;
    @FXML
    private TextField resultIterationForm;
    @FXML
    private TextField resultTimeForm;
    @FXML
    private RadioButton minRadioButton;
    @FXML
    private RadioButton maxRadioButton;
    @FXML
    private Label resultLabelForm;
    @FXML
    private ProgressIndicator progressIndicatorForm;
    @FXML
    private void Min(){
        min = true;
        maxRadioButton.setSelected(false);
    }
    @FXML
    private void Max(){
        min = false;
        minRadioButton.setSelected(false);
    }
    @FXML
    private void OpenExcel(){
        try{
            File file = new File("LookingForOneOptPoint.xlsx");
            String command = "excel \"" + file + "\"";
            Runtime.getRuntime().exec("cmd.exe /c start " + command);
        }
        catch (Exception err){
            System.out.println(String.valueOf(err.getMessage()));
        }
    }
    @FXML
    private void ChangeLookingExcel(){
        try {
            Runtime.getRuntime().exec("cmd.exe /c taskkill /f /im excel.exe");
        } catch (Exception err){
            System.out.println(String.valueOf(err.getMessage()));
        }
    }
    @FXML
    private void ButtonSeach() {
        Task task= new Task(){
            @Override
            protected Object call() {
                try {
                    cond = 0;
                    Method method = new Method();
                    method.startTime = System.currentTimeMillis();
                    if (functionForm.getText().isEmpty()) {
                        throw new Exception("Function empty !");
                    }
                    if (x0Form.getText().isEmpty()) {
                        throw new Exception("X0 empty !");
                    }
                    if (h0Form.getText().isEmpty()) {
                        throw new Exception("h0 empty !");
                    }
                    if (rForm.getText().isEmpty()||new Double(rForm.getText())< 1 ){
                        throw new Exception("r empty or r < 1,R must be 1 or greater !");
                    }
                    if (toleranceForm.getText().isEmpty()) {
                        throw new Exception("Tolerance (epsilon) empty !");
                    }
                    if (iterationForm.getText().isEmpty()) {
                        throw new Exception("Iteration limit empty !");
                    }
                    if (timeForm.getText().isEmpty()) {
                        throw new Exception("Time limit empty !");
                    }
                    if (new Integer(iterationForm.getText()) <= 0) {
                        throw new Exception("Iteration can not be 0 or negative !");
                    }
                    if (new Double(timeForm.getText()) <= 0) {
                        throw new Exception("Time can not be 0 or negative !");
                    }
                    progressIndicatorForm.setVisible(true);
                    method.function=functionForm.getText().toLowerCase();
                    method.x0 = new BigDecimal(Double.valueOf(x0Form.getText().replace(',','.')));
                    method.h0 = new BigDecimal(Double.valueOf(h0Form.getText().replace(',','.')));
                    method.r = new BigDecimal(Double.valueOf(rForm.getText().replace(',','.')));
                    method.tol = new BigDecimal(Double.valueOf(toleranceForm.getText().replace(',','.')));
                    method.iteration=Integer.valueOf(iterationForm.getText());
                    method.itermax=Integer.valueOf(iterationForm.getText());
                    method.timeLimit=Long.parseLong(timeForm.getText());
                    method.timeMax = Long.parseLong(timeForm.getText());
                    list = new Expression(method.function).getUsedVariables();
                    if(list.get(0)!=null){
                        method.perem = String.valueOf(list.get(0));
                    }
                    else{
                        throw new Exception("Error entering operator!");
                    }
                    method.x1 = method.x0.add(method.h0);
                    method.h1 = method.h0;

                    method.latch = new CountDownLatch(1);
                    while (method.h1.abs().compareTo(method.tol)>0 && cond==0){
                        if (method.latch.getCount() != 1) {
                            method.latch = new CountDownLatch(1);
                        }
                        method.functionx0 = new Expression(method.function).with(method.perem,method.x0).eval();
                        method.functionx1 = new Expression(method.function).with(method.perem,method.x1).eval();
                        if(min){
                            method.SearchMin();
                        }
                        else{
                            method.SearchMax();
                        }
                        method.iter++;
                        if (method.iter >= method.iteration) {
                            Platform.runLater(() -> {
                                        method.StartPause();
                                        progressIndicatorForm.setVisible(false);
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Confirmation Dialog");
                                        alert.setHeaderText("number of iteration reached iteration limit !");
                                        alert.setContentText("You want to add iteration ?");
                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK) {
                                            method.iteration = method.iteration + method.itermax;
                                            iterationForm.setText(String.valueOf(method.iteration));
                                            progressIndicatorForm.setVisible(true);
                                        } else {
                                            cond = 1;
                                            resultLabelForm.setText("Solution not found, number of iteration reached by iteration limit !");
                                        }
                                        method.latch.countDown();
                                        method.StopPause();
                                    }
                            );
                            method.latch.await();
                        }
                        if ((System.currentTimeMillis() - method.startTime) >= method.timeLimit) {
                            Platform.runLater(() -> {
                                        method.StartPause();
                                        progressIndicatorForm.setVisible(false);
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Confirmation Dialog");
                                        alert.setHeaderText("The time exceeded !");
                                        alert.setContentText("You want to add time ?");
                                        Optional<ButtonType> result = alert.showAndWait();
                                        if (result.get() == ButtonType.OK) {
                                            method.timeLimit = method.timeLimit + method.timeMax;
                                            timeForm.setText(String.valueOf(method.timeLimit));
                                            progressIndicatorForm.setVisible(true);
                                        } else {
                                            cond = 1;
                                            resultLabelForm.setText("Solution not found, time exceeded !");
                                        }
                                        method.latch.countDown();
                                        method.StopPause();
                                    }

                            );
                            method.latch.await();
                        }
                        Thread.sleep(15);
                    }

                    Platform.runLater(() -> {
                        if (cond == 0) {
                            resultLabelForm.setText("Solution found!");
                        }
                        method.resultTime = System.currentTimeMillis() - method.startTime;
                        resultXForm.setText(String.valueOf(method.x1.setScale(34,RoundingMode.UP)));
                        resultFunctionXForm.setText(String.valueOf(method.functionx1.setScale(34,RoundingMode.UP)));
                        resultAbsForm.setText(String.valueOf(method.h1.abs().setScale(34,RoundingMode.UP)));
                        resultIterationForm.setText(String.valueOf(method.iter));
                        resultTimeForm.setText(String.valueOf(method.resultTime));
                        progressIndicatorForm.setVisible(false);
                    });
                }
                catch(Exception ex){
                    try {
                        Platform.runLater(() -> {
                            resultXForm.setText("");
                            resultFunctionXForm.setText("");
                            resultAbsForm.setText("");
                            resultIterationForm.setText("");
                            resultTimeForm.setText("");
                            progressIndicatorForm.setVisible(false);
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error Dialog");
                            alert.setHeaderText("Error !!! ");
                            alert.setContentText(String.valueOf(ex.getMessage()));
                            alert.showAndWait();
                            resultLabelForm.setText(String.valueOf(ex.getMessage()));
                        });
                    } catch (Exception err) {
                        System.out.println(err.getMessage());
                    }
                }
                return null;
            }
            };
        Thread thread = new Thread(task);
        thread.start();
        Platform.runLater(()->ButtonClean());
    }
    @FXML
    private void ButtonClean(){
        resultXForm.clear();
        resultFunctionXForm.clear();
        resultAbsForm.clear();
        resultIterationForm.clear();
        resultTimeForm.clear();
        resultLabelForm.setText("");
    }
}
