import java.text.DecimalFormat;

/**
 * @see <a href="https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories">For more information.</a>
 */
public record MetValue(double metValue, double speedA, double speedB, String activity) {

    public double getMetValue() {
        return metValue;
    }

    public double getSpeedA() {
        return speedA; //In kilometers per hour
    }

    public double getSpeedB() {
        return speedB; //In kilometers per hour
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        if (speedB == 0) {
            if (speedA == 0) {
                return activity;
            } else {
                return activity + " at " + decimalFormat.format(speedA) + "km/h";
            }
        } else {
            return activity + " between " + decimalFormat.format(speedA) + " and " + decimalFormat.format(speedB) + "km/h";
        }
    }
}
