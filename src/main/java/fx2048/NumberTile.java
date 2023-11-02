package fx2048;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import fx2048.model.Number;

public class NumberTile extends Label {

    private final Number number;
    private final ColorSettings colorSettings;

    public NumberTile(Number number) {
        this(number, AppProperties.colorSettings);
    }

    public NumberTile(Number number, ColorSettings colorSettings) {
        this.number = number;
        this.colorSettings = colorSettings;

        updateBackground();

        colorSettings.seedProperty().addListener((observable, oldValue, newValue) -> {
            updateBackground();
        });
        colorSettings.saturationProperty().addListener((observable, oldValue, newValue) -> {
            updateBackground();
        });
        colorSettings.brightnessProperty().addListener((observable, oldValue, newValue) -> {
            updateBackground();
        });

        setText(String.valueOf(number.getValue()));

        getStyleClass().add("number-tile");
        setFont(Font.font("Comic Sans MS", createFontSize()));
    }

    private static Color createColor(Number number, ColorSettings colorSettings) {
        int hue = (number.getValue() + colorSettings.getSeed().hashCode()) * 1000 % 360;
        return Color.hsb(hue, colorSettings.getSaturation(), colorSettings.getBrightness());
    }

    private void updateBackground() {
        Color color = createColor(number, colorSettings);
        setBackground(new Background(new BackgroundFill(color, new CornerRadii(8), new Insets(3))));
    }

    private int createFontSize() {
        switch (getText().length()) {
            case 1:
            case 2:
                return 36;
            case 3:
                return 32;
            case 4:
                return 24;
        }
        return 12;
    }

    public Number getNumber() {
        return number;
    }
}
