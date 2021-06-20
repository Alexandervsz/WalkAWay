import java.text.DecimalFormat;

/**
 * Data class for met values.
 *
 * @see <a href="https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories">For more information.</a>
 */
public record MetValue(double metValue, double speedA, double speedB, String activity) {

    /**
     * Returns the met score
     *
     * @return the met score
     */
    public double getMetValue() {
        return metValue;
    }

    /**
     * Returns the beginning of the speed range, in kilometers per hour.
     *
     * @return The beginning of the speed range.
     */
    public double getSpeedA() {
        return speedA;
    }

    /**
     * Returns the beginning of the speed range, in kilometers per hour.
     *
     * @return The beginning of the speed range.
     */
    public double getSpeedB() {
        return speedB;
    }

    /**
     * Checks whether speed a or b is 0, and adjusts the string accordingly.
     *
     * @return A string to be used for the GUI.
     */
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
