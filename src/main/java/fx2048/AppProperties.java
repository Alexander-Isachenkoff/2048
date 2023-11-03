package fx2048;

public class AppProperties {

    private static final ColorSettings colorSettings = ColorSettings.loadOrDefault();

    public static ColorSettings getColorSettings() {
        return colorSettings;
    }

}
