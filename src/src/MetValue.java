import java.text.DecimalFormat;

public class MetValue {
    private float metValue;
    private float speedA;
    private float speedB;
    private String activity;

    public MetValue(float metValue, float speedA, float speedB, String activity) {
        this.metValue = metValue;
        this.speedA = speedA;
        this.speedB = speedB;
        this.activity = activity;
    }

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
            return activity + " at " + decimalFormat.format(speedA) + "km/h";
        } else {
            return activity + " between " + decimalFormat.format(speedA) + " and " + decimalFormat.format(speedB) + "km/h";
        }
    }
}
