package fx2048;

import fx2048.model.Number;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class SettingsController {

    private final ColorSettings colorSettings = new ColorSettings();
    @FXML
    private TextField seedField;
    @FXML
    private Slider saturationSlider;
    @FXML
    private Slider brightnessSlider;
    @FXML
    private FlowPane numbersPane;

    @FXML
    private void initialize() {
        createNumbersPane();

        seedField.textProperty().bindBidirectional(colorSettings.seedProperty());
        saturationSlider.valueProperty().bindBidirectional(colorSettings.saturationProperty());
        brightnessSlider.valueProperty().bindBidirectional(colorSettings.brightnessProperty());

        reset();
    }

    private void createNumbersPane() {
        Integer[] values = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};
        numbersPane.getChildren().clear();
        for (Integer value : values) {
            Number number = new Number(0, 0, value);
            NumberTile numberTile = new NumberTile(number, colorSettings);
            numberTile.setPrefSize(80, 80);
            numbersPane.getChildren().add(numberTile);
        }
    }

    @FXML
    private void onReset() {
        reset();
    }

    private void reset() {
        colorSettings.init(AppProperties.getColorSettings());
    }

    @FXML
    void onCancel() {
        close();
    }

    @FXML
    void onSave() {
        colorSettings.save();
        AppProperties.getColorSettings().init(colorSettings);
        close();
    }

    private void close() {
        numbersPane.getScene().getWindow().hide();
    }

}
