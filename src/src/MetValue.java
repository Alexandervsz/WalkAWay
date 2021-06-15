import java.text.DecimalFormat;

public class MetValue {
    private final double metValue;
    private final double speedA;
    private final double speedB;
    private final String activity;

    public MetValue(String metValue, String speedA, String speedB, String activity) {
        this.metValue = Double.parseDouble(metValue);
        this.speedA = Double.parseDouble(speedA);
        this.speedB = Double.parseDouble(speedB);
        DecimalFormat df = new DecimalFormat("#.#");

        this.activity = activity;
    }

    public double getMetValue() {
        return metValue;
    }

    public double getSpeedA() {
        return speedA;
    }

    public double getSpeedB() {
        return speedB;
    }

    public String getActivity() { return activity; }

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
