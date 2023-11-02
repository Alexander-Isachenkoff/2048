package fx2048;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import fx2048.utils.FileUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ColorSettings {

    private static final String COLORS_SETTING_FILE = "colors.xml";
    private final SimpleStringProperty seedProperty = new SimpleStringProperty("");
    private final SimpleDoubleProperty saturationProperty = new SimpleDoubleProperty();
    private final SimpleDoubleProperty brightnessProperty = new SimpleDoubleProperty();

    public ColorSettings() {
    }

    public ColorSettings(String seed, double saturation, double brightness) {
        setSeed(seed);
        setSaturation(saturation);
        setBrightness(brightness);
    }

    public static ColorSettings load() {
        return FileUtils.loadXmlObject(COLORS_SETTING_FILE, ColorSettings.class);
    }

    public void init(ColorSettings colorSettings) {
        setSeed(colorSettings.getSeed());
        setSaturation(colorSettings.getSaturation());
        setBrightness(colorSettings.getBrightness());
    }

    public void save() {
        FileUtils.saveXmlObject(this, COLORS_SETTING_FILE);
    }

    public String getSeed() {
        return seedProperty.get();
    }

    public void setSeed(String seed) {
        this.seedProperty.set(seed);
    }

    public double getSaturation() {
        return saturationProperty.get();
    }

    public void setSaturation(double saturation) {
        this.saturationProperty.set(saturation);
    }

    public double getBrightness() {
        return brightnessProperty.get();
    }

    public void setBrightness(double brightness) {
        this.brightnessProperty.set(brightness);
    }

    public SimpleStringProperty seedProperty() {
        return seedProperty;
    }

    public SimpleDoubleProperty saturationProperty() {
        return saturationProperty;
    }

    public SimpleDoubleProperty brightnessProperty() {
        return brightnessProperty;
    }

}
