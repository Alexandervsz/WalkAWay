import java.text.DecimalFormat;

public class MetValue {
    private final float metValue;
    private final float speedA;
    private final float speedB;
    private final String activity;

    public MetValue(String metValue, String speedA, String speedB, String activity) {
        this.metValue = Float.parseFloat(metValue);
        this.speedA = Float.parseFloat(speedA);
        this.speedB = Float.parseFloat(speedB);
        this.activity = activity;
    }

    public float getMetValue() {
        return metValue;
    }

    public float getSpeedA() {
        return speedA;
    }

    public float getSpeedB() {
        return speedB;
    }

    public String getActivity() {
        return activity;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        if (speedB == -1) {
            if (speedA == -1) {
                return activity;
            } else {
                return activity + " at " + decimalFormat.format(speedA) + "km/h";
            }
        } else {
            return activity + " between " + decimalFormat.format(speedA) + " and " + decimalFormat.format(speedB) + "km/h";
        }
    }
}
